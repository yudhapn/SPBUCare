package com.pertamina.spbucare.ui.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.pertamina.spbucare.R
import com.pertamina.spbucare.model.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter("setDateModief")
fun TextView.setDateModief(date: Date) {
    val myFormat = "dd MMM, yyyy" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    text = sdf.format(date.time)
}

@BindingAdapter("setTimeModief")
fun TextView.setTimeModief(date: Date) {
    val myFormat = "hh:mm" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    text = sdf.format(date.time)
}


@BindingAdapter("setDateTime")
fun TextView.setDateTime(date: Date) {
    val myFormat = "hh:mm | dd MMM, yyyy" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    text = sdf.format(date.time)
}

@BindingAdapter("setTotal")
fun TextView.setTotal(data: OrderVolume) {
    val total =
        data.premium + data.biosolar + data.pertamax + data.dexlite + data.pxturbo + data.pertadex + data.pertalite
    text = total.toString()
}

@BindingAdapter("quizListData")
fun RecyclerView.bindRvQuiz(data: List<Quiz>?) {
    val adapter = adapter as QuizAdapter
    adapter.submitList(data)
}

@BindingAdapter("documentListData")
fun RecyclerView.bindRvDocuments(data: List<Document>?) {
    val adapter = adapter as DocumentAdapter
    adapter.submitList(data)
}

@BindingAdapter("usersListData")
fun RecyclerView.bindRvUsers(data: List<User>?) {
    val adapter = adapter as UserAdapter
    adapter.submitList(data)
}

@BindingAdapter("informationListData")
fun RecyclerView.bindRvInformation(data: List<News>?) {
    val adapter = adapter as InformationAdapter
    adapter.submitList(data)
}

@BindingAdapter("educationListData")
fun RecyclerView.bindRvEducation(data: List<Education>?) {
    val adapter = adapter as EducationAdapter
    adapter.submitList(data)
}

@BindingAdapter("confirmListData")
fun RecyclerView.bindRvConfirm(data: List<Order>?) {
    val adapter = adapter as ConfirmAdapter
    adapter.submitList(data)
}

@BindingAdapter("historyListData")
fun RecyclerView.bindRvHistory(data: List<Order>?) {
    val adapter = adapter as HistoryAdapter
    adapter.submitList(data)
}

@BindingAdapter("topTenListData")
fun RecyclerView.bindRvTopTen(data: List<Order>?) {
    val adapter = adapter as TopTenAdapter
    adapter.submitList(null)
    adapter.submitList(data)
}

@BindingAdapter("notifListData")
fun RecyclerView.bindRvNotification(data: List<Notification>?) {
    val adapter = adapter as NotificationAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl", "imageType")
fun ImageView.loadImage(url: String?, type: String = "") {
    val placeholder =
        if (type.equals("information")) R.drawable.information_poster else R.drawable.education_poster
    Glide.with(context).load(url).placeholder(placeholder)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(26))).into(this)
}

@BindingAdapter("setCamelCase")
fun TextView.setCamelCase(value: String?) {
    text = value?.trim()?.capitalizeWords()
}

@BindingAdapter("setStatusOpen")
fun ImageView.setStatusOpen(isOpen: Boolean) {
    if (isOpen) {
        Glide.with(context).load(R.drawable.open_status).into(this)
    } else {
        Glide.with(context).load(R.drawable.close_status).into(this)
    }
}

private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

@BindingAdapter("setOrderIcon", "setPosition")
fun ImageView.setOrderIcon(type: String, position: Int = -1) {
    var image = 0
    if (position == -1) {
        image = when (type) {
            "pembatalan kiriman" -> R.drawable.cancel_menu
            "tambah perencanaan" -> R.drawable.add_menu
            "setor angkut" -> R.drawable.deposit_menu
            "emergency MS2 manual" -> R.drawable.emergency_menu
            "tarik LO" -> R.drawable.withdrawal_menu
            else -> R.drawable.cancel_menu
        }
    } else {
        image = when (position + 1) {
            1 -> R.drawable.ic_one
            2 -> R.drawable.ic_two
            3 -> R.drawable.ic_three
            4 -> R.drawable.ic_four
            5 -> R.drawable.ic_five
            6 -> R.drawable.ic_six
            7 -> R.drawable.ic_seven
            8 -> R.drawable.ic_eight
            9 -> R.drawable.ic_nine
            else -> R.drawable.ic_ten
        }
    }
    setBackgroundResource(image)
}

@BindingAdapter("setOrderIconSmall")
fun ImageView.setOrderIconSmall(type: String) {
    val image = when (type) {
        "pembatalan kiriman" -> R.drawable.cancel_menu_small
        "tambah perencanaan" -> R.drawable.add_menu_small
        "setor angkut" -> R.drawable.deposit_menu_small
        "emergency MS2 manual" -> R.drawable.emergency_menu_small
        "tarik LO" -> R.drawable.withdrawal_menu
        else -> R.drawable.cancel_menu
    }
    setBackgroundResource(image)
}

