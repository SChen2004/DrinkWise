package com.example.cauds.data.repository

import com.example.cauds.data.model.JournalData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JournalRepositoryTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var repo: JournalRepository

    @Before
    fun setup() {
        db = mockk()
        collection = mockk()
        repo = JournalRepository(db)

        every { db.collection(JOURNAL) } returns collection
    }

    // -------------------------
    // saveJournalEntry tests
    // -------------------------

    @Test
    fun `saveJournalEntry success returns docId and sets userId and createdAt`() {
        // Adjust this constructor to match your JournalData fields
        val original = JournalData(
            userId = "",
            createdAt = null,
            updatedAt = null,
            entry = "hello journal"
        )

        val task = mockk<Task<DocumentReference>>(relaxed = true)
        val docRef = mockk<DocumentReference>()

        val dataSlot = slot<JournalData>()
        every { collection.add(capture(dataSlot)) } returns task

        val successSlot = slot<OnSuccessListener<DocumentReference>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        every { docRef.id } returns "newJournal123"

        var ok: Boolean? = null
        var err: String? = "placeholder"
        var docId: String? = null

        repo.saveJournalEntry("u1", original) { success, errorMsg, returnedId ->
            ok = success
            err = errorMsg
            docId = returnedId
        }

        // Verify what was written
        val saved = dataSlot.captured
        Assert.assertEquals("u1", saved.userId)
        Assert.assertNotNull(saved.createdAt)

        // Your saveJournalEntry DOES NOT set updatedAt; it should remain null unless JournalData had it set.
        // If you want updatedAt always null on create, keep this assertion.
        Assert.assertNull(saved.updatedAt)

        // simulate success callback
        successSlot.captured.onSuccess(docRef)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, err)
        Assert.assertEquals("newJournal123", docId)

        verify { collection.add(any()) }
    }

    @Test
    fun `saveJournalEntry failure returns error and null docId`() {
        val journalData = mockk<JournalData>(relaxed = true)

        val task = mockk<Task<DocumentReference>>(relaxed = true)
        every { collection.add(any()) } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } returns task

        var ok: Boolean? = null
        var err: String? = null
        var docId: String? = "should become null"

        repo.saveJournalEntry("u1", journalData) { success, errorMsg, returnedId ->
            ok = success
            err = errorMsg
            docId = returnedId
        }

        failureSlot.captured.onFailure(RuntimeException("write failed"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals("write failed", err)
        Assert.assertEquals(null, docId)

        verify { collection.add(any()) }
    }

    // -------------------------
    // updateJournalEntry tests
    // -------------------------

    @Test
    fun `updateJournalEntry success calls onResult true and sets updatedAt`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        // set() returns Task<Void>
        val dataSlot = slot<JournalData>()
        every { docRef.set(capture(dataSlot), SetOptions.merge()) } returns task

        val successSlot = slot<OnSuccessListener<Void>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        // Adjust this constructor to match your JournalData fields
        val existing = JournalData(
            userId = "u1",
            createdAt = Timestamp.now(),
            updatedAt = null,
            entry = "old"
        )

        var ok: Boolean? = null
        var err: String? = "placeholder"

        repo.updateJournalEntry("abc", existing) { success, errorMsg ->
            ok = success
            err = errorMsg
        }

        // Verify what was sent to Firestore
        val sent = dataSlot.captured
        Assert.assertNotNull(sent.updatedAt)   // repo sets updatedAt = now()
        Assert.assertEquals(existing.userId, sent.userId)
        Assert.assertEquals(existing.createdAt, sent.createdAt)

        // simulate success
        successSlot.captured.onSuccess(null)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, err)

        verify { collection.document("abc") }
        verify { docRef.set(any<JournalData>(), SetOptions.merge()) }
    }

    @Test
    fun `updateJournalEntry failure calls onResult false with message`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        every { docRef.set(any<JournalData>(), SetOptions.merge()) } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } returns task

        val journalData = mockk<JournalData>(relaxed = true)

        var ok: Boolean? = null
        var err: String? = null

        repo.updateJournalEntry("abc", journalData) { success, errorMsg ->
            ok = success
            err = errorMsg
        }

        failureSlot.captured.onFailure(RuntimeException("nope"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals("nope", err)

        verify { collection.document("abc") }
        verify { docRef.set(any<JournalData>(), SetOptions.merge()) }
    }

    // -------------------------
    // deleteJournalEntry tests
    // -------------------------

    @Test
    fun `deleteJournalEntry success calls onResult true`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        every { docRef.delete() } returns task

        val successSlot = slot<OnSuccessListener<Void>>()
        every { task.addOnSuccessListener(capture(successSlot)) } returns task
        every { task.addOnFailureListener(any()) } returns task

        var ok: Boolean? = null
        var err: String? = "placeholder"

        repo.deleteJournalEntry("abc") { success, errorMsg ->
            ok = success
            err = errorMsg
        }

        successSlot.captured.onSuccess(null)

        Assert.assertEquals(true, ok)
        Assert.assertEquals(null, err)

        verify { collection.document("abc") }
        verify { docRef.delete() }
    }

    @Test
    fun `deleteJournalEntry failure calls onResult false with message`() {
        val docRef = mockk<DocumentReference>()
        val task = mockk<Task<Void>>(relaxed = true)

        every { collection.document("abc") } returns docRef
        every { docRef.delete() } returns task

        val failureSlot = slot<OnFailureListener>()
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(failureSlot)) } returns task

        var ok: Boolean? = null
        var err: String? = null

        repo.deleteJournalEntry("abc") { success, errorMsg ->
            ok = success
            err = errorMsg
        }

        failureSlot.captured.onFailure(RuntimeException("nope"))

        Assert.assertEquals(false, ok)
        Assert.assertEquals("nope", err)

        verify { collection.document("abc") }
        verify { docRef.delete() }
    }
}