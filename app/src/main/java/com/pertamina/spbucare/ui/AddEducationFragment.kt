package com.pertamina.spbucare.ui


import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.databinding.FragmentAddEducationBinding
import com.pertamina.spbucare.model.Education
import com.pertamina.spbucare.viewmodel.EducationViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddEducationFragment : Fragment() {
    private lateinit var binding: FragmentAddEducationBinding
    private var authorName: String? = ""
    private var imageUri: Uri? = null
    private val educationVM: EducationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEducationBinding.inflate(inflater, container, false)
        val user = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        authorName = user.getString("position", "missing")
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            imageAdded.setOnClickListener {
                CropImage.activity(imageUri)
                    .setOutputCompressQuality(70)
                    .setAspectRatio(16, 9)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(requireContext(), this@AddEducationFragment)
            }
            btnSaveCategory.setOnClickListener { view ->
                val fileName = System.currentTimeMillis().toString() + "." + imageUri?.let { getFileExtension(it) }
                val education = Education(
                    imageName = fileName,
                    author = authorName.toString(),
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString()
                )
                educationVM.createEducation(education, imageUri)
                val action = AddEducationFragmentDirections.actionShowManageEducation()
                findNavController().navigate(action)
                Snackbar.make(view, "Edukasi telah berhasil dibuat", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                Glide.with(requireContext()).load(imageUri).into(binding.imageAdded)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        var extension: String? = ""
        extension = if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(requireContext().contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }
}
