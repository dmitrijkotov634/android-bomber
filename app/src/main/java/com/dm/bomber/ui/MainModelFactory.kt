package com.dm.bomber.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager

class MainModelFactory(
    private val repository: MainRepository,
    private val workManager: WorkManager
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == MainViewModel::class.java) return MainViewModel(repository, workManager) as T
        throw IllegalArgumentException()
    }
}
