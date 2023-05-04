package com.recipebuddy.util

import androidx.compose.runtime.MutableState
import com.recipebuddy.database.*
import com.recipebuddy.util.DatabaseManager.db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun sortByName (recipePrefix: String, recipes: List<Recipe>) = recipes.filter { it.name.startsWith(recipePrefix) }

fun sortByTag (tag: String, recipes: List<Recipe>) = recipes.filter { it.tags.contains(tag) }