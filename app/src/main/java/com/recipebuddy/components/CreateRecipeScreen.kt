package com.recipebuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import com.recipebuddy.util.*

private val MAIN_INPUT_SCREEN_SIZE = 320.dp

@Composable
fun CreateRecipeScreen(onCreate: (recipe: Recipe) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val title = remember { mutableStateOf("") }
        val rating = remember { mutableStateOf(0) }
        val instructions = remember { mutableStateOf(listOf<Instruction>()) }

        var showAddDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var editedInstructionIndex by remember { mutableStateOf(-1) }

        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
            InstructionInputSection(
                instructions,
                onAddInstruction = { showAddDialog = true },
                onEditInstruction = {
                    editedInstructionIndex = it
                    showEditDialog = true
                })

            ToolAndIngredientInputSection()
        }

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            MainInputSection(rating, title)
        }

        if (showAddDialog) {
            InstructionAlertDialog(
                onConfirm = { title, time ->
                    instructions.value = instructions.value + listOf(Instruction(title, time))
                    showAddDialog = false
                },
                onCancel = {
                    showAddDialog = false
                }
            )
        } else if (showEditDialog && editedInstructionIndex >= 0 && editedInstructionIndex < instructions.value.size) {
            InstructionAlertDialog(
                onConfirm = { title, time ->
                    instructions.value =
                        instructions.value.subList(0, editedInstructionIndex) + listOf(
                            Instruction(
                                title,
                                time
                            )
                        ) + instructions.value.subList(
                            editedInstructionIndex + 1,
                            instructions.value.size
                        )
                    showEditDialog = false
                },
                onCancel = {
                    showEditDialog = false
                },
                defaultText = instructions.value[editedInstructionIndex].text,
                defaultTime = with(instructions.value[editedInstructionIndex].time) {
                    if(this == null) {
                        null
                    } else {
                        minuteToHourMinute(this)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainInputSection(rating: MutableState<Int>, title: MutableState<String>) {
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "New Post", fontSize = 20.sp)
            }

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
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
        }


        Column {
            DashedRow()

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
                    .clickable {

                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = ""
                )
            }

            DashedRow()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(start = 10.dp, top = 5.dp)
        ) {
            for (i in 1..5) {
                Star(isChecked = rating.value >= i, width = 30.dp, height = 30.dp) {
                    rating.value = i
                }
            }
        }

        Column {
            val maxLength = 30
            val backgroundColor = AppColor.BACKGROUND_SECONDARY
            val textColor = Color.Black
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                value = title.value,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    cursorColor = Color.Black,
                    disabledLabelColor = backgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    if (it.length <= maxLength) title.value = it
                },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    if (title.value.isNotEmpty()) {
                        IconButton(onClick = { title.value = "" }) {
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
                text = "${title.value.length} / $maxLength",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 10.dp),
                textAlign = TextAlign.End,
                color = textColor
            )
        }


    }
}

@Composable
private fun InstructionInputSection(
    instructions: MutableState<List<Instruction>>,
    onAddInstruction: () -> Unit,
    onEditInstruction: (instructionIndex: Int) -> Unit
) {
    var isEditingInstructions by remember { mutableStateOf(false) }
    var tempInstructions by remember { mutableStateOf(instructions.value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(getScreenHeight() - MAIN_INPUT_SCREEN_SIZE)
            .border(1.dp, AppColor.BACKGROUND_SECONDARY, shape = RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(30.dp))
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

@Composable
private fun ToolAndIngredientInputSection() {

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
private fun Star(isChecked: Boolean, width: Dp, height: Dp, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(
                AppColor.BACKGROUND_SECONDARY
            )
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
    var minutes: Int? by remember { mutableStateOf(defaultTime?.second) }
    var hours: Int? by remember { mutableStateOf(defaultTime?.first) }
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
                BasicTextField(
                    value = (hours ?: "").toString(),
                    onValueChange = {
                        hours = it.toIntOrNull() ?: hours
                    },
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
                                    AppColor.BUTTON_OUTLINE,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hours == null) {
                                Text(
                                    text = "hh",
                                    color = AppColor.BACKGROUND_PRIMARY,
                                    fontSize = 17.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                Text(text = ":", fontSize = 20.sp)
                BasicTextField(
                    value = (minutes ?: "").toString(),
                    onValueChange = {
                        minutes = it.toIntOrNull() ?: minutes
                    },
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
                                    AppColor.BUTTON_OUTLINE,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (minutes == null) {
                                Text(
                                    text = "mm",
                                    color = AppColor.BACKGROUND_PRIMARY,
                                    fontSize = 17.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
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
                                if (hours == null && minutes == null) null else hourMinuteToMinute(
                                    minutes ?: 0,
                                    hours ?: 0
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
