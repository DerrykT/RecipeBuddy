package com.recipebuddy.components

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
import com.recipebuddy.components.ScreenManager.PANTRY_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.PROFILE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_COOKING_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.lastRecipePageScreen
import com.recipebuddy.components.ScreenManager.selectedRecipeIndex
import com.recipebuddy.components.ScreenManager.selectedHomeScreen
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.Recipe
import com.recipebuddy.util.RecipeTag
import com.recipebuddy.util.fetchFormattedRecipes
import com.recipebuddy.util.fetchSearchTags

object ScreenManager {
    var selectedHomeScreen by mutableStateOf(0)
    var lastRecipePageScreen by mutableStateOf(0)
    var selectedRecipeIndex by mutableStateOf(-1)

    const val RECIPE_HOME_SCREEN = 0
    const val PANTRY_HOME_SCREEN = 1
    const val PROFILE_HOME_SCREEN = 2

    const val RECIPE_COOKING_SCREEN = 3
    const val CREATE_RECIPE_SCREEN = 4
    const val EDIT_PROFILE_SCREEN = 5
    const val EDIT_PANTRY_SCREEN = 6
}

@Composable
fun HomeScreen() {
    val originalRecipesState = remember { mutableStateOf(listOf<Recipe>()) }
    val displayedRecipesState = remember { mutableStateOf(listOf<Recipe>()) }
    val searchTagsState = remember { mutableStateOf(listOf<RecipeTag>()) }

    var executedFetch by remember { mutableStateOf(false) }

    if(!executedFetch) {
        fetchSearchTags(searchTagsState)
        fetchFormattedRecipes(originalRecipesState, displayedRecipesState)
        executedFetch = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.BACKGROUND_PRIMARY)
    ) {

        if (selectedHomeScreen != CREATE_RECIPE_SCREEN) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.BUTTON_OUTLINE),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                HomeScreenTab(
                    onClick = {
                        if(lastRecipePageScreen == RECIPE_COOKING_SCREEN && selectedHomeScreen == RECIPE_COOKING_SCREEN) {
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
                        RecipeHomeScreen(displayedRecipesState, originalRecipesState, searchTagsState)
                    }
                    RECIPE_COOKING_SCREEN -> {
                        if (selectedRecipeIndex < 0) {
                            selectedHomeScreen = RECIPE_HOME_SCREEN
                            RecipeHomeScreen(displayedRecipesState, originalRecipesState, searchTagsState)
                        } else {
                            lastRecipePageScreen = RECIPE_COOKING_SCREEN
                            RecipeCookingScreen(recipe = displayedRecipesState.value[selectedRecipeIndex])
                        }
                    }

                }
            }
            PANTRY_HOME_SCREEN -> PantryHomeScreen()
            PROFILE_HOME_SCREEN, EDIT_PROFILE_SCREEN -> ProfileHomeScreen()
            RECIPE_COOKING_SCREEN -> {
                if (selectedRecipeIndex < 0) {
                    selectedHomeScreen = RECIPE_HOME_SCREEN
                    RecipeHomeScreen(displayedRecipesState, originalRecipesState, searchTagsState)
                } else {
                    lastRecipePageScreen = RECIPE_COOKING_SCREEN
                    RecipeCookingScreen(recipe = displayedRecipesState.value[selectedRecipeIndex])
                }
            }
            CREATE_RECIPE_SCREEN -> CreateRecipeScreen() { recipe ->
                displayedRecipesState.value = listOf(recipe) + displayedRecipesState.value
                originalRecipesState.value = listOf(recipe) + originalRecipesState.value
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


