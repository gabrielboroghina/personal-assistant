package com.example.personalassistant.linked_assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalassistant.databinding.GridViewAssetBinding

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class PhotoGridAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Asset, PhotoGridAdapter.AssetViewHolder>(DiffCallback) {

    /**
     * The AssetViewHolder constructor takes the binding variable from the associated
     * GridViewItem, which gives it access to the full [Asset] information.
     */
    class AssetViewHolder(private var binding: GridViewAssetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asset: Asset) {
            binding.assetThumbnail.setImageURI(asset.uri)
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Asset]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        return AssetViewHolder(GridViewAssetBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asset)
        }
        holder.bind(asset)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Asset]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Asset]
     */
    class OnClickListener(val clickListener: (asset: Asset) -> Unit) {
        fun onClick(asset: Asset) = clickListener(asset)
    }
}

