package com.example.trabalho_final_mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Migration da versão 1 para 2: adiciona tabela de viagens
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS trips (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                destination TEXT NOT NULL,
                type TEXT NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                budget REAL NOT NULL,
                userId INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

// Migration da versão 2 para 3: adiciona coluna totalSpent (total de gastos) na tabela trips
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE trips ADD COLUMN totalSpent REAL NOT NULL DEFAULT 0.0"
        )
    }
}

@Database(
    entities = [UserEntity::class, TripEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viagens_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
