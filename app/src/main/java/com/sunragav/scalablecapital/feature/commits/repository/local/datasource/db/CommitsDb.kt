package com.sunragav.scalablecapital.feature.commits.repository.local.datasource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CommitsEntity::class, CommitsKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CommitsDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory: Boolean): CommitsDb {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, CommitsDb::class.java)
            } else {
                Room.databaseBuilder(context, CommitsDb::class.java, "commits.db")
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun commits(): CommitsDao
    abstract fun keys(): CommitsKeyDao
}