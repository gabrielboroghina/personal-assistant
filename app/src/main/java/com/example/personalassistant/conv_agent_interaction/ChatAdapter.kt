package com.example.personalassistant.conv_agent_interaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalassistant.databinding.ActionSelectionBinding
import com.example.personalassistant.databinding.MessageBubbleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_ACTION = 0
private val ITEM_VIEW_TYPE_MSG = 1

class ChatAdapter(val linkPhotoListener: View.OnClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun updateMessages(list: List<String>, showActionSelector: Boolean) {
        adapterScope.launch {
            val items =
                list.map { DataItem.MessageItem(it) } + if (showActionSelector) listOf(DataItem.ActionSelector) else listOf()
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageViewHolder -> {
                val item = getItem(position) as DataItem.MessageItem
                holder.bind(item.message)
            }
            is ActionsViewHolder -> {
                holder.bind(linkPhotoListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ACTION -> ActionsViewHolder.from(parent)
            ITEM_VIEW_TYPE_MSG -> MessageViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.ActionSelector -> ITEM_VIEW_TYPE_ACTION
            is DataItem.MessageItem -> ITEM_VIEW_TYPE_MSG
        }
    }

    class ActionsViewHolder private constructor(val binding: ActionSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(linkPhotoListener: View.OnClickListener) {
            binding.actionTakePhoto.setOnClickListener(linkPhotoListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ActionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ActionSelectionBinding.inflate(layoutInflater, parent, false)
                return ActionsViewHolder(binding)
            }
        }
    }


    class MessageViewHolder private constructor(val binding: MessageBubbleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: String) {
            binding.message.text = message
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MessageBubbleBinding.inflate(layoutInflater, parent, false)
                return MessageViewHolder(binding)
            }
        }
    }
}


class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {
    data class MessageItem(val message: String) : DataItem() {
        override val id = message
    }

    object ActionSelector : DataItem() {
        override val id = "#ActionSelection"
    }

    abstract val id: String
}
