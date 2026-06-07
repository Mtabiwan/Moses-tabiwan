package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SikadwaDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("SELECT * FROM loans ORDER BY dateApplied DESC")
    fun getAllLoansFlow(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans ORDER BY dateApplied DESC")
    suspend fun getAllLoansDirect(): List<LoanEntity>

    @Query("SELECT * FROM loans WHERE id = :id LIMIT 1")
    suspend fun getLoanById(id: Int): LoanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Query("UPDATE loans SET status = :status WHERE id = :loanId")
    suspend fun updateLoanStatus(loanId: Int, status: String)

    @Query("UPDATE loans SET penaltyDays = :days, penaltyAmount = :amount WHERE id = :loanId")
    suspend fun updateLoanPenalty(loanId: Int, days: Int, amount: Double)

    @Query("SELECT * FROM referrals ORDER BY dateReferred DESC")
    fun getAllReferralsFlow(): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity)

    @Query("UPDATE referrals SET status = :status WHERE id = :refId")
    suspend fun updateReferralStatus(refId: Int, status: String)

    @Query("DELETE FROM user_profile")
    suspend fun clearUser()

    @Query("DELETE FROM loans")
    suspend fun clearLoans()

    @Query("DELETE FROM referrals")
    suspend fun clearReferrals()

    @Transaction
    suspend fun resetAllDatabase() {
        clearUser()
        clearLoans()
        clearReferrals()
    }
}
