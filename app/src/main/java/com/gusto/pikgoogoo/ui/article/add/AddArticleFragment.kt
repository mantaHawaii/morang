package com.gusto.pikgoogoo.ui.article.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gusto.pikgoogoo.databinding.FragmentAddArticleBinding
import com.gusto.pikgoogoo.util.DataState
import com.gusto.pikgoogoo.ui.components.fragment.LoadingIndicatorFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddArticleFragment(
    private val subjectId: Int
) : LoadingIndicatorFragment() {

    private lateinit var binding: FragmentAddArticleBinding
    private var imageUri: Uri? = null

    private val viewModel: AddArticleViewModel by viewModels()

    private var isSubscribedObserver = false

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.ivPreview.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddArticleBinding.inflate(inflater, container, false)
        val v = binding.root

        binding.bLoadImage.setOnClickListener {
            selectImage()
        }

        binding.bSubmit.setOnClickListener {
            submitArticle()
        }

        binding.sCropImage.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.ivPreview.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                binding.ivPreview.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }

        binding.ibCloseFas.setOnClickListener {
            backPressed(FragmentExitStyle.SLIDE_DOWN)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSubscribedObserver) {
            subscribeObservsers()
        }
    }

    private fun selectImage() {
        getContent.launch("image/*")
    }

    private fun submitArticle() {
        if (imageUri != null) {
            viewModel.submitImageStore(imageUri!!, requireActivity())
        } else {
            viewModel.params.content = binding.etContent.text.toString()
            viewModel.params.subjectId = subjectId
            viewModel.params.imageUrl = ""
            viewModel.params.cropImage = 0
            viewModel.submitArticleInsert()
        }
    }

    private fun subscribeObservsers() {
        isSubscribedObserver = true
        viewModel.insertState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    showMessage(dataState.result)
                    backPressed(FragmentExitStyle.SLIDE_DOWN)
                }
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?:"에러")
                }
            }
        })
        viewModel.storageState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Error -> {
                    loadEnd()
                    showMessage(dataState.exception.localizedMessage?: "에러")
                }
                is DataState.Loading -> {
                    loadStart(dataState.string)
                }
                is DataState.Success -> {
                    loadEnd()
                    val cropImage = if (binding.sCropImage.isChecked) 1 else 0
                    viewModel.params.content = binding.etContent.text.toString()
                    viewModel.params.subjectId = subjectId
                    viewModel.params.imageUrl = dataState.result
                    viewModel.params.cropImage = cropImage
                    viewModel.submitArticleInsert()
                }
            }
        })
    }
    
}