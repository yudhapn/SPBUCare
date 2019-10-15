package com.pertamina.spbucare.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pertamina.spbucare.model.Document
import com.pertamina.spbucare.model.News
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class InformationViewModel(
    private val repo: OrderRepository,
    private val storage: FirebaseStorage
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _information = MutableLiveData<List<News>>()
    val information: LiveData<List<News>>
        get() = _information
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList
    private val _progress = MutableLiveData<Long>()
    val progress: LiveData<Long>
        get() = _progress

    init {
        getInformations()
        _networkState.postValue(NetworkState.FAILED)
    }

    fun createInformation(information: News, imageUri: Uri?, document: Document, fileUri: Uri?) {
        if (imageUri != null) {
            val storageRef = storage.getReference("informations")
            val fileReference = storageRef.child(information.imageName)
            val uploadTask = fileReference.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    // throw task.getException()
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val imageUrl = downloadUri.toString()
                    information.imageUrl = imageUrl
                    ioScope.launch(getJobErrorHandler() + supervisorJob) {
                        val informationId = repo.createInformation(information)
                        fileUri?.let {
                            createFile(fileUri, document, informationId, information)
                        }
                    }
                }
            }
        } else {
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                val informationId = repo.createInformation(information)
                fileUri?.let {
                    createFile(fileUri, document, informationId, information)
                }
            }
        }

    }

    fun createFile(fileUri: Uri?, document: Document, informationId: String, information: News) {
        _networkState.postValue(NetworkState.RUNNING)
        val storageRef = storage.getReference("informations")
        val fileReference = storageRef.child(document.documentName)
        val uploadTask = fileUri?.let { fileReference.putFile(it) }
        uploadTask?.addOnFailureListener {
            _networkState.postValue(NetworkState.FAILED)
        }?.addOnSuccessListener {
            _networkState.postValue(NetworkState.SUCCESS)

        }?.addOnProgressListener {
            onProgress(it)
        }?.addOnCompleteListener {
            fileReference.downloadUrl.addOnSuccessListener {
                val fileUrl = it.toString()
                ioScope.launch(getJobErrorHandler() + supervisorJob) {
                    document.documentDownload = fileUrl
                    information.document = document
                    repo.updateInformation(information, informationId)
                }
            }
        }
    }

    private fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
        val fileSize = taskSnapshot!!.getTotalByteCount()
        val uploadBytes = taskSnapshot.getBytesTransferred()
        val progress = (100 * uploadBytes) / fileSize
        _progress.postValue(progress)
    }

    fun getInformations() {
        _networkState.postValue(NetworkState.RUNNING)
        var informationList: List<News>
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            informationList = repo.getInformations()
            _isEmptyList.postValue(informationList.size == 0)
            _information.postValue(informationList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

//    fun refreshHistory(userType: String) = getHistory(userType)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(InformationViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }
}