package com.pertamina.spbucare.ui


import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentDetailEducationBinding
import com.pertamina.spbucare.model.Education
import com.pertamina.spbucare.ui.adapter.DocumentAdapter
import com.pertamina.spbucare.ui.adapter.DocumentListener
import com.pertamina.spbucare.util.*
import com.pertamina.spbucare.viewmodel.EducationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

class EducationDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailEducationBinding
    private val eduVM: EducationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailEducationBinding.inflate(inflater, container, false).apply {
            var edu = Education()
            arguments?.let {
                edu = EducationDetailFragmentArgs.fromBundle(it).educationArg
            }
            education = edu
            requestPermissionsIfNecessary()
            rvFile.adapter = DocumentAdapter(DocumentListener { document ->
                try {
                    val documentName = document.documentName
                    val uriDownload = Uri.parse(document.documentDownload)
                    val outputDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!outputDir.exists()) {
                        outputDir.mkdirs() // should succeed
                    }

                    val path = "${outputDir.path}/$documentName"
                    val file = File(path)

                    if (file.isFile()) {
                        openFile(file, documentName)
                    } else {
                        MaterialAlertDialogBuilder(context)
                            .setMessage("File belum tersedia di penyimpanan lokal, apakah anda ingin mendownload file ini?")
                            .setNegativeButton("Tidak", null)
                            .setPositiveButton("Iya") { _, _ ->
                                Toast.makeText(getContext(), "Downloading", Toast.LENGTH_SHORT).show()
                                val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
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
                                openFile(file, documentName)
                            }
                            .show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })

            eduVM.getEducationDocuments(edu.educationId)
            setLifecycleOwner(this@EducationDetailFragment)
            educationVM = eduVM
        }
        return binding.root
    }

    private fun getFileExtension(someFilepath: String) =
        someFilepath.substring(someFilepath.lastIndexOf("."))

    private fun openFile(file: File, documentName: String) {
        val target = Intent(Intent.ACTION_VIEW)
        val apkURI = FileProvider.getUriForFile(
            requireContext(),
            requireContext()
                .packageName + ".provider", file
        )
        val type = when (getFileExtension(documentName)) {
            ".flv" -> "video/*"
            ".mp4" -> "video/*"
            ".mp4a" -> "audio/*"
            ".mp3" -> "audio/*"
            ".wav" -> "audio/*"
            ".csv" -> "text/csv"
            ".xls" -> "text/csv"
            ".pdf" -> "application/pdf"
            ".jpeg" -> "image/*"
            ".jpg" -> "image/*"
            ".png" -> "image/*"
            else -> "image/*"
        }
        Log.d("testing", "type: $type")

        target.setDataAndType(apkURI, type)
        target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val intent = Intent.createChooser(target, "Open File")
        startActivity(intent)
    }
    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions(requireContext())) {
            if (permissionRequestCount < maxNumberRequestPermissions) {
                permissionRequestCount += 1
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions.toTypedArray(),
                    requestCodePermission
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.set_permissions_in_settings,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}