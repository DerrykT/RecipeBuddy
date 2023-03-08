package com.recipebuddy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeHomeScreen() {
    Column {
        // Search Bar
        SearchBar()

        // Tags Row
        TagsRow()

        // Recipe List
        RecipeScrollable(recipes = listOf())
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .padding(start = 60.dp, end = 60.dp, top = 30.dp, bottom = 30.dp)
            .border(5.dp, Color.Black)
            .fillMaxWidth()
    ) {
        Text(text = "Search Recipe", fontSize = 12.sp)
        Spacer(modifier = Modifier.fillMaxWidth())
        Text(text = "Search", fontSize = 12.sp)
    }
}

@Composable
fun TagsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 40.dp, end = 40.dp),
        horizontalArrangement = Arrangement.End
    ) {
        // Add Tags Button
        Button(
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp, top = 3.dp, bottom = 3.dp)
                .background(Color.DarkGray),
            onClick = {}
        ) {
            Text(text = "Add Tags", fontSize = 12.sp, color = Color.White)

            // IMAGE
        }

        Tag(text = "Gluten Free") {}
        Tag(text = "Breakfast") {}
        Tag(text = "Pork") {}

    }
}

@Composable
fun Tag(
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 6.dp, end = 6.dp, top = 3.dp, bottom = 3.dp),
        onClick = onClick
    ) {
        Text(text = text, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun RecipeScrollable(recipes: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 15.dp, end = 15.dp, top = 30.dp)
    ) {
        recipes.forEach { recipe ->
            item {

            }
        }
    }
}

@Composable
fun RecipeScrollableItem(recipe: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable { expanded = !expanded }
    ) {
        Box(
            modifier = Modifier
                .border(4.dp, Color.Black)
        ) {
            //Image
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = recipe, fontSize = 18.sp)

            Spacer(modifier = Modifier.fillMaxWidth())

            RatingBar(0)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp)
            ) {
                // RECIPE TAGS
            }

            // IMAGE

            Text(text = "30 Minutes", fontSize = 12.sp)
        }

        if (expanded) {
            ExpandedRecipeDetailsView(recipe)
        }
    }
}

@Composable
fun RatingBar(rating: Int) {

}

@Composable
fun ExpandedRecipeDetailsView(recipe: String) {
    Column {
        Row {
            Column {
                Text(text = "Ingredients", fontSize = 12.sp)

                LazyColumn() {

                }
            }

            Column {
                Text(text = "Tools", fontSize = 12.sp)

                LazyColumn() {

                }
            }
        }

        Button(
            onClick = {},
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {

        }
    }
}