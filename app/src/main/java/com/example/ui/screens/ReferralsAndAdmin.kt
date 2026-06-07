package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LoanEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.UserEntity
import com.example.ui.SikadwaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralsScreen(viewModel: SikadwaViewModel, user: UserEntity?, referrals: List<ReferralEntity>) {
    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var promoAppliedMessage by remember { mutableStateOf("") }

    var friendName by remember { mutableStateOf("") }
    var friendPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewModel.translate("Invite & Earn GHS 15", "Referral mpeamu sika GHS 15"), fontWeight = FontWeight.Bold, color = Color.White) },
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
            // Core Invite Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFC49A00), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "GHS 15 REFERRAL BONUS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Gift friends instant loans & obtain GHS 15.00 cash directly inside wallet upon their subscription approvals.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        // Referral Code display
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = user?.referralCode ?: "SIKADWA-992A",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }

            // Enter friend manual code
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Claim Referral / Promo Code",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputCode,
                                onValueChange = { inputCode = it },
                                placeholder = { Text("e.g. SIKADWA-XXXX") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("referral_code_input")
                            )
                            Button(
                                onClick = {
                                    if (inputCode.isNotBlank()) {
                                        viewModel.submitPromoReferral(inputCode)
                                        promoAppliedMessage = "GHS 15.00 welcome referral bonus credited!"
                                        inputCode = ""
                                    }
                                },
                                modifier = Modifier.testTag("apply_promo_btn")
                            ) {
                                Text("Apply")
                            }
                        }
                        if (promoAppliedMessage.isNotEmpty()) {
                            Text(
                                text = promoAppliedMessage,
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            // Simulating Social Status Share Banner Card
            item {
                WhatsAppBannerGeneratorCard(viewModel, user?.creditScore ?: 300)
            }

            // Quick simulated friend adder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Simulate Team Invite", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        OutlinedTextField(
                            value = friendName,
                            onValueChange = { friendName = it },
                            label = { Text("Friend's Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("invite_friend_name")
                        )
                        OutlinedTextField(
                            value = friendPhone,
                            onValueChange = { friendPhone = it },
                            label = { Text("Friend's Phone Number (MoMo)") },
                            modifier = Modifier.fillMaxWidth().testTag("invite_friend_phone")
                        )
                        Button(
                            onClick = {
                                if (friendName.isNotBlank() && friendPhone.isNotBlank()) {
                                    viewModel.inviteFriend(friendName, friendPhone)
                                    Toast.makeText(context, "$friendName invited! +GHS 15.00 awarded.", Toast.LENGTH_SHORT).show()
                                    friendName = ""
                                    friendPhone = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("send_invite_btn"),
                            enabled = friendName.isNotBlank() && friendPhone.isNotBlank()
                        ) {
                            Text("Send Invitation (Instant Bonus GHS 15)")
                        }
                    }
                }
            }

            // List Referred details
            item {
                Text(
                    text = "Referral Logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (referrals.isEmpty()) {
                item {
                    Text(
                        "No referrals logged yet. Invite your colleagues using the banner generator!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                items(referrals) { ref ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ref.refereeName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(ref.refereePhone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+GHS ${ref.bonusCash}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text(ref.status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsAppBannerGeneratorCard(viewModel: SikadwaViewModel, score: Int) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366))
                Spacer(modifier = Modifier.width(8.dp))
                Text("🔥 WHATSAPP STATUS VIRAL GENERATOR", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF25D366))
            }
            Text(
                "Generate a high-conversion social banner and share on status to earn extra credit points.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Banner rendering canvas box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF004D25), Color(0xFFC49A00))
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "💰 SIKADWA INSTANT LOANS 🇬🇭",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "I just got instantaneously approved GHS loan with Credit Trust Score: $score/850!",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF2D3),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        "Unlock your Mobile Money credit right now. Code: SIKADWA-992A",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Sikadwa approval status shared, +15 Loyalty points verified!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast to WhatsApp Status")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: SikadwaViewModel, loans: List<LoanEntity>, onBack: () -> Unit) {
    val context = LocalContext.current
    var adminTab by remember { mutableStateOf("loans") } // "loans" vs "portfolio"

    // Metrics calculations
    val totalDisbursed = loans.filter { it.status == "DISBURSED" || it.status == "REPAID" }.sumOf { it.amount }
    val revenueCollected = loans.filter { it.status == "REPAID" }.sumOf { it.interestAmount }
    val penaltyOutstanding = loans.sumOf { it.penaltyAmount }
    val outstandingPrincipal = loans.filter { it.status == "DISBURSED" }.sumOf { it.amount }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("📊 Sikadwa Underwriting Portal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Export Report CSV
                        val dataCsv = "Loan ID,Amount,Purpose,Momo,Status,Interest\\n" +
                                loans.joinToString("\\n") { "${it.id},${it.amount},${it.purpose},${it.momoNumber},${it.status},GHS ${it.interestAmount}" }
                        Toast.makeText(context, "Exported ${loans.size} rows successfully to CSV cache!", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Export Report", tint = Color.White)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Header
            Row(modifier = Modifier.fillMaxWidth()) {
                Tab(
                    selected = adminTab == "loans",
                    onClick = { adminTab = "loans" },
                    modifier = Modifier.weight(1f).background(if (adminTab == "loans") MaterialTheme.colorScheme.primaryContainer else Color.Transparent).padding(16.dp),
                ) {
                    Text("Manual Operations", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Tab(
                    selected = adminTab == "portfolio",
                    onClick = { adminTab = "portfolio" },
                    modifier = Modifier.weight(1f).background(if (adminTab == "portfolio") MaterialTheme.colorScheme.primaryContainer else Color.Transparent).padding(16.dp),
                ) {
                    Text("Fintech Analytics", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            if (adminTab == "loans") {
                // List of underwriting loans
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = {
                                viewModel.resetSystem()
                                Toast.makeText(context, "Database fully reset!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth().testTag("reset_db_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Application States (Demo)")
                        }
                    }

                    if (loans.isEmpty()) {
                        item {
                            Text(
                                text = "No loan requests submitted yet inside the Room DB.",
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(loans) { loan ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Loan #${loan.id} - GHS ${loan.amount}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text(loan.status, color = Color.White, modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Purpose: ${loan.purpose}", style = MaterialTheme.typography.bodySmall)
                                    Text("MoMo Desk: ${loan.momoProvider} (${loan.momoNumber})", style = MaterialTheme.typography.bodySmall)
                                    Text("Dynamic Risk Rating: ${loan.riskScore}/100", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (loan.riskScore < 50) Color.Red else Color.Green)

                                    if (loan.penaltyAmount > 0) {
                                        Text("Penalty Accrued: GHS ${loan.penaltyAmount} (${loan.penaltyDays} Default checks)", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Underwriter operations mapping
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (loan.status == "PENDING") {
                                            Button(
                                                onClick = { viewModel.adminSetStatus(loan.id, "APPROVED") },
                                                modifier = Modifier.weight(1f).testTag("admin_approve_${loan.id}"),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                            ) {
                                                Text("Approve", fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = { viewModel.adminSetStatus(loan.id, "REJECTED") },
                                                modifier = Modifier.weight(1f).testTag("admin_reject_${loan.id}"),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            ) {
                                                Text("Reject", fontSize = 11.sp)
                                            }
                                        } else if (loan.status == "APPROVED") {
                                            Button(
                                                onClick = { viewModel.adminSetStatus(loan.id, "DISBURSED") },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB38600))
                                            ) {
                                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Disburse to Wallet (Simulation)", fontSize = 11.sp)
                                            }
                                        } else if (loan.status == "DISBURSED") {
                                            Button(
                                                onClick = { viewModel.adminTriggerPenalty(loan.id) },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            ) {
                                                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Trigger Delinquency Penalty (+GHS 10)", fontSize = 11.sp)
                                            }
                                        } else {
                                            Text(
                                                "Fully settled / archived.",
                                                color = MaterialTheme.colorScheme.secondary,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Portfolio spreadsheet metrics
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Portfolio Balance Statement",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Cumulative Disbursed:")
                                    Text("GHS ${String.format(Locale.US, "%,.2f", totalDisbursed)}", fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Interest Revenue Collected:")
                                    Text("GHS ${String.format(Locale.US, "%,.2f", revenueCollected)}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Unpaid Delinquent Debts:")
                                    Text("GHS ${String.format(Locale.US, "%,.2f", outstandingPrincipal)}", fontWeight = FontWeight.Bold, color = Color(0xFFC7161E))
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Penalties Accrued:")
                                    Text("GHS ${String.format(Locale.US, "%,.2f", penaltyOutstanding)}", fontWeight = FontWeight.Bold, color = Color(0xFFC7161E))
                                }
                            }
                        }
                    }

                    // Fraud warnings compliance check metrics
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("⚠️ AI ANTI-FRAUD WATCHDOG FLAGS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Device Clones Detected:", fontSize = 12.sp)
                                    Text("0 (Cleared)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Multiple Card Association:", fontSize = 12.sp)
                                    Text("None", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
