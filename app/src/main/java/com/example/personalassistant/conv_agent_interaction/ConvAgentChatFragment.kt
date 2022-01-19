package com.example.personalassistant.conv_agent_interaction

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.R
import com.example.personalassistant.databinding.FragmentConvAgentChatBinding
import com.example.personalassistant.services.conv_agent.ChatAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.linked_assets.Asset
import java.io.File
import java.util.*

class ConvAgentChatFragment : Fragment() {

    /**
     * Lazily initialize our [ConvAgentChatViewModel].
     */
    private val viewModel: ConvAgentChatViewModel by lazy {
        ViewModelProvider(this).get(ConvAgentChatViewModel::class.java)
    }

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
        (activity as AppCompatActivity).supportActionBar?.title = "Personal Assistant"

        // Get a reference to the binding object and inflate the fragment views
        val binding: FragmentConvAgentChatBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_conv_agent_chat, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val linkPhotoListener: View.OnClickListener = View.OnClickListener {
            takeImage()
        }

        val adapter = ChatAdapter(linkPhotoListener)
        binding.chat.adapter = adapter

        viewModel.chatMessages.observe(viewLifecycleOwner) {
            it?.let {
                adapter.addMessageToChat(it, viewModel.showActionSelector.value ?: false)
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

        viewModel.agentResponseStatus.observe(viewLifecycleOwner) { status ->
            status?.let {
                Snackbar.make(view!!, status, BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }

        viewModel.showAssets.observe(viewLifecycleOwner) { assetIds ->
            if (assetIds != null) {
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

                this.findNavController()
                    .navigate(ConvAgentChatFragmentDirections.actionConvAgentChatFragmentToAssetsFragment(assets.toTypedArray()))
                viewModel.showAssetsPageDone()
            }
        }

        return binding.root
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            viewModel.getTmpFileUri(applicationContext!!).let { uri ->
                viewModel.latestAssetUri = uri
                takeImageResult.launch(uri)
            }
        }
    }
}
