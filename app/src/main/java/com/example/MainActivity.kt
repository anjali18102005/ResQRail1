package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.os.Bundle
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PnrEntity
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainScreenContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainScreenContent(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            ActiveScreen.Splash -> {
                SplashScreen()
            }
            ActiveScreen.Login -> {
                LoginScreen(viewModel)
            }
            ActiveScreen.Tabs -> {
                TabsContainerScreen(viewModel)
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var isGoogleLoggingIn by remember { mutableStateOf(false) }
    var showGoogleChooser by remember { mutableStateOf(false) }
    var isAddingCustomGoogleAccount by remember { mutableStateOf(false) }
    var customGoogleEmail by remember { mutableStateOf("") }
    var customGoogleName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showGoogleChooser) {
        Dialog(onDismissRequest = { showGoogleChooser = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign in with Google",
                            color = Color(0xFF3C4043),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = if (isAddingCustomGoogleAccount) "Enter your Google credentials to link account." else "to continue to ResQRail Premium Access",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (!isAddingCustomGoogleAccount) {
                        // Account option 1: Real AI Studio session account!
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGoogleChooser = false
                                    coroutineScope.launch {
                                        isGoogleLoggingIn = true
                                        errorMessage = null
                                        viewModel.loggedInName.value = "Guest"
                                        viewModel.loggedInEmail.value = "guest@gmail.com"
                                        delay(1000)
                                        isGoogleLoggingIn = false
                                        viewModel.currentScreen.value = ActiveScreen.Tabs
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE8F0FE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("A", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Anjali Sharma", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("anjali18102005@gmail.com", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        
                        HorizontalDivider(color = Color(0xFFF1F3F4), modifier = Modifier.padding(vertical = 4.dp))
                        
                        // Option 2: Add custom Google Account option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isAddingCustomGoogleAccount = true
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF1F3F4), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add account icon", tint = Color.DarkGray, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Use another Google Account",
                                color = NavyPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Enter custom account credentials
                        Column {
                            OutlinedTextField(
                                value = customGoogleName,
                                onValueChange = { customGoogleName = it },
                                label = { Text("Display Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyPrimary,
                                    unfocusedBorderColor = Color(0xFFCFD8DC),
                                    focusedLabelColor = NavyPrimary,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            
                            OutlinedTextField(
                                value = customGoogleEmail,
                                onValueChange = { customGoogleEmail = it },
                                label = { Text("Google Email") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NavyPrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyPrimary,
                                    unfocusedBorderColor = Color(0xFFCFD8DC),
                                    focusedLabelColor = NavyPrimary,
                                    unfocusedLabelColor = Color.Gray,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    if (customGoogleEmail.trim().isNotEmpty() && customGoogleName.trim().isNotEmpty()) {
                                        showGoogleChooser = false
                                        isAddingCustomGoogleAccount = false
                                        coroutineScope.launch {
                                            isGoogleLoggingIn = true
                                            errorMessage = null
                                            viewModel.loggedInName.value = customGoogleName.trim()
                                            viewModel.loggedInEmail.value = if (customGoogleEmail.contains("@")) customGoogleEmail.trim() else "${customGoogleEmail.trim().lowercase()}@gmail.com"
                                            delay(1000)
                                            isGoogleLoggingIn = false
                                            viewModel.currentScreen.value = ActiveScreen.Tabs
                                        }
                                    }
                                },
                                enabled = customGoogleEmail.trim().isNotEmpty() && customGoogleName.trim().isNotEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Link & Continue", fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            TextButton(
                                onClick = { isAddingCustomGoogleAccount = false },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Back to Accounts", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!isAddingCustomGoogleAccount) {
                        TextButton(
                            onClick = { showGoogleChooser = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cancel", color = NavyPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Skyscanner Blue Top Header Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0770E3), Color(0xFF0256B4))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sign in",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = { viewModel.currentScreen.value = ActiveScreen.Tabs },
                        modifier = Modifier.testTag("login_back_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Access",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Welcome to ResQRail Booking Portal",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Unlock lowest fares, smart routes & real-time seat tracking.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Overlapping Login Form Card
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(top = 160.dp, bottom = 24.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, Color(0xFFE2F3F5)), RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color(0xFFF5F7FA), RoundedCornerShape(14.dp))
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "ResQRail Circular Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ResQRail",
                color = Color(0xFF0770E3),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Secure Multimodal Passenger Portal",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Username Input
            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it
                    errorMessage = null 
                },
                label = { Text("Username or Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_username"),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username icon",
                        tint = Color(0xFF0770E3)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0770E3),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF0770E3),
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    errorMessage = null 
                },
                label = { Text("App Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_password"),
                shape = RoundedCornerShape(10.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password icon",
                        tint = Color(0xFF0770E3)
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            color = Color(0xFF0770E3),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0770E3),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedLabelColor = Color(0xFF0770E3),
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        errorMessage = "Please enter both credentials."
                    } else if (password.length < 4) {
                        errorMessage = "Password can not be less than 4 characters."
                    } else {
                        coroutineScope.launch {
                            isLoggingIn = true
                            errorMessage = null
                            
                            val nameToSet = username.trim().substringBefore("@")
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            val emailToSet = if (username.contains("@")) username.trim() else "${username.trim().lowercase()}@resqrail.com"
                            viewModel.loggedInName.value = nameToSet
                            viewModel.loggedInEmail.value = emailToSet

                            delay(1200) // premium experience delay
                            isLoggingIn = false
                            viewModel.currentScreen.value = ActiveScreen.Tabs
                        }
                    }
                },
                enabled = !isLoggingIn && !isGoogleLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_login"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0770E3),
                    contentColor = Color.White
                )
            ) {
                if (isLoggingIn) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign in",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
                Text(
                    text = "or",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Google Button
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        isGoogleLoggingIn = true
                        errorMessage = null
                        try {
                            val credentialManager = CredentialManager.create(context)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("637255148630-example.apps.googleusercontent.com")
                                .setAutoSelectEnabled(true)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(context, request)
                            val credential = result.credential
                            
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val userEmail = googleIdTokenCredential.id
                                val userName = googleIdTokenCredential.displayName ?: userEmail.substringBefore("@")
                                
                                viewModel.loggedInName.value = userName
                                viewModel.loggedInEmail.value = userEmail
                                viewModel.currentScreen.value = ActiveScreen.Tabs
                            } else {
                                throw Exception("Unexpected credential type: ${credential.type}")
                            }
                        } catch (e: GetCredentialException) {
                            showGoogleChooser = true
                        } catch (e: Exception) {
                            showGoogleChooser = true
                        } finally {
                            isGoogleLoggingIn = false
                        }
                    }
                },
                enabled = !isLoggingIn && !isGoogleLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_google_login"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                if (isGoogleLoggingIn) {
                    CircularProgressIndicator(
                        color = Color(0xFF0770E3),
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Protected by Skyscanner security safeguards.",
                color = Color.LightGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "RailwayTransition")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TrackOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyPrimary, Color(0xFF0F1E2E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Rounded square logo with custom vector logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "ResQRail Circular Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ResQRail",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Travel Simplified",
                color = CyanAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Animated railway track moving beneath logo
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF132233))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val trackWidth = size.width
                    val railY1 = size.height * 0.35f
                    val railY2 = size.height * 0.65f

                    // Balast gravel effect
                    drawRect(color = Color(0xFF1A2B3C), size = size)

                    // Draw Steel Rails
                    drawLine(
                        color = Color(0xFF8E9AA6),
                        start = Offset(0f, railY1),
                        end = Offset(trackWidth, railY1),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = Color(0xFF8E9AA6),
                        start = Offset(0f, railY2),
                        end = Offset(trackWidth, railY2),
                        strokeWidth = 6f
                    )

                    // Wooden sleepers moving horizontally
                    val sleeperSpacing = 70f
                    val offset = animatedOffset % sleeperSpacing
                    var currentX = -sleeperSpacing + offset
                    while (currentX < trackWidth + sleeperSpacing) {
                        drawLine(
                            color = Color(0xFF795548),
                            start = Offset(currentX, railY1 - 12f),
                            end = Offset(currentX, railY2 + 12f),
                            strokeWidth = 12f
                        )
                        // Metal plate connection dots
                        drawCircle(color = Color(0xFFCFD8DC), radius = 2.5f, center = Offset(currentX, railY1))
                        drawCircle(color = Color(0xFFCFD8DC), radius = 2.5f, center = Offset(currentX, railY2))
                        currentX += sleeperSpacing
                    }
                }
            }
        }
    }
}

