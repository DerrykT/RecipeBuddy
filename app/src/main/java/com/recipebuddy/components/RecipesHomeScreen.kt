package com.recipebuddy.components

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.R
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.Recipe
import com.recipebuddy.util.TempDataObject

@Composable
fun RecipeHomeScreen() {
    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Initial top spacing
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            SearchBar()

            // Tags Row
            TagsRow()

            // Rating Bar
            RatingSelect(rating = 1)

            Spacer(modifier = Modifier.height(5.dp))

            // Recipe List
            RecipeScrollable(recipes = TempDataObject.recipes)
        }

//        Box(
//            contentAlignment = Alignment.BottomEnd,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(13.dp)
//        ) {
//            Button(
//                colors = ButtonDefaults.buttonColors(Color.White),
//                shape = CircleShape,
//                modifier = Modifier
//                    .border(3.dp, Color.Black, shape = CircleShape)
//                    .width(50.dp)
//                    .height(50.dp),
//                onClick = {
//                    ScreenManager.selectedScreen = ScreenManager.CREATE_RECIPE_SCREEN
//                }
//            ) {
//                Image(painter = painterResource(id = R.drawable.create_icon), contentDescription = "", modifier = Modifier.width(40.dp).height(40.dp))
//            }
//        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        singleLine = true,
        onValueChange = { newText ->
            searchText = newText
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Color.DarkGray,
        ),
        textStyle = TextStyle(color = Color.Black, fontSize = 22.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp)
            .border(3.dp, Color.DarkGray)
    )
}

@Composable
fun TagsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TempDataObject.tags.forEach { tagName ->
                item {
                    TagButton(text = tagName) {}
                }
            }
        }

        // Add Tags Button
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(Color.DarkGray),
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "",
                tint = Color.LightGray,
                modifier = Modifier
                    .size(25.dp)
            )
        }
    }
}

@Composable
fun TagButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(AppColor.TAG_BUTTON_GREEN),
        shape = RoundedCornerShape(50.dp)
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "",
            tint = Color.DarkGray,
            modifier = Modifier
                .size(17.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = text, fontSize = 12.sp, color = Color.White)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RatingSelect(
    modifier: Modifier = Modifier,
    rating: Int
) {
    var ratingState by remember {
        mutableStateOf(rating)
    }

    var selected by remember {
        mutableStateOf(false)
    }
    val size by animateDpAsState(
        targetValue = if (selected) 50.dp else 45.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.star_icon),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                ratingState = i
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xfffad428) else Color(0xFF757574)
            )
        }
    }
}

@Composable
fun RecipeScrollable(recipes: List<Recipe>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        recipes.forEachIndexed { index, recipe ->
            item {
                RecipeScrollableItem(recipe = recipe, index = index)
            }
        }
    }
}

@Composable
fun RecipeScrollableItem(recipe: Recipe, index: Int) {
    Column(
        modifier = Modifier
            .border(
                width = if (ScreenManager.selectedRecipeIndex == index) {
                    4.dp
                } else {
                    0.dp
                },
                color = AppColor.BACKGROUND_SECONDARY,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
            .clickable {
                ScreenManager.selectedRecipeIndex =
                    if (ScreenManager.selectedRecipeIndex == index) {
                        -1
                    } else {
                        index
                    }
            },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .border(2.5.dp, Color.Black)
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Image(
                painter = painterResource(id = recipe.imageRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = recipe.name, fontSize = 20.sp, modifier = Modifier.weight(1f))

            RatingBar(recipe.rating)
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
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                recipe.tags.forEach { tagName ->
                    item {
                        RecipeDetailsTag(tagName = tagName)
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.time_icon),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(3.dp))

            Text(text = recipe.time, fontSize = 15.sp)
        }

        if (ScreenManager.selectedRecipeIndex == index) {
            ExpandedRecipeDetailsView(recipe)
        }
    }
}

@Composable
fun RecipeDetailsTag(tagName: String, fontSize: TextUnit = 12.sp, padding: Dp = 5.dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(AppColor.TAG_ICON_LIGHT_ORANGE)
    ) {
        Text(text = tagName, fontSize = fontSize, modifier = Modifier.padding(padding))
    }
}

@Composable
fun RatingBar(rating: Int, modifier: Modifier = Modifier, bubbleSize: Dp = 12.dp) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = modifier
    ) {
        for (i in 0..5) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .width(bubbleSize)
                    .height(bubbleSize)
                    .background(
                        if (rating >= i) {
                            Color.Green
                        } else {
                            Color.Transparent
                        }
                    )
                    .border(2.dp, Color.Black)
            )
        }
    }
}

@Composable
fun ExpandedRecipeDetailsView(recipe: Recipe) {
    Column {
        Row {
            Text(
                text = "Ingredients",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Text(
                text = "Tools",
                fontSize = 16.sp,
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
                    .heightIn(0.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                recipe.ingredients.forEach { ingredient ->
                    item {
                        Text(text = "${ingredient.weight} ${ingredient.name}", fontSize = 14.sp)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .heightIn(0.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                recipe.tools.forEach { toolName ->
                    item {
                        Text(text = toolName, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Green),
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    ScreenManager.selectedScreen =
                        ScreenManager.RECIPE_COOKING_SCREEN
                }
            ) {
                Text(text = "Cook", fontSize = 20.sp, color = Color.Black)
            }
        }


    }
}
