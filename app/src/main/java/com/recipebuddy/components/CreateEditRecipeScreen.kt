package com.recipebuddy.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.recipebuddy.R
import com.recipebuddy.ui.resources.AppColor
import kotlin.math.roundToInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import com.recipebuddy.DEFAULT_BITMAP
import com.recipebuddy.database.Ingredient_List
import com.recipebuddy.database.RecipeIngredientList
import com.recipebuddy.database.Tag_List
import com.recipebuddy.database.toMutableStringToDoubleMap
import com.recipebuddy.util.*
import java.util.*

private val MAIN_INPUT_SCREEN_SIZE = 380.dp

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun CreateEditRecipeScreen(
    ingredientsState: MutableState<List<Ingredient_List>>,
    toolsState: MutableState<List<String>>,
    tagsState: MutableState<List<RecipeTag>>,
    recipe: Recipe? = null,
    onCreate: (recipe: Recipe) -> Unit
) {
    val titleState = remember { mutableStateOf(recipe?.name ?: "") }
    val ratingState = remember { mutableStateOf(recipe?.rating ?: 0) }
    val hourState = remember { mutableStateOf<Int?>(recipe?.time?.mod(60)) }
    val minuteState = remember { mutableStateOf<Int?>(recipe?.time?.div(60)) }
    val bitmapState = remember { mutableStateOf(recipe?.imageBitmap) }
    val selectedToolsState =
        remember { mutableStateOf(recipe?.tools?.toMutableSet() ?: mutableSetOf()) }
    val selectedIngredientsState =
        remember {
            mutableStateOf(
                recipe?.ingredients?.toMutableStringToDoubleMap() ?: mutableMapOf()
            )
        }
    val selectedTagsState = remember { mutableStateOf(mutableSetOf<String>()) }
    val instructionsState = remember { mutableStateOf(recipe?.instructions ?: listOf()) }
    var isCreatingTagState = remember { mutableStateOf(false) }
    var requiredTextColor = remember { mutableStateOf(Color.Black) }
    var requiredTitleOutlineColor = remember { mutableStateOf(Color.Transparent) }
    var requiredTimeOutlineColor = remember { mutableStateOf(Color.Transparent) }
    var requiredRatingOutlineColor = remember { mutableStateOf(Color.Transparent) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedInstructionIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
            InstructionInputSection(
                instructionsState,
                onAddInstruction = { showAddDialog = true },
                onEditInstruction = {
                    editedInstructionIndex = it
                    showEditDialog = true
                },
                requiredTextColor
            )

            ToolAndIngredientInputSection(
                selectedToolsState,
                selectedIngredientsState,
                ingredientsState,
                toolsState,
                recipe != null,
            ) {
                ScreenManager.originalRecipesState.value =
                    ScreenManager.originalRecipesState.value.filter { it.name != recipe?.name }
                ScreenManager.displayedRecipesState.value =
                    ScreenManager.displayedRecipesState.value.filter { it.name != recipe?.name }

                if (recipe != null) removeRecipe(recipe)

                ScreenManager.selectedHomeScreen = ScreenManager.RECIPE_HOME_SCREEN
            }
        }

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            MainInputSection(
                ratingState,
                titleState,
                bitmapState,
                selectedTagsState,
                tagsState,
                hourState,
                minuteState,
                isCreatingTagState,
                requiredTitleOutlineColor,
                requiredTimeOutlineColor,
                requiredRatingOutlineColor
            ) {
                val isValid =
                    (titleState.value.isNotEmpty() && ratingState.value != 0
                            && instructionsState.value.isNotEmpty()
                            && (hourState.value != null || minuteState.value != null))

                if (isValid) {
                    onCreate(
                        Recipe(
                            titleState.value,
                            ratingState.value,
                            ((hourState.value ?: 0) * 60 + (minuteState.value ?: 0)),
                            selectedTagsState.value.toList(),
                            selectedIngredientsState.value.map {
                                var unit = ""

                                ingredientsState.value.forEach { ingredient ->
                                    if (ingredient.IngredientName == it.key) unit = ingredient.Unit
                                }

                                RecipeIngredientList(it.key, it.value, unit)
                            },
                            toolsState.value,
                            instructionsState.value,
                            bitmapState.value ?: DEFAULT_BITMAP
                        )
                    )
                } else {
                    if (titleState.value.isEmpty()) requiredTitleOutlineColor.value = Color.Red
                    else requiredTitleOutlineColor.value = Color.Transparent

                    if (ratingState.value == 0) requiredRatingOutlineColor.value = Color.Red
                    else requiredRatingOutlineColor.value = Color.Transparent

                    if (hourState.value == null && minuteState.value == null) requiredTimeOutlineColor.value =
                        Color.Red
                    else requiredTimeOutlineColor.value = Color.Transparent

                    if (instructionsState.value.isEmpty()) requiredTextColor.value = Color.Red
                    else requiredTextColor.value = Color.Black
                }
            }
        }

        if (showAddDialog) {
            InstructionAlertDialog(
                onConfirm = { title, time ->
                    instructionsState.value =
                        instructionsState.value + listOf(Instruction(title, time))
                    showAddDialog = false
                },
                onCancel = {
                    showAddDialog = false
                }
            )
        } else if (showEditDialog && editedInstructionIndex >= 0 && editedInstructionIndex < instructionsState.value.size) {
            InstructionAlertDialog(
                onConfirm = { title, time ->
                    instructionsState.value =
                        instructionsState.value.subList(0, editedInstructionIndex) + listOf(
                            Instruction(
                                title,
                                time
                            )
                        ) + instructionsState.value.subList(
                            editedInstructionIndex + 1,
                            instructionsState.value.size
                        )
                    showEditDialog = false
                },
                onCancel = {
                    showEditDialog = false
                },
                defaultText = instructionsState.value[editedInstructionIndex].text,
                defaultTime = with(instructionsState.value[editedInstructionIndex].time) {
                    if (this == null) {
                        null
                    } else {
                        minuteToHourMinute(this)
                    }
                }
            )
        } else if (isCreatingTagState.value) {
            AddTagAlertDialog(
                onConfirm = { tag ->
                    persistTag(Tag_List(tag.lowercase(Locale.getDefault())), tagsState)
                    isCreatingTagState.value = !isCreatingTagState.value
                },
                onCancel = {
                    isCreatingTagState.value = !isCreatingTagState.value
                })
        }

    }
}

