package com.recipebuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.Instruction
import com.recipebuddy.util.Recipe

@Composable
fun RecipeCookingScreen(recipe: Recipe) {
    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        // Images
        Box(
            modifier = Modifier
                .border(2.5.dp, Color.Black)
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = recipe.imageRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Time Image

            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = com.recipebuddy.R.drawable.time_icon),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(text = recipe.time, fontSize = 15.sp)
            }

            RatingBar(rating = recipe.rating, bubbleSize = 15.dp)
        }

        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .background(AppColor.BACKGROUND_SECONDARY)
                .border(2.dp, AppColor.BUTTON_OUTLINE)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row {
                Text(
                    text = "Ingredients",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Text(
                    text = "Tools",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

            }

            Row {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .heightIn(0.dp, 150.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    recipe.ingredients.forEach { ingredient ->
                        item {
                            Text(text = "${ingredient.weight} ${ingredient.name}", fontSize = 18.sp)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .heightIn(0.dp, 150.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    recipe.tools.forEach { toolName ->
                        item {
                            Text(text = toolName, fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            recipe.tags.forEach { tagName ->
                item {
                    RecipeDetailsTag(tagName = tagName, fontSize = 15.sp, padding = 8.dp)
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            recipe.instructions.forEachIndexed { index, instruction ->
                item {
                    InstructionItem(instruction = instruction, number = index)
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun InstructionItem(instruction: Instruction, number: Int) {
    Row() {
        Text(text = "$number. ${instruction.text}", fontSize = 18.sp, modifier = Modifier.weight(1f))

        if(instruction.timer != null) {
            Timer(instruction.timer)
        }
    }
}

@Composable
private fun Timer(time: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .background(AppColor.BACKGROUND_SECONDARY)
            .border(2.dp, AppColor.BUTTON_OUTLINE)
            .padding(4.dp)

    ) {
        Text(text = time, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Green)
                .border(1.dp, Color.Black)
                .padding(top = 3.dp, bottom = 3.dp, start = 5.dp, end = 5.dp)
        ) {
            Text(text = "Start", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

    }
}