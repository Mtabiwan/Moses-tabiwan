package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SikadwaViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoanWizardScreen
import com.example.ui.screens.ReferralsScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: SikadwaViewModel = viewModel()
        val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
        val loansList by viewModel.loans.collectAsStateWithLifecycle()
        val referralsList by viewModel.referrals.collectAsStateWithLifecycle()

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          BoxPaddingContainer(
              modifier = Modifier.padding(innerPadding),
              viewModel = viewModel,
              userProfile = userProfile,
              loansList = loansList,
              referralsList = referralsList
          )
        }
      }
    }
  }
}

@Composable
fun BoxPaddingContainer(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    viewModel: SikadwaViewModel,
    userProfile: com.example.data.local.UserEntity?,
    loansList: List<com.example.data.local.LoanEntity>,
    referralsList: List<com.example.data.local.ReferralEntity>
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        when (viewModel.currentScreen) {
            "splash" -> {
                SplashScreen(viewModel) {
                    val dbPin = userProfile?.pinSecured ?: ""
                    if (dbPin.isEmpty()) {
                        viewModel.currentScreen = "login"
                    } else {
                        viewModel.currentScreen = "login"
                    }
                }
            }
            "login" -> {
                LoginScreen(viewModel, userProfile)
            }
            "onboarding" -> {
                OnboardingScreen(viewModel)
            }
            "dashboard" -> {
                DashboardScreen(
                    viewModel = viewModel,
                    user = userProfile,
                    loans = loansList,
                    onApplyLoanClicked = { viewModel.currentScreen = "loan_wizard" },
                    onAdminClicked = { viewModel.currentScreen = "admin" }
                )
            }
            "loan_wizard" -> {
                LoanWizardScreen(
                    viewModel = viewModel,
                    user = userProfile,
                    onFinished = { viewModel.currentScreen = "dashboard" }
                )
            }
            "referrals" -> {
                ReferralsScreen(
                    viewModel = viewModel,
                    user = userProfile,
                    referrals = referralsList
                )
            }
            "profile" -> {
                ProfileScreen(
                    viewModel = viewModel,
                    user = userProfile
                )
            }
            "admin" -> {
                AdminScreen(
                    viewModel = viewModel,
                    loans = loansList,
                    onBack = { viewModel.currentScreen = "dashboard" }
                )
            }
            else -> {
                // Return safely to splash if corrupted
                viewModel.currentScreen = "splash"
            }
        }
    }
}
