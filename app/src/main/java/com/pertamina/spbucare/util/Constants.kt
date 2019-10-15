/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("Constants")

package com.pertamina.spbucare.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.opencsv.CSVWriter
import com.pertamina.spbucare.R
import com.pertamina.spbucare.model.Order
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


const val USER_TYPE = "spbu"
const val CHOOSE_FILE_REQUESTCODE = 10

fun setIconFile(fileExtension: String?) =
    when (fileExtension) {
        "flv" -> R.drawable.video_file
        "mp4" -> R.drawable.video_file
        "mp4a" -> R.drawable.audio_file
        "mp3" -> R.drawable.audio_file
        "wav" -> R.drawable.audio_file
        "csv" -> R.drawable.document_file
        "xls" -> R.drawable.document_file
        "pdf" -> R.drawable.document_file
        "jpeg" -> R.drawable.image_file
        "jpg" -> R.drawable.image_file
        "png" -> R.drawable.image_file
        else -> R.drawable.document_file
    }

fun generateFile(beginDate: String, endDate: String, orders: List<Order>, category: String, context: Context) {
    val writer: CSVWriter?
    try {
        val name = String.format("Laporan-$category-%s.csv", "$beginDate-s-d-$endDate")
        var outputDir = context.filesDir
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val outputFile = File(outputDir, name)
        writer = CSVWriter(FileWriter(outputFile))

        val data = ArrayList<Array<String>>()
        data.add(arrayOf("Laporan Permintaan $category Periode $beginDate s/d $endDate"))
        data.add(arrayOf("Alamat", "SPBU", "P", "BS", "PX", "PL", "DT", "D", "PXT", "Total"))
        var totalPremium = 0
        var totalBiosolar = 0
        var totalPertamax = 0
        var totalPertalite = 0
        var totalDexlite = 0
        var totalPertadex = 0
        var totalPxturbo = 0
        var totalAll = 0
        orders.forEach { order ->
            val totalEachSpbu = order.orderVolume.premium + order.orderVolume.biosolar +
                    order.orderVolume.pertamax + order.orderVolume.pertalite + order.orderVolume.dexlite +
                    order.orderVolume.pertadex + order.orderVolume.pxturbo
            totalPremium += order.orderVolume.premium
            totalBiosolar += order.orderVolume.biosolar
            totalPertamax += order.orderVolume.pertamax
            totalPertalite += order.orderVolume.pertalite
            totalDexlite += order.orderVolume.dexlite
            totalPertadex += order.orderVolume.pertadex
            totalPxturbo += order.orderVolume.pxturbo
            totalAll += totalEachSpbu
            data.add(
                arrayOf(
                    order.adress,
                    order.applicantName,
                    order.orderVolume.premium.toString(),
                    order.orderVolume.biosolar.toString(),
                    order.orderVolume.pertamax.toString(),
                    order.orderVolume.pertalite.toString(),
                    order.orderVolume.dexlite.toString(),
                    order.orderVolume.pertadex.toString(),
                    order.orderVolume.pxturbo.toString(),
                    totalEachSpbu.toString()
                )
            )
        }
        data.add(
            arrayOf(
                "", "", totalPremium.toString(), totalBiosolar.toString(), totalPertamax.toString(),
                totalPertalite.toString(), totalDexlite.toString(), totalPertadex.toString(),
                totalPxturbo.toString(), totalAll.toString()
            )
        )

        writer.writeAll(data) // data is adding to csv

        writer.close()

        val path = "${outputDir.path}/$name"
        Toast.makeText(context, path, Toast.LENGTH_LONG).show()
        val file = File(path)
        val target = Intent(Intent.ACTION_VIEW)

        val apkURI = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
        target.setDataAndType(apkURI, "text/csv")
        target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        val intent = Intent.createChooser(target, "Open File")
        context.startActivity(intent)

    } catch (e: IOException) {
        e.printStackTrace()
    }
}

val requestCodePermission = 101

val maxNumberRequestPermissions = 2

val permissions =
    listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
var permissionRequestCount: Int = 0

/** Permission Checking  */
fun checkAllPermissions(context: Context): Boolean {
    var hasPermissions = true
    for (permission in permissions) {
        hasPermissions = hasPermissions and isPermissionGranted(permission, context)
    }
    return hasPermissions
}

fun isPermissionGranted(permission: String, context: Context) =
    ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED