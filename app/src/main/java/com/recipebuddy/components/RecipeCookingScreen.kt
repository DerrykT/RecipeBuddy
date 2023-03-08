package com.recipebuddy.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeCookingScreen(recipe: String) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        // Images
        LazyRow(
            modifier = Modifier
                .border(4.dp, Color.Black)
        ) {
            // add images
        }

        Row() {
            // Time Image

            Text(text = "15 Minutes", fontSize = 12.sp)

            RatingBar(rating = 0)
        }

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

        LazyRow() {

        }
    }
}