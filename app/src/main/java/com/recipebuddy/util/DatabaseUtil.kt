package com.recipebuddy.util

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

            for (i in 1..10) {
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
                Ingredient_List(IngredientName = "Butter", Quantity = 1, Unit = "cup"),
                Ingredient_List(IngredientName = "White Sugar", Quantity = 1, Unit = "cup"),
                Ingredient_List(IngredientName = "Brown Sugar", Quantity = 1, Unit = "cup"),
                Ingredient_List(IngredientName = "Eggs", Quantity = 2, Unit = ""),
                Ingredient_List(
                    IngredientName = "Vanilla Extract",
                    Quantity = 2,
                    Unit = "teaspoons"
                ),
                Ingredient_List(IngredientName = "Baking Soda", Quantity = 1, Unit = "teaspoon"),
                Ingredient_List(IngredientName = "Hot Water", Quantity = 2, Unit = "teaspoons"),
                Ingredient_List(IngredientName = "Salt", Quantity = 1, Unit = "teaspoon"),
                Ingredient_List(IngredientName = "All-Purpose Flour", Quantity = 3, Unit = "cups"),
                Ingredient_List(
                    IngredientName = "Semisweet Chocolate Chips",
                    Quantity = 2,
                    Unit = "cups"
                ),
                Ingredient_List(IngredientName = "Chopped Walnuts", Quantity = 1, Unit = "cup")
            )

            val baseTags = listOf(
                Tag_List("Dessert"),
                Tag_List("Dairy")
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
                    throw Exception("IG this is the issue")
                }

                val recipeIngredients = listOf(
                    Ingredient_Use(
                        IngredientName = "Butter",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "White Sugar",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "Brown Sugar",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
                        Unit = "cup"
                    ),
                    Ingredient_Use(
                        IngredientName = "Eggs",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2,
                        Unit = ""
                    ),
                    Ingredient_Use(
                        IngredientName = "Vanilla Extract",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2,
                        Unit = "teaspoons"
                    ),
                    Ingredient_Use(
                        IngredientName = "Baking Soda",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
                        Unit = "teaspoon"
                    ),
                    Ingredient_Use(
                        IngredientName = "Hot Water",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2,
                        Unit = "teaspoons"
                    ),
                    Ingredient_Use(
                        IngredientName = "Salt",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
                        Unit = "teaspoon"
                    ),
                    Ingredient_Use(
                        IngredientName = "All-Purpose Flour",
                        RecipeName = recipe.RecipeName,
                        Quantity = 3,
                        Unit = "cups"
                    ),
                    Ingredient_Use(
                        IngredientName = "Semisweet Chocolate Chips",
                        RecipeName = recipe.RecipeName,
                        Quantity = 2,
                        Unit = "cups"
                    ),
                    Ingredient_Use(
                        IngredientName = "Chopped Walnuts",
                        RecipeName = recipe.RecipeName,
                        Quantity = 1,
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
                    Recipe_Tags(recipe.RecipeName, "Dessert"),
                    Recipe_Tags(recipe.RecipeName, "Dairy")
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

fun formatRecipe(unformattedRecipe: Recipe_Info): Recipe {
    val ingredients = db?.readData()?.getRecipeIngredients(unformattedRecipe.RecipeName) ?: listOf()
    val tags = db?.readData()?.getTagsByRecipeName(unformattedRecipe.RecipeName) ?: listOf()
    val instructions = unformattedRecipe.RecipeInstructions.split('*')
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

fun fetchFormattedRecipes(recipes: MutableState<List<Recipe>>) {
    GlobalScope.launch(Dispatchers.IO) {
        val unformattedRecipes = db?.readData()?.getRecipes() ?: return@launch
        val formattedRecipes = mutableListOf<Recipe>()

        unformattedRecipes.forEach { unformattedRecipe ->
            val ingredients =
                db?.readData()?.getRecipeIngredients(unformattedRecipe.RecipeName) ?: listOf()
            val tags = db?.readData()?.getTagsByRecipeName(unformattedRecipe.RecipeName) ?: listOf()
            val instructions = unformattedRecipe.RecipeInstructions.split('*')
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

            formattedRecipes.add(
                Recipe(
                    unformattedRecipe.RecipeName,
                    unformattedRecipe.RecipeRating,
                    unformattedRecipe.Time,
                    tags,
                    ingredients,
                    tools,
                    formattedInstructions,
                    BitmapFactory.decodeByteArray(
                        unformattedRecipe.Picture,
                        0,
                        unformattedRecipe.Picture.size
                    )
                )
            )
        }

        withContext(Dispatchers.Main) {
            recipes.value = formattedRecipes
        }
    }
}

fun fetchSearchTags(tagsState: MutableState<List<Tag_List>>) {
    GlobalScope.launch(Dispatchers.IO) {
        val fetchedTags = db?.readData()?.getTags() ?: listOf()

        withContext(Dispatchers.Main) {
            tagsState.value = fetchedTags
        }
    }
}

fun fetchIngredients(ingredientsState: MutableState<List<Ingredient_List>>) {
    GlobalScope.launch(Dispatchers.IO) {
        val fetchedIngredients = db?.readData()?.getIngredients() ?: listOf()

        withContext(Dispatchers.Main) {
            ingredientsState.value = fetchedIngredients
        }
    }
}

fun fetchTools(toolsState: MutableState<List<String>>) {
    GlobalScope.launch(Dispatchers.IO) {
        val fetchedTools = db?.readData()?.getTools() ?: listOf()

        withContext(Dispatchers.Main) {
            toolsState.value = fetchedTools
        }
    }
}

fun getUsername(): String = "Derryk Taylor"

fun persistTool(newTool: String, toolsState: MutableState<List<String>>) {
    GlobalScope.launch(Dispatchers.IO) {
        db?.insertion()?.insertTool(Tool_List(newTool))

        withContext(Dispatchers.Main) {
            toolsState.value = listOf(newTool) + toolsState.value
        }
    }
}

fun persistIngredient(
    newIngredient: Ingredient_List,
    ingredientsState: MutableState<List<Ingredient_List>>
) {
    GlobalScope.launch(Dispatchers.IO) {
        db?.insertion()?.insertIngredientList(newIngredient)

        withContext(Dispatchers.Main) {
            ingredientsState.value = listOf(newIngredient) + ingredientsState.value
        }
    }
}

fun persistTag(newTag: Tag_List, tagsState: MutableState<List<Tag_List>>) {
    GlobalScope.launch(Dispatchers.IO) {
        db?.insertion()?.insertTagList(newTag)

        withContext(Dispatchers.Main) {
            tagsState.value = listOf(newTag) + tagsState.value
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

data class Instruction(val text: String, val time: Int?)