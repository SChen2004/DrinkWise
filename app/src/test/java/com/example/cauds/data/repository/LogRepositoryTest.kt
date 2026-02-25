package com.example.cauds.data.repository

import com.example.cauds.data.model.LogData
import com.example.cauds.data.model.LogItem
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LogRepositoryTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var query: Query
    private lateinit var repo: LogRepository

    @Before
    fun setup() {
        db = mockk()
        collection = mockk()
        query = mockk()
        repo = LogRepository(db)

        every { db.collection(LOGS) } returns collection
    }

    // -------------------------
    // deleteLog tests
    // -------------------------

    @Test
    fun `deleteLog success calls onResult true`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        every { docRef.delete() } returns task

        val successSlot = slot<OnSuccessListener<Void>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        var resultSuccess: Boolean? = null
        var resultErr: String? = "placeholder"

        repo.deleteLog("abc") { success, err ->
            resultSuccess = success
            resultErr = err
        }

        successSlot.captured.onSuccess(null)

        Assert.assertEquals(true, resultSuccess)
        Assert.assertEquals(null, resultErr)
        verify { collection.document("abc") }
        verify { docRef.delete() }
    }

    @Test
    fun `deleteLog failure calls onResult false with message`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        every { docRef.delete() } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } returns task

        var ok: Boolean? = null
        var err: String? = null

        repo.deleteLog("abc") { success, error ->
            ok = success
            err = error
        }

        failureSlot.captured.onFailure(RuntimeException("nope"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals("nope", err)
        verify { collection.document("abc") }
        verify { docRef.delete() }
    }

    // -------------------------
    // saveLog tests
    // -------------------------

    @Test
    fun `saveLog success returns docId and sets userId`() {

        val original = LogData(
            userId = "",        // or just omit userId entirely if it has a default
            timestamp = null,
            date = "",
            updatedAt = 0L,
            drinkType = "",
            drinkAmount = 0L,
            drinkCount = 0L,
            drinkCost = 0L,
            alcoholByVolume = 0L
        )

        val task = mockk<Task<DocumentReference>>(relaxed = true)
        val docRef = mockk<DocumentReference>()

        val dataSlot = slot<LogData>()
        every { collection.add(capture(dataSlot)) } returns task

        val successSlot = slot<OnSuccessListener<DocumentReference>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        every { docRef.id } returns "newDoc123"

        var ok: Boolean? = null
        var error: String? = "placeholder"
        var docId: String? = null

        repo.saveLog("u1", original) { success, errMsg, returnedId ->
            ok = success
            error = errMsg
            docId = returnedId
        }

        // 🔍 Verify the data written to Firestore
        val savedData = dataSlot.captured
        Assert.assertEquals("u1", savedData.userId)
        Assert.assertNotNull(savedData.timestamp)

        // simulate success
        successSlot.captured.onSuccess(docRef)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, error)
        Assert.assertEquals("newDoc123", docId)
    }

    @Test
    fun `saveLog failure returns error`() {
        val logData = mockk<LogData>(relaxed = true)

        val task = mockk<Task<DocumentReference>>(relaxed = true)
        every { collection.add(any()) } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } returns task

        var ok: Boolean? = null
        var error: String? = null
        var docId: String? = "should stay null on failure"

        repo.saveLog("u1", logData) { success, errMsg, returnedId ->
            ok = success
            error = errMsg
            docId = returnedId
        }

        failureSlot.captured.onFailure(RuntimeException("write failed"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals("write failed", error)
        Assert.assertEquals(null, docId)

        verify { collection.add(any()) }
    }

    // -------------------------
    // fetchLogs tests
    // -------------------------

    @Test
    fun `fetchLogs failure returns error`() {
        val task = mockk<Task<QuerySnapshot>>(relaxed = true)

        every { collection.whereEqualTo(USER_ID, "u1") } returns query
        every { query.get() } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } returns task
        every { task.addOnSuccessListener(any()) } returns task

        var ok: Boolean? = null
        var logs: List<LogItem>? = listOf()
        var err: String? = null

        repo.fetchLogs("u1") { success, list, error ->
            ok = success
            logs = list
            err = error
        }

        failureSlot.captured.onFailure(RuntimeException("boom"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals(null, logs)
        Assert.assertEquals("boom", err)
        verify { collection.whereEqualTo(USER_ID, "u1") }
        verify { query.get() }
    }

    @Test
    fun `fetchLogs success maps documents into LogItems`() {
        val task = mockk<Task<QuerySnapshot>>(relaxed = true)
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        val data1 = mockk<LogData>(relaxed = true)
        val data2 = mockk<LogData>(relaxed = true)

        every { collection.whereEqualTo(USER_ID, "u1") } returns query
        every { query.get() } returns task

        every { snapshot.documents } returns listOf(doc1, doc2)

        every { doc1.id } returns "id1"
        every { doc2.id } returns "id2"

        every { doc1.toObject(LogData::class.java) } returns data1
        every { doc2.toObject(LogData::class.java) } returns data2

        val successSlot = slot<OnSuccessListener<QuerySnapshot>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        var ok: Boolean? = null
        var logs: List<LogItem>? = null
        var err: String? = "placeholder"

        repo.fetchLogs("u1") { success, list, error ->
            ok = success
            logs = list
            err = error
        }

        // simulate success callback:
        successSlot.captured.onSuccess(snapshot)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, err)
        Assert.assertNotNull(logs)
        Assert.assertEquals(2, logs!!.size)
        Assert.assertEquals("id1", logs!![0].id)
        Assert.assertEquals(data1, logs!![0].data)
        Assert.assertEquals("id2", logs!![1].id)
        Assert.assertEquals(data2, logs!![1].data)

        verify { collection.whereEqualTo(USER_ID, "u1") }
        verify { query.get() }
    }

    // -------------------------
// fetchLogsInRange tests
// -------------------------

    @Test
    fun `fetchLogsInRange failure returns error`() {
        val task = mockk<Task<QuerySnapshot>>(relaxed = true)

        val q1 = mockk<Query>() // after whereEqualTo
        val q2 = mockk<Query>() // after timestamp filters

        val start = com.google.firebase.Timestamp.now()
        val end = com.google.firebase.Timestamp.now()

        every { collection.whereEqualTo(USER_ID, "u1") } returns q1
        every { q1.whereGreaterThanOrEqualTo(TIMESTAMP, start) } returns q2
        every { q2.whereLessThan(TIMESTAMP, end) } returns q2
        every { q2.get() } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnFailureListener(capture(failureSlot)) } returns task
        every { task.addOnSuccessListener(any()) } returns task

        var ok: Boolean? = null
        var logs: List<LogItem>? = listOf()
        var err: String? = null

        repo.fetchLogsInRange("u1", start, end) { success, list, error ->
            ok = success
            logs = list
            err = error
        }

        failureSlot.captured.onFailure(RuntimeException("range boom"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals(null, logs)
        Assert.assertEquals("range boom", err)

        verify { collection.whereEqualTo(USER_ID, "u1") }
        verify { q1.whereGreaterThanOrEqualTo(TIMESTAMP, start) }
        verify { q2.whereLessThan(TIMESTAMP, end) }
        verify { q2.get() }
    }

    @Test
    fun `fetchLogsInRange success maps documents into LogItems`() {
        val task = mockk<Task<QuerySnapshot>>(relaxed = true)
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        val data1 = mockk<LogData>(relaxed = true)
        val data2 = mockk<LogData>(relaxed = true)

        val q1 = mockk<Query>() // after whereEqualTo
        val q2 = mockk<Query>() // after timestamp filters

        val start = com.google.firebase.Timestamp.now()
        val end = com.google.firebase.Timestamp.now()

        every { collection.whereEqualTo(USER_ID, "u1") } returns q1
        every { q1.whereGreaterThanOrEqualTo(TIMESTAMP, start) } returns q2
        every { q2.whereLessThan(TIMESTAMP, end) } returns q2
        every { q2.get() } returns task

        every { snapshot.documents } returns listOf(doc1, doc2)

        every { doc1.id } returns "id1"
        every { doc2.id } returns "id2"

        every { doc1.toObject(LogData::class.java) } returns data1
        every { doc2.toObject(LogData::class.java) } returns data2

        val successSlot = slot<OnSuccessListener<QuerySnapshot>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        var ok: Boolean? = null
        var logs: List<LogItem>? = null
        var err: String? = "placeholder"

        repo.fetchLogsInRange("u1", start, end) { success, list, error ->
            ok = success
            logs = list
            err = error
        }

        // simulate success callback:
        successSlot.captured.onSuccess(snapshot)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, err)
        Assert.assertNotNull(logs)
        Assert.assertEquals(2, logs!!.size)
        Assert.assertEquals("id1", logs!![0].id)
        Assert.assertEquals(data1, logs!![0].data)
        Assert.assertEquals("id2", logs!![1].id)
        Assert.assertEquals(data2, logs!![1].data)

        verify { collection.whereEqualTo(USER_ID, "u1") }
        verify { q1.whereGreaterThanOrEqualTo(TIMESTAMP, start) }
        verify { q2.whereLessThan(TIMESTAMP, end) }
        verify { q2.get() }
    }
}