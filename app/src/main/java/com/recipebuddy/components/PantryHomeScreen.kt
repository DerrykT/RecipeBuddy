package com.recipebuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.recipebuddy.R
import com.recipebuddy.database.Ingredient_List
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.*

@Composable
fun PantryHomeScreen(
    ingredientsState: MutableState<List<Ingredient_List>>,
    toolsState: MutableState<List<String>>
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box() {
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
                ingredientsState
            )

            ToolsBox(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(AppColor.BACKGROUND_SECONDARY)
                    .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                toolsState
            )

            AddBox() {
                showAddDialog = true
            }
        }

        if (showAddDialog) {
            IngredientToolAlertDialog(
                onConfirm = { text, type ->
                    if (type == "Tool") {
                        persistTool(text, toolsState)
                    } else {
                        persistIngredient(Ingredient_List(text, 0.0, ""), ingredientsState)
                    }
                    showAddDialog = !showAddDialog
                }
            ) { showAddDialog = !showAddDialog }
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
            .clickable {
                onClick()
            }
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
private fun IngredientsBox(
    modifier: Modifier = Modifier,
    ingredients: MutableState<List<Ingredient_List>>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ingredients.value.forEach { ingredient ->
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = ingredient.IngredientName, fontSize = 25.sp)
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "",
                            modifier = Modifier
                                .clickable {
                                    removeIngredient(ingredient.IngredientName)
                                }
                        )
                    }
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
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = toolName, fontSize = 25.sp)
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "",
                            modifier = Modifier
                                .clickable {
                                    ScreenManager.toolsState.value = ScreenManager.toolsState.value.filter { it != toolName }

                                    removeTool(toolName)
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IngredientToolAlertDialog(
    onConfirm: (text: String, type: String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var types = listOf("Tool", "Ingredient")
    var selectedIndex by remember { mutableStateOf(0) }
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
                .height(280.dp)
                .padding(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.LightGray)
                .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            LargeDropdownMenu(
                label = "Type",
                items = types,
                selectedIndex = selectedIndex,
                onItemSelected = { index, _ -> selectedIndex = index },
            )

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
                        backgroundColor =
                        Color.Red

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
                            onConfirm(
                                text,
                                types[selectedIndex]
                            )
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

@Composable
fun <T> LargeDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
        )
    },
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text(label) },
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val icon = Icons.Filled.ArrowDropDown
                Icon(icon, "")
            },
            onValueChange = { },
            readOnly = true,
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(enabled = enabled) { expanded = true },
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
            ) {
                val listState = rememberLazyListState()
                if (selectedIndex > -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = selectedIndex)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                    if (notSetLabel != null) {
                        item {
                            LargeDropdownMenuItem(
                                text = notSetLabel,
                                selected = false,
                                enabled = false,
                                onClick = { },
                            )
                        }
                    }
                    itemsIndexed(items) { index, item ->
                        val selectedItem = index == selectedIndex
                        drawItem(
                            item,
                            selectedItem,
                            true
                        ) {
                            onItemSelected(index, item)
                            expanded = false
                        }

                        if (index < items.lastIndex) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val contentColor = if (selected) Color.Black else Color.DarkGray

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(modifier = Modifier
            .clickable(enabled) { onClick() }
            .fillMaxWidth()
            .padding(16.dp)) {
            Text(
                text = text,
            )
        }
    }
}

