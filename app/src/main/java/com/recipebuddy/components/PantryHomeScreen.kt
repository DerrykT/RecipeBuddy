package com.recipebuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.database.Ingredient_List
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.fetchIngredients
import com.recipebuddy.util.fetchTools
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PantryHomeScreen() {
    val ingredients = remember { mutableStateOf(listOf<Ingredient_List>()) }
    val tools = remember { mutableStateOf(listOf<String>()) }

    fetchTools(tools)
    fetchIngredients(ingredients)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp)
    ) {
        IngredientsBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(AppColor.BACKGROUND_SECONDARY)
                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
            ingredients
        )

        ToolsBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(AppColor.BACKGROUND_SECONDARY)
                .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
            tools
        )

        AddBox() {

        }
    }
}

@Composable
private fun AddBox(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(AppColor.BACKGROUND_SECONDARY)
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(AppColor.BUTTON_OUTLINE)
        ) {
            Image(
                painterResource(id = com.recipebuddy.R.drawable.add_icon),
                contentDescription = ""
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Text(text = "Add Ingredients or Tools", fontSize = 22.sp)
    }
}

@Composable
private fun IngredientsBox(modifier: Modifier = Modifier, ingredients: MutableState<List<Ingredient_List>>) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ingredients.value.forEach { ingredient ->
            item {
                Row {
                    Text(text = ingredient.IngredientName, fontSize = 25.sp, modifier = Modifier.weight(1f))

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(text = "${ingredient.Quantity} ${ingredient.Unit}", fontSize = 25.sp)
                }
            }
        }
    }
}

@Composable
private fun ToolsBox(modifier: Modifier = Modifier, tools: MutableState<List<String>>) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tools.value.forEach { toolName ->
            item {
                Text(text = toolName, fontSize = 25.sp)
            }
        }
    }
}