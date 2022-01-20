package com.example.personalassistant.transport_indications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentTransportBinding


class TransportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = "Transportation"

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentTransportBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_transport, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Initialize ViewModel
        val journey = TransportFragmentArgs.fromBundle(arguments!!).journey
        val viewModelFactory = TransportViewModelFactory(journey)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(TransportViewModel::class.java)

        viewModel.response.observe(viewLifecycleOwner) { text ->
            binding.dummyText.text = text
        }

        return binding.root
    }
}
