package com.recipebuddy.util

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.room.Room
import com.recipebuddy.R
import com.recipebuddy.database.*
import com.recipebuddy.util.DatabaseManager.db
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

object DatabaseManager {
    var db: AppDatabase? = null

    fun initDatabase(applicationContext: Context) {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recipe_database"
        ).fallbackToDestructiveMigration().build()
    }

    fun populate(context: Context) {
        db ?: return
        Thread(Runnable {
            val recipeTools = listOf(
                "Baking Sheets",
                "Electric Mixer",
                "Wire Rack"
            )

            val recipeInstructions = listOf(
                Instruction(
                    "Gather your ingredients, making sure your butter is softened, and your eggs are room temperature.",
                    null
                ),
                Instruction("Preheat the oven to 350 degrees F (175 degrees C).", null),
                Instruction(
                    "Beat butter, white sugar, and brown sugar with an electric mixer in a large bowl until smooth.",
                    null
                ),
                Instruction("Beat in eggs, one at a time, then stir in vanilla.", null),
                Instruction(
                    "Dissolve baking soda in hot water. Add to batter along with salt.",
                    null
                ),
                Instruction("Stir in flour, chocolate chips, and walnuts.", null),
                Instruction(
                    "Drop spoonfuls of dough 2 inches apart onto ungreased baking sheets.",
                    null
                ),
                Instruction(
                    "Bake in the preheated oven until edges are nicely browned, about 10 minutes.",
                    10
                ),
                Instruction(
                    "Cool on the baking sheets briefly before removing to a wire rack to cool completely.",
                    null
                )
            )

            val formattedInstructionsList = formatInstructionList(instructions = recipeInstructions)

            val recipes = mutableListOf<Recipe_Info>()

            for (i in 1..2) {
                recipes.add(
                    Recipe_Info(
                        "Chocolate Chip Cookies $i",
                        formattedInstructionsList.second,
                        formattedInstructionsList.first,
                        4,
                        getUsername(),
                        45,
                        with(
                            BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.cookies_recipe_image
                            )
                        ) {
                            val stream = ByteArrayOutputStream()
                            this.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            stream.toByteArray()
                        },
                        formatToolsList(recipeTools)
                    )
                )
            }

            val baseIngredients = listOf(
                Ingredient_List(IngredientName = "Butter", Quantity = 1.0, Unit = "cup"),
                Ingredient_List(IngredientName = "White Sugar", Quantity = 1.0, Unit = "cup"),
                Ingredient_List(IngredientName = "Brown Sugar", Quantity = 1.0, Unit = "cup"),
                Ingredient_List(IngredientName = "Eggs", Quantity = 2.0, Unit = ""),
                Ingredient_List(
                    IngredientName = "Vanilla Extract",
                    Quantity = 2.0,
                    Unit = "teaspoons"
                ),
                Ingredient_List(IngredientName = "Baking Soda", Quantity = 1.0, Unit = "teaspoon"),
                Ingredient_List(IngredientName = "Hot Water", Quantity = 2.0, Unit = "teaspoons"),
                Ingredient_List(IngredientName = "Salt", Quantity = 1.0, Unit = "teaspoon"),
                Ingredient_List(
                    IngredientName = "All-Purpose Flour",
                    Quantity = 3.0,
                    Unit = "cups"
                ),
                Ingredient_List(
                    IngredientName = "Semisweet Chocolate Chips",
                    Quantity = 2.0,
                    Unit = "cups"
                ),
                Ingredient_List(IngredientName = "Chopped Walnuts", Quantity = 1.0, Unit = "cup")
            )

            val baseTags = listOf(
                Tag_List("dessert"),
                Tag_List("dairy")
            )

            try {
                baseIngredients.forEach { ingredientList ->
                    db?.insertion()?.insertIngredientList(ingredientList)
                }
            } catch (exception: SQLiteConstraintException) {

            }

            baseTags.forEach { tag ->
                try {
                    db?.insertion()?.insertTagList(tag)
                } catch (exception: SQLiteConstraintException) {

                }
            }

            recipeTools.forEach { tool ->
                try {
                    db?.insertion()?.insertTool(Tool_List(tool))
                } catch (exception: SQLiteConstraintException) {

                }
            }

            recipes.forEach { recipe ->

                try {
                    db?.insertion()?.insertRecipeInfo(recipe)
                } catch (exception: SQLiteConstraintException) {
                }

                val recipeIngredients = listOf(
                    Ingredient_Use(
                        IngredientName = "Butter",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "White Sugar",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "Brown Sugar",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "Eggs",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2.0,
                        Unit = ""
                    ),
                    Ingredient_Use(
                        IngredientName = "Vanilla Extract",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2.0,
                        Unit = "teaspoons"
                    ),
                    Ingredient_Use(
                        IngredientName = "Baking Soda",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "teaspoon"
                    ),
                    Ingredient_Use(
                        IngredientName = "Hot Water",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2.0,
                        Unit = "teaspoons"
                    ),
                    Ingredient_Use(
                        IngredientName = "Salt",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "teaspoon"
                    ),
                    Ingredient_Use(
                        IngredientName = "All-Purpose Flour",
                        RecipeName = recipe.RecipeName,
                        Quantity = 3.0,
                        Unit = "cups"
                    ),
                    Ingredient_Use(
                        IngredientName = "Semisweet Chocolate Chips",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2.0,
                        Unit = "cups"
                    ),
                    Ingredient_Use(
                        IngredientName = "Chopped Walnuts",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1.0,
                        Unit = "cup"
                    )
                )

                recipeIngredients.forEach { ingredient ->
                    try {
                        db?.insertion()?.insertIngredientUse(ingredient)
                    } catch (exception: SQLiteConstraintException) {

                    }
                }

                val recipeTags = listOf(
                    Recipe_Tags(recipe.RecipeName, "dessert"),
                    Recipe_Tags(recipe.RecipeName, "dairy")
                )

                recipeTags.forEach { tag ->
                    try {
                        db?.insertion()?.insertRecipeTags(tag)
                    } catch (exception: SQLiteConstraintException) {

                    }
                }
            }
        }).start()
    }

    fun formatInstructionList(instructions: List<Instruction>): Pair<String, String> {
        var formattedTextString = ""
        var formattedTimeString = ""

        instructions.forEach {
            formattedTextString += (it.text + "*")
            formattedTimeString += ((it.time?.toString() ?: "0") + "*")
        }

        return Pair(formattedTextString.dropLast(1), formattedTimeString.dropLast(1))
    }

    fun formatToolsList(tools: List<String>): String {
        var formattedString = ""

        tools.forEach {
            formattedString += "$it*"
        }

        return formattedString.dropLast(1)
    }
}

