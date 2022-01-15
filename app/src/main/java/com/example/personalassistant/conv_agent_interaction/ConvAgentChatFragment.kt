package com.example.personalassistant.conv_agent_interaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentConvAgentChatBinding
import com.example.personalassistant.services.conv_agent.ChatAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class ConvAgentChatFragment : Fragment() {

    /**
     * Lazily initialize our [ConvAgentChatViewModel].
     */
    private val viewModel: ConvAgentChatViewModel by lazy {
        ViewModelProvider(this).get(ConvAgentChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentConvAgentChatBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_conv_agent_chat, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val linkPhotoListener: View.OnClickListener = View.OnClickListener {
            Toast.makeText(view?.context, "clicked link photo", Toast.LENGTH_SHORT).show()
        }

        val adapter = ChatAdapter(linkPhotoListener)
        binding.chat.adapter = adapter

        viewModel.chatMessages.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addMessageToChat(it)
            }
        }

        binding.sendMessageBtn.setOnClickListener {
            val text: String = binding.newMessage.text.toString()
            viewModel.postAgentMessage(text)
        }

        viewModel.agentResponseStatus.observe(viewLifecycleOwner) { status ->
            status?.let { Snackbar.make(view!!, status, BaseTransientBottomBar.LENGTH_SHORT).show() }
        }

        return binding.root
    }
}
