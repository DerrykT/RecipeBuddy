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
import com.recipebuddy.components.ScreenManager.LOGIN_SCREEN
import com.recipebuddy.components.ScreenManager.PANTRY_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.PROFILE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_COOKING_SCREEN
import com.recipebuddy.components.ScreenManager.RECIPE_HOME_SCREEN
import com.recipebuddy.components.ScreenManager.selectedRecipeIndex
import com.recipebuddy.components.ScreenManager.selectedScreen
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.TempDataObject

object ScreenManager {
    var selectedScreen by mutableStateOf(0)
    var selectedRecipeIndex by mutableStateOf(-1)

    const val RECIPE_HOME_SCREEN = 0
    const val PANTRY_HOME_SCREEN = 1
    const val PROFILE_HOME_SCREEN = 2

    const val RECIPE_COOKING_SCREEN = 3
    const val CREATE_RECIPE_SCREEN = 4
    const val EDIT_PROFILE_SCREEN = 5
    const val EDIT_PANTRY_SCREEN = 6

    const val LOGIN_SCREEN = 7
}

fun SetSelectedScreen() {
    selectedScreen = 0
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.BACKGROUND_PRIMARY)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.BUTTON_OUTLINE),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            HomeScreenTab(
                onClick = { selectedScreen = RECIPE_HOME_SCREEN },
                modifier = Modifier.weight(1f),
                selected = (selectedScreen == RECIPE_HOME_SCREEN || selectedScreen == RECIPE_COOKING_SCREEN || selectedScreen == CREATE_RECIPE_SCREEN)
            ) {
                Text(text = "Recipes", fontSize = 20.sp)
            }

            HomeScreenTab(
                onClick = { selectedScreen = PANTRY_HOME_SCREEN },
                modifier = Modifier.weight(1f),
                selected = selectedScreen == PANTRY_HOME_SCREEN
            ) {
                Text(text = "Pantry", fontSize = 20.sp)
            }

            HomeScreenTab(
                onClick = { selectedScreen = PROFILE_HOME_SCREEN },
                selected = (selectedScreen == PROFILE_HOME_SCREEN || selectedScreen == EDIT_PROFILE_SCREEN)
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

        when (selectedScreen) {
            RECIPE_HOME_SCREEN -> RecipeHomeScreen()
            PANTRY_HOME_SCREEN -> PantryHomeScreen()
            PROFILE_HOME_SCREEN, EDIT_PROFILE_SCREEN -> ProfileHomeScreen()
           // if (selectedScreen === LOGIN_SCREEN) {
             //   selectedScreen = RECIPE_HOME_SCREEN
               // Logout()
            //}
            LOGIN_SCREEN -> Logout()
            RECIPE_COOKING_SCREEN -> {
                if(selectedRecipeIndex < 0) {
                    selectedScreen = RECIPE_HOME_SCREEN
                    RecipeHomeScreen()
                } else {
                    RecipeCookingScreen(recipe = TempDataObject.recipes[selectedRecipeIndex])
                }
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
            if(selected) {
                AppColor.BACKGROUND_PRIMARY
            } else {
                AppColor.BACKGROUND_SECONDARY
            }
        ),
        shape = RectangleShape
    )


