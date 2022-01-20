package com.example.personalassistant.services.transport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.personalassistant.databinding.RouteSegmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.personalassistant.R
import android.net.Uri
import android.content.Context

import androidx.appcompat.content.res.AppCompatResources
import kotlin.time.Duration.Companion.minutes


class RouteAdapter(private val context: Context) : ListAdapter<RouteSegment, RouteAdapter.RouteSegmentViewHolder>(DiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onBindViewHolder(holder: RouteSegmentViewHolder, position: Int) {
        val item = getItem(position) as RouteSegment
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteSegmentViewHolder {
        return RouteSegmentViewHolder.from(parent, context)
    }

    fun updateRoute(list: List<RouteSegment>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    class RouteSegmentViewHolder private constructor(val binding: RouteSegmentBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: RouteSegment) {
            binding.transportName.text = model.transportName
            binding.duration.text = model.duration.div(60000).minutes.toString()
            if (model.transportType == "TRAM") {
                binding.icon.setImageResource(R.drawable.ic_baseline_tram_24)
            }

            if (model.transportType == "SUBWAY") {
                binding.icon.setImageResource(R.drawable.ic_baseline_directions_subway_24)
            }

            if (model.transportType == "BUS") {
                binding.icon.setImageResource(R.drawable.ic_baseline_directions_bus_24)
            }

            if (model.transportType == "WALK") {
                binding.icon.setImageResource(R.drawable.ic_baseline_directions_walk_24)
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): RouteSegmentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RouteSegmentBinding.inflate(layoutInflater, parent, false)
                return RouteSegmentViewHolder(binding, context)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<RouteSegment>() {
    override fun areItemsTheSame(oldItem: RouteSegment, newItem: RouteSegment): Boolean {
        return oldItem.duration == newItem.duration && oldItem.transportName == newItem.transportName && oldItem.transportType == newItem.transportType
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: RouteSegment, newItem: RouteSegment): Boolean {
        return oldItem == newItem
    }
}