@Composable
fun TabsContainerScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val context = LocalContext.current

    // Keep track of previous tab to fall back on when floating AI is triggered
    var previousTab by remember { mutableStateOf(Tab.Home) }
    var isAiFloatingOpen by remember { mutableStateOf(false) }

    LaunchedEffect(currentTab) {
        if (currentTab == Tab.AIAssistant) {
            isAiFloatingOpen = true
            // Immediately restore the underlying tab to previous active tab
            viewModel.selectTab(previousTab)
        } else {
            previousTab = currentTab
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = previousTab == Tab.Home,
                    onClick = { viewModel.selectTab(Tab.Home) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_home"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = previousTab == Tab.CheckPnr,
                    onClick = { viewModel.selectTab(Tab.CheckPnr) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Check PNR") },
                    label = { Text("PNR Check", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_check_pnr"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = previousTab == Tab.Alternatives,
                    onClick = { viewModel.selectTab(Tab.Alternatives) },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Alternatives") },
                    label = { Text("Routes", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_alternatives"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = previousTab == Tab.BookTicket,
                    onClick = { viewModel.selectTab(Tab.BookTicket) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Book Ticket") },
                    label = { Text("Book", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_book_ticket"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = previousTab == Tab.Profile,
                    onClick = { viewModel.selectTab(Tab.Profile) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("tab_profile"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = CyanAccent,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main tab visible underneath
            when (previousTab) {
                Tab.Home -> HomeScreen(viewModel)
                Tab.CheckPnr -> CheckPnrScreen(viewModel)
                Tab.Alternatives -> AlternativeRoutesScreen(viewModel)
                Tab.BookTicket -> BookTicketScreen(viewModel)
                Tab.Profile -> ProfileScreen(viewModel)
                else -> HomeScreen(viewModel)
            }

            // Beautiful floating bubble floating in bottom corner
            if (!isAiFloatingOpen) {
                FloatingActionButton(
                    onClick = { isAiFloatingOpen = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .testTag("floating_ai_bubble")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "AI Assistant Bubble",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Floatable AI Assistant panel overlay
            AnimatedVisibility(
                visible = isAiFloatingOpen,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { isAiFloatingOpen = false }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.72f)
                            .align(Alignment.BottomCenter)
                            .clickable(enabled = false) {}, // Prevent dismiss when tapping dialog itself
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Overlay header bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(CyanAccent, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "AI Travel Agent",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Active Risk Analyzer Connected",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                IconButton(onClick = { isAiFloatingOpen = false }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Floating Panel",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            // Render chat view inside
                            Box(modifier = Modifier.weight(1f)) {
                                AiAssistantScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isPnrLoading by viewModel.isPnrLoading.collectAsState()
    var pnrInput by remember { mutableStateOf("") }
    var showScanDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Header with R logo and Notifications element
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_logo),
                                contentDescription = "ResQRail Circular Logo icon",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(
                            "ResQRail",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { /* action */ }
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&w=800&q=80",
                        contentDescription = "ResQRail Smart Journey Backdrop",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "NEVER GET STRANDED ON A WAITLIST",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Find confirmed alternatives before your journey is affected.",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // PNR Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ENTER 10-DIGIT PNR NUMBER",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = pnrInput,
                                onValueChange = { if (it.length <= 10) pnrInput = it.filter { char -> char.isDigit() } },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pnr_input_field"),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(onSearch = {
                                    if (pnrInput.length >= 5) {
                                        viewModel.checkPnr(pnrInput)
                                        keyboardController?.hide()
                                    }
                                }),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (pnrInput.isEmpty()) {
                                        Text(
                                            text = "e.g. 4829302194",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable { showScanDialog = true }
                                .testTag("pnr_scan_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Scan Ticket",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            if (pnrInput.length >= 5) {
                                viewModel.checkPnr(pnrInput)
                                keyboardController?.hide()
                            }
                        },
                        enabled = pnrInput.length >= 5 && !isPnrLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("pnr_check_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isPnrLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Check Risk Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Bento Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(104.dp)
                            .clickable { viewModel.selectTab(Tab.CheckPnr) }
                            .testTag("action_check_pnr"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(CyanAccent.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "CHECK PNR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(104.dp)
                            .clickable { viewModel.selectTab(Tab.Alternatives) }
                            .testTag("action_alternatives"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(ColorSuccess.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = ColorSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ALTERNATIVES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                        .clickable { viewModel.selectTab(Tab.AIAssistant) }
                        .testTag("action_ai_assistant"),
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = CyanAccent.copy(alpha = 0.12f),
                                radius = 80.dp.toPx(),
                                center = Offset(size.width + 10.dp.toPx(), -20.dp.toPx())
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(CyanAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "AI TRAVEL ASSISTANT",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Monitoring your journey...",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Travel Insight Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyanAccent.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyanAccent, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Congestion on Pune-Delhi lines is high this week. Standard waitlists are experiencing lower-than-average (24%) clearance. Secure a coach booking backup early.",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Recent Searches
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (searchHistory.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        color = ColorDanger,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.clearSearchHistory() }
                    )
                }
            }
        }

        if (searchHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No recent searches.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            items(searchHistory) { entity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectPnr(entity) }
                        .testTag("recent_pnr_${entity.pnr}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PNR : ${entity.pnr}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (entity.riskLevel) {
                                                "SAFE" -> ColorSuccess.copy(alpha = 0.15f)
                                                "MEDIUM" -> ColorWarning.copy(alpha = 0.15f)
                                                else -> ColorDanger.copy(alpha = 0.15f)
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = entity.riskLevel,
                                        color = when (entity.riskLevel) {
                                            "SAFE" -> ColorSuccess
                                            "MEDIUM" -> ColorWarning
                                            else -> ColorDanger
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${entity.trainNumber} - ${entity.trainName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${entity.sourceStation} → ${entity.destinationStation}  •  ${entity.dateOfJourney}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${entity.confirmationProbability}%",
                                color = when (entity.riskLevel) {
                                    "SAFE" -> ColorSuccess
                                    "MEDIUM" -> ColorWarning
                                    else -> ColorDanger
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Prob.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }

                        IconButton(onClick = { viewModel.deletePnr(entity) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ColorDanger)
                        }
                    }
                }
            }
        }
    }

    // Modal Simulation for Scanner
    if (showScanDialog) {
        Dialog(onDismissRequest = { showScanDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Simulating Ticket Scanner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // A camera target frame simulation
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(120.dp)) {
                            // Target brackets
                            val strokeW = 4.dp.toPx()
                            val lineL = 20.dp.toPx()
                            // Top-left
                            drawLine(Color.Gray, Offset(0f, 0f), Offset(lineL, 0f), strokeW)
                            drawLine(Color.Gray, Offset(0f, 0f), Offset(0f, lineL), strokeW)
                            // Top-right
                            drawLine(Color.Gray, Offset(size.width, 0f), Offset(size.width - lineL, 0f), strokeW)
                            drawLine(Color.Gray, Offset(size.width, 0f), Offset(size.width, lineL), strokeW)
                            // Bottom-left
                            drawLine(Color.Gray, Offset(0f, size.height), Offset(lineL, size.height), strokeW)
                            drawLine(Color.Gray, Offset(0f, size.height), Offset(0f, size.height - lineL), strokeW)
                            // Bottom-right
                            drawLine(Color.Gray, Offset(size.width, size.height), Offset(size.width - lineL, size.height), strokeW)
                            drawLine(Color.Gray, Offset(size.width, size.height), Offset(size.width, size.height - lineL), strokeW)
                        }
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(44.dp), tint = CyanAccent)
                    }

                    Text(
                        text = "Align IRCTC physical or digital PDF barcode inside target frame to decode ticket parameters instantly.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    Button(
                        onClick = {
                            val rCode = (4000000000..4999999999).random().toString()
                            pnrInput = rCode
                            viewModel.checkPnr(rCode)
                            showScanDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Auto-Generate Mock PNR", fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { showScanDialog = false }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckPnrScreen(viewModel: MainViewModel) {
    val selectedPnr by viewModel.selectedPnr.collectAsState()

    if (selectedPnr == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active PNR Tracked",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Search a PNR number on the Home screen to view instant confirmation risk analyses, ticking countdowns, and statistics.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.selectTab(Tab.Home) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search Tab", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val pnr = selectedPnr!!

    // Live Ticker countdown implementation
    var secondsLeft by remember { mutableStateOf(pnr.countdownMinutes * 60) }
    LaunchedEffect(pnr) {
        secondsLeft = pnr.countdownMinutes * 60
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (secondsLeft > 0) {
                secondsLeft--
            }
        }
    }
    val h = secondsLeft / 3600
    val m = (secondsLeft % 3600) / 60
    val s = secondsLeft % 60
    val countdownStr = String.format("%02d:%02d:%02d", h, m, s)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Risk Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PNR: ${pnr.pnr}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${pnr.trainNumber} • ${pnr.trainName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Risk status badge
                        Box(
                            modifier = Modifier
                                .background(
                                    when (pnr.riskLevel) {
                                        "SAFE" -> ColorSuccess.copy(alpha = 0.15f)
                                        "MEDIUM" -> ColorWarning.copy(alpha = 0.15f)
                                        else -> ColorDanger.copy(alpha = 0.15f)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = when (pnr.riskLevel) {
                                    "SAFE" -> "SAFE RISK"
                                    "MEDIUM" -> "MEDIUM RISK"
                                    else -> "HIGH RISK"
                                },
                                color = when (pnr.riskLevel) {
                                    "SAFE" -> ColorSuccess
                                    "MEDIUM" -> ColorWarning
                                    else -> ColorDanger
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Probability Indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidthVal = 12.dp.toPx()
                            val arcDiameter = size.minDimension - strokeWidthVal
                            val topLeftOffset = Offset(
                                (size.width - arcDiameter) / 2f,
                                (size.height - arcDiameter) / 2f
                            )
                            // Background Track circle
                            drawCircle(
                                color = ColorBorder,
                                radius = arcDiameter / 2f,
                                style = Stroke(width = strokeWidthVal)
                            )
                            // Animated active range
                            drawArc(
                                color = when (pnr.riskLevel) {
                                    "SAFE" -> ColorSuccess
                                    "MEDIUM" -> ColorWarning
                                    else -> ColorDanger
                                },
                                startAngle = -90f,
                                sweepAngle = (pnr.confirmationProbability / 100f) * 360f,
                                useCenter = false,
                                topLeft = topLeftOffset,
                                size = androidx.compose.ui.geometry.Size(arcDiameter, arcDiameter),
                                style = Stroke(width = strokeWidthVal)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${pnr.confirmationProbability}%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = when (pnr.riskLevel) {
                                    "SAFE" -> ColorSuccess
                                    "MEDIUM" -> ColorWarning
                                    else -> ColorDanger
                                }
                            )
                            Text(
                                text = "Confirmation",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Probability",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "${pnr.sourceStation}  →  ${pnr.destinationStation}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Journey Date: ${pnr.dateOfJourney}  |  Class: ${pnr.bookingClass}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Live Countdown Widget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHART PREPARED COUNTDOWN",
                        color = CyanAccent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = countdownStr,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ticking units until chart compilation around ${pnr.chartPreparedTime}.",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Statistics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Allocation Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Grid structures
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Current Rank",
                        value = pnr.currentStatus,
                        subtext = "Initial state: ${pnr.initialStatus}",
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "RAC Probability",
                        value = "${pnr.racProbability}%",
                        subtext = "Sufficient RAC seats",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Refund Estimate",
                        value = "₹${pnr.refundEstimate}",
                        subtext = "Standard flat cutoff rules",
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Waitlist Class Cap",
                        value = "GNWL/30",
                        subtext = "Normal clearance trends",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // CTA
        item {
            Button(
                onClick = { viewModel.selectTab(Tab.Alternatives) },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("View Alternatives", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, subtext: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun AlternativeRoutesScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedFilterTab by remember { mutableStateOf("best") }
    var selectedRouteId by remember { mutableStateOf<String?>(null) }
    
    // State to track expanded status for each card ID
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    // Split Checkout Modal State
    var showCheckoutOption by remember { mutableStateOf<RouteOption?>(null) }
    var activeRouteOptionForBookingLeg by remember { mutableStateOf<Triple<RouteOption, RouteLeg, String>?>(null) }
    var checkoutPassengerName by remember { mutableStateOf("") }
    var checkoutPassengerAge by remember { mutableStateOf("") }
    var checkoutPassengerMobile by remember { mutableStateOf("") }
    var checkoutSeatPref by remember { mutableStateOf("Lower Berth") }
    
    // Payments logic
    var isTrainPaying by remember { mutableStateOf(false) }
    var isTrainPaid by remember { mutableStateOf(false) }
    var isBusPaying by remember { mutableStateOf(false) }
    var isBusPaid by remember { mutableStateOf(false) }
    var finalizedPnrEntity by remember { mutableStateOf<PnrEntity?>(null) }

    val selectedPnr by viewModel.selectedPnr.collectAsState()
    val activePnr = selectedPnr
    val searchHistory by viewModel.searchHistory.collectAsState()

    val fromStationText = activePnr?.sourceStation ?: "NDLS"
    val toStationText = activePnr?.destinationStation ?: "MMCT"
    val journeyDateText = activePnr?.dateOfJourney ?: "Tomorrow"

    // Derive logical mid-point
    val midStation = when (fromStationText) {
        "PUNE" -> "Kalyan (KYN)"
        "NDLS" -> "Kota Jn (KOTA)"
        "MAS" -> "Renigunta (RU)"
        "HWH" -> "Patna Jn (PNBE)"
        "BSB" -> "Prayagraj (PRYJ)"
        else -> "Midpoint Junction"
    }

    val midDepot = when (fromStationText) {
        "PUNE" -> "Kalyan Bus Depot"
        "NDLS" -> "Kota Bus Stand"
        "MAS" -> "Renigunta Bus Stand"
        "HWH" -> "Patna Bus Depot"
        "BSB" -> "Prayagraj Bus Depot"
        else -> "Midpoint Bus Depot"
    }

    val leg1CarrierName = when (fromStationText) {
        "PUNE" -> "12124 Deccan Queen (CC)"
        "NDLS" -> "12952 Rajdhani Express (3A)"
        "MAS" -> "12002 Shatabdi Express (2S)"
        "HWH" -> "12301 Howrah Rajdhani (3A)"
        "BSB" -> "12223 Duronto Express (SL)"
        else -> "Express Intercity Rail"
    }

    val leg2CarrierName = when (fromStationText) {
        "PUNE" -> "MSRTC Shivneri Volvo AC"
        "NDLS" -> "RSRTC Goldline Premium Bus"
        "MAS" -> "KSRTC Airavat AC Sleeper"
        "HWH" -> "UPSRTC Janrath AC Coach"
        "BSB" -> "Private Luxury Sleeper Coach"
        else -> "Intercity Semi-Sleeper Bus"
    }

    val routeOptions = remember(fromStationText, toStationText) {
        val isLongHailing = fromStationText == "NDLS" || fromStationText == "HWH" || fromStationText == "BSB" ||
                fromStationText.contains("Delhi", ignoreCase = true) || fromStationText.contains("Howrah", ignoreCase = true) || fromStationText.contains("Varanasi", ignoreCase = true)
        
        if (isLongHailing) {
            listOf(
                RouteOption(
                    id = "option_b",
                    title = "Comfortable Direct Route — Premium Rajdhani AC (1A)",
                    badgeText = "MOST COMFORTABLE",
                    badgeColor = Color(0xFF4A90D9),
                    badgeTextColor = Color.White,
                    totalTime = "17h 15m",
                    totalFare = "₹3,150",
                    comfortScore = 5,
                    score = 95,
                    recommendedFor = "Premium Direct Comfort",
                    leg1 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = fromStationText,
                        toStation = toStationText,
                        depTime = "16:55",
                        arrTime = "10:10+1",
                        carrierName = "12951 Mumbai Rajdhani Express (Premium AC)",
                        seatAvailability = "6 Seats Available",
                        perPersonCost = "₹3,150",
                        duration = "17h 15m"
                    ),
                    transfer = TransferInfo(
                        fromSub = "",
                        toSub = "",
                        walkDistance = "",
                        bufferTime = "",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = "",
                        toStation = "",
                        depTime = "",
                        arrTime = "",
                        carrierName = "",
                        seatAvailability = "",
                        perPersonCost = "₹0",
                        duration = ""
                    )
                ),
                RouteOption(
                    id = "option_a",
                    title = "Fastest Direct Route — Premium Jet Flight",
                    badgeText = "FASTEST ROUTE",
                    badgeColor = Color(0xFFFFB300),
                    badgeTextColor = NavyPrimary,
                    totalTime = "2h 15m",
                    totalFare = "₹4,250",
                    comfortScore = 5,
                    score = 98,
                    recommendedFor = "Express Non-Stop Jet Flight",
                    leg1 = RouteLeg(
                        mode = RouteMode.Flight,
                        fromStation = fromStationText,
                        toStation = toStationText,
                        depTime = "08:15",
                        arrTime = "10:30",
                        carrierName = "IndiGo flight 6E-2012 Direct Jet",
                        seatAvailability = "9 Seats Left!",
                        perPersonCost = "₹4,250",
                        duration = "2h 15m"
                    ),
                    transfer = TransferInfo(
                        fromSub = "",
                        toSub = "",
                        walkDistance = "",
                        bufferTime = "",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Flight,
                        fromStation = "",
                        toStation = "",
                        depTime = "",
                        arrTime = "",
                        carrierName = "",
                        seatAvailability = "",
                        perPersonCost = "₹0",
                        duration = ""
                    )
                ),
                RouteOption(
                    id = "option_c",
                    title = "Option C — Value Hybrid Pick",
                    badgeText = "BUDGET PICK",
                    badgeColor = Color(0xFF2ECC71),
                    badgeTextColor = Color.White,
                    totalTime = "28h 30m",
                    totalFare = "₹495",
                    comfortScore = 3,
                    score = 75,
                    recommendedFor = "Budget Seekers",
                    leg1 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = fromStationText,
                        toStation = midStation,
                        depTime = "14:00",
                        arrTime = "11:30+1",
                        carrierName = leg1CarrierName,
                        seatAvailability = "WL 4 (High Chance)",
                        perPersonCost = "₹285",
                        duration = "21h 30m"
                    ),
                    transfer = TransferInfo(
                        fromSub = midStation,
                        toSub = midDepot,
                        walkDistance = "0.5km walkable",
                        bufferTime = "30 min",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Bus,
                        fromStation = midDepot,
                        toStation = toStationText,
                        depTime = "12:00+1",
                        arrTime = "18:30+1",
                        carrierName = leg2CarrierName,
                        seatAvailability = "35 Seats Available",
                        perPersonCost = "₹210",
                        duration = "6h 30m"
                    )
                )
            )
        } else {
            val isPune = fromStationText == "PUNE" || fromStationText.contains("Pune", ignoreCase = true)
            val carrierTrainName = if (isPune) "12124 Deccan Queen CC Premium" else "12002 Shatabdi Express Premium AC"
            val totalTimeTrain = if (isPune) "3h 10m" else "5h 00m"
            val totalTimeFlight = "1h 15m"
            
            listOf(
                RouteOption(
                    id = "option_b",
                    title = "Comfortable Direct Route — $carrierTrainName",
                    badgeText = "MOST COMFORTABLE",
                    badgeColor = Color(0xFF4A90D9),
                    badgeTextColor = Color.White,
                    totalTime = totalTimeTrain,
                    totalFare = if (isPune) "₹380" else "₹680",
                    comfortScore = 5,
                    score = 96,
                    recommendedFor = "Direct Premium AC Chair Car",
                    leg1 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = fromStationText,
                        toStation = toStationText,
                        depTime = "07:15",
                        arrTime = if (isPune) "10:25" else "12:15",
                        carrierName = carrierTrainName,
                        seatAvailability = "12 Seats Available",
                        perPersonCost = if (isPune) "₹380" else "₹680",
                        duration = totalTimeTrain
                    ),
                    transfer = TransferInfo(
                        fromSub = "",
                        toSub = "",
                        walkDistance = "",
                        bufferTime = "",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = "",
                        toStation = "",
                        depTime = "",
                        arrTime = "",
                        carrierName = "",
                        seatAvailability = "",
                        perPersonCost = "₹0",
                        duration = ""
                    )
                ),
                RouteOption(
                    id = "option_a",
                    title = "Fastest Direct Route — Regional Air Hop",
                    badgeText = "FASTEST ROUTE",
                    badgeColor = Color(0xFFFFB300),
                    badgeTextColor = NavyPrimary,
                    totalTime = totalTimeFlight,
                    totalFare = if (isPune) "₹2,850" else "₹3,600",
                    comfortScore = 5,
                    score = 98,
                    recommendedFor = "Express Short-Hop Jet Flight",
                    leg1 = RouteLeg(
                        mode = RouteMode.Flight,
                        fromStation = fromStationText,
                        toStation = toStationText,
                        depTime = "09:00",
                        arrTime = "10:15",
                        carrierName = "IndiGo Regional Hop Direct Flight",
                        seatAvailability = "4 Seats Left!",
                        perPersonCost = if (isPune) "₹2,850" else "₹3,600",
                        duration = totalTimeFlight
                    ),
                    transfer = TransferInfo(
                        fromSub = "",
                        toSub = "",
                        walkDistance = "",
                        bufferTime = "",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Flight,
                        fromStation = "",
                        toStation = "",
                        depTime = "",
                        arrTime = "",
                        carrierName = "",
                        seatAvailability = "",
                        perPersonCost = "₹0",
                        duration = ""
                    )
                ),
                RouteOption(
                    id = "option_c",
                    title = "Cheap Direct Route — Express General (2S)",
                    badgeText = "BUDGET PICK",
                    badgeColor = Color(0xFF2ECC71),
                    badgeTextColor = Color.White,
                    totalTime = if (isPune) "3h 30m" else "6h 00m",
                    totalFare = if (isPune) "₹120" else "₹220",
                    comfortScore = 3,
                    score = 80,
                    recommendedFor = "Insanely Economical Direct Rail",
                    leg1 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = fromStationText,
                        toStation = toStationText,
                        depTime = "14:15",
                        arrTime = if (isPune) "17:45" else "20:15",
                        carrierName = if (isPune) "12126 Pragati Express (2S)" else "16526 Bangalore Mail (2S)",
                        seatAvailability = "88 Seats Available",
                        perPersonCost = if (isPune) "₹120" else "₹220",
                        duration = if (isPune) "3h 30m" else "6h 00m"
                    ),
                    transfer = TransferInfo(
                        fromSub = "",
                        toSub = "",
                        walkDistance = "",
                        bufferTime = "",
                        mode = RouteMode.Cab
                    ),
                    leg2 = RouteLeg(
                        mode = RouteMode.Train,
                        fromStation = "",
                        toStation = "",
                        depTime = "",
                        arrTime = "",
                        carrierName = "",
                        seatAvailability = "",
                        perPersonCost = "₹0",
                        duration = ""
                    )
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (activePnr == null) {
            // Explanatory Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("route_not_selected_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Compass Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Station-Hopping Backups",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To find alternative train-bus connections, choose a waitlisted booking from your search history below, or simulate any high-demand Indian Railways corridor instantly:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Search History Selection List
            if (searchHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Select from recent Waitlisted Searches",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }

                items(searchHistory) { pnr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectedPnr.value = pnr }
                            .testTag("history_item_${pnr.pnr}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pnr.trainName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Route: ${pnr.sourceStation} ⇆ ${pnr.destinationStation}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "PNR ${pnr.pnr} · Class: ${pnr.bookingClass} · Status: ${pnr.currentStatus}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Button(
                                onClick = { viewModel.selectedPnr.value = pnr },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("Select", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Trunk Corridor Templates
            item {
                Text(
                    text = "Quick Corridor Choice Simulators",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }

            val corridors = listOf(
                Triple("Delhi ⇆ Mumbai", Pair("NDLS", "MMCT"), "12952 Rajdhani Express"),
                Triple("Pune ⇆ Mumbai", Pair("PUNE", "CSMT"), "12124 Deccan Queen CC"),
                Triple("Howrah ⇆ Delhi", Pair("HWH", "NDLS"), "12301 Howrah Rajdhani"),
                Triple("Varanasi ⇆ Delhi", Pair("BSB", "NDLS"), "12223 Duronto Express"),
                Triple("Chennai ⇆ Bengaluru", Pair("MAS", "SBC"), "12002 Shatabdi Express")
            )

            items(corridors) { (label, stations, trainName) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val mockPnr = PnrEntity(
                                pnr = "9" + (100000000 + java.util.Random().nextInt(900000000)),
                                trainNumber = "12000",
                                trainName = trainName,
                                dateOfJourney = "Tomorrow",
                                sourceStation = stations.first,
                                destinationStation = stations.second,
                                bookingClass = "SL",
                                currentStatus = "WL 12",
                                initialStatus = "WL 24",
                                confirmationProbability = 35,
                                riskLevel = "HIGH",
                                refundEstimate = 200,
                                racProbability = 15,
                                chartPreparedTime = "Departure in 4h",
                                countdownMinutes = 240
                            )
                            viewModel.selectedPnr.value = mockPnr
                        }
                        .testTag("corridor_${stations.first}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Train Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${stations.first} ⇆ ${stations.second} (${trainName})",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Simulate Corridor",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            // Search Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("search_header"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0770E3)),
                    border = BorderStroke(1.dp, Color(0xFF0056B3))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${fromStationText} → ${toStationText}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${journeyDateText} · Multimodal Station-Hopping Router",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.selectedPnr.value = null },
                            modifier = Modifier.testTag("reset_routing_selection_btn").size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Deselect Journey PNR",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Section Title & Skyscanner style sorted tabs
        item {
            val optBest = routeOptions.getOrNull(0)
            val optFastest = routeOptions.getOrNull(1)
            val optCheapest = routeOptions.getOrNull(2)

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("skyscanner_tabs_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cheapest Tab
                    optCheapest?.let { opt ->
                        val isSelected = selectedFilterTab == "cheapest"
                        SkyscannerTabItem(
                            label = "Budgeted",
                            price = opt.totalFare,
                            duration = opt.totalTime,
                            isSelected = isSelected,
                            onClick = {
                                selectedFilterTab = "cheapest"
                                selectedRouteId = opt.id
                                expandedStates[opt.id] = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Best Tab
                    optBest?.let { opt ->
                        val isSelected = selectedFilterTab == "best"
                        SkyscannerTabItem(
                            label = "Comfortable",
                            price = opt.totalFare,
                            duration = opt.totalTime,
                            isSelected = isSelected,
                            onClick = {
                                selectedFilterTab = "best"
                                selectedRouteId = opt.id
                                expandedStates[opt.id] = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fastest Tab
                    optFastest?.let { opt ->
                        val isSelected = selectedFilterTab == "fastest"
                        SkyscannerTabItem(
                            label = "Fastest",
                            price = opt.totalFare,
                            duration = opt.totalTime,
                            isSelected = isSelected,
                            onClick = {
                                selectedFilterTab = "fastest"
                                selectedRouteId = opt.id
                                expandedStates[opt.id] = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = "Alternative Station-Hopping Combinations",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        val orderedOptions = when (selectedFilterTab) {
            "cheapest" -> listOfNotNull(routeOptions.getOrNull(2), routeOptions.getOrNull(0), routeOptions.getOrNull(1))
            "fastest" -> listOfNotNull(routeOptions.getOrNull(1), routeOptions.getOrNull(0), routeOptions.getOrNull(2))
            else -> listOfNotNull(routeOptions.getOrNull(0), routeOptions.getOrNull(1), routeOptions.getOrNull(2))
        }

        // List out dynamic routeOptions
        items(orderedOptions) { option ->
            val isExpanded = expandedStates[option.id] ?: (selectedRouteId == option.id)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        val nextExpanded = !isExpanded
                        expandedStates[option.id] = nextExpanded
                        selectedRouteId = option.id
                        selectedFilterTab = when (option.id) {
                            "option_a" -> "fastest"
                            "option_c" -> "cheapest"
                            else -> "best"
                        }
                    }
                    .testTag("route_card_${option.id}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedRouteId == option.id) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Column {
                    // Bleed Image banner matching transport mode
                    val localImage = option.imageUrl ?: when (option.id) {
                        "option_a" -> "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=600&q=80" // Flight
                        "option_b" -> "https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&w=600&q=80" // Train
                        else -> "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80" // Bus
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    ) {
                        AsyncImage(
                            model = localImage,
                            contentDescription = option.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .background(option.badgeColor.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = option.badgeText,
                                color = option.badgeTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title Row and Expand Arrow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand details",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual Journey timeline stepper
                        JourneyStepperLine(option.leg1, option.leg2, option.transfer)

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Summary Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDirect = option.leg2.perPersonCost == "₹0" || option.leg2.carrierName.isEmpty()
                        Column {
                            Text(
                                text = "Fare/Cost",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = if (isDirect) "${option.totalFare} Direct" else "${option.totalFare} (${option.leg1.perPersonCost} Train + ${option.leg2.perPersonCost} Bus)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Duration",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = option.totalTime,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Comfort Rating",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "★".repeat(option.comfortScore) + "☆".repeat(5 - option.comfortScore),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB300)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val isDirectRoute = option.leg2.perPersonCost == "₹0" || option.leg2.carrierName.isEmpty()

                    if (isDirectRoute) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SECURE BOOKING RATE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = option.totalFare,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            if (!isExpanded) {
                                Button(
                                    onClick = {
                                        activeRouteOptionForBookingLeg = Triple(option, option.leg1, "DIRECT")
                                    },
                                    modifier = Modifier.testTag("route_payment_quick_${option.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CyanAccent,
                                        contentColor = NavyPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Choose Booking Site",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CHOOSE BOOKING SITE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    } else {
                        // Hybrid Split Payment Layout
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "COMBINED FARE RATE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = option.totalFare,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "Split-Leg Safe Tickets",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (!isExpanded) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Leg 1 Payment Button
                                    Button(
                                        onClick = {
                                            activeRouteOptionForBookingLeg = Triple(option, option.leg1, "LEG 1")
                                        },
                                        modifier = Modifier.weight(1f).testTag("route_payment_quick_leg1_${option.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "Book ${option.leg1.mode.name.uppercase()} [${option.leg1.perPersonCost}]",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Leg 2 Payment Button
                                    Button(
                                        onClick = {
                                            activeRouteOptionForBookingLeg = Triple(option, option.leg2, "LEG 2")
                                        },
                                        modifier = Modifier.weight(1f).testTag("route_payment_quick_leg2_${option.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ColorSuccess,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "Book ${option.leg2.mode.name.uppercase()} [${option.leg2.perPersonCost}]",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Expandable Detail Panel
                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            val isDirect = option.leg2.perPersonCost == "₹0" || option.leg2.carrierName.isEmpty()
                            if (isDirect) {
                                val modeEmojiAndName = when (option.leg1.mode) {
                                    RouteMode.Train -> "🚂 Direct Train Journey"
                                    RouteMode.Bus -> "🚌 Direct Bus Journey"
                                    RouteMode.Flight -> "✈️ Direct Flight Journey"
                                    else -> "🚗 Direct Journey"
                                }
                                Text(
                                    text = modeEmojiAndName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${option.leg1.carrierName}  •  ${option.leg1.fromStation} → ${option.leg1.toStation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ticket Cost: ${option.leg1.perPersonCost} / person",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Status: ${option.leg1.seatAvailability}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorSuccess
                                    )
                                }
                            } else {
                                // Leg 1 detail
                                Text(
                                    text = "🚂 Leg 1: Train Journey",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${option.leg1.carrierName}  •  ${option.leg1.fromStation} → ${option.leg1.toStation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Train Ticket Cost: ${option.leg1.perPersonCost} / person",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Status: ${option.leg1.seatAvailability}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorSuccess
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Intermediate Connection Transfer Detail
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (option.id == "option_a") Icons.Default.Place else Icons.Default.Person,
                                            contentDescription = "Transfer indicator",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Transfer: ${option.leg1.toStation} → ${option.leg2.fromStation}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Distance: ${option.transfer.walkDistance}  |  Buffer Time: ${option.transfer.bufferTime}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Leg 2 detail
                                Text(
                                    text = "🚌 Leg 2: Bus Transfer",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${option.leg2.carrierName}  •  ${option.leg2.fromStation} → ${option.leg2.toStation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Bus Ticket Cost: ${option.leg2.perPersonCost} / person",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Status: ${option.leg2.seatAvailability}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ColorSuccess
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Spacer(modifier = Modifier.height(12.dp))

                            // Official Portal Redirect / Booking card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("route_official_book_card_${option.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                ),
                                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "🔒 OFFICIAL BOOKING CORRIDOR",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = CyanAccent
                                            )
                                            Text(
                                                text = "Secure Route Booking",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = option.totalFare,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    val isDirect = option.leg2.perPersonCost == "₹0" || option.leg2.carrierName.isEmpty()
                                    if (isDirect) {
                                        val siteUrl = when (option.leg1.mode) {
                                            RouteMode.Train -> "https://www.irctc.co.in/nget/train-search"
                                            RouteMode.Bus -> "https://www.redbus.in/"
                                            RouteMode.Flight -> "https://www.goindigo.in/"
                                            else -> "https://www.irctc.co.in/"
                                        }
                                        val portalName = when (option.leg1.mode) {
                                            RouteMode.Train -> "IRCTC Official Rail Portal"
                                            RouteMode.Bus -> "redBus Coach Station"
                                            RouteMode.Flight -> "IndiGo Aviation Hub"
                                            else -> "General Travel Desk"
                                        }

                                        Text(
                                            text = "Book this entire direct route on the provider's official portal at the absolute lowest fare with zero platform surcharges.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 15.sp
                                        )

                                        Button(
                                            onClick = {
                                                activeRouteOptionForBookingLeg = Triple(option, option.leg1, "DIRECT")
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("route_payment_direct_${option.id}"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Secure Check", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("CHOOSE BOOKING SITE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    } else {
                                        // Hybrid segmented checkout
                                        Text(
                                            text = "This is a Smart Hybrid route combining consecutive segments. Select a leg below to compare verified booking partners and reserve ticket spaces securely:",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 15.sp
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Leg 1: Train
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("TRAIN SEGMENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                    Text(option.leg1.carrierName.substringBefore(" ("), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text("Fares: ${option.leg1.perPersonCost}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Button(
                                                    onClick = {
                                                        activeRouteOptionForBookingLeg = Triple(option, option.leg1, "LEG 1")
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Choose Booking Site", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // Leg 2: Bus
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("BUS SEGMENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ColorSuccess)
                                                    Text(option.leg2.carrierName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text("Fares: ${option.leg2.perPersonCost}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Button(
                                                    onClick = {
                                                        activeRouteOptionForBookingLeg = Triple(option, option.leg2, "LEG 2")
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(30.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess)
                                                ) {
                                                    Text("Choose Booking Site", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Informational notice pointing to Booker screen rather than direct checkout
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "ℹ️ Backup station-hopping combinations are dynamically calculated. Tap any segment above to view details, or secure tickets on the official portals listed above.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Comparison Bar / Matrix title
        item {
            Text(
                text = "Inter-modal Comparison Grid",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Matrix Comparison Row Table
        item {
            RouteComparisonTable(routeOptions)
        }
    }

    // Split Checkout Dialog for Hybrid modes
    if (false) {
        val option = showCheckoutOption!!
        Dialog(
            onDismissRequest = {
                if (!isTrainPaying && !isBusPaying) {
                    showCheckoutOption = null
                }
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp)
                    .testTag("split_checkout_dialog"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, CyanAccent.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (finalizedPnrEntity != null) "Combination Booked" else "Hybrid Split Checkout",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!isTrainPaying && !isBusPaying) {
                            IconButton(onClick = { showCheckoutOption = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Checkout")
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    if (finalizedPnrEntity != null) {
                        // SUCCESS BOOKING STATE (E-TICKET DETAILS WITH DOUBLE QR)
                        val pnrEntity = finalizedPnrEntity!!

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(ColorSuccess.copy(alpha = 0.15f), RoundedCornerShape(30.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🟢", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HYBRID COMBINATION CONFIRMED",
                                        color = ColorSuccess,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // E-Ticket Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // PNR details
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("SHARED PNR NUMBER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                                            Text(pnrEntity.pnr, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("JOURNEY DATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                            Text(journeyDateText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                                    // 2 Leg status details
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("TRAIN CONFIRMED [LEG 1]", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text(option.leg1.carrierName, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                            Text("Coach S1, Seat 14 (LB)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                            Text("BUS CONFIRMED [LEG 2]", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ColorSuccess)
                                            Text(option.leg2.carrierName, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                            Text("Seat No 12 (Sleeper)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    // Display Double QRs Side-by-Side
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            SimulatedQrCode(modifier = Modifier.size(56.dp), color = Color.Black)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("Train pass code", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(50.dp)
                                                .background(Color.LightGray)
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            SimulatedQrCode(modifier = Modifier.size(56.dp), color = Color.Black)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text("Bus voucher pass", fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    showCheckoutOption = null
                                    viewModel.selectTab(Tab.CheckPnr)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("track_hybrid_pnr_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyPrimary)
                            ) {
                                Text("Track Hybrid Journey", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    } else {
                        // FORM INPUT & SPLIT CHECKOUT DETAILS
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Route Details Info Banner
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = option.title,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Combination: ${fromStationText} → ${midStation} (Train) then ${toStationText} (Bus)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Total integrated fare: ${option.totalFare} (GST and bookings fee inclusive)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorSuccess
                                )
                            }

                            // Passenger full detail inputs
                            OutlinedTextField(
                                value = checkoutPassengerName,
                                onValueChange = { checkoutPassengerName = it },
                                label = { Text("Passenger Full Name") },
                                placeholder = { Text("e.g. Anjali Sharma") },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("split_checkout_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = checkoutPassengerAge,
                                    onValueChange = { checkoutPassengerAge = it },
                                    label = { Text("Age") },
                                    placeholder = { Text("21") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.8f).testTag("split_checkout_age"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                OutlinedTextField(
                                    value = checkoutPassengerMobile,
                                    onValueChange = { checkoutPassengerMobile = it },
                                    label = { Text("Contact No") },
                                    placeholder = { Text("e.g. 9812345670") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.5f).testTag("split_checkout_mobile"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            // Split Payments Layout Section
                            Text(
                                text = "Split Payments Schedule (Hybrid Mode Fares)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            // Part 1: Train protection Booking
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isTrainPaid) ColorSuccess.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isTrainPaid) ColorSuccess else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🚂 Part 1:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${option.leg1.carrierName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(
                                            text = if (isTrainPaid) "🔵 PAID" else "🕒 PENDING",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = if (isTrainPaid) ColorSuccess else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    Text(
                                        text = "Route Segment: ${fromStationText} → ${midStation} (Train) | Cost: ${option.leg1.perPersonCost}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!isTrainPaid) {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    isTrainPaying = true
                                                    delay(1200)
                                                    isTrainPaid = true
                                                    isTrainPaying = false
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("pay_train_leg_btn"),
                                            enabled = !isTrainPaying,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            if (isTrainPaying) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp)
                                            } else {
                                                Text("Pay Train Segment Fares [${option.leg1.perPersonCost}]", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Part 2: Bus Transfer Booking
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isBusPaid) ColorSuccess.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isBusPaid) ColorSuccess else if (isTrainPaid) ColorSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🚌 Part 2:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${option.leg2.carrierName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(
                                            text = if (isBusPaid) "🔵 PAID" else if (isTrainPaid) "🕒 PENDING" else "🔒 LOCKED",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = if (isBusPaid) ColorSuccess else if (isTrainPaid) ColorSuccess else MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Text(
                                        text = "Route Segment: ${midDepot} → ${toStationText} (Bus) | Cost: ${option.leg2.perPersonCost}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!isBusPaid) {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    isBusPaying = true
                                                    delay(1200)
                                                    isBusPaid = true
                                                    isBusPaying = false
                                                    
                                                    // Complete combination, create confirmed PNR record
                                                    val finalPassenger = if (checkoutPassengerName.isBlank()) "Anjali Sharma" else checkoutPassengerName
                                                    val randPnr = "4" + (100000000 + java.util.Random().nextInt(900000000))
                                                    val entity = PnrEntity(
                                                        pnr = randPnr,
                                                        trainNumber = "COMBINED",
                                                        trainName = option.title,
                                                        dateOfJourney = journeyDateText,
                                                        sourceStation = fromStationText,
                                                        destinationStation = toStationText,
                                                        bookingClass = "Premium Hybrid (Train+Bus)",
                                                        currentStatus = "CNF (Train+Bus Paid)",
                                                        initialStatus = "CNF",
                                                        confirmationProbability = 100,
                                                        riskLevel = "SAFE",
                                                        refundEstimate = 0,
                                                        racProbability = 0,
                                                        chartPreparedTime = "Departure in 2h 45m",
                                                        countdownMinutes = 120L
                                                    )
                                                    viewModel.addConfirmedBooking(entity)
                                                    finalizedPnrEntity = entity
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().testTag("pay_bus_leg_btn"),
                                            enabled = isTrainPaid && !isBusPaying,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess)
                                        ) {
                                            if (isBusPaying) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp)
                                            } else {
                                                Text(
                                                    text = if (isTrainPaid) "Pay Bus Segment Fares [${option.leg2.perPersonCost}]" else "🔒 Complete Train Payment First",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Intelligent Skyscanner Route Booking site selector dialog
    if (activeRouteOptionForBookingLeg != null) {
        val (option, leg, legLabel) = activeRouteOptionForBookingLeg!!
        Dialog(
            onDismissRequest = { activeRouteOptionForBookingLeg = null }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp)
                    .testTag("route_booking_site_dialog_${leg.mode.name}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Choose Booking Site",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "For $legLabel: ${leg.mode.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = { activeRouteOptionForBookingLeg = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Dialog",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    Text(
                        text = "We aggregated fares for ${leg.carrierName} in real-time. Select an official provider or trusted partner to complete your secure ticket reservation:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    // Partners of this transport mode type
                    val costNum = leg.perPersonCost.replace("₹", "").replace(",", "").trim().toIntOrNull() ?: 1200
                    val partners = when (leg.mode) {
                        RouteMode.Flight -> listOf(
                            Triple("IndiGo Airways", "₹" + (costNum - 120), "Direct discount partner checkout."),
                            Triple("Air India Direct", "₹" + costNum, "Full airline service with premium catering."),
                            Triple("MakeMyTrip Aviation", "₹" + (costNum + 80), "Partner protection and instant receipts.")
                        )
                        RouteMode.Train -> listOf(
                            Triple("IRCTC Official Direct", "₹" + (costNum - 40), "No processing fees, official confirmation."),
                            Triple("ConfirmTkt Rail Sync", "₹" + costNum, "Advanced WL prediction backup protection."),
                            Triple("MakeMyTrip Rail Link", "₹" + (costNum + 30), "Guaranteed coach refund assistance.")
                        )
                        RouteMode.Bus -> listOf(
                            Triple("redBus India Direct", "₹" + (costNum - 15), "Luxury multi-axle Volvo partner route link."),
                            Triple("AbhiBus Connect", "₹" + costNum, "Instant seat-map integration and dynamic alerts."),
                            Triple("Zingbus Premium Care", "₹" + (costNum + 20), "Complementary premium climate lounge access.")
                        )
                        else -> listOf(
                            Triple("Uber Intercity", "₹" + costNum, "Professional doorstep transit dispatch."),
                            Triple("Ola Outstation Sync", "₹" + (costNum + 15), "Instant cashless payment and tracking.")
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        partners.forEach { (partnerName, partnerPrice, description) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeRouteOptionForBookingLeg = null
                                        val siteUrl = when (leg.mode) {
                                            RouteMode.Train -> "https://www.irctc.co.in/nget/train-search"
                                            RouteMode.Bus -> "https://www.redbus.in/"
                                            RouteMode.Flight -> "https://www.goindigo.in/"
                                            else -> "https://www.uber.com/in"
                                        }
                                        val webUri = Uri.parse(siteUrl)
                                        val bookIntent = Intent(Intent.ACTION_VIEW, webUri)
                                        context.startActivity(bookIntent)
                                        Toast.makeText(context, "Opening safe channel on $partnerName...", Toast.LENGTH_LONG).show()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = partnerName,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Verified Partner Site",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = partnerPrice,
                                                fontWeight = FontWeight.Black,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Fare inclusive of taxes",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = description,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Book with " + partnerName,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Route data classes
data class RouteOption(
    val id: String,
    val title: String,
    val badgeText: String,
    val badgeColor: Color,
    val badgeTextColor: Color,
    val totalTime: String,
    val totalFare: String,
    val comfortScore: Int,
    val score: Int,
    val recommendedFor: String,
    val leg1: RouteLeg,
    val transfer: TransferInfo,
    val leg2: RouteLeg,
    val imageUrl: String? = null
)

data class RouteLeg(
    val mode: RouteMode,
    val fromStation: String,
    val toStation: String,
    val depTime: String,
    val arrTime: String,
    val carrierName: String,
    val seatAvailability: String,
    val perPersonCost: String,
    val duration: String
)

data class TransferInfo(
    val fromSub: String,
    val toSub: String,
    val walkDistance: String,
    val bufferTime: String,
    val mode: RouteMode
)

enum class RouteMode {
    Train, Bus, Flight, Cab
}

fun getModeEmoji(mode: RouteMode): String = when (mode) {
    RouteMode.Train -> "🚂"
    RouteMode.Bus -> "🚌"
    RouteMode.Flight -> "✈️"
    RouteMode.Cab -> "🚖"
}

fun getModeIcon(mode: RouteMode): androidx.compose.ui.graphics.vector.ImageVector = when (mode) {
    RouteMode.Train -> Icons.Default.Place
    RouteMode.Bus -> Icons.Default.LocationOn
    RouteMode.Flight -> Icons.Default.Send
    RouteMode.Cab -> Icons.Default.Star
}

@Composable
fun SkyscannerTabItem(
    label: String,
    price: String,
    duration: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = price,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = duration,
                fontSize = 10.sp,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Horizontal stepper custom sub-view
@Composable
fun JourneyStepperLine(leg1: RouteLeg, leg2: RouteLeg, transfer: TransferInfo) {
    val isDirect = leg2.perPersonCost == "₹0" || leg2.carrierName.isEmpty()

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDirect) {
            // --- DIRECT ROUTE (2 NODES, 1 CONNECTOR) ---
            // Node 1: Start
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(max = 85.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = leg1.fromStation,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = leg1.depTime,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Connector
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getModeIcon(leg1.mode),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = leg1.duration,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                }
            }

            // Node 2: Destination
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(max = 85.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(ColorSuccess, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = leg1.toStation,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = leg1.arrTime,
                    fontSize = 9.sp,
                    color = ColorSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // --- HYBRID ROUTE (3 NODES, 2 CONNECTORS) ---
            // Stop Node 1: Start
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(max = 70.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = leg1.fromStation,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = leg1.depTime,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Connector Leg 1 Line
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getModeIcon(leg1.mode),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = leg1.duration,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                }
            }

            // Stop Node 2: Transfer Terminal
            val midLabel = if (leg1.toStation.isNotEmpty()) leg1.toStation.split(" ")[0] else "Mid"
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(max = 75.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ColorWarning, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = midLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${leg1.arrTime} / ${leg2.depTime}",
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Connector Leg 2 Line
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = getModeIcon(leg2.mode),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = leg2.duration,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                }
            }

            // Stop Node 3: End
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.widthIn(max = 70.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(ColorSuccess, CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (leg2.toStation.isNotEmpty()) leg2.toStation else "End",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = leg2.arrTime,
                    fontSize = 9.sp,
                    color = ColorSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Route Comparison Matrix Table Custom Composable
@Composable
fun RouteComparisonTable(options: List<RouteOption>) {
    val optionB = options.getOrNull(0)
    val optionA = options.getOrNull(1)
    val optionC = options.getOrNull(2)

    Card(
        modifier = Modifier.fillMaxWidth().testTag("comparison_matrix"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Inter-modal Performance Analysis",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Headers Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Travel Metrics",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = optionB?.badgeText ?: "Most Comf",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A90D9)
                )
                Text(
                    text = optionA?.badgeText ?: "Fastest",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300)
                )
                Text(
                    text = optionC?.badgeText ?: "Budget",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2ECC71)
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

            // Row: Total Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Time",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option B
                Text(
                    text = optionB?.totalTime ?: "N/A",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option A - Fastest Highlighted
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFB300).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${optionA?.totalTime ?: "N/A"} ⚡",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC48B00)
                    )
                }
                // Option C
                Text(
                    text = optionC?.totalTime ?: "N/A",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Row: Total Cost
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Cost",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option B
                Text(
                    text = optionB?.totalFare ?: "N/A",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option A
                Text(
                    text = optionA?.totalFare ?: "N/A",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option C - Best Price Highlighted
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF2ECC71).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${optionC?.totalFare ?: "N/A"} 💎",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E8449)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Row: Comfort
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comfort",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option B - Comfort Highlighted
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF4A90D9).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "★".repeat(optionB?.comfortScore ?: 5) + "☆".repeat(5 - (optionB?.comfortScore ?: 5)) + " ⭐",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B62AB)
                    )
                }
                // Option A
                Text(
                    text = "★".repeat(optionA?.comfortScore ?: 4) + "☆".repeat(5 - (optionA?.comfortScore ?: 4)),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option C
                Text(
                    text = "★".repeat(optionC?.comfortScore ?: 3) + "☆".repeat(5 - (optionC?.comfortScore ?: 3)),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Row: Connections
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connections",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option B
                val isBDirect = (optionB?.leg2?.perPersonCost ?: "₹0") == "₹0"
                val bLoc = if (optionB?.leg1?.toStation?.contains(" ") == true) optionB.leg1.toStation.split(" ")[0] else optionB?.leg1?.toStation ?: ""
                Text(
                    text = if (isBDirect) "Direct\nJourney" else "1 Connection\n($bLoc)",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option A
                val isADirect = (optionA?.leg2?.perPersonCost ?: "₹0") == "₹0"
                val aLoc = if (optionA?.leg1?.toStation?.contains(" ") == true) optionA.leg1.toStation.split(" ")[0] else optionA?.leg1?.toStation ?: ""
                Text(
                    text = if (isADirect) "Direct\nJourney" else "1 Connection\n($aLoc)",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option C
                val isCDirect = (optionC?.leg2?.perPersonCost ?: "₹0") == "₹0"
                val cLoc = if (optionC?.leg1?.toStation?.contains(" ") == true) optionC.leg1.toStation.split(" ")[0] else optionC?.leg1?.toStation ?: ""
                Text(
                    text = if (isCDirect) "Direct\nJourney" else "1 Connection\n($cLoc)",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    lineHeight = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Row: Recommended For
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Best For",
                    modifier = Modifier.width(85.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Option B
                Text(
                    text = optionB?.recommendedFor?.replace(" ", "\n") ?: "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B62AB)
                )
                // Option A
                Text(
                    text = optionA?.recommendedFor?.replace(" ", "\n") ?: "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC48B00)
                )
                // Option C
                Text(
                    text = optionC?.recommendedFor?.replace(" ", "\n") ?: "",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E8449)
                )
            }
        }
    }
}

@Composable
fun AiAssistantScreen(viewModel: MainViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiTyping by viewModel.isAiTyping.collectAsState()
    val monitoredPnr by viewModel.selectedPnr.collectAsState()
    val lazyListState = rememberLazyListState()
    var inputMsg by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Keep chat scrolled down when new inputs arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header Status Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(CyanAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("AI Travel Agent", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = if (monitoredPnr != null) "Monitoring PNR ${monitoredPnr!!.pnr} (WL status)" else "Active Risk Analyzer Connected",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                if (monitoredPnr != null) {
                    Box(
                        modifier = Modifier
                            .background(ColorWarning.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "RISK: ${monitoredPnr!!.riskLevel}",
                            color = ColorWarning,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Chat Feeds
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                val alignHorizontal = if (msg.isUser) Alignment.End else Alignment.Start
                val cardColor = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                val textColor = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignHorizontal
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isUser) 16.dp else 2.dp,
                            bottomEnd = if (msg.isUser) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier.widthIn(max = 280.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 19.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (msg.isUser) "You" else "ResQRail Assistant",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }

            if (isAiTyping) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = CyanAccent, modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agent is checking backup seats...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            }
        }

        // Quick Action Chips Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Check Alternatives", "Track PNR", "Cheapest Route", "Fastest Route").forEach { prompt ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.clickable {
                        inputMsg = prompt
                        viewModel.sendUserChatMessage(prompt)
                        inputMsg = ""
                    }
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Input bottom tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .windowInsetsPadding(WindowInsets.ime),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMsg,
                onValueChange = { inputMsg = it },
                placeholder = { Text("Ask anything about your journey...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputMsg.trim().isNotEmpty()) {
                        viewModel.sendUserChatMessage(inputMsg)
                        inputMsg = ""
                        keyboardController?.hide()
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (inputMsg.trim().isNotEmpty()) {
                        viewModel.sendUserChatMessage(inputMsg)
                        inputMsg = ""
                        keyboardController?.hide()
                    }
                },
                enabled = inputMsg.trim().isNotEmpty(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (inputMsg.trim().isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .testTag("chat_send_button")
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputMsg.trim().isNotEmpty()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val loggedInName by viewModel.loggedInName.collectAsState()
    val loggedInEmail by viewModel.loggedInEmail.collectAsState()
    var notificationsActive by remember { mutableStateOf(true) }
    var persistentMonitorSetting by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF0B1521) else Color(0xFFF5F7FA)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Skyscanner Profile Header Banner (Full Bleed Blue Gradient)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkTheme) {
                                listOf(Color(0xFF0C2340), Color(0xFF04142F))
                            } else {
                                listOf(Color(0xFF0770E3), Color(0xFF0256B4))
                            }
                        )
                    )
                    .padding(top = 32.dp, bottom = 36.dp, start = 20.dp, end = 20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Account",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val initialLetter = if (loggedInName.isNotEmpty()) loggedInName.trim().take(1).uppercase() else "P"
                            Text(
                                text = initialLetter,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            val displayName = if (loggedInName.isNotEmpty()) loggedInName else "Premium Passenger"
                            val displayEmail = if (loggedInEmail.isNotEmpty()) loggedInEmail else "active@resqrail.com"
                            Text(
                                text = displayName,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = displayEmail,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Skyscanner Section: Settings & System Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "APP CONFIGURATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color(0xFF89D6E8) else Color(0xFF64748B),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF132235) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 1.dp),
                    border = if (isDarkTheme) BorderStroke(1.dp, Color(0xFF1E3554)) else BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column {
                        // Priority Queue Tracker
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Active Waitlist Tracker",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Direct deep lookups into relative RAC allocations",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = persistentMonitorSetting,
                                onCheckedChange = { persistentMonitorSetting = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0770E3),
                                    uncheckedThumbColor = Color(0xFF9CA3AF),
                                    uncheckedTrackColor = Color(0xFFE5E7EB)
                                )
                            )
                        }

                        HorizontalDivider(
                            color = if (isDarkTheme) Color(0xFF1E3554) else Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        // Push Alerts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Real-time Push Alerts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Instant update alerts for priority queue shifts",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = notificationsActive,
                                onCheckedChange = { notificationsActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0770E3),
                                    uncheckedThumbColor = Color(0xFF9CA3AF),
                                    uncheckedTrackColor = Color(0xFFE5E7EB)
                                )
                            )
                        }

                        HorizontalDivider(
                            color = if (isDarkTheme) Color(0xFF1E3554) else Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        // Dark Theme Toggle Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dark Theme Landscape",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                                )
                                Text(
                                    text = "Toggle elegant high-contrast dark style backdrop",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { viewModel.toggleDarkTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0770E3),
                                    uncheckedThumbColor = Color(0xFF9CA3AF),
                                    uncheckedTrackColor = Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier.testTag("theme_switch")
                            )
                        }

                        HorizontalDivider(
                            color = if (isDarkTheme) Color(0xFF1E3554) else Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        // Theme Mode Buttons Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Theme Style Quick Select",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isDarkTheme) Color.White else Color(0xFF1F2937)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { if (!isDarkTheme) viewModel.toggleDarkTheme() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkTheme) Color(0xFF0770E3) else Color(0xFFF3F4F6),
                                        contentColor = if (isDarkTheme) Color.White else Color(0xFF374151)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("theme_dark_button")
                                ) {
                                    Text("Dark Theme", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { if (isDarkTheme) viewModel.toggleDarkTheme() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isDarkTheme) Color(0xFF0770E3) else Color(0xFFF3F4F6),
                                        contentColor = if (!isDarkTheme) Color.White else Color(0xFF374151)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("theme_light_button")
                                ) {
                                    Text("Light Theme", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Skyscanner style: Direct Bookers & Official Partners Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "OFFICIAL DIRECT REDIRECT PARTNERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color(0xFF89D6E8) else Color(0xFF64748B),
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val partners = listOf(
                    Pair("IRCTC Rail", "https://www.irctc.co.in/"),
                    Pair("redBus Cabin", "https://www.redbus.in/"),
                    Pair("AbhiBus Connect", "https://www.abhibus.com/"),
                    Pair("IndiGo flight", "https://www.goindigo.in/"),
                    Pair("Air India Connect", "https://www.airindia.com/"),
                    Pair("MakeMyTrip travel", "https://www.makemytrip.com/"),
                    Pair("Goibibo routes", "https://www.goibibo.com/")
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF132235) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 1.dp),
                    border = if (isDarkTheme) BorderStroke(1.dp, Color(0xFF1E3554)) else BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        partners.chunked(2).forEach { rowPartners ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowPartners.forEach { partner ->
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(partner.second))
                                                context.startActivity(intent)
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF0770E3).copy(alpha = 0.08f)
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFF0770E3).copy(alpha = 0.25f)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingCart,
                                                contentDescription = partner.first,
                                                tint = Color(0xFF0770E3),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = partner.first,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isDarkTheme) Color.White else Color(0xFF1F2937),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                if (rowPartners.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Skyscanner style: App Details Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF0E1A29) else Color(0xFFF1F5F9)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = Color(0xFF0770E3),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "About ResQRail Version 2.4",
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Intelligent travel preservation framework built specifically for Indian Railway passengers. Tracks Waitlist (WL) confirmation parameters in real-time, alerts on high risk compilation milestones, and aggregates luxury coach and domestic air routes sequentially inside a fluid M3 container layout.",
                            fontSize = 11.sp,
                            color = if (isDarkTheme) Color.LightGray else Color(0xFF475569),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketTypeBadge(emoji: String, colorAccent: Color) {
    val (text, icon) = when (emoji) {
        "🚌", "BUS", "Bus" -> Pair("COACH", Icons.Default.LocationOn)
        "🚐", "Bus Express" -> Pair("BUS", Icons.Default.LocationOn)
        "✈️", "FLIGHT", "Flight" -> Pair("FLIGHT", Icons.Default.Send)
        "🚂", "TRAIN", "Train" -> Pair("TRAIN", Icons.Default.Place)
        "🌟", "COMBINED" -> Pair("COMBINED", Icons.Default.Star)
        else -> Pair("TRANSIT", Icons.Default.Place)
    }
    Box(
        modifier = Modifier
            .background(colorAccent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .border(1.dp, colorAccent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorAccent,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = text,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = colorAccent
            )
        }
    }
}

data class ExploreDestinationItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val dealPrice: String,
    val transportType: String,
    val fromCity: String,
    val toCity: String,
    val imageUrl: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookTicketScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var fromStation by remember { mutableStateOf("New Delhi (NDLS)") }
    var toStation by remember { mutableStateOf("Mumbai Central (MMCT)") }
    var travelDate by remember { mutableStateOf("Tomorrow") }
    var serviceClass by remember { mutableStateOf("3A - AC 3 Tier") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Transport Category Selectors
    var selectedTransport by remember { mutableStateOf("Train") } // "Train", "Bus", "Flight"
    var activeCategoryFilter by remember { mutableStateOf("All Deals") }

    val categories = remember {
        listOf("All Deals", "Beach Escapes", "City Breaks", "Nature Wonders", "Popular Deals", "Weekend Trips")
    }

    val exploreDestinations = remember {
        listOf(
            ExploreDestinationItem(
                id = "goa",
                title = "Delhi to Goa",
                subtitle = "Sunny beaches, warm waves, and heritage churches",
                dealPrice = "₹2,050",
                transportType = "Train",
                fromCity = "New Delhi (NDLS)",
                toCity = "Goa Madgaon (MAO)",
                imageUrl = "https://images.unsplash.com/photo-1519046904884-53103b34b206?auto=format&fit=crop&w=600&q=80",
                category = "Beach Escapes"
            ),
            ExploreDestinationItem(
                id = "mumbai",
                title = "Delhi to Mumbai",
                subtitle = "Dynamic markets, financial heartbeat, and Marine Drive",
                dealPrice = "₹1,180",
                transportType = "Bus",
                fromCity = "Delhi ISBT Kashmere Gate",
                toCity = "Mumbai Central Depot",
                imageUrl = "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=600&q=80",
                category = "City Breaks"
            ),
            ExploreDestinationItem(
                id = "srinagar",
                title = "Delhi to Kashmir (Srinagar)",
                subtitle = "Scenic lakes, pristine pine forests, and snowy peaks",
                dealPrice = "₹4,200",
                transportType = "Flight",
                fromCity = "DEL - Indira Gandhi Airport",
                toCity = "SXI - Srinagar Airport",
                imageUrl = "https://images.unsplash.com/photo-1583143874828-de3d288be51a?auto=format&fit=crop&w=600&q=80",
                category = "Nature Wonders"
            ),
            ExploreDestinationItem(
                id = "kochi",
                title = "Mumbai to Kochi",
                subtitle = "Scenic backwaters, coconut groves, and Chinese fishing nets",
                dealPrice = "₹3,100",
                transportType = "Flight",
                fromCity = "BOM - Chhatrapati Shivaji",
                toCity = "COK - Cochin Airport",
                imageUrl = "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=600&q=80",
                category = "Beach Escapes"
            ),
            ExploreDestinationItem(
                id = "varanasi",
                title = "Bengaluru to Varanasi",
                subtitle = "Ganga Aarti, ancient spiritual heritage, and historic ghats",
                dealPrice = "₹4,900",
                transportType = "Flight",
                fromCity = "BLR - Kempegowda Airport",
                toCity = "VNS - Lal Bahadur Shastri",
                imageUrl = "https://images.unsplash.com/photo-1561361531-99e2277af963?auto=format&fit=crop&w=600&q=80",
                category = "Popular Deals"
            ),
            ExploreDestinationItem(
                id = "jaipur",
                title = "Delhi to Jaipur",
                subtitle = "Palaces, traditional handicrafts, and rich Rajputana heritage",
                dealPrice = "₹920",
                transportType = "Bus",
                fromCity = "Delhi ISBT Kashmere Gate",
                toCity = "Sindhi Camp Jaipur",
                imageUrl = "https://images.unsplash.com/photo-1477584308802-e9c2400213bc?auto=format&fit=crop&w=600&q=80",
                category = "Weekend Trips"
            ),
            ExploreDestinationItem(
                id = "manali",
                title = "Delhi to Manali",
                subtitle = "Snowy peaks, adventure sports, and scenic Solang Valley",
                dealPrice = "₹1,440",
                transportType = "Bus",
                fromCity = "Delhi ISBT Kashmere Gate",
                toCity = "Manali Private Bus Stand",
                imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80",
                category = "Nature Wonders"
            )
        )
    }
    var busClassPreferred by remember { mutableStateOf("AC Sleeper") }
    var flightClassPreferred by remember { mutableStateOf("Economy") }
    var flightPassengersCount by remember { mutableStateOf("1 Passenger") }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                            val formattedDate = sdf.format(java.util.Date(selectedDateMillis))
                            travelDate = formattedDate
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    headlineContentColor = MaterialTheme.colorScheme.onSurface,
                    weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    dayContentColor = MaterialTheme.colorScheme.onSurface,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                    todayContentColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
    
    // Direct Booking overlay states
    var showBookingSiteDialogByDest by remember { mutableStateOf<ExploreDestinationItem?>(null) }
    var activeBookingTicket by remember { mutableStateOf<BookableTicket?>(null) }
    var passengerName by remember { mutableStateOf("") }
    var passengerAge by remember { mutableStateOf("") }
    var mobileNo by remember { mutableStateOf("") }
    var seatPreference by remember { mutableStateOf("Window Seat") }
    var selectedPaymentMethod by remember { mutableStateOf("UPI / GPay") }
    
    // Processing / Status states
    var isBookingProcessing by remember { mutableStateOf(false) }
    var bookingProcessingStep by remember { mutableStateOf("") }
    var confirmedPnrEntity by remember { mutableStateOf<PnrEntity?>(null) }

    // Live multi-modal tickets structure
    val tickets = remember {
        listOf(
            BookableTicket(
                id = "irctc_exp",
                portalName = "IRCTC Official Rail",
                serviceName = "12952 Rajdhani Express (Premium)",
                type = "Train Booking",
                price = "₹2,050",
                availability = "8 Seats Available",
                timing = "16:55 → 08:35 (+1 day)",
                duration = "15h 40m",
                siteUrl = "https://www.irctc.co.in/nget/train-search",
                colorAccent = Color(0xFFFF9933),
                emoji = "🚂",
                testTag = "book_irctc_direct"
            ),
            BookableTicket(
                id = "redbus_volvo",
                portalName = "redBus Coach",
                serviceName = "Intercity SmartBus Multi-Axle",
                type = "AC Sleeper Bus",
                price = "₹1,180",
                availability = "15 Seats Left",
                timing = "18:00 → 11:30 (+1 day)",
                duration = "17h 30m",
                siteUrl = "https://www.redbus.in/",
                colorAccent = Color(0xFFD84E55),
                emoji = "🚌",
                testTag = "book_redbus_direct"
            ),
            BookableTicket(
                id = "abhibus_lux",
                portalName = "AbhiBus Connect",
                serviceName = "GSRTC Volvo Club Class",
                type = "AC Seater Bus",
                price = "₹920",
                availability = "20 Seats Left",
                timing = "19:15 → 13:00 (+1 day)",
                duration = "17h 45m",
                siteUrl = "https://www.abhibus.com/",
                colorAccent = Color(0xFF0078FF),
                emoji = "🚐",
                testTag = "book_abhibus_direct"
            ),
            BookableTicket(
                id = "indigo_direct",
                portalName = "IndiGo Aviation",
                serviceName = "6E-5326 Non-Stop Flight",
                type = "Economy Air",
                price = "₹4,200",
                availability = "4 Seats at this price",
                timing = "11:15 → 13:20",
                duration = "2h 05m",
                siteUrl = "https://www.goindigo.in/",
                colorAccent = Color(0xFF002244),
                emoji = "✈️",
                testTag = "book_indigo_direct"
            ),
            BookableTicket(
                id = "mmt_bundle",
                portalName = "MakeMyTrip Combo",
                serviceName = "Flight + Airport Cab Connect",
                type = "Premium Combo",
                price = "₹5,100",
                availability = "Instantly Confirmed",
                timing = "10:00 → 13:20",
                duration = "3h 20m",
                siteUrl = "https://www.makemytrip.com/",
                colorAccent = Color(0xFFE50914),
                emoji = "🌟",
                testTag = "book_mmt_direct"
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredTickets = remember(searchQuery) {
        if (searchQuery.isBlank()) tickets else {
            tickets.filter { 
                it.portalName.contains(searchQuery, ignoreCase = true) || 
                it.serviceName.contains(searchQuery, ignoreCase = true) ||
                it.type.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val filteredTicketsByTransport = remember(filteredTickets, selectedTransport) {
        filteredTickets.filter { ticket ->
            when (selectedTransport) {
                "Train" -> ticket.type.contains("Train", ignoreCase = true)
                "Bus" -> ticket.type.contains("Bus", ignoreCase = true)
                "Flight" -> ticket.type.contains("Air", ignoreCase = true) || ticket.type.contains("Combo", ignoreCase = true)
                else -> true
            }
        }
    }

    val isDark by viewModel.isDarkTheme.collectAsState()
    val bgPrimary = if (isDark) AppBackgroundDark else AppBackgroundLight
    val bgSecondary = if (isDark) AppBackgroundDarkSecondary else AppBackgroundLightSecondary
    val cardBlue = if (isDark) NavyPrimary else Color(0xFFFFFFFF)
    val accentCyan = if (isDark) CyanAccent else Color(0xFF12478E)
    val textPrimary = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecondary = if (isDark) TextSecondaryDark else TextSecondaryLight
    val borderSubtle = if (isDark) ColorBorderDark else ColorBorder

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgPrimary)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App banner for ticket booking (Skyscanner Explore style!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, borderSubtle, RoundedCornerShape(20.dp))
                    .testTag("skyscanner_explore_banner")
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
                    contentDescription = "Skyscanner Explore Everywhere Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay matching Dark Navy secondary
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, bgSecondary.copy(alpha = 0.95f)),
                                startY = 100f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Explore Everywhere",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )
                    Text(
                        text = "Compare or book flights, trains, and buses with top-tier destination partners inside India",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                }
            }
        }

        // Live Booking Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("booking_search_form"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, borderSubtle),
                colors = CardDefaults.cardColors(containerColor = cardBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Define Journey Parameters",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    // 1. Transport Mode Selection Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("Train", Icons.Default.Place),
                            Pair("Bus", Icons.Default.LocationOn),
                            Pair("Flight", Icons.Default.Send)
                        ).forEach { (mode, icon) ->
                            val isSelected = selectedTransport == mode
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        selectedTransport = mode 
                                        // Update presets for nicer UX
                                        when (mode) {
                                            "Train" -> {
                                                fromStation = "New Delhi (NDLS)"
                                                toStation = "Mumbai Central (MMCT)"
                                            }
                                            "Bus" -> {
                                                fromStation = "Delhi ISBT Kashmere Gate"
                                                toStation = "Mumbai Central Depot"
                                            }
                                            "Flight" -> {
                                                fromStation = "DEL - Indira Gandhi Airport"
                                                toStation = "BOM - Chhatrapati Shivaji"
                                            }
                                        }
                                    }
                                    .testTag("mode_selector_$mode"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        accentCyan.copy(alpha = 0.15f)
                                    } else {
                                        bgSecondary
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) accentCyan else borderSubtle
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = mode,
                                        tint = if (isSelected) accentCyan else textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = mode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) accentCyan else textSecondary
                                    )
                                }
                            }
                        }
                    }

                    // 2. From Station / Airport / Depot - Dynamic label
                    val fromLabel = when (selectedTransport) {
                        "Train" -> "Departing Station (Source)"
                        "Bus" -> "Departing Terminal (Source)"
                        "Flight" -> "Origin Airport"
                        else -> "Departing From"
                    }
                    OutlinedTextField(
                        value = fromStation,
                        onValueChange = { fromStation = it },
                        label = { Text(fromLabel) },
                        modifier = Modifier.fillMaxWidth().testTag("input_from_station"),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Departure",
                                tint = accentCyan
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedBorderColor = accentCyan,
                            unfocusedBorderColor = borderSubtle,
                            focusedLabelColor = accentCyan,
                            unfocusedLabelColor = textSecondary
                        )
                    )

                    // 3. To Station / Airport / Depot - Dynamic label
                    val toLabel = when (selectedTransport) {
                        "Train" -> "Destination Station (To)"
                        "Bus" -> "Arrival Terminal (To)"
                        "Flight" -> "Destination Airport"
                        else -> "Destination To"
                    }
                    OutlinedTextField(
                        value = toStation,
                        onValueChange = { toStation = it },
                        label = { Text(toLabel) },
                        modifier = Modifier.fillMaxWidth().testTag("input_to_station"),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Destination",
                                tint = accentCyan
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedBorderColor = accentCyan,
                            unfocusedBorderColor = borderSubtle,
                            focusedLabelColor = accentCyan,
                            unfocusedLabelColor = textSecondary
                        )
                    )

                    // Full-width Travel Date Text Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = travelDate,
                            onValueChange = { },
                            label = { Text("Travel Date") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_travel_date"),
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date picker",
                                    tint = accentCyan
                                )
                            },
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedBorderColor = accentCyan,
                                unfocusedBorderColor = borderSubtle,
                                focusedLabelColor = accentCyan,
                                unfocusedLabelColor = textSecondary
                            )
                        )
                        // Transparent intercept overlay to trigger selection dialog
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4. Dynamic Options / Class Configurations according to selection
                    when (selectedTransport) {
                        "Train" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Select Class Preferred (IRCTC)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(accentCyan.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = serviceClass.substringBefore(" -"),
                                            color = accentCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                val railwayClasses = listOf(
                                    Pair("1A", "AC First Class"),
                                    Pair("2A", "AC 2 Tier"),
                                    Pair("3A", "AC 3 Tier"),
                                    Pair("SL", "Sleeper Class"),
                                    Pair("CC", "AC Chair Car"),
                                    Pair("2S", "Second Seating")
                                )

                                val chunkedRows = railwayClasses.chunked(3)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    chunkedRows.forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowItems.forEach { item ->
                                                val isSelected = serviceClass.startsWith(item.first)
                                                Card(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(58.dp)
                                                        .clickable { serviceClass = "${item.first} - ${item.second}" }
                                                        .testTag("irctc_class_option_${item.first}"),
                                                    shape = RoundedCornerShape(10.dp),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isSelected) {
                                                            accentCyan.copy(alpha = 0.12f)
                                                        } else {
                                                            bgSecondary
                                                        }
                                                    ),
                                                    border = BorderStroke(
                                                        width = if (isSelected) 1.5.dp else 1.dp,
                                                        color = if (isSelected) {
                                                            accentCyan
                                                        } else {
                                                            borderSubtle
                                                        }
                                                    )
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxSize().padding(4.dp),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = item.first,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 14.sp,
                                                            color = if (isSelected) accentCyan else textPrimary
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = item.second,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontSize = 8.8.sp,
                                                            maxLines = 1,
                                                            color = if (isSelected) accentCyan.copy(alpha = 0.8f) else textSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Bus" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Select Bus Seat / Coach Configuration",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                                
                                val busOptions = listOf(
                                    Pair("SL", "AC Sleeper"),
                                    Pair("ST", "AC Seater"),
                                    Pair("EX", "Volvo Club Class"),
                                    Pair("NSL", "Non-AC Sleeper")
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    busOptions.forEach { item ->
                                        val isSelected = busClassPreferred == item.second
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(58.dp)
                                                .clickable { busClassPreferred = item.second }
                                                .testTag("bus_option_${item.first}"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) {
                                                    accentCyan.copy(alpha = 0.12f)
                                                } else {
                                                    bgSecondary
                                                }
                                            ),
                                            border = BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) {
                                                    accentCyan
                                                } else {
                                                    borderSubtle
                                                }
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = item.first,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = if (isSelected) accentCyan else textPrimary
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = item.second,
                                                    fontSize = 8.8.sp,
                                                    maxLines = 1,
                                                    color = if (isSelected) accentCyan.copy(alpha = 0.8f) else textSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "Flight" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Flight Cabin & Passenger Booking Details",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                                
                                val flightOptions = listOf("Economy", "Premium", "Business", "First Class")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    flightOptions.forEach { item ->
                                        val isSelected = flightClassPreferred == item
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) accentCyan.copy(alpha = 0.15f) else bgSecondary,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) accentCyan else borderSubtle,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { flightClassPreferred = item }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = item,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accentCyan else textPrimary
                                            )
                                        }
                                    }
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Passengers:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary
                                    )
                                    listOf("1 Pax", "2 Pax", "3 Pax", "4+ Pax").forEach { count ->
                                        val isSelected = flightPassengersCount.startsWith(count.split(" ").first())
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) accentCyan.copy(alpha = 0.15f) else bgSecondary,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) accentCyan else Color.Transparent,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .clickable { flightPassengersCount = if (count.contains("4+")) "4+ Passengers" else "${count.substringBefore(" ")} Passenger" + if (count.startsWith("1")) "" else "s" }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = count,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accentCyan else textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Live Search Results Segment ---
        val transportLabel = when (selectedTransport) {
            "Train" -> "Direct Express Trains"
            "Bus" -> "Direct Express Coach Routes"
            "Flight" -> "Direct Flight Services"
            else -> "Direct Operators"
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "$transportLabel from ${fromStation.substringBefore(" (")} to ${toStation.substringBefore(" (")}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )

                if (filteredTicketsByTransport.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBlue),
                        border = BorderStroke(1.dp, borderSubtle)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No direct services found. Try changing your parameters.", color = textSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    filteredTicketsByTransport.forEach { ticket ->
                        val ticketImage = when (selectedTransport) {
                            "Train" -> "https://images.unsplash.com/photo-1474487548417-781cb71495f3?auto=format&fit=crop&w=600&q=80"
                            "Flight" -> "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=600&q=80"
                            else -> "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=600&q=80" // Bus
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeBookingTicket = ticket
                                    passengerName = ""
                                    passengerAge = ""
                                    mobileNo = ""
                                    confirmedPnrEntity = null
                                    isBookingProcessing = false
                                    bookingProcessingStep = ""
                                }
                                .testTag(ticket.testTag),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBlue),
                            border = BorderStroke(1.dp, borderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column {
                                // Mini Header Image
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                ) {
                                    AsyncImage(
                                        model = ticketImage,
                                        contentDescription = ticket.portalName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, cardBlue.copy(alpha = 0.9f))
                                                )
                                            )
                                    )
                                    // Portal and Type Tag over image
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                            .align(Alignment.BottomStart),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(ticket.emoji, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = ticket.portalName,
                                                color = textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(ticket.colorAccent.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .border(0.5.dp, ticket.colorAccent, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = ticket.type,
                                                color = if (isDark) ticket.colorAccent else accentCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Body Details
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = ticket.serviceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Timings",
                                                color = textSecondary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = ticket.timing,
                                                color = textPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Duration",
                                                color = textSecondary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = ticket.duration,
                                                color = textPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Divider(color = borderSubtle.copy(alpha = 0.5f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(ColorSuccess, CircleShape)
                                            )
                                            Text(
                                                text = ticket.availability,
                                                color = ColorSuccess,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = ticket.price,
                                                color = accentCyan,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(accentCyan, RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        activeBookingTicket = ticket
                                                        passengerName = ""
                                                        passengerAge = ""
                                                        mobileNo = ""
                                                        confirmedPnrEntity = null
                                                        isBookingProcessing = false
                                                        bookingProcessingStep = ""
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = "Instant Book",
                                                    color = bgPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Skyscanner Explore Destinations Gallery ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Discover Trending Deals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                // Horizontal Category filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = activeCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) accentCyan else cardBlue,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) accentCyan else borderSubtle,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { activeCategoryFilter = cat }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) bgPrimary else textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // List of Destination Cards matching category and showing ONLY the selected transport type
                var filteredExploreDestinations = exploreDestinations.filter { dest ->
                    val matchesCategory = if (activeCategoryFilter == "All Deals") {
                        true
                    } else {
                        dest.category == activeCategoryFilter
                    }
                    val matchesTransport = dest.transportType == selectedTransport
                    matchesCategory && matchesTransport
                }

                if (filteredExploreDestinations.isEmpty()) {
                    // Fallback to all deals for the selected transport so the screen never feels broken
                    filteredExploreDestinations = exploreDestinations.filter { it.transportType == selectedTransport }
                }

                filteredExploreDestinations.forEach { dest ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                fromStation = dest.fromCity
                                toStation = dest.toCity
                                selectedTransport = dest.transportType
                                showBookingSiteDialogByDest = dest
                            }
                            .testTag("explore_dest_${dest.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBlue),
                        border = BorderStroke(1.dp, borderSubtle),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column {
                            // Bleed Image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                AsyncImage(
                                    model = dest.imageUrl,
                                    contentDescription = dest.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                
                                // Top-Left Transport Category Chip (Classic Skyscanner styling, NO emojis)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                        .background(bgSecondary.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    val modeText = when (dest.transportType) {
                                        "Flight" -> "Flight Class"
                                        "Train" -> "Rail Pass"
                                        else -> "Coach Express"
                                    }
                                    Text(
                                        text = modeText,
                                        color = textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Top-Right Price tag
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .background(accentCyan, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "Deals from ${dest.dealPrice}",
                                        color = bgPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            // Info Section
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dest.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .background(accentCyan.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = dest.category,
                                            color = accentCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = dest.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive action button row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = "route",
                                            tint = accentCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${dest.fromCity.substringBefore(" (")} • Direct",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textSecondary
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(accentCyan, RoundedCornerShape(8.dp))
                                            .clickable {
                                                fromStation = dest.fromCity
                                                toStation = dest.toCity
                                                selectedTransport = dest.transportType
                                                showBookingSiteDialogByDest = dest
                                            }
                                            .padding(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Text(
                                            text = "View Deals",
                                            color = bgPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Choose Booking Site Dialog (like Skyscanner!)
    if (showBookingSiteDialogByDest != null) {
        val dest = showBookingSiteDialogByDest!!
        Dialog(
            onDismissRequest = { showBookingSiteDialogByDest = null }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp)
                    .testTag("choose_booking_site_dialog"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = bgSecondary),
                border = BorderStroke(1.5.dp, accentCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose Booking Site",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )

                        IconButton(onClick = { showBookingSiteDialogByDest = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Dialog",
                                tint = textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "We compare direct fares for ${dest.title} in real-time. Pick an official operator or partner below to proceed to checkout:",
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 16.sp
                    )

                    // Partners of this transport mode type
                    val partners = when (dest.transportType) {
                        "Flight" -> listOf(
                            Triple("IndiGo Airways", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it - 250 } ?: 3999), "Express direct flight from Delhi Indira Gandhi Intl."),
                            Triple("Air India Direct", dest.dealPrice, "Premium full-service carriage with check-in baggage included."),
                            Triple("Akasa Air Eco", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it + 350 } ?: 4500), "Brand new eco carriage fleet with fast handshakes.")
                        )
                        "Train" -> listOf(
                            Triple("IRCTC Official Direct", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it - 80 } ?: 850), "No intermediate commissions, instant PNR handshakes."),
                            Triple("ConfirmTkt Premium", dest.dealPrice, "Enhanced seat prediction, free cancellation option."),
                            Triple("MakeMyTrip Rail", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it + 40 } ?: 990), "Trip guarantee protection with active live status updates.")
                        )
                        else -> listOf(
                            Triple("redBus India", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it - 50 } ?: 1200), "Top rated intercity luxury Volvo sleeper fleet."),
                            Triple("AbhiBus Prime", dest.dealPrice, "Partner discounts applied, instant seat reservation."),
                            Triple("Zingbus Premium", "₹" + (dest.dealPrice.replace("₹", "").replace(",", "").trim().toIntOrNull()?.let { it + 80 } ?: 1350), "Live location tracking, guaranteed AC climate lounge.")
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        partners.forEachIndexed { idx, (partnerName, partnerPrice, description) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showBookingSiteDialogByDest = null
                                        activeBookingTicket = BookableTicket(
                                            id = "partner_${idx}_${dest.id}",
                                            portalName = partnerName,
                                            type = dest.transportType,
                                            serviceName = "${dest.fromCity.substringBefore(" (")} to ${dest.toCity.substringBefore(" (")}",
                                            timing = "09:00 AM - 12:45 PM",
                                            duration = "3h 45m",
                                            price = partnerPrice,
                                            availability = "7 Seats Available",
                                            siteUrl = "https://" + partnerName.lowercase().replace(" ", "") + ".co.in",
                                            colorAccent = accentCyan,
                                            emoji = when (dest.transportType) {
                                                "Flight" -> "✈️"
                                                "Train" -> "🚂"
                                                else -> "🚌"
                                            },
                                            testTag = "book_" + partnerName.lowercase().replace(" ", "_")
                                        )
                                        passengerName = ""
                                        passengerAge = ""
                                        mobileNo = ""
                                        confirmedPnrEntity = null
                                        isBookingProcessing = false
                                        bookingProcessingStep = ""
                                    }
                                    .testTag("partner_site_card_" + partnerName.replace(" ", "_")),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBlue),
                                border = BorderStroke(1.dp, borderSubtle)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = partnerName,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Direct Booking Partner",
                                                color = accentCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = partnerPrice,
                                                fontWeight = FontWeight.Black,
                                                color = accentCyan,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Fare inclusive of tax",
                                                color = textSecondary,
                                                fontSize = 8.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = description,
                                        fontSize = 10.sp,
                                        color = textSecondary,
                                        lineHeight = 13.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(accentCyan, RoundedCornerShape(8.dp))
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Select & Continue with " + partnerName,
                                            color = bgPrimary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Booking Modal Dialog
    if (activeBookingTicket != null) {
        val ticket = activeBookingTicket!!
        Dialog(
            onDismissRequest = { 
                if (!isBookingProcessing) {
                    activeBookingTicket = null
                }
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 10.dp)
                    .testTag("direct_booking_dialog"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppBackgroundDarkSecondary),
                border = BorderStroke(1.5.dp, CyanAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TicketTypeBadge(ticket.emoji, ticket.colorAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (confirmedPnrEntity != null) "Booking Receipt" else "Direct Express Book",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (!isBookingProcessing) {
                            IconButton(onClick = { activeBookingTicket = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Dialog",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.15f))

                    if (confirmedPnrEntity != null) {
                        // SUCCESS BOOKING STATE (E-TICKET DETAILS WITH BRAND NEW QR)
                        val pnrEntity = confirmedPnrEntity!!

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(ColorSuccess.copy(alpha = 0.15f), RoundedCornerShape(30.dp))
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🟢", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TICKET ISSUED SUCCESSFULLY",
                                        color = ColorSuccess,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // High-contrast Authentic E-Ticket card mockup
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D3E)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // PNR Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "RESERVATION PNR",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyanAccent
                                            )
                                            Text(
                                                pnrEntity.pnr,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "PROVIDER ID",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                            Text(
                                                ticket.portalName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(color = Color.White.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Travel Sector Details
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                pnrEntity.sourceStation,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                            Text(
                                                "DEPARTURE",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }

                                        Text(
                                            "Duration: " + ticket.duration,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                pnrEntity.destinationStation,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                            Text(
                                                "DESTINATION",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Seats allocation details
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "PASSENGER / CLASS",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                            val nameToUse = if (passengerName.isBlank()) "Anjali" else passengerName
                                            Text(
                                                text = "$nameToUse (${pnrEntity.bookingClass})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                "STATUS / SEAT",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                            Text(
                                                text = pnrEntity.currentStatus,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    
                                    // High contrast embedded Canvas QR code
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            SimulatedQrCode(
                                                modifier = Modifier.size(70.dp),
                                                color = Color.Black
                                            )
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    "Scan boarding pass",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                                Text(
                                                    "Digital signature verified",
                                                    fontSize = 9.sp,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    "FARE TOTAL: ${ticket.price}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val webUri = Uri.parse(ticket.siteUrl)
                                        val bookIntent = Intent(Intent.ACTION_VIEW, webUri)
                                        context.startActivity(bookIntent)
                                    },
                                    modifier = Modifier.weight(1f).testTag("e_ticket_open_partner_web"),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, CyanAccent)
                                ) {
                                    Text("Open Portal", color = CyanAccent, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.selectTab(Tab.CheckPnr)
                                        activeBookingTicket = null
                                    },
                                    modifier = Modifier.weight(1.2f).testTag("e_ticket_track_pnr_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyPrimary)
                                ) {
                                    Text("Track PNR Status", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    } else if (isBookingProcessing) {
                        // PROGRESS LOADER STATE
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = CyanAccent,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Text(
                                text = bookingProcessingStep,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            Text(
                                text = "Securing direct channels. Do not minimize the application.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        // BOOKING FORM INPUT DIALOG - REDIRECT DIALOG
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Selected Portal review
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("OFFICIAL PROVIDER SECURE WEB CORRIDOR", fontSize = 8.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                                    Text("${ticket.portalName} Platform", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                    Text(ticket.serviceName, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                                Text(ticket.price, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }

                            // Notice description
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Official Booking Portal Handshake",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CyanAccent
                                    )
                                    Text(
                                        text = "To guarantee lowest fares, authentic seat inventory, and instant cancellation support, you are about to navigate to ${ticket.portalName}'s official booking site at:",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        lineHeight = 15.sp
                                    )
                                    Text(
                                        text = ticket.siteUrl,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Dual Action Buttons: Confirm Redirection vs Cancel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { activeBookingTicket = null },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                                ) {
                                    Text("Cancel", color = Color.White, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        val webUri = Uri.parse(ticket.siteUrl)
                                        val bookIntent = Intent(Intent.ACTION_VIEW, webUri)
                                        context.startActivity(bookIntent)
                                        activeBookingTicket = null
                                        Toast.makeText(context, "Opening safe channel to ${ticket.portalName}...", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.weight(1.8f).testTag("confirm_external_book_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyPrimary)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Secure Check", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PROCEED TO PORTAL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedQrCode(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val numGrid = 15
        val cellSize = sizePx / numGrid
        
        // Top-Left locator
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 5, cellSize * 5),
            topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
            style = Stroke(cellSize * 1.25f)
        )
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 1.5f, cellSize * 1.5f),
            topLeft = androidx.compose.ui.geometry.Offset(cellSize * 1.75f, cellSize * 1.75f)
        )
        
        // Top-Right locator
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 5, cellSize * 5),
            topLeft = androidx.compose.ui.geometry.Offset(cellSize * 10, 0f),
            style = Stroke(cellSize * 1.25f)
        )
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 1.5f, cellSize * 1.5f),
            topLeft = androidx.compose.ui.geometry.Offset(cellSize * 11.75f, cellSize * 1.75f)
        )
        
        // Bottom-Left locator
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 5, cellSize * 5),
            topLeft = androidx.compose.ui.geometry.Offset(0f, cellSize * 10),
            style = Stroke(cellSize * 1.25f)
        )
        drawRect(
            color = color,
            size = androidx.compose.ui.geometry.Size(cellSize * 1.5f, cellSize * 1.5f),
            topLeft = androidx.compose.ui.geometry.Offset(cellSize * 1.75f, cellSize * 11.75f)
        )

        // Draw some random pseudo-QR cells
        val randomState = java.util.Random(42)
        for (r in 0 until numGrid) {
            for (c in 0 until numGrid) {
                // skip locator areas
                if ((r < 5 && c < 5) || (r < 5 && c >= 10) || (r >= 10 && c < 5)) continue
                if (randomState.nextBoolean()) {
                    drawRect(
                        color = color,
                        topLeft = androidx.compose.ui.geometry.Offset(c * cellSize, r * cellSize),
                        size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

data class BookableTicket(
    val id: String,
    val portalName: String,
    val serviceName: String,
    val type: String,
    val price: String,
    val availability: String,
    val timing: String,
    val duration: String,
    val siteUrl: String,
    val colorAccent: Color,
    val emoji: String,
    val testTag: String
)

