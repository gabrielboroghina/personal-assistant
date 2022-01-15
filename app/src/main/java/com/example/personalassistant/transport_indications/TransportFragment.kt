package com.example.personalassistant.transport_indications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentTransportBinding


class TransportFragment : Fragment() {

    /**
     * Lazily initialize our [TransportViewModel].
     */
    private val viewModel: TransportViewModel by lazy {
        ViewModelProvider(this).get(TransportViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentTransportBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transport, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        viewModel.response.observe(viewLifecycleOwner) { text ->
            binding.dummyText.text = text
        }

        return binding.root
    }
}
