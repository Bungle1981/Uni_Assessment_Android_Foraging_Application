package com.example.foragingapplicationv2.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ForageSpotEntity::class], version=2, exportSchema = false)
abstract class ForageSpotDatabase: RoomDatabase() {

    abstract fun forageSpotDao(): ForageSpotDao

    companion object {

        @Volatile
        private var INSTANCE: ForageSpotDatabase? = null

        fun getInstance(context: Context): ForageSpotDatabase {

            synchronized(this) {

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, ForageSpotDatabase::class.java, "forageSpot-database").fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }

                return instance

            }
        }




    }
}