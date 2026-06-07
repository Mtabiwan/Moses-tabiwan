@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LoanEntity
import com.example.data.local.UserEntity
import com.example.ui.SikadwaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SikadwaViewModel,
    user: UserEntity?,
    loans: List<LoanEntity>,
    onApplyLoanClicked: () -> Unit,
    onAdminClicked: () -> Unit
) {
    val activeLoans = loans.filter { it.status == "PENDING" || it.status == "APPROVED" || it.status == "DISBURSED" }
    val historicLoans = loans.filter { it.status == "REPAID" || it.status == "REJECTED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (user?.fullName?.take(1) ?: "S"),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sikadwa Mobile",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = viewModel.translate("Instant Ghana MoMo Credit", "Sika nkabom dadeɛ"),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFA5D6A7)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onAdminClicked, modifier = Modifier.testTag("admin_portal_btn")) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Mode", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            BottomNavigationBar(viewModel = viewModel)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User greeting and balance card
            item {
                WalletBalanceCard(viewModel, user)
            }

            // Credit Score Indicator Chart
            item {
                CreditScoreCard(viewModel, user?.creditScore ?: 300)
            }

            // High-viral action recommendations
            item {
                EligiblePromoBanner(viewModel, user, onApplyLoanClicked)
            }

            // Active micro-loans title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.translate("Active Micro-loans", "Enye sika dadeɛ mudi"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("${activeLoans.size} ${viewModel.translate("Active", "Eda hɔ")}", modifier = Modifier.padding(4.dp), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            if (activeLoans.isEmpty()) {
                item {
                    EmptyActivityCard(viewModel)
                }
            } else {
                items(activeLoans) { loan ->
                    ActiveLoanItemCard(viewModel, loan)
                }
            }

            // History segment
            if (historicLoans.isNotEmpty()) {
                item {
                    Text(
                        text = viewModel.translate("Transaction History", "Tete sika nkabom list"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                items(historicLoans) { loan ->
                    HistoricLoanItemCard(viewModel, loan)
                }
            }
        }
    }
}

