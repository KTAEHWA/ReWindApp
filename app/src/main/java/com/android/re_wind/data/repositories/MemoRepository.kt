package com.android.re_wind.data.repositories

import androidx.annotation.WorkerThread
import com.android.re_wind.data.model.Memo
import com.android.re_wind.data.model.MemoDao
import kotlinx.coroutines.flow.Flow

class MemoRepository(private val memoDao: MemoDao) {
    val memo: Flow<Memo?> = memoDao.getMemo()

    @WorkerThread
    suspend fun insert(memo: Memo) {
        memoDao.insert(memo)
    }

    @WorkerThread
    suspend fun deleteAll() {
        memoDao.deleteAll()
    }
}
