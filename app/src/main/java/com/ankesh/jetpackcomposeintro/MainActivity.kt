package com.ankesh.jetpackcomposeintro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TextDragDropApp()
        }
    }
}

@Composable
fun TextDragDropApp() {
    var text by remember { mutableStateOf("Sample Text") }
    var position by remember { mutableStateOf(Offset(0f, 0f)) }
    val positionHistory = remember { mutableStateListOf<Offset>() }
    val redoStack = remember { mutableStateListOf<Offset>() }
    var showText by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(24.sp) }
    val fonts = listOf(FontFamily.Default, FontFamily.Cursive, FontFamily.Monospace)
    var currentFontIndex by remember { mutableStateOf(0) }
    var fontFamily by remember { mutableStateOf(fonts[currentFontIndex]) }
    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var isBold by remember { mutableStateOf(false) }
    var textColor by remember { mutableStateOf(Color.Black) }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Undo Button
                IconButton(
                    onClick = {
                        if (positionHistory.isNotEmpty()) {
                            redoStack.add(position.copy())
                            position = positionHistory.removeAt(positionHistory.lastIndex)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Undo")
                }

                // Redo Button
                IconButton(
                    onClick = {
                        if (redoStack.isNotEmpty()) {
                            positionHistory.add(position.copy())
                            position = redoStack.removeAt(redoStack.lastIndex)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Redo")
                }
            }

            // Text Box for Dragging
            if (showText && text.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                positionHistory.add(position.copy())
                                redoStack.clear()
                                position = Offset(position.x + pan.x, position.y + pan.y)
                            }
                        }
                ) {
                    Text(
                        text = text,
                        fontSize = fontSize,
                        fontFamily = fontFamily,
                        color = textColor,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.offset {
                            IntOffset(
                                position.x.roundToInt(),
                                position.y.roundToInt()
                            )
                        }
                    )
                }
            }

            // Control Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) {
                        Text(text = "Add Text", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { textColor = if (textColor == Color.Black) Color.Red else Color.Black },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FormatColorFill,
                            contentDescription = "Change Color",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Color", fontSize = 12.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Font Button
                    Button(
                        onClick = {
                            currentFontIndex = (currentFontIndex + 1) % fonts.size
                            fontFamily = fonts[currentFontIndex]
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Font", fontSize = 12.sp)
                    }

                    // Bold Button
                    Button(onClick = { isBold = !isBold }, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Filled.FormatBold,
                            contentDescription = "Bold",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Bold", fontSize = 12.sp)
                    }

                    // Increase Font Size Button
                    Button(onClick = { fontSize = (fontSize.value + 2).sp }, modifier = Modifier.weight(1f)) {
                        Text(text = "Size +", fontSize = 12.sp)
                    }

                    // Decrease Font Size Button
                    Button(
                        onClick = { if (fontSize.value > 10) fontSize = (fontSize.value - 2).sp },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Size -", fontSize = 12.sp)
                    }
                }
            }

            // Dialog for Adding Text
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Enter Text") },
                    text = {
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (inputText.isNotBlank()) {
                                positionHistory.add(position.copy())
                                text = inputText
                                showText = true
                                showDialog = false
                                redoStack.clear()
                            }
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTextDragDropApp() {
    TextDragDropApp()
}