@Composable
private fun MainInputSection(
    ratingState: MutableState<Int>,
    titleState: MutableState<String>,
    bitmapState: MutableState<Bitmap?>,
    selectedTagsState: MutableState<MutableSet<String>>,
    originalTagsState: MutableState<List<RecipeTag>>,
    hourState: MutableState<Int?>,
    minuteState: MutableState<Int?>,
    isCreatingTag: MutableState<Boolean>,
    requiredTitleOutlineColorState: MutableState<Color>,
    requiredRatingOutlineColorState: MutableState<Color>,
    requiredTimeOutlineColorState: MutableState<Color>,
    onCreate: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                Text(text = "New Post", fontSize = 20.sp)
            }

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.directional_arrow),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            ScreenManager.selectedHomeScreen = ScreenManager.RECIPE_HOME_SCREEN
                        }
                )
            }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .height(33.dp)
                        .width(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        AppColor.BACKGROUND_SECONDARY
                    ),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        onCreate()
                    }
                ) {
                    Text(
                        text = "Post",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Column {
            DashedRow()

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .clickable {
                        launcher.launch("image/*")

                        imageUri?.let {
                            if (Build.VERSION.SDK_INT < 28) {
                                bitmapState.value = MediaStore.Images
                                    .Media.getBitmap(context.contentResolver, it)

                            } else {
                                val source = ImageDecoder
                                    .createSource(context.contentResolver, it)
                                bitmapState.value = ImageDecoder.decodeBitmap(source)
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = ""
                )
            }

            DashedRow()
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                for (i in 1..5) {
                    Star(
                        isChecked = ratingState.value >= i,
                        width = 30.dp,
                        height = 30.dp,
                        borderColor = requiredRatingOutlineColorState.value
                    ) {
                        ratingState.value = i
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                TimeField(
                    hoursState = hourState,
                    minutesState = minuteState,
                    requiredTimeOutlineColorState.value
                )
            }
        }

        Column {
            val maxLength = 30
            val backgroundColor = AppColor.BACKGROUND_SECONDARY
            val textColor = Color.Black
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
                    .border(
                        3.dp,
                        requiredTitleOutlineColorState.value,
                        RoundedCornerShape(8.dp)
                    ),
                value = titleState.value,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    cursorColor = Color.Black,
                    disabledLabelColor = backgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    if (it.length <= maxLength) titleState.value = it
                },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    if (titleState.value.isNotEmpty()) {
                        IconButton(onClick = { titleState.value = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = "Recipe Title...",
                        fontSize = 18.sp,
                        color = AppColor.BACKGROUND_PRIMARY,
                        fontWeight = FontWeight.Bold
                    )
                },
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp)
            )
            Text(
                text = "${titleState.value.length} / $maxLength",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 10.dp),
                textAlign = TextAlign.End,
                color = textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.BACKGROUND_PRIMARY_DARKER)
                .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                originalTagsState.value.forEachIndexed { index, tag ->
                    item {
                        TagButton(tag.text, selectedTagsState.value.contains(tag.text)) {
                            if (selectedTagsState.value.contains(tag.text)) {
                                selectedTagsState.value.remove(tag.text)
                            } else {
                                selectedTagsState.value.add(tag.text)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    isCreatingTag.value = true
                },
                colors = ButtonDefaults.buttonColors(Color.DarkGray)
            ) {
                Text(text = "Add", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun InstructionInputSection(
    instructions: MutableState<List<Instruction>>,
    onAddInstruction: () -> Unit,
    onEditInstruction: (instructionIndex: Int) -> Unit,
    textColorState: MutableState<Color>
) {
    var isEditingInstructions by remember { mutableStateOf(false) }
    var tempInstructions by remember { mutableStateOf(instructions.value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(getScreenHeight() - MAIN_INPUT_SCREEN_SIZE)
            .border(1.dp, AppColor.BACKGROUND_SECONDARY, shape = RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(30.dp))
            .background(AppColor.BACKGROUND_PRIMARY_DARK)
    ) {
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
                    backgroundColor = if (isEditingInstructions) {
                        Color.Red
                    } else {
                        AppColor.BACKGROUND_SECONDARY
                    }
                ),
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    if (isEditingInstructions) {
                        instructions.value = tempInstructions
                        isEditingInstructions = !isEditingInstructions
                    } else {
                        onAddInstruction()
                    }
                }
            ) {
                Text(
                    text = if (isEditingInstructions) {
                        "Cancel"
                    } else {
                        "Add"
                    },
                    color = textColorState.value,
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
                    backgroundColor = if (isEditingInstructions) {
                        Color.Green
                    } else {
                        AppColor.BACKGROUND_SECONDARY
                    }
                ),
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    if (isEditingInstructions) tempInstructions = instructions.value
                    else tempInstructions = instructions.value
                    isEditingInstructions = !isEditingInstructions
                }
            ) {
                Text(
                    text = if (isEditingInstructions) {
                        "Confirm"
                    } else {
                        "Edit"
                    },
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            instructions.value.forEachIndexed { index, instruction ->
                item {
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(AppColor.BUTTON_OUTLINE)
                    )

                    var isExpanded by remember { mutableStateOf(false) }

                    if (isExpanded && !isEditingInstructions) {
                        InstructionExpandedListItem(instruction) {
                            isExpanded = !isExpanded
                        }
                    } else {
                        InstructionPreviewListItem(instruction) {
                            if (isEditingInstructions) {
                                onEditInstruction(index)
                            } else {
                                isExpanded = !isExpanded
                            }
                        }
                    }
                }
            }

            if (instructions.value.isNotEmpty()) item {
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(AppColor.BUTTON_OUTLINE)
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
private fun ToolAndIngredientInputSection(
    selectedToolsState: MutableState<MutableSet<String>>,
    selectedIngredientsState: MutableState<MutableMap<String, Double>>,
    ingredients: MutableState<List<Ingredient_List>>,
    tools: MutableState<List<String>>,
    showDelete: Boolean,
    onDelete: () -> Unit
) {
    val minHeight = 50.dp
    val maxHeight = getScreenHeight() - MAIN_INPUT_SCREEN_SIZE
    val topRowHeight = 31.dp

    var currentHeight by remember { mutableStateOf(minHeight) }
    var selectedTab by remember { mutableStateOf("tools") }

    val animateUp = {
        Thread(Runnable {
            while (currentHeight < maxHeight) {
                Thread.sleep(1.toLong())
                currentHeight += 10.dp
            }
        }).start()
    }

    val animateDown = {
        Thread(Runnable {
            while (currentHeight > minHeight) {
                Thread.sleep(1.toLong())
                currentHeight -= 10.dp
            }
        }).start()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clip(RoundedCornerShape(30.dp))
            .clickable { },
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(AppColor.BACKGROUND_PRIMARY_DARKER)
                .border(1.dp, AppColor.BACKGROUND_SECONDARY, shape = RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 7.dp)
            ) {
                Text(
                    text = "Tools",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    textDecoration = if (selectedTab == "tools") TextDecoration.Underline else null,
                    modifier = Modifier.clickable {
                        selectedTab = "tools"
                    }
                )

                Text(
                    text = "Ingredients",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    textDecoration = if (selectedTab == "ingredients") TextDecoration.Underline else null,
                    modifier = Modifier
                        .clickable { selectedTab = "ingredients" }
                )
            }

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 15.dp,
                        top = if (currentHeight <= minHeight) 15.dp
                        else 5.dp
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.directional_arrow),
                    contentDescription = "",
                    modifier = Modifier
                        .rotate(
                            if (currentHeight <= minHeight) 90f
                            else 270f
                        )
                        .clickable {
                            if (currentHeight <= minHeight) animateUp()
                            else animateDown()
                        }

                )
            }

            if (showDelete) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            end = 15.dp,
                            top = if (currentHeight <= minHeight) 15.dp
                            else 5.dp
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "",
                        modifier = Modifier
                            .clickable {
                                onDelete()
                            }

                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeight - topRowHeight)
                .background(AppColor.BACKGROUND_PRIMARY_DARKER),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(34.dp))
            }

            if (currentHeight > minHeight) {
                if (selectedTab == "tools") {
                    tools.value.forEach {
                        item {
                            ToolListItem(it, selectedToolsState)
                        }
                    }
                } else {
                    ingredients.value.forEach {
                        item {
                            IngredientListItem(it, selectedIngredientsState)
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ToolListItem(tool: String, selectedToolsState: MutableState<MutableSet<String>>) {
    var isSelected by remember { mutableStateOf(selectedToolsState.value.contains(tool)) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .border(3.dp, Color.Black, CircleShape)
                .clickable {
                    if (isSelected) {
                        selectedToolsState.value.remove(tool)
                    } else {
                        selectedToolsState.value.add(tool)
                    }
                    isSelected = !isSelected
                }
        ) {
            if (isSelected) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10f
                    val color = Color.Black
                    drawLine(
                        color = color,
                        strokeWidth = strokeWidth,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                    drawLine(
                        color = color,
                        strokeWidth = strokeWidth,
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height)
                    )
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, size.height)
                        moveTo(size.width, 0f)
                        lineTo(0f, size.height)
                    }
                    drawPath(path = path, color = color, style = Stroke(width = strokeWidth))
                }
            }
        }

        Text(text = tool, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }

}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
private fun IngredientListItem(
    ingredient: Ingredient_List,
    selectedIngredientsState: MutableState<MutableMap<String, Double>>,
) {
    var text by remember {
        mutableStateOf(
            selectedIngredientsState.value.getOrDefault(
                ingredient.IngredientName,
                0.0
            )
                .toString()
        )
    }

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
                .wrapContentSize()
                .padding(horizontal = 8.dp)
                .border(width = 1.5.dp, color = Color.Black, RoundedCornerShape(12.dp)),
            value = text,
            textStyle = MaterialTheme.typography.h4.copy(
                textAlign = TextAlign.Center,
                fontSize = 23.sp
            ),
            onValueChange = {
                if (it.toDoubleOrNull() != null) {
                    text = it
                    selectedIngredientsState.value[ingredient.IngredientName] = it.toDouble()
                } else if (it.isEmpty()) {
                    text = ""
                    selectedIngredientsState.value.remove(ingredient.IngredientName)
                }
            },
            maxLines = 1,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            text = ingredient.Unit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.padding(start = 3.dp, end = 5.dp)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = ingredient.IngredientName,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp
        )
    }
}

