package com.example.personalassistant.transport_indications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import com.example.personalassistant.databinding.RoutesBinding
import com.example.personalassistant.services.transport.Route
import kotlin.time.Duration.Companion.seconds


class RoutesAdapter(private val context: Context) : ListAdapter<Route, RoutesAdapter.RouteViewHolder>(DiffCallbackRoutes()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val item = getItem(position) as Route
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder.from(parent, context)
    }

    fun updateRoutes(list: List<Route>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    class RouteViewHolder private constructor(val binding: RoutesBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Route) {
            binding.transportationOptionDuration.text = model.duration.seconds.toString()
            val childAdapter = RouteAdapter(context);
            binding.routeSegmentsRecyclerview.adapter = childAdapter
            childAdapter.updateRoute(model.segments)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): RouteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RoutesBinding.inflate(layoutInflater, parent, false)
                return RouteViewHolder(binding, context)
            }
        }
    }
}

class DiffCallbackRoutes : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.duration == newItem.duration && oldItem.segments == newItem.segments
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}
