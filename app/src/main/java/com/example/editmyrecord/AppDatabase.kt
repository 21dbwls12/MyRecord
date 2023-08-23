package com.example.editmyrecord

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Record::class], version = 2)
//@TypeConverters(UriListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "contacts_db"
                ).build()
                db
            }
        }
    }
}

val Migration_1_2 = object  : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE Record_temp (uid INTERGER PRIMARY KEY, title TEXT, place TEXT, date  TEXT, mainText TEXT, tag TEXT, photo TEXT)")
        database.execSQL("INSERT INTO Record_temp (uid, title, place, date, mainText, tag, photo) SELECT uid, title, place, date, mainText, tag, photo FROM Record")
        database.execSQL("DROP TABLE Record")
        database.execSQL("ALTER TABLE Record_temp RENAME TO Record")
    }
}

//@Database(
//    version = 2,
//    entities = [Record::class],
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2)
//    ]
//)
//@TypeConverters(UriListConverter::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun recordDao(): RecordDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val db = Room.databaseBuilder(
//                    context,
//                    AppDatabase::class.java, "contacts_db"
//                ).build()
//                db
//            }
//        }
//    }
//}