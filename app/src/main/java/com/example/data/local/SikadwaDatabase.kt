package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, LoanEntity::class, ReferralEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SikadwaDatabase : RoomDatabase() {
    abstract fun sikadwaDao(): SikadwaDao

    companion object {
        @Volatile
        private var INSTANCE: SikadwaDatabase? = null

        fun getDatabase(context: Context): SikadwaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SikadwaDatabase::class.java,
                    "sikadwa_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
