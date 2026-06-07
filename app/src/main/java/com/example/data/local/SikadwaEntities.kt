package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1, // Single profile row
    val fullName: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val ghanaCardNumber: String = "",
    val phone: String = "",
    val email: String = "",
    val residentialAddress: String = "",
    val regionCity: String = "",
    val employmentStatus: String = "",
    val occupation: String = "",
    val monthlyIncome: Double = 0.0,
    val workAddress: String = "",
    val employerName: String = "",
    val creditScore: Int = 450, // Starting default
    val loyaltyPoints: Int = 100, // Welcome gift
    val walletBalance: Double = 0.0,
    val referralCode: String = "SIKADWA-992A",
    val referralCount: Int = 0,
    val pinSecured: String = "", // 4-digit security code
    val isOnboarded: Boolean = false
)

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val purpose: String,
    val durationDays: Int, // e.g. 14, 30, 90
    val repaymentMethod: String, // "Mobile Money" or "Bank Transfer"
    val momoProvider: String, // "MTN MoMo", "Telecel Cash", "AirtelTigo Money"
    val momoNumber: String,
    val dateApplied: Long = System.currentTimeMillis(),
    val dueDate: Long,
    val status: String, // "PENDING", "APPROVED", "DISBURSED", "REPAID", "REJECTED"
    val riskScore: Int, // Heuristic-based/Gemini-based Risk Rating (0 - 100)
    val limitRecommended: Double,
    val interestAmount: Double,
    val totalRepayable: Double,
    var penaltyAmount: Double = 0.0,
    var penaltyDays: Int = 0
)

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val refereeName: String,
    val refereePhone: String,
    val dateReferred: Long = System.currentTimeMillis(),
    val bonusCash: Double = 15.0, // Ghana Cedi bonus (GHS)
    val status: String // "PENDING", "SUCCESS" (changes to SUCCESS when referees are approved)
)
