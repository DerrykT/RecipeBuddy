package com.recipebuddy.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.R
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.Instruction
import com.recipebuddy.util.Recipe
import com.recipebuddy.util.Timer.isPaused
import com.recipebuddy.util.Timer.pauseTimer
import com.recipebuddy.util.Timer.recipeName
import com.recipebuddy.util.Timer.resumeTimer
import com.recipebuddy.util.Timer.startTimer
import com.recipebuddy.util.Timer.timeLeft
import com.recipebuddy.util.Timer.totalTime
import com.recipebuddy.util.minuteToString
import com.recipebuddy.util.secondsToString

@Composable
fun RecipeCookingScreen(recipe: Recipe) {
    val refreshState = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        if (refreshState.value < 10 && recipeName.value == recipe.name) {
            Progressbar(
                percent = timeLeft.value.toFloat() / totalTime.toFloat(),
                text = com.recipebuddy.util.Timer.timeStringState.value
            )
        }

        // Images
        Box(
            modifier = Modifier
                .border(2.5.dp, Color.Black)
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Image(
                bitmap = recipe.imageBitmap.asImageBitmap(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Time Image

            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.time_icon),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(text = minuteToString(recipe.time), fontSize = 15.sp)
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
                            Text(
                                text = "${ingredient.Quantity} ${ingredient.Unit} ${ingredient.IngredientName}",
                                fontSize = 18.sp
                            )
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            recipe.instructions.forEachIndexed { index, instruction ->
                item {
                    InstructionItem(
                        instruction = instruction,
                        number = index,
                        recipeName = recipe.name,
                        refreshState = refreshState
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun InstructionItem(
    recipeName: String,
    instruction: Instruction,
    number: Int,
    refreshState: MutableState<Int>
) {
    Row() {
        Text(
            text = "$number. ${instruction.text}",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        if (instruction.time != null) {
            Timer(instruction.time, recipeName, number, refreshState)
        }
    }
}

@Composable
private fun Timer(
    startTime: Int,
    name: String,
    instructionNumber: Int,
    refreshState: MutableState<Int>
) {
    var textName by remember {
        mutableStateOf(
            when (name) {
                recipeName.value -> if (isPaused) "Resume" else "Pause"
                else -> "Start"
            }
        )
    }

    val currentTimeString = remember {
        mutableStateOf(
            when (name) {
                recipeName.value -> secondsToString(timeLeft.value)
                else -> minuteToString(startTime)
            }
        )
    }

    if (name == recipeName.value) {
        com.recipebuddy.util.Timer.refreshState = refreshState
        com.recipebuddy.util.Timer.timeStringState = currentTimeString
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(5.dp))
            .background(AppColor.BACKGROUND_SECONDARY)
            .border(2.dp, AppColor.BUTTON_OUTLINE)
            .padding(4.dp)

    ) {
        Text(text = currentTimeString.value, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Green)
                .border(1.dp, Color.Black)
                .padding(top = 3.dp, bottom = 3.dp, start = 5.dp, end = 5.dp)
                .clickable {
                    if (textName == "Start") {
                        textName = "Pause"
                        startTimer(
                            startTime * 60,
                            name,
                            instructionNumber,
                            currentTimeString,
                            refreshState
                        )
                    } else if (textName == "Resume") {
                        textName = "Pause"
                        resumeTimer()
                    } else {
                        textName = "Resume"
                        pauseTimer()
                    }
                }
        ) {
            Text(text = textName, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

    }
}

@Composable
fun Progressbar(
    percent: Float,
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .border(2.dp, Color.Black, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
           Box(modifier = Modifier.fillMaxWidth(if(percent < 0 || percent > 1) 1f else (1f - percent)).fillMaxHeight().clip(RoundedCornerShape(10.dp)).background(AppColor.TAG_BUTTON_GREEN))
        }

        Text(
            text = text, fontSize = 15.sp, fontWeight = FontWeight.Light, color = Color.Black
        )
    }
}
