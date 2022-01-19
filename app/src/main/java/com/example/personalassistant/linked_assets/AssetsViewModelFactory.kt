package com.example.personalassistant.linked_assets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Simple ViewModel factory that provides the MarsProperty and context to the ViewModel.
 */
class AssetsViewModelFactory(
    private val assetIds: Array<Asset>,
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssetsViewModel::class.java)) {
            return AssetsViewModel(assetIds) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
