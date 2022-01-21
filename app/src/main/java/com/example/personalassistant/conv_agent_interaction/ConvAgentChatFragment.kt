package com.example.personalassistant.conv_agent_interaction

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentConvAgentChatBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.database.PADatabase
import com.example.personalassistant.linked_assets.Asset
import java.io.File
import java.util.*

class ConvAgentChatFragment : Fragment() {

    /**
     * Lazily initialize our [ConvAgentChatViewModel].
     */
    private lateinit var viewModel: ConvAgentChatViewModel

    private val applicationContext by lazy { activity?.applicationContext }
    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            viewModel.postAssetId(viewModel.latestAssetId ?: "unknown")
        }
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

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = PADatabase.getInstance(application).databaseDao
        val viewModelFactory = ConvAgentViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConvAgentChatViewModel::class.java)

        // Listeners for the inline action buttons
        val linkPhotoListener = View.OnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.getTmpFileUri(applicationContext!!).let { uri ->
                    takeImageResult.launch(uri)
                }
            }
        }

        val adapter = ChatAdapter(linkPhotoListener)
        binding.chat.adapter = adapter

        viewModel.chatMessages.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    // Hide empty state illustration
                    binding.chatEmptyState.visibility = View.INVISIBLE
                    binding.chatEmptyStateTitle.visibility = View.INVISIBLE
                    binding.chatEmptyStateMsg.visibility = View.INVISIBLE
                    binding.chatEmptyStateExtra.visibility = View.INVISIBLE
                }
                // Update the list of messages and scroll to the end
                adapter.updateMessages(it, viewModel.showActionSelector.value ?: false)
                binding.chat.smoothScrollToPosition(adapter.itemCount)
            }
        }

        binding.sendMessageBtn.setOnClickListener {
            val text: String = binding.newMessage.text.toString()
            viewModel.postAgentMessage(text)
            binding.newMessage.text.clear()
        }

        binding.newMessage.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    viewModel.postAgentMessage(v.text.toString())
                    binding.newMessage.text.clear()
                    true
                }
                else -> false
            }
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { status ->
            status?.let {
                Snackbar.make(view!!, status, BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }

        viewModel.showAssets.observe(viewLifecycleOwner) { content ->
            if (content != null) {
                val description = content.first
                val assetIds = content.second
                val storageDir: File? = applicationContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                val assets: MutableList<Asset> = mutableListOf()
                for (assetId in assetIds) {
                    val file = File(storageDir, "$assetId.jpg")
                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(
                            applicationContext!!,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            file
                        )
                        assets.add(Asset(assetId, uri))
                    }
                }

                // Hide keyboard
                val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)

                // Navigate to the assets view
                this.findNavController()
                    .navigate(
                        ConvAgentChatFragmentDirections.actionConvAgentChatFragmentToAssetsFragment(
                            assets.toTypedArray(),
                            description
                        )
                    )
                viewModel.showAssetsPageDone()
            }
        }

        viewModel.transportationLoc.observe(viewLifecycleOwner) { srcDst ->
            if (srcDst !== null) {
                // Navigate to the transportation indications page
                this.findNavController()
                    .navigate(ConvAgentChatFragmentDirections.actionConvAgentChatFragmentToTransportFragment(srcDst))
                viewModel.showTransportationPageDone()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = "Personal Assistant"
    }
}
