package com.example.search_feature

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.search_feature.ui.theme.Search_FeatureTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState()
            var textFieldState by remember {
                mutableStateOf("")
            }
            val scope = rememberCoroutineScope()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = scaffoldState
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    TextField(
                        value = textFieldState,
                        label = {
                            Text("Search Recipe...")
                        },
                        onValueChange = {
                            textFieldState = it
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(305.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        scope.launch{
                            scaffoldState.snackbarHostState.showSnackbar("You searched: $textFieldState")
                        }
                    }) {
                        Text("Enter")
                    }
                }
            }
        }
    }
}