fun formatRecipes(unformattedRecipes: List<Recipe_Info>): List<Recipe> {
    val formattedRecipes = mutableListOf<Recipe>()

    unformattedRecipes.forEach {
        formattedRecipes.add(formatRecipe(it))
    }

    return formattedRecipes
}

fun formatRecipe(unformattedRecipe: Recipe_Info): Recipe {
    val ingredients = db?.readData()?.getRecipeIngredients(unformattedRecipe.RecipeName) ?: listOf()
    val tags = db?.readData()?.getTagsByRecipeName(unformattedRecipe.RecipeName) ?: listOf()
    if (unformattedRecipe.RecipeInstructions.last() == '*') unformattedRecipe.RecipeInstructions =
        unformattedRecipe.RecipeInstructions.dropLast(1)
    val instructions = unformattedRecipe.RecipeInstructions.split('*')
    if (unformattedRecipe.RecipeTimers.last() == '*') unformattedRecipe.RecipeTimers =
        unformattedRecipe.RecipeTimers.dropLast(1)
    val timers = unformattedRecipe.RecipeTimers.split('*')
    val formattedInstructions = mutableListOf<Instruction>()
    var totalTime = 0
    val tools = unformattedRecipe.Tools.split('*')

    instructions.forEachIndexed { index, text ->
        totalTime += timers[index].toIntOrNull() ?: 0
        formattedInstructions.add(
            Instruction(
                text,
                if (timers[index].toInt() == 0) null else timers[index].toInt()
            )
        )
    }

    return Recipe(
        unformattedRecipe.RecipeName,
        unformattedRecipe.RecipeRating,
        unformattedRecipe.Time,
        tags,
        ingredients,
        tools,
        formattedInstructions,
        BitmapFactory.decodeByteArray(unformattedRecipe.Picture, 0, unformattedRecipe.Picture.size)
    )
}

fun reverseFormatRecipe(recipe: Recipe): Recipe_Info {
    var timers = ""
    var instructions = ""
    recipe.instructions.forEach {
        timers += "${it.time ?: 0}*"
        instructions += "${it.text}*"
    }
    timers = timers.dropLast(1)
    instructions.dropLast(1)

    var tools = ""
    recipe.tools.forEach {
        tools += "$it*"
    }
    tools.dropLast(1)

    val stream = ByteArrayOutputStream()
    recipe.imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    return Recipe_Info(
        recipe.name,
        timers,
        instructions,
        recipe.rating,
        getUsername(),
        recipe.time,
        stream.toByteArray(),
        tools
    )
}

fun fetchFormattedRecipes(
    displayedRecipes: MutableState<List<Recipe>>,
    originalRecipes: MutableState<List<Recipe>>
) {
    try {
        GlobalScope.launch(Dispatchers.IO) {
            val unformattedRecipes = db?.readData()?.getRecipes() ?: return@launch
            val formattedRecipes = formatRecipes(unformattedRecipes)

            withContext(Dispatchers.Main) {
                displayedRecipes.value = formattedRecipes
                originalRecipes.value = formattedRecipes
            }
        }
    } catch (exception: java.lang.Exception) {
        Log.d("Debugging", "ISSUE")
    }
}

fun fetchSearchTags(tagsState: MutableState<List<RecipeTag>>) {
    try {
        GlobalScope.launch(Dispatchers.IO) {
            val fetchedTags = db?.readData()?.getTags() ?: listOf()

            withContext(Dispatchers.Main) {
                tagsState.value = fetchedTags.map {
                    RecipeTag(it.Tag, false)
                }
            }
        }
    } catch (exception: java.lang.Exception) {
        Log.d("Debugging", "ISSUE")
    }
}

