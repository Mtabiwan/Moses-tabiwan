package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.local.LoanEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.SikadwaDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SikadwaRepository(private val dao: SikadwaDao) {

    val userProfile: Flow<UserEntity?> = dao.getUserProfileFlow()
    val allLoans: Flow<List<LoanEntity>> = dao.getAllLoansFlow()
    val allReferrals: Flow<List<ReferralEntity>> = dao.getAllReferralsFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Ensuring a default profile exists
    suspend fun checkAndInitializeProfile() = withContext(Dispatchers.IO) {
        val existing = dao.getUserProfileDirect()
        if (existing == null) {
            dao.insertOrUpdateUser(UserEntity())
        }
    }

    suspend fun updateProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateUser(user)
    }

    suspend fun savePin(pin: String) = withContext(Dispatchers.IO) {
        val current = dao.getUserProfileDirect() ?: UserEntity()
        dao.insertOrUpdateUser(current.copy(pinSecured = pin))
    }

    suspend fun completeOnboarding(name: String, phone: String, card: String, city: String) = withContext(Dispatchers.IO) {
        val current = dao.getUserProfileDirect() ?: UserEntity()
        dao.insertOrUpdateUser(
            current.copy(
                fullName = name,
                phone = phone,
                ghanaCardNumber = card,
                regionCity = city,
                isOnboarded = true
            )
        )
    }

    /**
     * core FEATURE: LOAN APPLICATION wizard
     * Evaluates the risk score utilizing Gemini model 'gemini-3.5-flash'.
     * Falls back to high-grade heuristic logic if offline or key is inactive.
     */
    suspend fun submitLoanApplication(
        amount: Double,
        purpose: String,
        durationDays: Int,
        repaymentMethod: String,
        momoProvider: String,
        momoNumber: String
    ): LoanEntity = withContext(Dispatchers.IO) {
        val user = dao.getUserProfileDirect() ?: UserEntity()

        // 1. Evaluate Credit Scoring via API or Heuristics
        val assessment = evaluateCreditRisk(user, amount, purpose)

        // 2. Compute parameters
        val interestRate = assessment.optDouble("interestRate", 0.15)
        val interestAmount = amount * interestRate
        val totalRepayable = amount + interestAmount
        val recommendedLimit = assessment.optDouble("limit", user.monthlyIncome * 0.4)
        val score = assessment.optInt("score", 65)
        
        // Initial status: If risk score is extremely low (e.g., < 40) or request exceeds limit limit, trigger PENDING or AUTO-REJECTED, otherwise PENDING to let Admin or Auto-approval disburse.
        // Let's defaulted newly added loans to "PENDING" so admins can review, or approve immediately if score > 50 for satisfying instant micro-loan feel!
        val status = if (score >= 45 && amount <= recommendedLimit) "APPROVED" else "PENDING"

        val activeLoan = LoanEntity(
            amount = amount,
            purpose = purpose,
            durationDays = durationDays,
            repaymentMethod = repaymentMethod,
            momoProvider = momoProvider,
            momoNumber = momoNumber,
            dueDate = System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L),
            status = status,
            riskScore = score,
            limitRecommended = recommendedLimit,
            interestAmount = interestAmount,
            totalRepayable = totalRepayable
        )

        dao.insertLoan(activeLoan)

        // Update User Loyalty Points or Credit Score if approved
        if (status == "APPROVED") {
            val updatedScore = (user.creditScore + 20).coerceAtMost(850)
            dao.insertOrUpdateUser(user.copy(creditScore = updatedScore))
        }

        return@withContext activeLoan
    }

    /**
     * Executes the direct REST Gemini API request
     */
    private suspend fun evaluateCreditRisk(user: UserEntity, requestedAmount: Double, purpose: String): JSONObject {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("SikadwaRepo", "Gemini API key is empty or using placeholder. Running on-device heuristic engine.")
            return computeHeuristicRisk(user, requestedAmount, purpose)
        }

        val prompt = """
            You are Sikadwa Loan AI Risk Evaluator. Analyze the micro-loan creditworthiness of a user in Ghana.
            
            User Demographics:
            - Full Name: ${user.fullName}
            - Base Credit Score: ${user.creditScore}/850
            - Monthly Income: GHS ${user.monthlyIncome}
            - Employment Status: ${user.employmentStatus}
            - Occupation: ${user.occupation}
            
            Loan Request:
            - Requested Amount: GHS $requestedAmount
            - Purpose of Loan: $purpose
            
            You must run credit calculations and reply with a strictly structured JSON block. Do not add markdown format flags outside the valid JSON.
            JSON Schema:
            {
              "approved": true/false (approve if monthly income is > 1.5x of the loan installment, and score > 400),
              "score": integer_rating_0_to_100,
              "limit": recommended_highest_loan_amount_in_GHS_for_this_user,
              "interestRate": decimal_interest_percentage_between_0.10_and_0.25_based_on_risk,
              "reason": "Clear 1-sentence reason outlining the risk tier or recommendation."
            }
        """.trimIndent()

        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBodyJson = JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBodyJson.toString().toRequestBody(mediaType))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string() ?: ""
                val rootJson = JSONObject(responseBodyStr)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        return JSONObject(text.trim())
                    }
                }
            } else {
                Log.e("SikadwaRepo", "Gemini API HTTP Error: ${response.code} ${response.message}")
            }
        } catch (e: Exception) {
            Log.e("SikadwaRepo", "Exception contacting Gemini API, using offline fallback", e)
        }

        return computeHeuristicRisk(user, requestedAmount, purpose)
    }

    /**
     * Safe fallback scoring algorithm based on pure fintech prudence rules
     */
    private fun computeHeuristicRisk(user: UserEntity, requestedAmount: Double, purpose: String): JSONObject {
        val maxAvailableLimit = (user.monthlyIncome * 0.45).coerceIn(200.0, 5000.0)
        val baseScore = user.creditScore
        
        // Formulate score out of 100
        var safetyRank = 50
        if (baseScore > 600) safetyRank += 25
        if (baseScore > 750) safetyRank += 15
        if (user.employmentStatus == "Employed") safetyRank += 15
        if (user.employmentStatus == "Unemployed") safetyRank -= 30
        
        // Compare request to limit
        val approved = requestedAmount <= maxAvailableLimit && safetyRank >= 40

        val interestRate = when {
            safetyRank > 80 -> 0.11 // VIP rate
            safetyRank > 60 -> 0.15 // Normal rate
            else -> 0.22           // High risk rate
        }

        return JSONObject().apply {
            put("approved", approved)
            put("score", safetyRank.coerceIn(0, 100))
            put("limit", maxAvailableLimit)
            put("interestRate", interestRate)
            put("reason", if (approved) "On-device AI assessment: Optimal debt-to-income ratio." else "Request exceeds recommended local credit capacity limit.")
        }
    }

    /**
     * Admin triggers & action simulation
     */
    suspend fun adminUpdateLoanStatus(loanId: Int, status: String) = withContext(Dispatchers.IO) {
        dao.updateLoanStatus(loanId, status)
        
        // Handle direct cash simulation when disbursed
        if (status == "DISBURSED") {
            val loan = dao.getLoanById(loanId)
            val user = dao.getUserProfileDirect()
            if (loan != null && user != null) {
                // Deposit money directly into customer wallet simulating MoMo disbursement
                dao.insertOrUpdateUser(user.copy(
                    walletBalance = user.walletBalance + loan.amount,
                    loyaltyPoints = user.loyaltyPoints + 15
                ))
            }
        }
    }

    /**
     * User payment simulation
     */
    suspend fun repayLoan(loanId: Int) = withContext(Dispatchers.IO) {
        val loan = dao.getLoanById(loanId)
        val user = dao.getUserProfileDirect()
        if (loan != null && user != null) {
            val costToPay = loan.totalRepayable + loan.penaltyAmount
            val finalBalance = (user.walletBalance - costToPay).coerceAtLeast(0.0)
            
            dao.updateLoanStatus(loanId, "REPAID")
            
            // Raise credit score for successful repayment
            val currentScore = user.creditScore
            val newScore = (currentScore + 45).coerceAtMost(850)
            
            dao.insertOrUpdateUser(user.copy(
                walletBalance = finalBalance,
                creditScore = newScore,
                loyaltyPoints = user.loyaltyPoints + 50 // Cashback point rewards!
            ))
        }
    }

    /**
     * Referral System Simulation
     */
    suspend fun submitReferralCode(code: String) = withContext(Dispatchers.IO) {
        val user = dao.getUserProfileDirect() ?: return@withContext
        if (code.lowercase().contains("sikadwa") && user.referralCount == 0) {
            // Apply GHS 15.0 welcome referral bonus to wallet balance!
            dao.insertOrUpdateUser(user.copy(
                walletBalance = user.walletBalance + 15.0,
                referralCount = 1
            ))
            dao.insertReferral(ReferralEntity(
                refereeName = "Kwame Asante (Simulated)",
                refereePhone = "0244123456",
                status = "SUCCESS",
                bonusCash = 15.0
            ))
        }
    }

    suspend fun simulateReferralInvite(name: String, phone: String) = withContext(Dispatchers.IO) {
        dao.insertReferral(ReferralEntity(
            refereeName = name,
            refereePhone = phone,
            status = "PENDING",
            bonusCash = 15.0
        ))
        
        // Simulating immediate approval for exciting testing:
        val user = dao.getUserProfileDirect() ?: return@withContext
        dao.insertOrUpdateUser(user.copy(
            referralCount = user.referralCount + 1,
            walletBalance = user.walletBalance + 15.0
        ))
    }

    /**
     * Apply Daily Penalty Simulation
     * Increments overdue loans with GHS 5.0 penalty per overdue check
     */
    suspend fun triggerPenaltySimulation(loanId: Int) = withContext(Dispatchers.IO) {
        val loan = dao.getLoanById(loanId)
        if (loan != null && (loan.status == "DISBURSED" || loan.status == "APPROVED")) {
            val updatedPenaltyDays = loan.penaltyDays + 1
            val addedPenaltyAmount = loan.penaltyAmount + 10.0 // GHS 10 flat penalty
            dao.updateLoanPenalty(loanId, updatedPenaltyDays, addedPenaltyAmount)
            
            // Penalty degrades credit score:
            val user = dao.getUserProfileDirect()
            if (user != null) {
                val downgradedScore = (user.creditScore - 30).coerceAtLeast(300)
                dao.insertOrUpdateUser(user.copy(creditScore = downgradedScore))
            }
        }
    }

    suspend fun resetAll() = withContext(Dispatchers.IO) {
        dao.resetAllDatabase()
        checkAndInitializeProfile()
    }
}
