package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SecurityScanEntity::class], version = 1, exportSchema = false)
abstract class SecurityDatabase : RoomDatabase() {
    abstract fun securityScanDao(): SecurityScanDao

    companion object {
        @Volatile
        private var INSTANCE: SecurityDatabase? = null

        fun getInstance(context: Context): SecurityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecurityDatabase::class.java,
                    "fx_security_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
