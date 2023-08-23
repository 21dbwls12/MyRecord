package com.example.editmyrecord

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM record")
    fun getAll(): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Record>

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): Record

    @Insert
    fun insertAll(vararg records: Record)

    @Delete
    fun delete(record: Record)

    @Update
    suspend fun update(record: Record)
}