package com.example.cauds.ui.learning

import com.example.cauds.R
import com.example.cauds.data.model.Article
import com.example.cauds.data.model.Subject

val subjects = listOf(
    Subject(
        title = "Learning More About AUD",
        description = "What is AUD, what are the symptoms and risks, how can you treat it, and what can you do to help your loved ones who have it?",
        imageRes = R.drawable.learning_more_aud,
        route = "learning_more_aud"
    ),
    Subject(
        title = "Getting Help",
        description = "Plan what to do in an emergency, guides for finding treatment, the do's and don'ts of talking about AUD, how to help others, and types of help!...",
        imageRes = R.drawable.getting_help,
        route = "getting_help"
    ),
    Subject(
        title = "Health Information",
        description = "Nutritional tips, information on medications, and facts about alcohol's effects on the body.",
        imageRes = R.drawable.health_info,
        route = "health_info"
    ),
    Subject(
        title = "Facts About Alcohol",
        description = "What is alcohol, and facts about what is considered a standard drink.",
        imageRes = R.drawable.facts_alcohol,
        route = "facts_alcohol"
    )
)

// ── Article lists for each subject ──────────────────────────────
// Each list is the set of articles that appear when you tap
// one of the 4 subjects on the Understanding AUD screen.
// These are internal (not private) so UnderstandingAudScreen can access them.

internal val learningMoreArticles = listOf(
    Article("what-is-aud", "What is AUD?", "Alcohol Use Disorder, or AUD, is a health condition that can cause intense cravings, a compulsion to drink, and a loss of control — even if it worsens it.", R.drawable.what_is_aud, R.raw.article_what_is_aud),
    Article("aud-is-treatable", "AUD is Treatable", "Facts about alcohol. How treatable and how it is diagnosed.", R.drawable.aud_is_treatable, R.raw.article_aud_is_treatable),
    Article("what-increases-aud-risk", "What Increases AUD Risk?", "A person's risk for developing AUD depends on how much, how often, and how quickly they consume alcohol. Factors include: genetics, mental health, and...", R.drawable.what_increases_aud_risk, R.raw.article_what_increases_aud_risk),
    Article("symptoms-of-aud", "What Are the Symptoms of AUD?", "In AUD, severity is based on the number of criteria a person meets based on their symptoms. These criteria range from mild, moderate, to severe.", R.drawable.symptoms_of_aud, R.raw.article_symptoms_of_aud),
    Article("treatments-for-aud", "What Treatments are Available for AUD?", "Several evidence-based treatment approaches are available for AUD. One size does not fit all, and a treatment approach that may work for one per...", R.drawable.treatments_for_aud, R.raw.article_treatments_for_aud),
    Article("myths-about-addiction", "Common Myths About Addiction", "What is AUD, what are the symptoms and risks, how can you treat it, and what can you do to help your loved ones who have it?", R.drawable.myths_about_addiction, R.raw.article_myths_about_addiction),
//    Article("tips-to-reduce", "Tips to Reduce", "How you can keep track of how much you drink, set new goals or reduce your consumption.", R.drawable.tips_to_reduce, R.raw.article_tips_to_reduce)
)

internal val gettingHelpArticles = listOf<Article>(
    // Add articles here as content is ready
)

internal val healthInfoArticles = listOf<Article>(
    // Add articles here as content is ready
)

internal val factsAlcoholArticles = listOf<Article>(
    // Add articles here as content is ready
)

// ── Map from subject route → article list ───────────────────────
// UnderstandingAudScreen uses this to look up which articles
// to pass to the ViewModel when a subject tile is tapped.
internal val subjectArticlesMap: Map<String, List<Article>> = mapOf(
    "learning_more_aud" to learningMoreArticles,
    "getting_help" to gettingHelpArticles,
    "health_info" to healthInfoArticles,
    "facts_alcohol" to factsAlcoholArticles
)