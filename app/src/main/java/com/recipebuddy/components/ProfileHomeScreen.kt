package com.recipebuddy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipebuddy.ui.resources.AppColor
import com.recipebuddy.util.TempDataObject

@Composable
fun ProfileHomeScreen() {
    var tempUsername by remember { mutableStateOf(TempDataObject.username) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .padding(top = 30.dp)
            .clip(shape = RoundedCornerShape(20.dp))
            .background(AppColor.BACKGROUND_SECONDARY)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(shape = CircleShape)
                .width(150.dp)
                .height(150.dp)
        ) {
            Image(
                painter = painterResource(id = com.recipebuddy.R.drawable.blank_profile_image),
                contentDescription = ""
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(20.dp))
                .background(AppColor.BACKGROUND_PRIMARY)
                .padding(13.dp)
        ) {
            Text(text = "Username:", fontSize = 30.sp)

            if (ScreenManager.selectedScreen == ScreenManager.EDIT_PROFILE_SCREEN) {
                TextField(
                    value = tempUsername,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 30.sp),
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = AppColor.BUTTON_OUTLINE,
                        focusedLabelColor = AppColor.BUTTON_OUTLINE,
                        focusedIndicatorColor = AppColor.BUTTON_OUTLINE
                    ),
                    onValueChange = {
                        tempUsername = it
                    }
                )
            } else {
                Text(text = TempDataObject.username, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                if (ScreenManager.selectedScreen == ScreenManager.EDIT_PROFILE_SCREEN) {
                    Button(
                        onClick = {
                            ScreenManager.selectedScreen = ScreenManager.PROFILE_HOME_SCREEN
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            TempDataObject.username = tempUsername
                            ScreenManager.selectedScreen = ScreenManager.PROFILE_HOME_SCREEN
                        },
                        colors = ButtonDefaults.buttonColors(Color.Green),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(text = "Confirm", fontSize = 18.sp, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            tempUsername = TempDataObject.username
                            ScreenManager.selectedScreen = ScreenManager.EDIT_PROFILE_SCREEN
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(text = "Edit", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}