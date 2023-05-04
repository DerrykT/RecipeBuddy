package com.recipebuddy.util

import androidx.compose.runtime.MutableState
import com.recipebuddy.database.*
import com.recipebuddy.util.DatabaseManager.db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun sortByName (recipeName: String?, recipes: MutableState<List<Recipe>>) = GlobalScope.launch(Dispatchers.IO) {
    val filtered = formatRecipes(db?.readData()?.getRecipeListByRecipeName(recipeName ?: "\'%\'") ?: listOf())
    withContext(Dispatchers.Main) {
        recipes.value = filtered
    }
}


fun sortByRating (rating: Int?, recipes: MutableState<List<Recipe>>) = GlobalScope.launch(Dispatchers.IO) {
    val filtered = db?.readData()?.getRecipeListByRating(rating) ?: listOf()

    withContext(Dispatchers.Main) {

        recipes.value =
            formatRecipes(filtered)
    }
}



fun sortByTag (tag: String?) {
    val recipeNamesList = tag?.let { db?.readData()?.getRecipeNamesByTag(it) }

    if (recipeNamesList != null) {
        for (element in recipeNamesList) {
            //sortByName(element)
        }
    }
}