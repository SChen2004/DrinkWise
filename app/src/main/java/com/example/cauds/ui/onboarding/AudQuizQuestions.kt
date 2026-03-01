package com.example.cauds.ui.onboarding

data class Option(val text: String, val score: Int)
data class Question(val text: String, val options: List<Option>)

val questions = listOf(
    Question(
        text = "How often do you have a drink containing alcohol?",
        options = listOf(
            Option("Not at all", 0),
            Option("Monthly or less", 1),
            Option("2-4 times a month", 2),
            Option("2-3 times a week", 3),
            Option("4 or more times a week", 4)
        )
    ),
    Question(
        text = "On days when you drink alcohol, how many standard drinks do you usually have?",
        options = listOf(
            Option("1 to 2", 0),
            Option("3 to 4", 1),
            Option("5 to 6", 2),
            Option("7 to 9", 3),
            Option("10 or more", 4)
        )
    ),
    Question(
        text = "How often do you have six or more drinks on one occasion?",
        options = listOf(
            Option("Never", 0),
            Option("Less than monthly", 1),
            Option("Monthly", 2),
            Option("Weekly", 3),
            Option("Daily or almost daily", 4)
        )
    ),
    Question(
        text = "In the past 6 months, how often have you drunk more than you intended?",
        options = listOf(
            Option("Never", 0),
            Option("Less than monthly", 1),
            Option("Monthly", 2),
            Option("Weekly", 3),
            Option("Daily or almost daily", 4)
        )
    ),
    Question(
        text = "In the past year, how often has drinking made it hard to meet your plans or responsibilities?",
        options = listOf(
            Option("Never", 0),
            Option("Less than monthly", 1),
            Option("Monthly", 2),
            Option("Weekly", 3),
            Option("Daily or almost daily", 4)
        )
    ),
    Question(
        text = "Have you ever felt the need to drink in the morning to feel better or get going?",
        options = listOf(
            Option("No", 0),
            Option("Yes", 4)
        )
    ),
    Question(
        text = "After drinking, how often do you feel uneasy or regret about what you did the night before?",
        options = listOf(
            Option("Never", 0),
            Option("Less than monthly", 1),
            Option("Monthly", 2),
            Option("Weekly", 3),
            Option("Daily or almost daily", 4)
        )
    ),
    Question(
        text = "In the past 6 months, how often have you been unable to remember the night before from drinking?",
        options = listOf(
            Option("Never", 0),
            Option("Less than monthly", 1),
            Option("Monthly", 2),
            Option("Weekly", 3),
            Option("Daily or almost daily", 4)
        )
    ),
    Question(
        text = "Can you think of a time when drinking led to an accident or injury for you or someone else?",
        options = listOf(
            Option("No", 0),
            Option("Yes, but not in the past year", 2),
            Option("Yes, during the past year", 4)
        )
    ),
    Question(
        text = "Have friends, family, or a doctor ever suggested you cut back on drinking?",
        options = listOf(
            Option("No", 0),
            Option("Yes, but not in the past year", 2),
            Option("Yes, during the past year", 4)
        )
    )
)