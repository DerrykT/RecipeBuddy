package com.recipebuddy.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.R
import com.recipebuddy.components.ScreenManager.CREATE_RECIPE_SCREEN
import com.recipebuddy.components.ScreenManager.EDIT_PROFILE_SCREEN
import com.recipebuddy.components.ScreenManager.EDIT_RECIPE_SCREEN
import com.recipebuddy.components.ScreenManager.LOG_OUT
import com.recipebuddy.components.ScreenManager.PANTRY_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.PROFILE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_COOKING_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.displayedRecipesState
import com.recipebuddy.components.ScreenManager.editedRecipe
import com.recipebuddy.components.ScreenManager.ingredientsState
import com.recipebuddy.components.ScreenManager.lastRecipePageScreen
import com.recipebuddy.components.ScreenManager.originalRecipesState
import com.recipebuddy.components.ScreenManager.searchTagsState
import com.recipebuddy.components.ScreenManager.selectedRecipeIndex
import com.recipebuddy.components.ScreenManager.selectedHomeScreen
import com.recipebuddy.components.ScreenManager.toolsState
import com.recipebuddy.database.Ingredient_List
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.*

object ScreenManager {
    val originalRecipesState = mutableStateOf(listOf<Recipe>())
    val displayedRecipesState = mutableStateOf(listOf<Recipe>())
    val searchTagsState = mutableStateOf(listOf<RecipeTag>())
    val ingredientsState = mutableStateOf(listOf<Ingredient_List>())
    val toolsState = mutableStateOf(listOf<String>())

    var selectedHomeScreen by mutableStateOf(0)
    var lastRecipePageScreen by mutableStateOf(0)
    var selectedRecipeIndex by mutableStateOf(-1)

    const val RECIPE_HOME_SCREEN = 0
    const val PANTRY_HOME_SCREEN = 1
    const val PROFILE_HOME_SCREEN = 2

    const val RECIPE_COOKING_SCREEN = 3
    const val CREATE_RECIPE_SCREEN = 4
    const val EDIT_PROFILE_SCREEN = 5
    const val EDIT_RECIPE_SCREEN = 6
    const val LOG_OUT = 7

    var editedRecipe by mutableStateOf<Recipe?>(null)

    fun openEditRecipeScreen(recipe: Recipe) {
        editedRecipe = recipe
        selectedHomeScreen = EDIT_RECIPE_SCREEN
    }

