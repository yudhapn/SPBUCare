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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentManageInformationBinding
import com.pertamina.spbucare.model.Document
import com.pertamina.spbucare.model.News
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.util.CHOOSE_FILE_REQUESTCODE
import com.pertamina.spbucare.viewmodel.InformationViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ManageInformationFragment : Fragment() {
    private lateinit var binding: FragmentManageInformationBinding
    private var authorName: String? = ""
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null
    private val informationVM: InformationViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageInformationBinding.inflate(inflater, container, false).apply {
            val user = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
            authorName = user.getString("position", "missing")

            imageAdded.setOnClickListener {
                imageUri = null
                CropImage.activity(imageUri)
                        .setOutputCompressQuality(60)
                        .setAspectRatio(16, 9)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(requireContext(), this@ManageInformationFragment)
            }

            informationVM.networkState.observe(viewLifecycleOwner, Observer { networkState ->
                when (networkState) {
                    NetworkState.SUCCESS -> {
                        binding.progressbar.visibility = View.GONE
                        binding.etCaption.setText("")
                        binding.fileAdded.setImageResource(R.drawable.ic_folder)
                    }
                    NetworkState.RUNNING -> {
                        binding.progressbar.visibility = View.VISIBLE

                        informationVM.progress.observe(viewLifecycleOwner, Observer {
                            binding.progressbar.progress = it.toInt()
                        })
                    }
                    else -> binding.progressbar.visibility = View.GONE
                }
            })
            fileAdded.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                val i = Intent.createChooser(intent, "Document")
                startActivityForResult(i, CHOOSE_FILE_REQUESTCODE)
            }
            btnSaveInformation.setOnClickListener { view ->
                MaterialAlertDialogBuilder(context)
                        .setMessage("Apakah anda yakin ingin MEMBUAT informasi ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya") { _, _ ->
                            createInformation()
                            val action = ManageInformationFragmentDirections.actionShowDashboard()
                            findNavController().navigate(action)
                            Snackbar.make(view, "Informasi telah berhasil dibuat", Snackbar.LENGTH_SHORT).show()
                        }
                        .show()
            }
        }
        return binding.root
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
        } else if (requestCode == CHOOSE_FILE_REQUESTCODE) {
            fileUri = data?.data
            fileUri?.let {
                val fileExtension = fileUri?.let { getFileExtension(it) }
                Glide.with(requireContext()).load(com.pertamina.spbucare.util.setIconFile(fileExtension)).into(binding.fileAdded)
            }
        }
    }

    private fun createInformation() {
        val imageName = System.currentTimeMillis().toString() + "." + imageUri?.let { getFileExtension(it) }
        var document = Document()
        fileUri?.let {
            val caption = binding.etCaption.text.toString()
            val fileExtension = getFileExtension(it)
            val fileName = System.currentTimeMillis().toString() + "." + fileExtension
            document = Document(
                    documentName = fileName,
                    caption = caption
            )

        }
        val information = News(
                imageName = imageName,
                author = authorName.toString(),
                title = binding.etTitle.text.toString(),
                description = binding.etDescription.text.toString()
        )
        informationVM.createInformation(information, imageUri, document, fileUri)
    }

    private fun getFileExtension(uri: Uri) =
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(requireContext().contentResolver.getType(uri))
        } else {
            //If scheme is a Document
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path.toString())).toString())
        }
}
