package com.pertamina.spbucare.ui


import android.app.Activity
import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentEditEducationBinding
import com.pertamina.spbucare.model.Document
import com.pertamina.spbucare.model.Education
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.ui.adapter.DocumentAdapter
import com.pertamina.spbucare.ui.adapter.DocumentListener
import com.pertamina.spbucare.ui.adapter.RecyclerItemTouchHelperDocument
import com.pertamina.spbucare.util.CHOOSE_FILE_REQUESTCODE
import com.pertamina.spbucare.util.setIconFile
import com.pertamina.spbucare.viewmodel.EducationViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

class EditEducationFragment : Fragment() {
    private lateinit var binding: FragmentEditEducationBinding
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null
    private val eduVM: EducationViewModel by viewModel()
    private lateinit var snackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditEducationBinding.inflate(inflater, container, false)
        var education = Education()
        arguments?.let { education = EditEducationFragmentArgs.fromBundle(it).educationArg }
        binding.educationArg = education
        eduVM.getEducationDocuments(education.educationId)
        binding.lifecycleOwner = this
        binding.educationVM = eduVM
        setupRecyclerView(education)
        setupListener(education)
        return binding.root
    }

    private fun setupListener(education: Education) {
        binding.apply {
            imageAdded.setOnClickListener {
                CropImage.activity(imageUri)
                    .setOutputCompressQuality(70)
                    .setAspectRatio(2, 3)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(requireContext(), this@EditEducationFragment)
            }
            fileAdded.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                val i = Intent.createChooser(intent, "Document")
                startActivityForResult(i, CHOOSE_FILE_REQUESTCODE)
            }
            submitButtonFile.setOnClickListener { view ->
                addFile(education.educationId)
                eduVM.networkStateFile.observe(viewLifecycleOwner, Observer {
                    when (it) {
                        NetworkState.SUCCESS -> {
                            Snackbar.make(view, "File telah berhasil ditambahkan", Snackbar.LENGTH_SHORT).show()
                            binding.progressbar.visibility = View.GONE
                            binding.etCaption.setText("")
                            binding.fileAdded.setImageResource(R.drawable.ic_folder)
                            fileUri = null
                        }
                        NetworkState.RUNNING -> {
                            binding.progressbar.visibility = View.VISIBLE

                            eduVM.progress.observe(viewLifecycleOwner, Observer { progress ->
                                binding.progressbar.progress = progress.toInt()
                            })
                        }
                        else -> binding.progressbar.visibility = View.GONE
                    }
                })
            }
        }
    }

    private fun setupRecyclerView(education: Education) {
        binding.apply {
            rvFile.adapter = DocumentAdapter(DocumentListener { document ->
                try {
                    val documentName = document.documentName
                    val uriDownload = Uri.parse(document.documentDownload)
                    val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!outputDir.exists()) {
                        outputDir.mkdirs() // should succeed
                    }

                    val path = "${outputDir.path}/$documentName"
//                    Toast.makeText(requireContext(), path, Toast.LENGTH_LONG).show()
                    val file = File(path)

                    if (file.isFile) {
                        Toast.makeText(context, "$path/n exists", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show()
                        val downloadManager =
                            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                        val req = DownloadManager.Request(uriDownload)

                        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false)
                            .setTitle("Demo")
                            .setDescription("Something useful. No, really.")
                            .setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                documentName
                            )

                        downloadManager?.enqueue(req)
                    }


                    val target = Intent(Intent.ACTION_VIEW)

                    val apkURI = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext()
                            .packageName + ".provider", file
                    )
                    target.setDataAndType(apkURI, "video/*")
                    target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    val intent = Intent.createChooser(target, "Open File")
                    startActivity(intent)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })


            rvFile.itemAnimator = DefaultItemAnimator()
            val itemTouchHelperCallback =
                RecyclerItemTouchHelperDocument(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, object :
                    RecyclerItemTouchHelperDocument.RecyclerItemTouchHelperListener {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
                        if (viewHolder is DocumentAdapter.ViewHolder) {
                            val documentItem = eduVM.getItem(position)
                            snackbar = Snackbar.make(binding.rootLayout, "File telah dihapus!", Snackbar.LENGTH_LONG)
                            snackbar.setBackgroundTint(Color.parseColor("#DA251C"))
                            eduVM.removeItem(position)
                            deleteOrder(documentItem, position, snackbar, eduVM, education)
                        }
                    }

                    override fun onRefreshDisable(dX: Float, dY: Float) {
//                            val enable = dY == 0f && dX == 0f
//                            binding.refreshLayout.isEnabled = enable
                    }

                })
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvFile)
            rvFile.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun deleteOrder(
        documentItem: Document,
        position: Int,
        snackbar: Snackbar,
        eduVM: EducationViewModel,
        education: Education
    ) {
        MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setMessage("Apakah anda ingin MENHAPUS file ini?")
            .setNegativeButton("Tidak") { _, _ ->
                eduVM.restoreItem(documentItem, position)
            }
            .setPositiveButton("Iya") { _, _ ->
                eduVM.deleteDocument(documentItem, education.educationId)
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
            .show()

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
                Glide.with(requireContext()).load(setIconFile(fileExtension)).into(binding.fileAdded)
            }
        }
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

    private fun addFile(educationId: String) {
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
        eduVM.addFile(educationId, document, fileUri)
    }
}
