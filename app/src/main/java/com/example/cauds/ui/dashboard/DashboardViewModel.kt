package com.example.cauds.ui.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.JournalRepository
import com.example.cauds.data.repository.LogRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * DashboardViewModel — provides lightweight summaries for the dashboard:
 *   - Today's drink log count + total spend
 *   - Today's journal entry preview (first ~100 chars)
 *   - This month's drink count per day (for the mini calendar heat map)
 *
 * STATE PATTERN: Uses plain mutableStateOf (not StateFlow) to stay consistent
 * with the rest of the codebase (JournalViewModel, onboarding, etc.).
 */
class DashboardViewModel(
    private val logRepository: LogRepository = LogRepository(),
    private val journalRepository: JournalRepository = JournalRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // --- Drink log state ---

    var todayDrinkCount by mutableStateOf(0)
        private set

    var todayTotalSpent by mutableStateOf(0.0)
        private set

    // --- Week summary state ---

    // Total drinks and spend for the current week (last 7 days including today).
    // Fed into WeekSummarySection on the dashboard.
    var weekDrinkCount by mutableStateOf(0)
        private set

    var weekTotalSpent by mutableStateOf(0.0)
        private set

    // Pure UI toggle for the "I didn't drink" chip's green dot.
    // NOT persisted to Firestore — resets on app restart.
    var didntDrinkToggled by mutableStateOf(false)
        private set

    // --- Journal state ---

    var todayEntryPreview by mutableStateOf<String?>(null)
        private set

    // --- Mini calendar state ---

    // Map of LocalDate → drink count for the current month.
    // Used by MiniCalendarSection to color-code days by intensity.
    var drinkCountByDay by mutableStateOf<Map<LocalDate, Int>>(emptyMap())
        private set

    // Loading flag so the UI can show a placeholder while fetching
    var isLoading by mutableStateOf(true)
        private set

    init {
        loadTodaySummary()
        loadTodayJournal()
        loadMonthCalendar()
    }

    /**
     * Fetches all logs and computes both today's and this week's summaries.
     *
     * WHY both in one call:
     * fetchLogs already returns ALL logs for the user. Rather than making
     * two separate Firestore queries (one for today, one for the week),
     * we filter the same result set twice. One network round trip, two summaries.
     */
    fun loadTodaySummary() {
        val userId = authRepository.getUserId() ?: return
        val today = LocalDate.now()
        val todayIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        // "Start of week" = 6 days ago. So the range is [today-6 .. today] = 7 days.
        val weekStartIso = today.minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE)

        isLoading = true

        logRepository.fetchLogs(userId) { success, logs, _ ->
            if (success && logs != null) {
                // Today's stats
                val todayLogs = logs.filter { it.data.date == todayIso }
                todayDrinkCount = todayLogs.size
                todayTotalSpent = todayLogs.sumOf { it.data.drinkCost }

                // Week stats — filter for logs where date >= weekStartIso.
                // ISO date strings (e.g. "2026-03-11") compare correctly with >= and <=
                // because they're lexicographically ordered (year-month-day).
                val weekLogs = logs.filter { it.data.date >= weekStartIso && it.data.date <= todayIso }
                weekDrinkCount = weekLogs.size
                weekTotalSpent = weekLogs.sumOf { it.data.drinkCost }
            }
            isLoading = false
        }
    }

    /**
     * Fetches journal entries and checks if any were created today.
     * If so, grabs the first ~100 characters as a preview.
     *
     * HOW THIS WORKS:
     * JournalRepository.getJournalEntries returns entries sorted newest-first.
     * Each entry has a `createdAt` Timestamp. We format that to "MMM d" (e.g. "Mar 17")
     * and compare it to today's formatted date. If there's a match, we take the
     * entry text and truncate it for the dashboard preview.
     *
     * WHY we reuse JournalRepository instead of a separate query:
     * The existing getJournalEntries() already fetches the 20 most recent entries.
     * Today's entry (if it exists) will always be in that batch since it's the newest.
     * No need for a separate Firestore query — we just filter client-side.
     */
    fun loadTodayJournal() {
        val userId = authRepository.getUserId() ?: return
        val todayFormatted = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())

        journalRepository.getJournalEntries(userId) { success, entries, _ ->
            if (success && entries != null) {
                // Find the first entry whose createdAt date matches today.
                // entries is List<Pair<String, JournalData>> — first = docId, second = data
                val todayEntry = entries.firstOrNull { (_, data) ->
                    val entryDate = data.createdAt?.toDate()?.let { date ->
                        SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
                    }
                    entryDate == todayFormatted
                }

                todayEntryPreview = todayEntry?.second?.entry?.let { text ->
                    if (text.length > 100) text.take(100) + "…" else text
                }
            }
        }
    }

    /** Flips the "I didn't drink" green dot on or off. No Firestore involved. */
    fun toggleDidntDrink() {
        didntDrinkToggled = !didntDrinkToggled
    }

    /**
     * Fetches all logs for the current month and groups them into a
     * Map<LocalDate, Int> where the value is the number of drinks that day.
     *
     * HOW IT WORKS:
     * We use LogRepository.fetchLogsInRange() — the same method CalendarViewModel
     * uses — with Timestamps for the start and end of the current month.
     * Then we convert each log's timestamp to a LocalDate and count occurrences.
     *
     * The resulting map feeds into MiniCalendarSection's intensity highlighting.
     */
    fun loadMonthCalendar() {
        val userId = authRepository.getUserId() ?: return

        val currentMonth = YearMonth.now()

        val startOfMonth = currentMonth.atDay(1)
            .atStartOfDay(ZoneId.systemDefault())
        val endOfMonth = currentMonth.plusMonths(1).atDay(1)
            .atStartOfDay(ZoneId.systemDefault())

        val startTimestamp = Timestamp(Date.from(startOfMonth.toInstant()))
        val endTimestamp = Timestamp(Date.from(endOfMonth.toInstant()))

        logRepository.fetchLogsInRange(userId, startTimestamp, endTimestamp) { success, logs, _ ->
            if (success && logs != null) {

                drinkCountByDay = logs.mapNotNull { item ->
                    item.data.timestamp?.toDate()?.toInstant()
                        ?.atZone(ZoneId.systemDefault())
                        ?.toLocalDate()
                }.groupingBy { it }.eachCount()
            }
        }
    }
}