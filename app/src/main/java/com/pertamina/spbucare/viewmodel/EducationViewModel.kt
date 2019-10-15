package com.pertamina.spbucare.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pertamina.spbucare.model.Document
import com.pertamina.spbucare.model.Education
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class EducationViewModel(
    private val repo: OrderRepository,
    private val storage: FirebaseStorage
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val _networkStateFile = MutableLiveData<NetworkState>()
    val networkStateFile: LiveData<NetworkState>
        get() = _networkStateFile
    private val _educations = MutableLiveData<List<Education>>()
    val educations: LiveData<List<Education>>
        get() = _educations
    private val _documents = MutableLiveData<List<Document>>()
    val documents: LiveData<List<Document>>
        get() = _documents
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList
    private val _progress = MutableLiveData<Long>()
    val progress: LiveData<Long>
        get() = _progress

    init {
        getEducations()
    }

    fun createEducation(education: Education, imageUri: Uri?) {
        if (imageUri != null) {
            val storageRef = storage.getReference("educations")
            val fileReference = storageRef.child(education.imageName)
            val uploadTask = imageUri.let { fileReference.putFile(it) }
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    // throw task.getException()
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val imageUrl = downloadUri.toString()
                    education.imageUrl = imageUrl
                    ioScope.launch(getJobErrorHandler() + supervisorJob) {
                        Log.d("testing", "create education")
                        repo.createEducation(education)
                    }
                }
            }
        } else {
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                Log.d("testing", "create education")
                repo.createEducation(education)
            }
        }
    }

    fun addFile(educationId: String, document: Document, fileUri: Uri?) {
        _networkStateFile.postValue(NetworkState.RUNNING)
        val storageRef = storage.getReference("educations")
        val fileReference = storageRef.child(document.documentName)
        val uploadTask = fileUri?.let { fileReference.putFile(it) }
        uploadTask?.addOnFailureListener {
            _networkStateFile.postValue(NetworkState.FAILED)
        }?.addOnSuccessListener {
            _networkStateFile.postValue(NetworkState.SUCCESS)

        }?.addOnProgressListener {
            onProgress(it)
        }?.addOnCompleteListener {
            fileReference.downloadUrl.addOnSuccessListener {
                val fileUrl = it.toString()
                ioScope.launch(getJobErrorHandler() + supervisorJob) {
                    document.documentDownload = fileUrl
                    repo.addEducationDocument(document, educationId)
                    getEducationDocuments(educationId)
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


    fun getEducations() {
        _networkState.postValue(NetworkState.RUNNING)
        var educationList: List<Education>
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            educationList = repo.getEducations()
            _isEmptyList.postValue(educationList.size == 0)
            _educations.postValue(educationList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun getEducationDocuments(educationId: String) {
        _networkState.postValue(NetworkState.RUNNING)
        var documentList: List<Document>
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            documentList = repo.getEducationDocuments(educationId)
            _isEmptyList.postValue(documentList.size == 0)
            _documents.postValue(documentList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
    }

    fun refreshEducation() = getEducations()

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(HistoryViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }

    fun removeItem(position: Int) {
        val documents = _documents.value?.toMutableList()
        documents?.removeAt(position)
        _documents.postValue(documents)
    }

    fun restoreItem(item: Document, position: Int) {
        val documents = _documents.value?.toMutableList()
        documents?.add(position, item)
        _documents.postValue(documents)
    }

    fun getItem(position: Int): Document = _documents.value!!.get(position)

    fun deleteDocument(document: Document, educationId: String) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            repo.deleteEducationDocument(educationId, document)
        }
    }
}