@Composable
fun WalletBalanceCard(viewModel: SikadwaViewModel, user: UserEntity?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.translate("Sikadwa Wallet Balance", "Wo Sikadwa Kotoku emu sika"),
                    color = Color(0xFFA5D6A7),
                    style = MaterialTheme.typography.labelLarge
                )
                // Loyalty Points display badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFC49A00).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFFC49A00))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFCD116), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user?.loyaltyPoints ?: 0} PTS (Gold)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCD116)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "GHS ${String.format(Locale.US, "%,.2f", user?.walletBalance ?: 0.0)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color.White.copy(alpha = 0.15f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "MoMo Number",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFA5D6A7)
                    )
                    Text(
                        text = user?.phone ?: "024XXXXXXX",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            // Quick simulation: Deposit GHS 100 via Mobile Money
                            viewModel.submitPromoReferral("SIKADWA-MOMO-DEMO")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CreditScoreCard(viewModel: SikadwaViewModel, score: Int) {
    val tier = when {
        score >= 750 -> "Platinum Sovereign"
        score >= 650 -> "Gold Premium"
        score >= 501 -> "Silver Standard"
        else -> "Bronze Entry"
    }

    val color = when {
        score >= 750 -> Color(0xFF1976D2)
        score >= 650 -> Color(0xFFD3A100)
        score >= 510 -> Color(0xFF78909C)
        else -> Color(0xFF9E5C0D)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📈 CUSTOMER CREDIT TRUST STATUS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large score display circular container
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f))
                        .border(3.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = "/850",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = "", tint = color, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(tier, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                    }
                    Text(
                        text = when (score) {
                            in 750..850 -> "Excellent creditworthiness! Instant maximum limits and prioritized approvals."
                            in 650..749 -> "Secure capacity. Good repayment history boosts your cash limits."
                            else -> "Credit building phase. Maintain swift repayments to unlock bigger liquidity."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progressive bars indicating levels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val levels = listOf("BRONZE", "SILVER", "GOLD", "PLATINUM")
                levels.forEachIndexed { i, lv ->
                    val active = when (i) {
                        0 -> true
                        1 -> score >= 501
                        2 -> score >= 650
                        3 -> score >= 750
                        else -> false
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (active) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun EligiblePromoBanner(viewModel: SikadwaViewModel, user: UserEntity?, onApplyClicked: () -> Unit) {
    val dynamicLimit = ((user?.monthlyIncome ?: 1200.0) * 0.45).coerceIn(200.0, 5000.0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFC49A00), Color(0xFFE5B200))
                )
            )
            .clickable { onApplyClicked() }
            .padding(16.dp)
            .testTag("promo_apply_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viewModel.translate("⭐ ELIGIBILITY SPECIAL RATE", "Akwanya foforɔ ma wo sika"),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Request up to GHS ${String.format(Locale.US, "%,.0f", dynamicLimit)} instantly based on your KYC net salary assessment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color(0xFFC49A00), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyActivityCard(viewModel: SikadwaViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = viewModel.translate("No Overdue or Active Debts", "Woni nkabom sika foforɔ biara egu so"),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = viewModel.translate("Your slate is fully clear. Apply above to generate instant Mobile Money loan.", "Wo kɛkrɛ nyinaa yi asie. Wobɛtumi akɔ sika dadeɛ wizard mu anonom."),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ActiveLoanItemCard(viewModel: SikadwaViewModel, loan: LoanEntity) {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val totalCost = loan.totalRepayable + loan.penaltyAmount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        loan.purpose,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Due: ${sdf.format(Date(loan.dueDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Badge(
                    containerColor = when (loan.status) {
                        "APPROVED" -> Color(0xFFC49A00)
                        "DISBURSED" -> Color(0xFF2E7D32)
                        "PENDING" -> Color(0xFF78909C)
                        else -> Color(0xFFC7161E)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = loan.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Due", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("GHS ${String.format(Locale.US, "%,.2f", totalCost)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Interest & Charges", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("GHS ${String.format(Locale.US, "%,.2f", loan.interestAmount)} (Rate: ${String.format(Locale.US, "%.0f", (loan.interestAmount/loan.amount)*100)}%)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            if (loan.penaltyAmount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OVERDUE PENALTY! +GHS ${loan.penaltyAmount} (${loan.penaltyDays} Days Defaulted)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row
            when (loan.status) {
                "APPROVED" -> {
                    Button(
                        onClick = { viewModel.adminSetStatus(loan.id, "DISBURSED") },
                        modifier = Modifier.fillMaxWidth().testTag("disburse_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.translate("Instant Disburse to Wallet", "Nom sika kɔ wo kotoku mu"))
                    }
                }
                "DISBURSED" -> {
                    Button(
                        onClick = { viewModel.repayLoan(loan.id) },
                        modifier = Modifier.fillMaxWidth().testTag("repay_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.translate("Pay with Mobile Money (GHS ${String.format(Locale.US, "%.1f", totalCost)})", "Tua mu sika a ɛsɛ yɛ"))
                    }
                }
                "PENDING" -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.translate("AI Risk Verification in progress", "AI siesie nkabom mudi..."))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoricLoanItemCard(viewModel: SikadwaViewModel, loan: LoanEntity) {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val color = if (loan.status == "REPAID") Color(0xFF2E7D32) else Color(0xFFC7161E)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(loan.purpose, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("Applied: ${sdf.format(Date(loan.dateApplied))}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("GHS ${loan.amount}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = loan.status,
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun LoanWizardScreen(viewModel: SikadwaViewModel, user: UserEntity?, onFinished: () -> Unit) {
    var step by remember { mutableStateOf(1) }

    var loanAmount by remember { mutableStateOf(500.0) }
    val purposes = listOf("Business Expansion / Inventory", "Teacher / School fees", "Emergency Medical Care", "Agricultural Inputs", "Personal Transit")
    var selectedPurpose by remember { mutableStateOf(purposes[0]) }
    var purposeExpanded by remember { mutableStateOf(false) }

    val durations = listOf("7 days", "14 days", "30 days", "90 days")
    var selectedDuration by remember { mutableStateOf(durations[2]) } // 30 days
    var durationExpanded by remember { mutableStateOf(false) }

    var selectedNetwork by remember { mutableStateOf("MTN MoMo") }
    var momoPhoneNum by remember { mutableStateOf(user?.phone ?: "") }

    // Dynamic calculations
    val calculatedInterestRate = when (selectedDuration) {
        "7 days" -> 0.08
        "14 days" -> 0.12
        "30 days" -> 0.15
        "90 days" -> 0.22
        else -> 0.15
    }
    val calculatedInterest = loanAmount * calculatedInterestRate
    val totalRepay = loanAmount + calculatedInterest

    var showRadarAssessment by remember { mutableStateOf(false) }
    var promptDisplaySentence by remember { mutableStateOf("Authenticating ID registry...") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(viewModel.translate("Micro-loan Application", "Sika nkabom wizard")) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else onFinished()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        if (showRadarAssessment) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.Black.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "SIKADWA RISK AI RADAR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = promptDisplaySentence,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Simulated AI processing time-delays
            LaunchedEffect(key1 = true) {
                delay(800)
                promptDisplaySentence = "Simulating Telecommunication data flow..."
                delay(800)
                promptDisplaySentence = "Checking monthly income criteria ratios..."
                delay(800)
                promptDisplaySentence = "Sending payload query to Gemini-3.5-Flash model..."
                delay(1200)

                // Trigger application to repo
                viewModel.applyForLoan(
                    amount = loanAmount,
                    purpose = selectedPurpose,
                    durationDays = when (selectedDuration) {
                        "7 days" -> 7
                        "14 days" -> 14
                        "30 days" -> 30
                        "90 days" -> 90
                        else -> 30
                    },
                    repaymentMethod = "Mobile Money",
                    momoProvider = selectedNetwork,
                    momoNumber = momoPhoneNum,
                    onComplete = {
                        showRadarAssessment = false
                        onFinished()
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Stepper info
                item {
                    Text(
                        "Step $step of 3: " + when (step) {
                            1 -> "Set Amount & Purpose"
                            2 -> "Repayment Terms"
                            else -> "MoMo Destination"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LinearProgressIndicator(
                        progress = step / 3.0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (step == 1) {
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text("Select Amount Requested", fontWeight = FontWeight.SemiBold)
                                Text("GHS ${loanAmount.toInt()}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(
                                value = loanAmount.toFloat(),
                                onValueChange = { loanAmount = it.toDouble() },
                                valueRange = 100f..4000f,
                                steps = 39,
                                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GHS 100", style = MaterialTheme.typography.labelSmall)
                                Text("GHS 4,000", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    item {
                        Box {
                            OutlinedTextField(
                                value = selectedPurpose,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Purpose of micro-loan") },
                                trailingIcon = {
                                    IconButton(onClick = { purposeExpanded = !purposeExpanded }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = purposeExpanded, onDismissRequest = { purposeExpanded = false }) {
                                purposes.forEach { prp ->
                                    DropdownMenuItem(text = { Text(prp) }, onClick = {
                                        selectedPurpose = prp
                                        purposeExpanded = false
                                    })
                                }
                            }
                        }
                    }

                    item {
                        Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth().testTag("next_wizard_step_1")) {
                            Text("Next: Repayment terms")
                        }
                    }
                } else if (step == 2) {
                    item {
                        Box {
                            OutlinedTextField(
                                value = selectedDuration,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Repayment Duration Term") },
                                trailingIcon = {
                                    IconButton(onClick = { durationExpanded = !durationExpanded }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = durationExpanded, onDismissRequest = { durationExpanded = false }) {
                                durations.forEach { dur ->
                                    DropdownMenuItem(text = { Text(dur) }, onClick = {
                                        selectedDuration = dur
                                        durationExpanded = false
                                    })
                                }
                            }
                        }
                    }

                    // Pricing calculations summary card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("💰 Sikadwa PRICING BREAKDOWN", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Principal Requested:")
                                    Text("GHS ${loanAmount.toInt()}", fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Interest Rate ($selectedDuration):")
                                    Text("${String.format(Locale.US, "%.0f", calculatedInterestRate * 100)}%", fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Dynamic Interest Amount:")
                                    Text("GHS ${String.format(Locale.US, "%.2f", calculatedInterest)}", fontWeight = FontWeight.Bold)
                                }
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Payable (Tax inclusive):", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text("GHS ${String.format(Locale.US, "%.2f", totalRepay)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }

                    item {
                        Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth().testTag("next_wizard_step_2")) {
                            Text("Next: MoMo Wallet channel")
                        }
                    }
                } else if (step == 3) {
                    item {
                        Text("Choose Mobile Money Provider", fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val networks = listOf("MTN MoMo", "Telecel Cash", "AirtelTigo Money")
                            networks.forEach { net ->
                                val active = selectedNetwork == net
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                                        .clickable { selectedNetwork = net }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(net, color = if (active) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = momoPhoneNum,
                            onValueChange = { momoPhoneNum = it },
                            label = { Text("Payout Phone Number") },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_input_phone")
                        )
                    }

                    // Fraud warnings GDPR compliances
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Your payout and payments are securely verified beneath Central Bank regulations.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = { showRadarAssessment = true },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_submit_btn")
                        ) {
                            Text("Assess Sika Limit & Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(viewModel: SikadwaViewModel) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = viewModel.currentScreen == "dashboard" || viewModel.currentScreen == "loan_wizard",
            onClick = { viewModel.currentScreen = "dashboard" },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
            label = { Text(viewModel.translate("Lending", "Sika Kotoku")) }
        )
        NavigationBarItem(
            selected = viewModel.currentScreen == "referrals",
            onClick = { viewModel.currentScreen = "referrals" },
            icon = { Icon(Icons.Default.Share, contentDescription = null) },
            label = { Text(viewModel.translate("Refer & Win", "Referral")) }
        )
        NavigationBarItem(
            selected = viewModel.currentScreen == "profile",
            onClick = { viewModel.currentScreen = "profile" },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            label = { Text(viewModel.translate("Account", "Wo ho")) }
        )
    }
}
