package com.example.personalassistant.linked_assets

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Asset(
    val id: String,
    val uri: Uri,
) : Parcelable

/**
 * The [ViewModel] that is attached to the [AssetsFragment].
 */
class AssetsViewModel(assetIds: Array<Asset>) : ViewModel() {

    // Internally, we use a MutableLiveData, because we will be updating the List of assets with new values
    private val _assets = MutableLiveData<List<Asset>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val assets: LiveData<List<Asset>>
        get() = _assets

    init {
        _assets.value = assetIds.toMutableList()
    }
}
