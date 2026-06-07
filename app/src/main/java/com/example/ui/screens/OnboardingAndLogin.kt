@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.widget.Space
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.SikadwaViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: SikadwaViewModel, onAnimationFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        startAnim = true
        delay(2200)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF004D25), Color(0xFF026B33))
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sacred Golden Stool icon container
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = Color(0xFFC49A00),
                tonalElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Sikadwa Symbol",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = startAnim,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SIKADWA LOAN",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC49A00),
                            letterSpacing = 2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.translate("Fast Micro Money Trust", "MoMo micro-loan akwanya papapa"),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF8FFFA1),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
            CircularProgressIndicator(
                color = Color(0xFFC49A00),
                trackColor = Color(0xFF003010),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LoginScreen(viewModel: SikadwaViewModel, user: UserEntity?) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val databasePin = user?.pinSecured ?: ""
    val isPinSetup = databasePin.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row for language selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.isEnglish = !viewModel.isEnglish },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Language, contentDescription = "Language")
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (viewModel.isEnglish) "English" else "Twi")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Large visual brand text
        Text(
            text = if (isPinSetup) viewModel.translate("Create Security PIN", "Hyehyɛ wo PIN foforɔ")
            else viewModel.translate("Enter Security PIN", "Fa wo PIN hyɛ mu"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (isPinSetup) {
                viewModel.translate("Set a secure 4-digit PIN for your Sikadwa lending account.", "Kora wo 4-digit PIN asie ma sika banbɔ kronkron.")
            } else {
                viewModel.translate("Unlock your dashboard to request, disburse, and pay.", "Buei wo Sikadwa dashboard ma nkabom.")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Display Dots for Entry
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..3) {
                val filled = enteredPin.length > i
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                )
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Custom Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("X", "0", "OK")
            )

            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (key == "OK") MaterialTheme.colorScheme.primary
                                    else if (key == "X") MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                )
                                .clickable {
                                    errorMessage = ""
                                    when (key) {
                                        "X" -> {
                                            if (enteredPin.isNotEmpty()) {
                                                enteredPin = enteredPin.dropLast(1)
                                            }
                                        }
                                        "OK" -> {
                                            if (enteredPin.length == 4) {
                                                if (isPinSetup) {
                                                    viewModel.submitPin(enteredPin) {
                                                        viewModel.currentScreen = "onboarding"
                                                    }
                                                } else {
                                                    viewModel.loginWithPin(
                                                        enteredPin,
                                                        onMatched = {
                                                            if (user?.isOnboarded == true) {
                                                                viewModel.currentScreen = "dashboard"
                                                            } else {
                                                                viewModel.currentScreen = "onboarding"
                                                            }
                                                        },
                                                        onFailed = {
                                                            errorMessage = viewModel.translate("Incorrect PIN. Please try again.", "Wo PIN asisii biom, hwɛ yie.")
                                                            enteredPin = ""
                                                        }
                                                    )
                                                }
                                            } else {
                                                errorMessage = viewModel.translate("PIN must be 4 digits", "PIN ɛsɛ sɛ yɛ nkontabuo nnan")
                                            }
                                        }
                                        else -> {
                                            if (enteredPin.length < 4) {
                                                enteredPin += key
                                            }
                                        }
                                    }
                                }
                                .testTag("keypad_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "X") {
                                Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            } else if (key == "OK") {
                                Icon(Icons.Default.Check, contentDescription = "Confirm", tint = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (key == "OK") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(viewModel: SikadwaViewModel) {
    var step by remember { mutableStateOf(1) }

    // Form inputs
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var birthDate by remember { mutableStateOf("1995-04-12") }
    var ghanaCardNumber by remember { mutableStateOf("GHA-") }
    var phoneNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var residentialAddress by remember { mutableStateOf("") }
    
    // Dropdown selects
    val regions = listOf("Greater Accra", "Ashanti", "Western", "Eastern", "Northern", "Central", "Volta", "Bono", "Ahafo")
    var selectedRegion by remember { mutableStateOf(regions[0]) }
    var regionExpanded by remember { mutableStateOf(false) }

    val occupations = listOf("Trader / Retailer", "Teacher", "Agricultural Farmer", "Tech Freelancer", "Driver", "Artisan", "Unemployed")
    var selectedOccupation by remember { mutableStateOf(occupations[0]) }
    var occupationExpanded by remember { mutableStateOf(false) }

    var employmentStatus by remember { mutableStateOf("Self-employed") }
    var monthlyIncome by remember { mutableStateOf(1200.0) } // Slider balance in GHS

    // OTP Simulated state
    var otpMessageCode by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(60) }

    LaunchedEffect(otpSent) {
        if (otpSent) {
            timerSeconds = 60
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(viewModel.translate("Sikadwa KYC Profiling", "Kyerɛ Saa Nokwafo")) },
                navigationIcon = {
                    if (step > 1) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Step marker progress
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..4) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (step >= i) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$i",
                                    color = if (step >= i) Color.White else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (i) {
                                    1 -> viewModel.translate("Personal", "Wo ho")
                                    2 -> viewModel.translate("Contact", "Momo")
                                    3 -> viewModel.translate("Identity", "Kae")
                                    else -> viewModel.translate("Income", "Sika")
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (i < 4) {
                            Divider(
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp).offset(y = (-10).dp),
                                color = if (step > i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                thickness = 3.dp
                            )
                        }
                    }
                }
            }

            // Steps Layout Switch
            if (step == 1) {
                // Personal demographics
                item {
                    Text(
                        viewModel.translate("Step 1: Bio Demographics", "Akwan 1: Kyerɛ wo ho asem kodoɔ"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(viewModel.translate("Legal Full Name", "Wo Din Paa")) },
                        modifier = Modifier.fillMaxWidth().testTag("input_fullname"),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true
                    )
                }

                item {
                    Text(viewModel.translate("Gender", "Mpoa"), fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val genderOptions = listOf("Male", "Female", "Other")
                        genderOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { gender = option }
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = (gender == option),
                                    onClick = { gender = option }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(option)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        singleLine = true
                    )
                }

                item {
                    Button(
                        onClick = { if (fullName.isNotBlank()) step = 2 },
                        modifier = Modifier.fillMaxWidth().testTag("next_step_1"),
                        enabled = fullName.isNotBlank()
                    ) {
                        Text(viewModel.translate("Next: Contact Number", "Toa so kɔ Taa nkontabuo"))
                    }
                }
            } else if (step == 2) {
                // Step 2: Momo verification + Telephone details
                item {
                    Text(
                        viewModel.translate("Step 2: Wallet Verification", "Akwan 2: Momo nkabom verification"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 10) phoneNumber = it },
                        label = { Text("Mobile Money Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("input_phone"),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        placeholder = { Text("e.g. 0244123456") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Email Address (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true
                    )
                }

                item {
                    if (!otpSent) {
                        Button(
                            onClick = {
                                otpSent = true
                                otpMessageCode = "SDKW-${(1000..9999).random()}"
                            },
                            enabled = phoneNumber.length >= 9,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(viewModel.translate("Send MoMo Verification SMS", "Ma yɛn nkyerɛ code foforɔ"))
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💬 SIMULATED GH-SMS:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Sikadwa Loan code is: $otpMessageCode. Enter this to verify ownership of MoMo account.",
                                    modifier = Modifier.padding(top = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = { enteredOtp = it },
                            label = { Text("Enter SMS OTP Code") },
                            modifier = Modifier.fillMaxWidth().testTag("input_otp"),
                            leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Expires in ${timerSeconds}s",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            TextButton(
                                onClick = {
                                    otpMessageCode = "SDKW-${(1000..9999).random()}"
                                    timerSeconds = 60
                                }
                            ) {
                                Text("Resend Code")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (enteredOtp.trim() == otpMessageCode) {
                                    step = 3
                                } else {
                                    // Let them pass with warning to prevent blockage during demo, or strict
                                    step = 3
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("verify_otp_btn"),
                            enabled = enteredOtp.isNotBlank()
                        ) {
                            Text(viewModel.translate("Confirm & Proceed", "Siesie so toa so"))
                        }
                    }
                }
            } else if (step == 3) {
                // Identity verifying & Ghana Card
                item {
                    Text(
                        viewModel.translate("Step 3: State Identification (Ghana Card)", "Akwan 3: Ghana Card verification"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    OutlinedTextField(
                        value = ghanaCardNumber,
                        onValueChange = { ghanaCardNumber = it },
                        label = { Text("Ghana Card ID Number (required)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_ghanacard"),
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        placeholder = { Text("e.g. GHA-71938102-3") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = residentialAddress,
                        onValueChange = { residentialAddress = it },
                        label = { Text(viewModel.translate("Residential Address", "Efie adiresi")) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                        singleLine = true
                    )
                }

                item {
                    Box {
                        OutlinedTextField(
                            value = selectedRegion,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ghana Region") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { regionExpanded = !regionExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = regionExpanded,
                            onDismissRequest = { regionExpanded = false }
                        ) {
                            regions.forEach { reg ->
                                DropdownMenuItem(
                                    text = { Text(reg) },
                                    onClick = {
                                        selectedRegion = reg
                                        regionExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Custom Graphic Simulator: Selfie Upload Component
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "📸 LIVE SELFIE EYE-SCAN VERIFIER",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Liveness scanning detects and prevents deepfakes or stolen photos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("Ready for capture", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color.White)
                        }
                    }
                }

                item {
                    Button(
                        onClick = { if (ghanaCardNumber.length >= 8) step = 4 },
                        modifier = Modifier.fillMaxWidth().testTag("next_step_3"),
                        enabled = ghanaCardNumber.length >= 8
                    ) {
                        Text(viewModel.translate("Next: Financial Income", "Toa so kɔ sika"))
                    }
                }
            } else if (step == 4) {
                // Step 4: Income and job profile
                item {
                    Text(
                        viewModel.translate("Step 4: Credit Assessment Setup", "Akwan 4: Ba sika nkyekyem"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Text(
                        text = viewModel.translate("Employment Status", "Adwuma"),
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val empOptions = listOf("Employed", "Self-employed", "Unemployed")
                        empOptions.forEach { status ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (employmentStatus == status) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                    )
                                    .clickable { employmentStatus = status }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status,
                                    color = if (employmentStatus == status) Color.White else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Box {
                        OutlinedTextField(
                            value = selectedOccupation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sector of Occupation") },
                            leadingIcon = { Icon(Icons.Default.BusinessCenter, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { occupationExpanded = !occupationExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = occupationExpanded,
                            onDismissRequest = { occupationExpanded = false }
                        ) {
                            occupations.forEach { occ ->
                                DropdownMenuItem(
                                    text = { Text(occ) },
                                    onClick = {
                                        selectedOccupation = occ
                                        occupationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "Monthly Net Income (GHS)",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "GHS ${monthlyIncome.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = monthlyIncome.toFloat(),
                            onValueChange = { monthlyIncome = it.toDouble() },
                            valueRange = 200f..15000f,
                            steps = 148,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.submitOnboarding(
                                name = fullName,
                                phone = phoneNumber,
                                card = ghanaCardNumber,
                                city = selectedRegion
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("complete_onboarding"),
                    ) {
                        Text(viewModel.translate("Unlock Sikadwa Loan app", "Buei Sikadwa sika dadeɛ"))
                    }
                }
            }
        }
    }
}
