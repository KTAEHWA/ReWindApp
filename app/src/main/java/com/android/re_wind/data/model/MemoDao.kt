package com.android.re_wind.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    @Query("SELECT * FROM memo_table LIMIT 1")
    fun getMemo(): Flow<Memo?>

    @Query("DELETE FROM memo_table")
    suspend fun deleteAll()
}