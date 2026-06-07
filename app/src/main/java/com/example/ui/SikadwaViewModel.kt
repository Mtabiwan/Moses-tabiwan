package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.LoanEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.SikadwaDatabase
import com.example.data.local.UserEntity
import com.example.data.repository.SikadwaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SikadwaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SikadwaDatabase.getDatabase(application)
    private val repository = SikadwaRepository(database.sikadwaDao())

    // Language Toggle: true for English, false for Twi
    var isEnglish by mutableStateOf(true)

    // Dynamic Navigation state
    var currentScreen by mutableStateOf("splash")

    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val loans: StateFlow<List<LoanEntity>> = repository.allLoans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val referrals: StateFlow<List<ReferralEntity>> = repository.allReferrals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSubmittingLoan = MutableStateFlow(false)
    val isSubmittingLoan = _isSubmittingLoan.asStateFlow()

    private val _lastSubmittedLoan = MutableStateFlow<LoanEntity?>(null)
    val lastSubmittedLoan = _lastSubmittedLoan.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndInitializeProfile()
        }
    }

    // --- Authentication ---
    fun submitPin(pin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.savePin(pin)
            onSuccess()
        }
    }

    fun loginWithPin(pin: String, onMatched: () -> Unit, onFailed: () -> Unit) {
        viewModelScope.launch {
            val user = userProfile.value
            if (user != null && user.pinSecured == pin) {
                onMatched()
            } else {
                onFailed()
            }
        }
    }

    // --- Onboarding ---
    fun submitOnboarding(name: String, phone: String, card: String, city: String) {
        viewModelScope.launch {
            repository.completeOnboarding(name, phone, card, city)
            currentScreen = "dashboard"
        }
    }

    // --- Core Loan Wizard ---
    fun applyForLoan(
        amount: Double,
        purpose: String,
        durationDays: Int,
        repaymentMethod: String,
        momoProvider: String,
        momoNumber: String,
        onComplete: (LoanEntity) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmittingLoan.value = true
            val resultingLoan = repository.submitLoanApplication(
                amount = amount,
                purpose = purpose,
                durationDays = durationDays,
                repaymentMethod = repaymentMethod,
                momoProvider = momoProvider,
                momoNumber = momoNumber
            )
            _lastSubmittedLoan.value = resultingLoan
            _isSubmittingLoan.value = false
            onComplete(resultingLoan)
        }
    }

    // --- User Portfolio Operations ---
    fun repayLoan(loanId: Int) {
        viewModelScope.launch {
            repository.repayLoan(loanId)
        }
    }

    fun submitPromoReferral(code: String) {
        viewModelScope.launch {
            repository.submitReferralCode(code)
        }
    }

    fun inviteFriend(name: String, phone: String) {
        viewModelScope.launch {
            repository.simulateReferralInvite(name, phone)
        }
    }

    // --- Administrative System Analytics ---
    fun adminSetStatus(loanId: Int, status: String) {
        viewModelScope.launch {
            repository.adminUpdateLoanStatus(loanId, status)
        }
    }

    fun adminTriggerPenalty(loanId: Int) {
        viewModelScope.launch {
            repository.triggerPenaltySimulation(loanId)
        }
    }

    fun resetSystem() {
        viewModelScope.launch {
            repository.resetAll()
            currentScreen = "login"
        }
    }

    // --- Localization Helper ---
    fun translate(en: String, tw: String): String {
        return if (isEnglish) en else tw
    }
}
