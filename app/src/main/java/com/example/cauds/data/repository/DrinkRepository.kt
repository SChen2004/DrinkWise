package com.example.cauds.data.repository

import com.example.cauds.data.model.DrinkData
import com.example.cauds.data.model.DrinkItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

const val DRINKS = "drinks"
const val USER_ID_DRINKS = "userId"

class DrinkRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private var isInitializing = false
    }

    // Default drinks provided if no custom selection exists
    private val defaultDrinks = listOf(
        DrinkData(name = "Ale", category = "Beer", defaultSize = "PINT", isSelected = true),
        DrinkData(name = "Cider", category = "Fermented Drinks", defaultSize = "AVERAGE", isSelected = true),
        DrinkData(name = "Rose", category = "Wine", defaultSize = "STANDARD", isSelected = true),
        DrinkData(name = "Cabernet Sauvignon", category = "Wine", defaultSize = "STANDARD", isSelected = true),
        DrinkData(name = "Hard Seltzer", category = "Fermented Drinks", defaultSize = "AVERAGE", isSelected = true),
        DrinkData(name = "Rum", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = true),
        DrinkData(name = "Whiskey", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = true),

        
        // Other defaults (not selected by default)
        DrinkData(name = "Pilsner", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Pale Ale", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Lager", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Stout", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Porter", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Double IPA", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Gose", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "IPA", category = "Beer", defaultSize = "PINT", isSelected = false),
        DrinkData(name = "Wheat Beer", category = "Beer", defaultSize = "PINT", isSelected = false),
        
        DrinkData(name = "Pino Noir", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Merlot", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Shiraz", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Malbec", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Zinfandel", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Chardonnay", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Sauvignon Blanc", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Pinot Grigio", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Riesling", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Moscato", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Port", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Sherry", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Champagne", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Prosecco", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        DrinkData(name = "Sparkling Wine", category = "Wine", defaultSize = "STANDARD", isSelected = false),
        
        DrinkData(name = "Sake", category = "Fermented Drinks", defaultSize = "AVERAGE", isSelected = false),
        DrinkData(name = "Malt Liquor", category = "Fermented Drinks", defaultSize = "AVERAGE", isSelected = false),
        
        DrinkData(name = "Gin", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = false),
        DrinkData(name = "Vodka", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = false),
        DrinkData(name = "Brandy", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = false),
        DrinkData(name = "Tequila", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = false),
        DrinkData(name = "Flavoured Liqueur", category = "Hard Liquor", defaultSize = "AVERAGE", isSelected = false),
        
        DrinkData(name = "Old Fashioned", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Margarita", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Negroni", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Manhattan", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Martini (vodka)", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Martini (gin)", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Daiquiri", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Bloody Mary", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Mojito", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Moscow Mule", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Dark'n'Stormy", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Gin & Tonic", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Cosmopolitan", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Long Island Iced Tea", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Pina Colada", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Mai Tai", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Paloma", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Vodka Cranberry", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Screwdriver", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Whiskey Sour", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Mimosa", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Gimlet", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Amaretto Sour", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Aperol Spritz", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Sidecar", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Irish Coffee", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
        DrinkData(name = "Tom Collins", category = "Mixed Drinks", defaultSize = "REGULAR", isSelected = false),
    )

    /**
     * Fetch drinks for a specific user from Firestore.
     * If the user doesn't have any drinks yet, initialize their list with the default drinks.
     */
    fun fetchDrinks(userId: String, onResult: (Boolean, List<DrinkItem>?, String?) -> Unit) {
        db.collection(DRINKS)
            .whereEqualTo(USER_ID_DRINKS, userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // No drinks found, initialize with defaults
                    if (isInitializing) {
                        onResult(true, emptyList(), null)
                    } else {
                        isInitializing = true
                        initializeDefaultDrinks(userId) { success, items, error ->
                            isInitializing = false
                            onResult(success, items, error)
                        }
                    }
                } else {
                    val drinks = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(DrinkData::class.java)?.let { data ->
                            // Backward compatibility: map old firestore "selected" bool if custom mapped "isSelected" is defaulted false
                            val isSelectedManual = doc.getBoolean("selected") ?: data.isSelected
                            val isCustomManual = doc.getBoolean("custom") ?: data.isCustom
                                
                            DrinkItem(
                                id = doc.id,
                                data = data.copy(
                                    isSelected = isSelectedManual,
                                    isCustom = isCustomManual
                                )
                            )
                        }
                    }
                    
                    // Deduplicate logic: if a user duplicated default drinks due to previous race conditions
                    val uniqueDrinks = drinks.distinctBy { it.data.name + it.data.category }
                    val duplicates = drinks.filter { it !in uniqueDrinks }
                    duplicates.forEach { 
                        deleteDrink(it.id) { _, _ -> }
                    }

                    val existingKeys = uniqueDrinks.map { it.data.name + "_" + it.data.category }.toSet()
                    val missingDefaults = defaultDrinks.filter { (it.name + "_" + it.category) !in existingKeys }
                    
                    if (missingDefaults.isNotEmpty()) {
                        val batch = db.batch()
                        val newItems = mutableListOf<DrinkItem>()
                        missingDefaults.forEach { defaultData ->
                            val docRef = db.collection(DRINKS).document()
                            val savedData = defaultData.copy(userId = userId)
                            batch.set(docRef, savedData)
                            newItems.add(DrinkItem(id = docRef.id, data = savedData))
                        }
                        batch.commit().addOnSuccessListener {
                            val combined = uniqueDrinks + newItems
                            val sortedDrinks = combined.sortedWith(compareBy({ it.data.category }, { it.data.name }))
                            onResult(true, sortedDrinks, null)
                        }.addOnFailureListener {
                            val sortedDrinks = uniqueDrinks.sortedWith(compareBy({ it.data.category }, { it.data.name }))
                            onResult(true, sortedDrinks, null)
                        }
                    } else {
                        val sortedDrinks = uniqueDrinks.sortedWith(compareBy({ it.data.category }, { it.data.name }))
                        onResult(true, sortedDrinks, null)
                    }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    private fun initializeDefaultDrinks(userId: String, onResult: (Boolean, List<DrinkItem>?, String?) -> Unit) {
        val batch = db.batch()
        val defaultItems = mutableListOf<DrinkItem>()
        
        defaultDrinks.forEach { defaultData ->
            val docRef = db.collection(DRINKS).document()
            val savedData = defaultData.copy(userId = userId)
            batch.set(docRef, savedData)
            defaultItems.add(DrinkItem(id = docRef.id, data = savedData))
        }

        batch.commit()
            .addOnSuccessListener {
                val sortedDrinks = defaultItems.sortedWith(compareBy({ it.data.category }, { it.data.name }))
                onResult(true, sortedDrinks, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    /**
     * Add a new custom drink.
     */
    fun addDrink(userId: String, drinkData: DrinkData, onResult: (Boolean, String?, String?) -> Unit) {
        val drinkWithUser = drinkData.copy(userId = userId)
        
        db.collection(DRINKS)
            .add(drinkWithUser)
            .addOnSuccessListener { docRef ->
                onResult(true, null, docRef.id)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message, null)
            }
    }

    /**
     * Update an existing drink (e.g., toggling isSelected).
     */
    fun updateDrink(drinkId: String, updates: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        db.collection(DRINKS).document(drinkId)
            .update(updates)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    /**
     * Toggle the selection status of a drink.
     */
    fun toggleDrinkSelection(drinkId: String, isSelected: Boolean, onResult: (Boolean, String?) -> Unit) {
        updateDrink(drinkId, mapOf("isSelected" to isSelected), onResult)
    }

    /**
     * Delete a custom drink.
     */
    fun deleteDrink(drinkId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection(DRINKS).document(drinkId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