@Composable
private fun DashedRow() {
    Row(
        Modifier
            .fillMaxWidth()
    ) {

        Spacer(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(
                    color = Color.Black,
                    shape = DottedShape(step = 16.dp)
                )
        )

    }
}

@Composable
private fun InstructionExpandedListItem(instruction: Instruction, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        TimerIcon(isCrossed = instruction.time == null)

        Text(
            text = instruction.text,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = R.drawable.directional_arrow),
            contentDescription = "",
            modifier = Modifier
                .rotate(180f)
                .padding(bottom = 0.dp)
        )
    }
}

@Composable
private fun InstructionPreviewListItem(instruction: Instruction, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        TimerIcon(isCrossed = instruction.time == null)

        Text(
            text = instruction.text,
            maxLines = 1,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(id = R.drawable.directional_arrow),
            contentDescription = "",
            modifier = Modifier
                .rotate(180f)
                .padding(bottom = 0.dp)
        )
    }
}

@Composable
private fun TimerIcon(isCrossed: Boolean, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.clock),
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )

        if (isCrossed) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }
        }
    }
}

@Composable
private fun Star(
    borderColor: Color,
    isChecked: Boolean,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(
                AppColor.BACKGROUND_SECONDARY
            )
            .border(3.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        if (isChecked) {
            Image(
                painter = painterResource(id = R.drawable.gold_star),
                contentDescription = ""
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.blank_star),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun InstructionAlertDialog(
    onConfirm: (title: String, time: Int?) -> Unit,
    onCancel: () -> Unit,
    defaultText: String = "",
    defaultTime: Pair<Int, Int>? = null
) {
    var minutesState = remember { mutableStateOf(defaultTime?.second) }
    var hoursState = remember { mutableStateOf(defaultTime?.first) }
    var text by remember { mutableStateOf(defaultText) }
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
                .height(600.dp)
                .padding(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color.LightGray)
                .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    if (it.isNotEmpty() && requireText) requireText = false
                    text = it
                },
                textStyle = TextStyle(fontSize = 20.sp, color = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
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
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppColor.BACKGROUND_SECONDARY)
                            .padding(all = 10.dp),
                    ) {
                        if (text == "") {
                            Text(
                                text = "Instructions",
                                color = AppColor.BACKGROUND_PRIMARY,
                                fontSize = 20.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
            ) {
                TimeField(hoursState = hoursState, minutesState = minutesState)
            }

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
                                if (hoursState.value == null && minutesState.value == null) null else hourMinuteToMinute(
                                    minutesState.value ?: 0,
                                    hoursState.value ?: 0
                                )
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
fun TimeField(
    hoursState: MutableState<Int?>,
    minutesState: MutableState<Int?>,
    borderColor: Color = AppColor.BUTTON_OUTLINE
) {
    BasicTextField(
        value = (hoursState.value ?: "").toString(),
        onValueChange = {
            hoursState.value = it.toIntOrNull() ?: if (it.isEmpty()) null else hoursState.value
        },
        textStyle = MaterialTheme.typography.h4.copy(
            textAlign = TextAlign.Center,
            fontSize = 17.sp
        ),
        modifier = Modifier
            .width(40.dp)
            .height(45.dp)
            .padding(0.dp),
        singleLine = true,
        enabled = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(AppColor.BACKGROUND_SECONDARY)
                    .border(
                        2.dp,
                        borderColor,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (hoursState.value == null) {
                    Text(
                        text = "hh",
                        color = AppColor.BACKGROUND_PRIMARY,
                        fontSize = 17.sp
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        }
    )
    Text(text = ":", fontSize = 20.sp)
    BasicTextField(
        value = (minutesState.value ?: "").toString(),
        onValueChange = {
            minutesState.value = it.toIntOrNull() ?: minutesState.value
        },
        textStyle = MaterialTheme.typography.h4.copy(
            textAlign = TextAlign.Center,
            fontSize = 17.sp
        ),
        modifier = Modifier
            .width(40.dp)
            .height(45.dp)
            .padding(0.dp),
        singleLine = true,
        enabled = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(AppColor.BACKGROUND_SECONDARY)
                    .border(
                        2.dp,
                        borderColor,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (minutesState.value == null) {
                    Text(
                        text = "mm",
                        color = AppColor.BACKGROUND_PRIMARY,
                        fontSize = 17.sp
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        }
    )
}

data class DottedShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.width / stepPx).roundToInt()
        val actualStep = size.width / stepsCount
        val dotSize = Size(width = actualStep / 2, height = size.height)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = i * actualStep, y = 0f),
                    size = dotSize
                )
            )
        }
        close()
    })

}
