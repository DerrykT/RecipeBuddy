package com.recipebuddy.util

import com.recipebuddy.util.DatabaseManager.db

//fun sortByName (recipeName: String?): List<Recipe>? {
//    return db?.readData()?.getRecipeListByRecipeName(recipeName)
//}
//
//fun sortByRating (rating: Int?): List<Recipe>? {
//    return db?.readData()?.getRecipeListByRating(rating)
//}
//
//fun sortByTag (tag: String?) {
//    val recipeNamesList = tag?.let { db?.readData()?.getRecipeNamesByTag(it) }
//
//    if (recipeNamesList != null) {
//        for (element in recipeNamesList) {
//            sortByName(element)
//        }
//    }
//}