fun fetchIngredients(ingredientsState: MutableState<List<Ingredient_List>>) {
    try {
        GlobalScope.launch(Dispatchers.IO) {
            val fetchedIngredients = db?.readData()?.getIngredients() ?: listOf()

            withContext(Dispatchers.Main) {
                ingredientsState.value = fetchedIngredients
            }
        }
    } catch (exception: java.lang.Exception) {
        Log.d("Debugging", "ISSUE")

    }

}

fun fetchTools(toolsState: MutableState<List<String>>) {
    try {
        GlobalScope.launch(Dispatchers.IO) {
            val fetchedTools = db?.readData()?.getTools() ?: listOf()

            withContext(Dispatchers.Main) {
                toolsState.value = fetchedTools
            }
        }
    } catch (exception: java.lang.Exception) {
        Log.d("Debugging", "ISSUE")
    }
}

fun getUsername(): String = "Derryk Taylor"

fun persistTool(newTool: String, toolsState: MutableState<List<String>>) {
    try {
        GlobalScope.launch(Dispatchers.IO) {
            db?.insertion()?.insertTool(Tool_List(newTool))

            withContext(Dispatchers.Main) {
                toolsState.value = listOf(newTool) + toolsState.value
            }
        }
    } catch (exception: java.lang.Exception) {
        Log.d("Debugging", "ISSUE")
    }

}

fun persistIngredient(
    newIngredient: Ingredient_List,
    ingredientsState: MutableState<List<Ingredient_List>>
) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.insertIngredientList(newIngredient)

            withContext(Dispatchers.Main) {
                ingredientsState.value = listOf(newIngredient) + ingredientsState.value
            }
        } catch (exception: java.lang.Exception) {
            Log.d("Debugging", "ISSUE")
        }
    }
}

fun persistTag(newTag: Tag_List, tagsState: MutableState<List<RecipeTag>>) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.insertTagList(newTag)

            withContext(Dispatchers.Main) {
                tagsState.value = listOf(RecipeTag(newTag.Tag, false)) + tagsState.value
            }
        } catch (exception: java.lang.Exception) {
            Log.d("Debugging", "ISSUE")
        }
    }
}

fun persistEditedRecipe(
    newRecipe: Recipe,
    originalRecipe: Recipe,
    displayedRecipesState: MutableState<List<Recipe>>,
    originalRecipesState: MutableState<List<Recipe>>
) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.deleteRecipe(reverseFormatRecipe(originalRecipe))
        } catch (exception: Exception) {
            Log.d("Debugging", "ISSUE")
        }

        try {
            db?.insertion()?.insertRecipeInfo(
                reverseFormatRecipe(newRecipe)
            )
        } catch (exception: Exception) {
            Log.d("Debugging", "ISSUE")
        }

        newRecipe.ingredients.forEach {
            try {
                db?.insertion()?.insertIngredientUse(
                    Ingredient_Use(
                        it.IngredientName,
                        newRecipe.name,
                        it.Quantity,
                        it.Unit
                    )
                )
            } catch (exception: Exception) {
                Log.d("Debugging", "ISSUE")
            }
        }
    }
}

fun persistRecipe(recipe: Recipe) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            recipe.ingredients.forEach {
                db?.insertion()?.insertIngredientUse(
                    Ingredient_Use(
                        it.IngredientName,
                        recipe.name,
                        it.Quantity,
                        it.Unit
                    )
                )
            }
            db?.insertion()?.insertRecipeInfo(
                reverseFormatRecipe(recipe)
            )
        } catch (exception: java.lang.Exception) {
            Log.d("Debugging", "ISSUE")
        }
    }
}

fun removeRecipe(recipe: Recipe) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.deleteRecipe(
                reverseFormatRecipe(recipe)
            )
        } catch (_: Exception) {
        }
    }
}

fun removeTool(name: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.deleteTool(Tool_List(name))
        } catch (_: Exception) {
        }
    }
}

fun removeTag(name: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {

            db?.insertion()?.deleteTag(Tag_List(name))
        } catch (_: Exception) {
        }
    }
}

fun removeIngredient(name: String) {
    GlobalScope.launch(Dispatchers.IO) {
        try {
            db?.insertion()?.deleteIngredient(name)

            withContext(Dispatchers.Main) {
                ScreenManager.ingredientsState.value =
                    ScreenManager.ingredientsState.value.filter { it.IngredientName != name }
            }
        } catch (_: Exception) {
        }
    }
}

data class Recipe(
    val name: String,
    val rating: Int,
    val time: Int,
    val tags: List<String>,
    val ingredients: List<RecipeIngredientList>,
    val tools: List<String>,
    val instructions: List<Instruction>,
    val imageBitmap: Bitmap
)

fun Recipe.clone() = Recipe(
    this.name,
    this.rating,
    this.time,
    this.tags,
    this.ingredients,
    this.tools,
    this.instructions,
    this.imageBitmap
)

data class Instruction(val text: String, val time: Int?)

data class RecipeTag(val text: String, var isSelected: Boolean)