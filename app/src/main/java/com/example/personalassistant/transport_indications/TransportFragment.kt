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
import com.example.personalassistant.services.transport.RouteAdapter
import com.example.personalassistant.services.transport.RoutesAdapter

class TransportFragment : Fragment() {

    /**
     * Lazily initialize our [TransportViewModel].
     */
    private val viewModel: TransportViewModel by lazy {
        ViewModelProvider(this).get(TransportViewModel::class.java)
    }

    private val applicationContext by lazy { activity?.applicationContext }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentTransportBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transport, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val adapter = RoutesAdapter(applicationContext!!)
        binding.routesRecyclerview.adapter = adapter

        viewModel.routes.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.updateRoutes(it)
                }
                //binding.chat.smoothScrollToPosition(adapter.itemCount)
            }
        }

        return binding.root
    }
}
