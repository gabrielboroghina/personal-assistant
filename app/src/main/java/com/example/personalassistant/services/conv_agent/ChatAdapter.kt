package com.example.personalassistant.services.conv_agent

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalassistant.R
import com.example.personalassistant.databinding.MessageBubbleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_ACTION = 0
private val ITEM_VIEW_TYPE_MSG = 1

class ChatAdapter() :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addMessageToChat(list: List<String>?) {
        list?.let {
            submitList(list.map { DataItem.MessageItem(it) })
        }

//        adapterScope.launch {
//            val items = when (list) {
//                null -> listOf(DataItem.ActionSelector)
//                else -> listOf(DataItem.ActionSelector) + list.map { DataItem.MessageItem(it) }
//            }
//            withContext(Dispatchers.Main) {
//                submitList(items)
//            }
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.MessageItem
                holder.bind(item.message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ACTION -> ActionsViewHolder.from(parent)
            ITEM_VIEW_TYPE_MSG -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.ActionSelector -> ITEM_VIEW_TYPE_ACTION
            is DataItem.MessageItem -> ITEM_VIEW_TYPE_MSG
        }
    }

    class ActionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): ActionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.action_selection, parent, false)
                return ActionsViewHolder(view)
            }
        }
    }


    class ViewHolder private constructor(val binding: MessageBubbleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: String) {
            binding.message.text = message
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MessageBubbleBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return false
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {
    data class MessageItem(val message: String) : DataItem() {
    }

    object ActionSelector : DataItem() {
    }
}