    fun getOriginalRecipe() = originalRecipesState.value
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun HomeScreen() {
    var executedFetch by remember { mutableStateOf(false) }

    if (!executedFetch) {
        fetchTools(toolsState)
        fetchIngredients(ingredientsState)
        fetchSearchTags(searchTagsState)
        fetchFormattedRecipes(originalRecipesState, displayedRecipesState)
        executedFetch = true
    }

//    Thread(
//        Runnable {
//            while (true) {
//                Thread.sleep(5000)
//                Log.d("Debugging", "\n\n\n======>\n")
//                displayedRecipesState.value.forEach {
//                    Log.d("Debugging", it.name)
//                }
//                Log.d("Debugging", "\n======>\n\n\n")
//            }
//        }
//    ).start()

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.BACKGROUND_PRIMARY)
        ) {
            if (selectedHomeScreen != CREATE_RECIPE_SCREEN && selectedHomeScreen != EDIT_RECIPE_SCREEN) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColor.BUTTON_OUTLINE),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    HomeScreenTab(
                        onClick = {
                            if (lastRecipePageScreen == RECIPE_COOKING_SCREEN && selectedHomeScreen == RECIPE_COOKING_SCREEN) {
                                selectedHomeScreen = RECIPE_HOME_SCREEN
                                lastRecipePageScreen = RECIPE_HOME_SCREEN
                            } else if (lastRecipePageScreen == RECIPE_COOKING_SCREEN) {
                                selectedHomeScreen = RECIPE_COOKING_SCREEN
                            } else {
                                selectedHomeScreen = RECIPE_HOME_SCREEN
                            }

                        },
                        modifier = Modifier.weight(1f),
                        selected = (selectedHomeScreen == RECIPE_HOME_SCREEN || selectedHomeScreen == RECIPE_COOKING_SCREEN || selectedHomeScreen == CREATE_RECIPE_SCREEN)
                    ) {
                        Text(text = "Recipes", fontSize = 20.sp)
                    }

                    HomeScreenTab(
                        onClick = { selectedHomeScreen = PANTRY_HOME_SCREEN },
                        modifier = Modifier.weight(1f),
                        selected = selectedHomeScreen == PANTRY_HOME_SCREEN
                    ) {
                        Text(text = "Pantry", fontSize = 20.sp)
                    }

                    HomeScreenTab(
                        onClick = { selectedHomeScreen = PROFILE_HOME_SCREEN },
                        selected = (selectedHomeScreen == PROFILE_HOME_SCREEN || selectedHomeScreen == EDIT_PROFILE_SCREEN)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .width(40.dp)
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.blank_profile_image),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }

            when (selectedHomeScreen) {
                RECIPE_HOME_SCREEN -> {
                    when (lastRecipePageScreen) {
                        RECIPE_HOME_SCREEN -> {
                            lastRecipePageScreen = RECIPE_HOME_SCREEN
                            RecipeHomeScreen()
                        }
                        RECIPE_COOKING_SCREEN -> {
                            if (selectedRecipeIndex < 0) {
                                selectedHomeScreen = RECIPE_HOME_SCREEN
                                RecipeHomeScreen()
                            } else {
                                lastRecipePageScreen = RECIPE_COOKING_SCREEN
                                RecipeCookingScreen(recipe = displayedRecipesState.value[selectedRecipeIndex])
                            }
                        }

                    }
                }
                PANTRY_HOME_SCREEN -> PantryHomeScreen(ingredientsState, toolsState)
                PROFILE_HOME_SCREEN, EDIT_PROFILE_SCREEN -> ProfileHomeScreen()
                RECIPE_COOKING_SCREEN -> {
                    if (selectedRecipeIndex < 0) {
                        selectedHomeScreen = RECIPE_HOME_SCREEN
                        RecipeHomeScreen()
                    } else {
                        lastRecipePageScreen = RECIPE_COOKING_SCREEN
                        RecipeCookingScreen(recipe = displayedRecipesState.value[selectedRecipeIndex])
                    }
                }
                CREATE_RECIPE_SCREEN -> CreateEditRecipeScreen(ingredientsState, toolsState, searchTagsState) { recipe ->
                    displayedRecipesState.value = listOf(recipe) + displayedRecipesState.value
                    originalRecipesState.value = listOf(recipe) + originalRecipesState.value
                    persistEditedRecipe(recipe, recipe, displayedRecipesState, originalRecipesState)
                    selectedHomeScreen = RECIPE_HOME_SCREEN
                }
                EDIT_RECIPE_SCREEN -> CreateEditRecipeScreen(ingredientsState, toolsState, searchTagsState, editedRecipe?.clone()) { recipe ->
                    displayedRecipesState.value = listOf(recipe) + displayedRecipesState.value
                    originalRecipesState.value = listOf(recipe) + originalRecipesState.value
                    persistEditedRecipe(recipe, editedRecipe ?: recipe, displayedRecipesState, originalRecipesState)
                    editedRecipe = null
                    selectedHomeScreen = RECIPE_HOME_SCREEN
                }
                LOG_OUT -> Logout()

            }
        }
    }
}

@Composable
fun HomeScreenTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) =
    Button(
        onClick = onClick,
        modifier = if (selected) {
            modifier
                .height(60.dp)
        } else {
            modifier
                .height(60.dp)
                .padding(bottom = 2.dp)
        },
        content = content,
        colors = ButtonDefaults.buttonColors(
            if (selected) {
                AppColor.BACKGROUND_PRIMARY
            } else {
                AppColor.BACKGROUND_SECONDARY
            }
        ),
        shape = RectangleShape
    )


