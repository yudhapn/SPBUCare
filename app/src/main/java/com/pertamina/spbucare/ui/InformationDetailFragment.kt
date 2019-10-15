package com.pertamina.spbucare.ui


import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pertamina.spbucare.R
import com.pertamina.spbucare.databinding.FragmentDetailInformationBinding
import com.pertamina.spbucare.model.News
import com.pertamina.spbucare.util.*
import java.io.File
import java.io.IOException


class InformationDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailInformationBinding.inflate(inflater, container, false)
        var information = News()
        arguments?.let { information = InformationDetailFragmentArgs.fromBundle(it).informationArg }
        binding.detailNews = information
        requestPermissionsIfNecessary()
        setDocument(information)
        setupListener(information)
        return binding.root
    }

    private fun setupListener(information: News) {
        binding.apply {
            ivFile.setOnClickListener {
                try {
                    val documentName = information.document.documentName
                    val uriDownload = Uri.parse(information.document.documentDownload)
                    val outputDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!outputDir.exists()) {
                        outputDir.mkdirs() // should succeed
                    }

                    val path = "${outputDir.path}/$documentName"
                    val file = File(path)

                    if (file.isFile) {
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
            }
        }
    }

    private fun setDocument(information: News) {
        val documentName = information.document.documentName
        if (documentName.isNotEmpty()) {
            val extension = documentName.substring(documentName.indexOf('.') + 1)
            Glide.with(requireContext()).load(setIconFile(extension)).into(binding.ivFile)
            binding.tvCaption.text = information.document.caption
        }
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