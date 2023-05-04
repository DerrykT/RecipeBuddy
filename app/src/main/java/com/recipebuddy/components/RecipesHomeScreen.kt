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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.R
import com.recipebuddy.database.Tag_List
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.*
import kotlinx.coroutines.Job

@Composable
fun RecipeHomeScreen(recipes: MutableState<List<Recipe>>) {
    var isCreatingTag by remember { mutableStateOf(false) }
    val searchTagsState = remember { mutableStateOf(listOf<Tag_List>()) }

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Initial top spacing
            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            SearchBar(recipes)

            // Tags Row
            TagsRow(searchTagsState) {
                isCreatingTag = true
            }

            RatingSelect(rating = 1, recipes = recipes)

            Spacer(modifier = Modifier.height(5.dp))

            // Recipe List
            RecipeScrollable(recipes)
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = CircleShape,
                modifier = Modifier
                    .border(3.dp, Color.Black, shape = CircleShape)
                    .width(50.dp)
                    .height(50.dp),
                onClick = {
                    ScreenManager.selectedHomeScreen = ScreenManager.CREATE_RECIPE_SCREEN
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.create_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )
            }
        }

        if (isCreatingTag) {
            AddTagAlertDialog(
                onConfirm = { tag ->
                    persistTag(Tag_List(tag), searchTagsState)
                    isCreatingTag = !isCreatingTag
                },
                onCancel = { isCreatingTag = !isCreatingTag })
        }

    }
}

@Composable
fun SearchBar(recipes: MutableState<List<Recipe>>) {
    var searchText by remember { mutableStateOf("") }
    var runningSearch by remember { mutableStateOf<Job?>(null) }

    TextField(
        value = searchText,
        singleLine = true,
        onValueChange = { newText ->
            searchText = newText
            runningSearch?.cancel()
            runningSearch = sortByName(searchText, recipes)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RatingSelect(modifier: Modifier = Modifier, rating: Int?, recipes: MutableState<List<Recipe>>) {
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
                                //recipes.value = sortByRating(ratingState)
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState!!) Color(0xfffad428) else Color(0xFF757574)
            )
        }
    }
}


@Composable
fun TagsRow(searchTags: MutableState<List<Tag_List>>, onClick: () -> Unit) {
    fetchSearchTags(searchTags)

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
            searchTags.value.forEach { tag ->
                item {
                    TagButton(text = tag.Tag) {}
                }
            }
        }

        // Add Tags Button
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(Color.DarkGray)
        ) {
            Text(text = "Add Tags", fontSize = 12.sp, color = Color.White)
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
        Text(text = text, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun RecipeScrollable(recipes: MutableState<List<Recipe>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        recipes.value.forEachIndexed { index, recipe ->
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
                bitmap = recipe.imageBitmap.asImageBitmap(),
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

            Text(text = minuteToString(recipe.time), fontSize = 15.sp)
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
                        Text(
                            text = "${ingredient.Quantity} ${ingredient.Unit} ${ingredient.IngredientName}",
                            fontSize = 14.sp
                        )
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
                    ScreenManager.selectedHomeScreen =
                        ScreenManager.RECIPE_COOKING_SCREEN
                }
            ) {
                Text(text = "Cook", fontSize = 20.sp, color = Color.Black)
            }
        }


    }
}

@Composable
private fun AddTagAlertDialog(
    onConfirm: (text: String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var requireText by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.6f))
            .clickable {}
    ) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .height(180.dp)
                .padding(15.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.LightGray)
                .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            BasicTextField(
                value = text,
                onValueChange = {
                    if (it.isNotEmpty() && requireText) requireText = false
                    text = it
                },
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(15.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 2.dp,
                        if (requireText) Color.Red else AppColor.BUTTON_OUTLINE,
                        RoundedCornerShape(20.dp)
                    ),
                enabled = true,
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColor.BACKGROUND_SECONDARY)
                            .padding(all = 10.dp),
                    ) {
                        if (text == "") {
                            Text(
                                text = "Name...",
                                color = AppColor.BACKGROUND_PRIMARY,
                                fontSize = 20.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(33.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    ),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = onCancel
                ) {
                    Text(
                        text =
                        "Cancel",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(33.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color.Green
                    ),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        if (text.isNotEmpty()) {
                            onConfirm(text)
                        } else {
                            requireText = true
                        }
                    }
                ) {
                    Text(
                        text = "Confirm",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}