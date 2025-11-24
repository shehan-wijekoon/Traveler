package com.example.traveler.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.traveler.ui.components.HeaderBar
import com.example.traveler.viewmodel.UploadPostUiState
import com.example.traveler.viewmodel.UploadPostViewModel
import com.example.traveler.controllers.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPostScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    uploadPostViewModel: UploadPostViewModel
) {
    val context = LocalContext.current
    val uiState by uploadPostViewModel.uiState.collectAsState()
    val imageUrls = remember { mutableStateListOf("") }
    var hashtagsInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var expanded by remember { mutableStateOf(false) }
    var googleMapLink by remember { mutableStateOf("") }
    var rulesGuidance by remember { mutableStateOf("") }

    val categories = listOf("Jungle", "Beach", "Forest", "Mountain", "Waterfall")

    // Get the first non-blank image for the preview box
    val previewImageUrl = imageUrls.firstOrNull { it.isNotBlank() } ?: ""

    LaunchedEffect(uiState) {
        when (uiState) {
            is UploadPostUiState.Success -> {
                Toast.makeText(context, "Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
                uploadPostViewModel.resetUiState()
            }
            is UploadPostUiState.Error -> {
                val errorMessage = (uiState as UploadPostUiState.Error).message
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                uploadPostViewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            HeaderBar(
                onBackClick = { navController.popBackStack() },
                onMenuClick = { }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create a New Post",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- IMAGE PREVIEW BOX ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (previewImageUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = previewImageUrl),
                        contentDescription = "Selected image preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Select Image",
                            modifier = Modifier.size(48.dp),
                            tint = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Paste public image URLs below",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- POST TITLE ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Post Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- DYNAMIC IMAGE URL INPUTS ---
            Text(
                text = "Image URLs",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            imageUrls.forEachIndexed { index, url ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = { newValue -> imageUrls[index] = newValue },
                        label = { Text("Image Link ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Uri)
                    )

                    if (imageUrls.size > 1) {
                        IconButton(onClick = { imageUrls.removeAt(index) }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Link")
                        }
                    }
                }
            }

            // PLUS BUTTON TO ADD NEW LINK FIELD
            Button(
                onClick = { imageUrls.add("") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add another link", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(Modifier.width(8.dp))
                Text("Add Another Image Link", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- HASHTAGS INPUT ---
            OutlinedTextField(
                value = hashtagsInput,
                onValueChange = { hashtagsInput = it },
                label = { Text("Hashtags (e.g., #travel #nature)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- GOOGLE MAPS LINK ---
            OutlinedTextField(
                value = googleMapLink,
                onValueChange = { googleMapLink = it },
                label = { Text("Google Maps Link (Full URL)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Uri)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- RULES/SPECIAL GUIDANCE ---
            OutlinedTextField(
                value = rulesGuidance,
                onValueChange = { rulesGuidance = it },
                label = { Text("Rules/Special Guidance for Visitors (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))


            // --- CATEGORY DROPDOWN ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    label = { Text("Category") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            Modifier.clickable { expanded = !expanded }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // --- DESCRIPTION ---
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Write a detailed description...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- FORM VALIDATION AND PROCESSING ---
            val finalImageUrls = imageUrls.filter { it.isNotBlank() }
            val isImageListValid = finalImageUrls.isNotEmpty()

            val isFormValid = isImageListValid && description.isNotBlank() && title.isNotBlank() && selectedCategory != "Select Category"


            Button(
                onClick = {
                    if (isFormValid) {
                        uploadPostViewModel.uploadPost(
                            imageUrls = finalImageUrls,
                            description = description,
                            title = title,
                            category = selectedCategory,
                            googleMapLink = googleMapLink,
                            rulesGuidance = rulesGuidance,
                            hashtagsInput = hashtagsInput
                        )
                    } else {
                        Toast.makeText(context, "Please provide at least one Image URL and complete all required fields.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState !is UploadPostUiState.Loading && isFormValid
            ) {
                if (uiState is UploadPostUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Upload Post", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}