package com.android.re_wind

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.re_wind.data.model.Memo
import com.android.re_wind.data.model.MemoDatabase
import com.android.re_wind.data.repositories.MemoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MemoRepository
    val memo: LiveData<Memo?>

    init {
        val memoDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memoDao)
        memo = repository.memo.asLiveData()
    }

    fun insert(memo: Memo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(memo)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}