@BindingAdapter("setConfirmIcon", "confirmBy")
fun ImageView.setConfirmIcon(isConfirm: Boolean, confirmBy: String) {
    var image: Int
    if (isConfirm) {
        image = when (confirmBy) {
            "sr" -> R.drawable.label_sr_acc
            "oh" -> R.drawable.label_oh_acc
            else -> R.drawable.label_pn_acc
        }
    } else {
        image = when (confirmBy) {
            "sr" -> R.drawable.label_sr
            "oh" -> R.drawable.label_oh
            else -> R.drawable.label_pn
        }
    }
    Glide.with(context).load(image).into(this)
}

@BindingAdapter("setType")
fun ImageView.setType(type: String) {
    when (type) {
        "P" -> Glide.with(context).load(R.drawable.premium).into(this)
        "BS" -> Glide.with(context).load(R.drawable.biosolar).into(this)
        "PX" -> Glide.with(context).load(R.drawable.pertamax).into(this)
        "PL" -> Glide.with(context).load(R.drawable.pertalite).into(this)
        "DT" -> Glide.with(context).load(R.drawable.dexlite).into(this)
        "D" -> Glide.with(context).load(R.drawable.pertadex).into(this)
        "PXT" -> Glide.with(context).load(R.drawable.pxturbo).into(this)
    }
}

@BindingAdapter("setTime")
fun TextView.setTime(createdOn: Date) {
    try {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - createdOn.getTime())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - createdOn.getTime())
        val hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - createdOn.getTime())
        val days = TimeUnit.MILLISECONDS.toDays(now.getTime() - createdOn.getTime())

        if (seconds < 60) {
            text = "$seconds seconds ago"
        } else if (minutes < 60) {
            text = "$minutes minutes ago"
        } else if (hours < 24) {
            text = "$hours hours ago"
        } else {
            text = "$days days ago"
        }
    } catch (j: Exception) {
        j.printStackTrace()
    }
}

@BindingAdapter("setOrder", "setOrderType")
fun TextView.setOrder(volume: Int, type: String) {
    if (volume > 0) {
        text = type + volume

    } else {
        text = type + "-"
    }
}

@BindingAdapter("setTotalItem")
fun TextView.setTotalItem(orderVolume: OrderVolume) {
    val total =
        orderVolume.premium + orderVolume.biosolar + orderVolume.pertamax + orderVolume.pertalite + orderVolume.dexlite +
                orderVolume.pertadex + orderVolume.pxturbo
    text = context.getString(R.string.set_total, total.toString())
}

@BindingAdapter("setIconTop")
fun ImageView.setIconTop(position: Int) {
    when (position + 1) {
        1 -> Glide.with(context).load(R.drawable.ic_one).into(this)
        2 -> Glide.with(context).load(R.drawable.ic_two).into(this)
        3 -> Glide.with(context).load(R.drawable.ic_three).into(this)
        4 -> Glide.with(context).load(R.drawable.ic_four).into(this)
        5 -> Glide.with(context).load(R.drawable.ic_five).into(this)
        6 -> Glide.with(context).load(R.drawable.ic_six).into(this)
        7 -> Glide.with(context).load(R.drawable.ic_seven).into(this)
        8 -> Glide.with(context).load(R.drawable.ic_eight).into(this)
        9 -> Glide.with(context).load(R.drawable.ic_nine).into(this)
        10 -> Glide.with(context).load(R.drawable.ic_ten).into(this)
    }
}

@BindingAdapter("setPeriodBegin", "setPeriodEnd")
fun TextView.setPeriod(begin: Date?, end: Date?) {
    val myFormat = "dd-MMM" // mention the format you need
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    if (begin != null && end != null)
        text = context.getString(R.string.set_periode, sdf.format(begin.time), sdf.format(end.time))
}

@BindingAdapter("setDocumentIcon")
fun ImageView.setDocumentIcon(nameDocument: String) {
    val image = when (nameDocument.substring(nameDocument.lastIndexOf(".") + 1)) {
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
    Glide.with(context).load(image).into(this)
}

@BindingAdapter("setNameSer")
fun TextView.setNameSer(user: User?) {
    text = user?.name
}

@BindingAdapter("setStatusOpen", "setStatusComplete")
fun ImageView.setStatusQuiz(isOpen: Boolean, isComplete: Boolean) {
    if (isOpen) {
        Glide.with(context).load(R.drawable.open_status).into(this)
    } else {
        if (isComplete) {
            Glide.with(context).load(R.drawable.complete_status).into(this)
        } else {
            Glide.with(context).load(R.drawable.close_status).into(this)
        }
    }
}

@BindingAdapter("setHintName", "viewType")
fun TextInputLayout.setHintName(userType: String?, viewType: String) {
    if (viewType == "name") {
        hint = if (userType == "spbu")
            context.getString(R.string.nama_penganggung_jawab)
        else
            context.getString(R.string.nama_pekerja)
    } else {
        hint = if (userType == "spbu")
            context.getString(R.string.nomor_spbu)
        else
            context.getString(R.string.nomor_pekerja)
    }
}

@BindingAdapter("tilVisibility", "tilType")
fun TextInputLayout.tilVisibility(userType: String?, viewType: String) {
    if (viewType == "position") {
        visibility = if (userType == "spbu")
            View.GONE
        else
            View.VISIBLE
    } else {
        visibility = if (userType == "spbu")
            View.VISIBLE
        else
            View.GONE
    }
}