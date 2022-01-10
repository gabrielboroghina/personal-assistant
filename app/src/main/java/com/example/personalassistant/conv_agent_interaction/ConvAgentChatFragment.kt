package com.example.personalassistant.conv_agent_interaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentConvAgentChatBinding


class ConvAgentChatFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentConvAgentChatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_conv_agent_chat, container, false)

        return binding.root
    }
}
