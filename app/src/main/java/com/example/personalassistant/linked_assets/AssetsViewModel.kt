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

    private val _assets = MutableLiveData<List<Asset>>()
    val assets: LiveData<List<Asset>>
        get() = _assets

    init {
        _assets.value = assetIds.toMutableList()
    }
}
