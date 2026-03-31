package com.example.cauds.ui.learning

import com.example.cauds.R
import com.example.cauds.data.model.Article
import com.example.cauds.data.model.Subject


// TODO: MAKE ALL OF THIS ALSO JSON FOR FUTURE TOOLING COMPATABILITY!!
val subjects = listOf(
    Subject(
        title = "Learning More About AUD",
        description = "What is AUD, what are the symptoms and risks, how can you treat it, and what can you do to help your loved ones who have it?",
        imageRes = R.drawable.learning_more_aud,
        route = "learning_more_aud",
        tidbit = "By CAUDS"
    ),
    Subject(
        title = "Getting Help",
        description = "Plan what to do in an emergency, guides for finding treatment, the do's and don'ts of talking about AUD, how to help others, and types of help!...",
        imageRes = R.drawable.getting_help,
        route = "getting_help",
        tidbit = "By CAUDS"
    ),
    Subject(
        title = "Health Information",
        description = "Nutritional tips, information on medications, and facts about alcohol's effects on the body.",
        imageRes = R.drawable.health_info,
        route = "health_info",
        tidbit = "By CAUDS"
    ),
    Subject(
        title = "Facts About Alcohol",
        description = "What is alcohol, and facts about what is considered a standard drink.",
        imageRes = R.drawable.facts_alcohol,
        route = "facts_alcohol",
        tidbit = "By CAUDS"
    )
)

// ── Article lists for each subject ──────────────────────────────
// Each list is the set of articles that appear when you tap
// one of the 4 subjects on the Understanding AUD screen.
// These are internal (not private) so UnderstandingAudScreen can access them.

internal val learningMoreArticles = listOf(
    Article("what-is-aud", "What is AUD?", "Alcohol Use Disorder, or AUD, is a health condition that can cause intense cravings, a compulsion to drink, and a loss of control — even if it worsens it.", R.drawable.what_is_aud, R.raw.article_what_is_aud, "3 minutes"),
    Article("aud-is-treatable", "AUD is Treatable", "Facts about alcohol. How treatable and how it is diagnosed.", R.drawable.aud_is_treatable, R.raw.article_aud_is_treatable, "3 minutes"),
    Article("what-increases-aud-risk", "What Increases AUD Risk?", "A person's risk for developing AUD depends on how much, how often, and how quickly they consume alcohol. Factors include: genetics, mental health, and...", R.drawable.what_increases_aud_risk, R.raw.article_what_increases_aud_risk, "3 minutes"),
    Article("symptoms-of-aud", "What Are the Symptoms of AUD?", "In AUD, severity is based on the number of criteria a person meets based on their symptoms. These criteria range from mild, moderate, to severe.", R.drawable.symptoms_of_aud, R.raw.article_symptoms_of_aud, "3 minutes"),
    Article("treatments-for-aud", "What Treatments are Available for AUD?", "Several evidence-based treatment approaches are available for AUD. One size does not fit all, and a treatment approach that may work for one per...", R.drawable.treatments_for_aud, R.raw.article_treatments_for_aud, "3 minutes"),
    Article("myths-about-addiction", "Common Myths About Addiction", "What is AUD, what are the symptoms and risks, how can you treat it, and what can you do to help your loved ones who have it?", R.drawable.myths_about_addiction, R.raw.article_myths_about_addiction, "3 minutes"),
    Article("tips-to-reduce", "Tips to Reduce", "How you can keep track of how much you drink, set new goals or reduce your consumption.", R.drawable.tips_to_reduce, R.raw.article_tips_to_reduce, "3 minutes")
)

internal val gettingHelpArticles = listOf(
    Article("when-should-you-call-for-help", "When Should You Call for Help?", "What to do in an alcohol emergency, and where to get help, 24 hours a day, 7 days a week", R.drawable.phone_911, R.raw.article_when_should_you_call, "4 minutes"),
    Article("types-of-counselling", "Types of Counselling", "Helpful information regarding types of therapy available and where to access it.", R.drawable.counsellors, R.raw.article_types_of_counselling, "5 minutes"),
    Article("supporting-those-with-aud", "Supporting Those With AUD", "How you can help support and talk to a loved one that is going through AUD.", R.drawable.helping_hand_heart, R.raw.article_supporting_those_with_aud, "8 minutes")
)

internal val healthInfoArticles = listOf(
    Article("nutrition-tips", "Nutrition Tips for Alcohol Recovery", "Guidance that supports energy and mood, while helping your body recover during and after changes to alcohol use.", R.drawable.brain_and_heart, R.raw.article_nutrition_tips, "3 minutes"),
    Article("about-aud-medications", "About AUD Medications", "Information about how medication can help people recover from AUD.", R.drawable.pharmacist_reading, R.raw.article_about_aud_medications, "2 minutes"),
    Article("medications-reduce-cravings", "Medications that Reduce Alcohol Cravings", "The types of medication that can help reduce alcohol cravings under a doctor's guidance.", R.drawable.plant_thing_2, R.raw.article_medications_reduce_cravings, "4 minutes"),
    Article("alcohol-effects-body", "Alcohol's Effects on the Body", "How heavy drinking can cause acute and chronic risks to the body.", R.drawable.gold_and_green, R.raw.article_alcohol_effects_body, "3 minutes"),
    Article("alcohol-effects-organs", "Alcohol's Effects on your Organs", "How alcohol affects the Brain, Heart, Lungs, Liver, Stomach, and Pancreas", R.drawable.blue_man, R.raw.article_alcohol_effects_organs, "7 minutes")
)

internal val factsAlcoholArticles = listOf(
    Article("what-is-alcohol", "What is Alcohol?", "Learn more about alcohol, how it's made, how it affects your body, and other types of alcohol.", R.drawable.questioning_man, R.raw.article_what_is_alcohol, "4 minutes"),
    Article("what-is-a-standard-drink", "What is a Standard Drink?", "Find out what is considered a standard drink here", R.drawable.wine_silhouette, R.raw.article_what_is_a_standard_drink, "1 minute")
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