package com.example.cauds.data.model

enum class AudRisk(val displayName: String, val description: String) {
    NO_RISK(
        displayName = "No-Risk",
        description = "You are in the no-risk range for alcohol use.\n\n" +
                "Choosing not to drink supports your overall health and reduces alcohol-related risk.\n\n" +
                "This result reflects your current habits — and you can revisit it anytime."
    ),
    LOW_RISK(
        displayName = "Low-Risk",
        description = "You are in the low-risk range for alcohol use.\n\n" +
                "Your current drinking pattern falls within safer limits.\n\n" +
                "Staying mindful of your habits can help keep things that way."
    ),
    MODERATE_RISK(
        displayName = "Medium-Risk",
        description = "You are in the medium-risk range for alcohol use.\n\n" +
                "Your current drinking pattern may increase your risk for certain health or personal consequences over time.\n\n" +
                "This is a starting point — not a label. Your habits can change, and so can your score."
    ),
    HIGH_RISK(
        displayName = "High-Risk",
        description = "You are in the high-risk range for alcohol use.\n\n" +
                "Your responses suggest a pattern of drinking that may indicate alcohol dependence.\n\n" +
                "This score is not a diagnosis — but it does suggest that additional support could be helpful."
    ),
    DEFAULT_RISK(
        displayName = "",
        description = ""
    );
}