package com.example.personalassistant.linked_assets

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.databinding.FragmentAssetsBinding
import java.io.File
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.example.personalassistant.BuildConfig


/**
 * Display the list of image assets linked to a specific description.
 */
class AssetsFragment : Fragment() {

    private val applicationContext by lazy { activity?.applicationContext }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.title = "Assets"

        val binding = FragmentAssetsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val description = AssetsFragmentArgs.fromBundle(arguments!!).description
        binding.assetsTitle.text = description

        // Initialize ViewModel
        val assets = AssetsFragmentArgs.fromBundle(arguments!!).assets
        val viewModelFactory = AssetsViewModelFactory(assets)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(AssetsViewModel::class.java)

        // Sets the adapter of the photosGrid RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        val adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            val storageDir: File? = applicationContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, "${it.id}.jpg")

            val intent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(
                    FileProvider.getUriForFile(applicationContext!!, "${BuildConfig.APPLICATION_ID}.provider", file),
                    "image/*"
                ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        })
        binding.photosGrid.adapter = adapter

        viewModel.assets.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                // Hide empty state illustration
                binding.emptyState.visibility = View.INVISIBLE
            }
            adapter.submitList(it)
        }

        setHasOptionsMenu(true)
        return binding.root
    }
}
