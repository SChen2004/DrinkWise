package com.example.cauds.data.model

import com.google.firebase.firestore.PropertyName

data class DrinkData(
    val userId: String = "",
    val name: String = "",
    val category: String = "", // Beer, Fermented Drinks, Wine, Hard Liquor, Mixed Drinks
    val defaultSize: String = "", // FLIGHT, PINT, PITCHER or maybe more different size to be add in future
    @get:PropertyName("isSelected")
    @set:PropertyName("isSelected")
    var isSelected: Boolean = false,
    @get:PropertyName("isCustom")
    @set:PropertyName("isCustom")
    var isCustom: Boolean = false
)

data class DrinkItem(
    val id: String,
    val data: DrinkData
)
