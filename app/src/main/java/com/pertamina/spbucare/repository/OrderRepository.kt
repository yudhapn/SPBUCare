package com.pertamina.spbucare.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pertamina.spbucare.model.*
import com.pertamina.spbucare.network.SpbuCareApiService
import kotlinx.coroutines.tasks.await
import java.util.*

class OrderRepository(private val service: SpbuCareApiService) {
    private val tag = OrderRepository::class.java.simpleName

    private val firestore = Firebase.firestore
    private val usersRef = firestore.collection("users")
    private val ordersRef = firestore.collection("orders")
    private val educationsRef = firestore.collection("educations")
    private val informationsRef = firestore.collection("informations")
    private val quizRef = firestore.collection("quiz")

    suspend fun createOrderManual(userId: String, order: Order) =
        service.createOrderManualAsync(userId, order).await()

    suspend fun createOrder(uid: String, order: Order) {
        val orderId = usersRef.document(uid).collection("orders").document().id
        order.orderId = orderId
        usersRef.document("$uid/orders/$orderId").set(order).await()
        ordersRef.document(orderId).set(order).await()
    }

    suspend fun updateOrder(userId: String, order: Order) {
        usersRef.document("$userId/orders/${order.orderId}").set(order, SetOptions.merge()).await()
        ordersRef.document(order.orderId).set(order, SetOptions.merge()).await()
    }

    suspend fun createAccount(email: String, password: String) =
        service.createAccountAsync(email, password).await()

    suspend fun updateAccount(uid: String, email: String, password: String) =
        service.updateAccountAsync(uid, email, password).await()

//    suspend fun createUserData(uid: String, user: User) = service.createUserDataAsync(uid, user).await()

    suspend fun createUserData(uid: String, user: User) {
        usersRef.document(uid).set(user).await()
    }

    suspend fun updateUser(user: User) =
        usersRef.document(user.userId).set(user, SetOptions.merge()).await()

    suspend fun getOrders(cookieType: String?, uid: String): List<Order> {
        var orders = listOf<Order>()
        try {
            val querySnapshot = when (cookieType) {
                "sales_retail" -> ordersRef.whereEqualTo("open", true)
                    .whereEqualTo("orderConfirmation.confirmSR", false)
                    .whereEqualTo("salesRetailId", uid)
                    .orderBy("modifiedOn", Query.Direction.DESCENDING).get().await()
                "oh" -> ordersRef.whereEqualTo("open", true)
                    .whereEqualTo("orderConfirmation.confirmSR", true)
                    .whereEqualTo("orderConfirmation.confirmOH", false)
                    .orderBy("modifiedOn", Query.Direction.DESCENDING).get().await()
                "sales_services" -> ordersRef.whereEqualTo("open", true)
                    .whereEqualTo("orderConfirmation.confirmSR", true)
                    .whereEqualTo("orderConfirmation.confirmOH", true)
                    .whereEqualTo("orderConfirmation.confirmSSGA", false)
                    .orderBy("modifiedOn", Query.Direction.DESCENDING).get().await()
                "patra_niaga" -> ordersRef.whereEqualTo("open", true)
                    .whereEqualTo("orderConfirmation.confirmSR", true)
                    .whereEqualTo("orderConfirmation.confirmOH", true)
                    .whereEqualTo("orderConfirmation.confirmSSGA", true)
                    .whereEqualTo("orderConfirmation.confirmPN", false)
                    .orderBy("modifiedOn", Query.Direction.DESCENDING).get().await()
                else -> usersRef.document(uid).collection("orders")
                    .orderBy("modifiedOn", Query.Direction.DESCENDING).get().await() //spbu
            }
            orders = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return orders
    }

    suspend fun getOrdersByCategory(
        category: String,
        beginDate: Date,
        endDate: Date,
        region: String = ""
    ): List<Order> {
        var orders = listOf<Order>()
        try {
            orders = when (category) {
                "Top" -> {
                    val ordersTop = mutableListOf<Order>()
                    val orders1: List<Order> = if (region.isEmpty()) {
                        Log.d("testing", "ser is empty")
                        ordersRef.whereEqualTo("type", "setor angkut")
                            .whereEqualTo("orderConfirmation.complete", true)
                            .whereEqualTo("orderConfirmation.confirmPN", true)
                            .whereGreaterThanOrEqualTo("createdOn", beginDate)
                            .whereLessThanOrEqualTo("createdOn", endDate)
                            .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
                            .toObjects()
                    } else {
                        Log.d("testing", "ser: $region")
                        ordersRef.whereEqualTo("type", "setor angkut")
                            .whereEqualTo("orderConfirmation.complete", true)
                            .whereEqualTo("orderConfirmation.confirmPN", true)
                            .whereEqualTo("salesRetailId", region)
                            .whereGreaterThanOrEqualTo("createdOn", beginDate)
                            .whereLessThanOrEqualTo("createdOn", endDate)
                            .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
                            .toObjects()
                    }
                    val orders2: List<Order> = if (region.isEmpty()) {
                        ordersRef.whereEqualTo("type", "tambah perencanaan")
                            .whereEqualTo("orderConfirmation.complete", true)
                            .whereEqualTo("orderConfirmation.confirmPN", true)
                            .whereGreaterThanOrEqualTo("createdOn", beginDate)
                            .whereLessThanOrEqualTo("createdOn", endDate)
                            .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
                            .toObjects()
                    } else {
                        ordersRef.whereEqualTo("type", "tambah perencanaan")
                            .whereEqualTo("orderConfirmation.complete", true)
                            .whereEqualTo("orderConfirmation.confirmPN", true)
                            .whereEqualTo("salesRetailId", region)
                            .whereGreaterThanOrEqualTo("createdOn", beginDate)
                            .whereLessThanOrEqualTo("createdOn", endDate)
                            .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
                            .toObjects()
                    }
                    val users: List<User> = usersRef.whereEqualTo("type", "spbu")
                        .orderBy("name", Query.Direction.ASCENDING).get().await().toObjects()
                    ordersTop.addAll(orders1)
                    ordersTop.addAll(orders2)

                    var ordersTemp = mutableListOf<Order>()
                    ordersTop.forEach { obj ->
                        var position = -1
                        if (ordersTemp.size == 0) {
                            ordersTemp.add(obj)
                        } else {
                            for (i in 0 until ordersTemp.size) {
                                if (ordersTemp[i].userId == obj.userId) {
                                    position = i
                                }
                            }
                            if (position != -1) {
                                ordersTemp[position].orderVolume.premium += obj.orderVolume.premium
                                ordersTemp[position].orderVolume.biosolar += obj.orderVolume.biosolar
                                ordersTemp[position].orderVolume.pertamax += obj.orderVolume.pertamax
                                ordersTemp[position].orderVolume.pertalite += obj.orderVolume.pertalite
                                ordersTemp[position].orderVolume.dexlite += obj.orderVolume.dexlite
                                ordersTemp[position].orderVolume.pertadex += obj.orderVolume.pertadex
                                ordersTemp[position].orderVolume.pxturbo += obj.orderVolume.pxturbo
                            } else {
                                ordersTemp.add(obj)
                            }
                        }
                    }

//                    val ordersTemp2 = mutableListOf<Order>()
                    // convert list users to mutablelist users
//                    val usersConvert = users.toMutableList()
                    // mutablelist for object that same between mutablelist _users and _orders2 by applicantname OR name
//                    val usersSame = mutableListOf<User>()
//                    ordersTemp.forEach { order ->
//                        usersConvert.forEach inner@{ user ->
//                            if (order.applicantName == user.name) {
//                                usersSame.add(user)
//                                return@inner
//                            }
//                        }
//                    }
//
//                    //remove object in collection _users which same object with object in collection _users2
//                    usersConvert.removeAll(usersSame)
//                    usersConvert.forEach {
//                        ordersTemp2.add(
//                                Order(
//                                        userId = it.userId,
//                                        applicantName = it.name,
//                                        adress = it.adress,
//                                        open = false
//                                )
//                        )
//                    }
//                    ordersTemp.addAll(ordersTemp2)
                    ordersTemp = ordersTemp.sortedBy { it.applicantName }.toMutableList()
                    ordersTemp = ordersTemp.sortedWith(Comparator { a, b ->
                        val totalA =
                            a.orderVolume.premium + a.orderVolume.biosolar + a.orderVolume.pertamax +
                                    a.orderVolume.pertalite + a.orderVolume.dexlite + a.orderVolume.pertadex +
                                    a.orderVolume.pxturbo
                        val totalB =
                            b.orderVolume.premium + b.orderVolume.biosolar + b.orderVolume.pertamax +
                                    b.orderVolume.pertalite + b.orderVolume.dexlite + b.orderVolume.pertadex +
                                    b.orderVolume.pxturbo
                        when {
                            totalA < totalB -> 1
                            totalA > totalB -> -1
                            else -> 0
                        }
                    }).toMutableList()
                    val chunkResult = ordersTemp.chunked(10)
                    chunkResult[0]
                }
                "Semua" -> {
                    val ordersSema = mutableListOf<Order>()
                    val orders1: List<Order> = ordersRef.whereEqualTo("type", "setor angkut")
                        .whereEqualTo("orderConfirmation.complete", true)
                        .whereEqualTo("orderConfirmation.confirmPN", true)
                        .whereGreaterThanOrEqualTo("createdOn", beginDate)
                        .whereLessThanOrEqualTo("createdOn", endDate)
                        .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                    val orders2: List<Order> = ordersRef.whereEqualTo("type", "tambah perencanaan")
                        .whereEqualTo("orderConfirmation.complete", true)
                        .whereEqualTo("orderConfirmation.confirmPN", true)
                        .whereGreaterThanOrEqualTo("createdOn", beginDate)
                        .whereLessThanOrEqualTo("createdOn", endDate)
                        .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                    ordersSema.addAll(orders1)
                    ordersSema.addAll(orders2)
                    ordersSema.sortedByDescending { it.createdOn }
                }
                "Pembatalan" -> ordersRef.whereEqualTo("type", "pembatalan kiriman")
                    .whereEqualTo("orderConfirmation.complete", true)
                    .whereEqualTo("orderConfirmation.confirmPN", true)
                    .whereGreaterThanOrEqualTo("createdOn", beginDate)
                    .whereLessThanOrEqualTo("createdOn", endDate)
                    .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                "Tarik LO" -> ordersRef.whereEqualTo("type", "tarik LO")
                    .whereEqualTo("orderConfirmation.complete", true)
                    .whereEqualTo("orderConfirmation.confirmPN", true)
                    .whereGreaterThanOrEqualTo("createdOn", beginDate)
                    .whereLessThanOrEqualTo("createdOn", endDate)
                    .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                "Darurat" -> ordersRef.whereEqualTo("type", "emergency MS2 manual")
                    .whereEqualTo("orderConfirmation.complete", true)
                    .whereEqualTo("orderConfirmation.confirmPN", true)
                    .whereGreaterThanOrEqualTo("createdOn", beginDate)
                    .whereLessThanOrEqualTo("createdOn", endDate)
                    .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                "Tambah" -> ordersRef.whereEqualTo("type", "tambah perencanaan")
                    .whereEqualTo("orderConfirmation.complete", true)
                    .whereEqualTo("orderConfirmation.confirmPN", true)
                    .whereGreaterThanOrEqualTo("createdOn", beginDate)
                    .whereLessThanOrEqualTo("createdOn", endDate)
                    .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects()
                else -> ordersRef.whereEqualTo("type", "setor angkut")
                    .whereEqualTo("orderConfirmation.complete", true)
                    .whereEqualTo("orderConfirmation.confirmPN", true)
                    .whereGreaterThanOrEqualTo("createdOn", beginDate)
                    .whereLessThanOrEqualTo("createdOn", endDate)
                    .orderBy("createdOn", Query.Direction.DESCENDING).get().await().toObjects() //hi
            }
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return orders
    }

    suspend fun getNotifications(uid: String): List<Notification> {
        var notifications = listOf<Notification>()
        try {
            val date = Calendar.getInstance()
            date.set(Calendar.HOUR_OF_DAY, 23)
//            val end = date.time
            date.set(Calendar.HOUR_OF_DAY, 1)
            val begin = date.time
            val querySnapshot = usersRef.document(uid).collection("notifications")
                .whereGreaterThan("createdOn", begin)
                .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
            notifications = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return notifications
    }

    suspend fun getEducations(): List<Education> {
        var educations = listOf<Education>()
        try {
            val querySnapshot =
                educationsRef.orderBy("createdOn", Query.Direction.DESCENDING).get().await()
            educations = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return educations
    }

    suspend fun getEducationDocuments(educationId: String): List<Document> {
        var documents = listOf<Document>()
        try {
            val querySnapshot = educationsRef.document(educationId).collection("documents")
                .orderBy("createdOn", Query.Direction.DESCENDING).get().await()
            documents = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return documents
    }


    suspend fun getInformations(): List<News> {
        var informations = listOf<News>()
        try {
            val querySnapshot =
                informationsRef.orderBy("createdOn", Query.Direction.DESCENDING).limit(30).get()
                    .await()
            informations = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return informations
    }

    suspend fun getUser(uid: String): User? {
        var user: User? = User()
        try {
            val docSnapshow = usersRef.document(uid).get().await()
            user = docSnapshow.toObject()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return user
    }

    suspend fun getUsers(type: String): List<User> {
        var users = listOf<User>()
        try {
            val querySnapshot =
                when (type) {
                    "pertamina" -> usersRef.whereEqualTo("pertamina", true).orderBy(
                        "name",
                        Query.Direction.ASCENDING
                    ).get().await()
                    "ser" -> usersRef.whereEqualTo("pertamina", true).whereEqualTo(
                        "type",
                        "sales_retail"
                    ).orderBy("name", Query.Direction.ASCENDING).get().await()
                    else -> usersRef.whereEqualTo("pertamina", false).whereEqualTo(
                        "type",
                        "spbu"
                    ).orderBy("name", Query.Direction.ASCENDING).get().await()
                }
            users = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return users
    }

    suspend fun createEducation(education: Education) {
        val educationId = educationsRef.document().id
        education.educationId = educationId
        educationsRef.document(educationId).set(education).await()
    }

    suspend fun createInformation(information: News): String {
        val informationId = informationsRef.document().id
        information.newsId = informationId
        informationsRef.document(informationId).set(information).await()
        return informationId
    }

    suspend fun updateInformation(information: News, informationId: String) {
        informationsRef.document(informationId).set(information, SetOptions.merge()).await()
    }

    suspend fun addEducationDocument(document: Document, educationId: String) {
        val documentId = educationsRef.document(educationId).collection("documents").document().id
        document.documentId = documentId
        educationsRef.document("$educationId/documents/$documentId")
            .set(document, SetOptions.merge()).await()
    }

    suspend fun createQuiz(quiz: Quiz?, listQuestion: MutableList<Question>) {
        // Get a new write batch
        val batch = firestore.batch()

        val quizId = quizRef.document().id
        quiz?.quizId = quizId
        val quizDocRef = quizRef.document(quizId)
        quiz?.let { batch.set(quizDocRef, it) }

        listQuestion.forEach { question ->
            val questionId = quizRef.document(quizId).collection("question").document().id
            val questionDocRef =
                quizRef.document(quizId).collection("question").document(questionId)
            batch.set(questionDocRef, question)
        }
        batch.commit().await()
    }

    suspend fun deleteEducationDocument(educationId: String, document: Document) {
        educationsRef.document(educationId).collection("documents")
            .document(document.documentId).delete().await()
        val storageRef = FirebaseStorage.getInstance().reference
        storageRef.child("educations/${document.documentName}").delete().await()
    }

    suspend fun getQuiz(): List<Quiz> {
        var quiz = listOf<Quiz>()
        try {
            val querySnapshot =
                quizRef.orderBy("createdOn", Query.Direction.DESCENDING).get().await()
            quiz = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return quiz
    }

    suspend fun updateQuiz(quiz: Quiz) {
        quizRef.document(quiz.quizId).set(quiz, SetOptions.merge()).await()
    }

    suspend fun getQuestions(quizId: String): List<Question> {
        var questions = listOf<Question>()
        try {
            val querySnapshot = quizRef.document(quizId).collection("question").get().await()
            questions = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return questions
    }

    suspend fun submitQuiz(quizResult: QuizResult, quizId: String) {
        val resultId = quizRef.document(quizId).collection("results").document().id
        quizRef.document("$quizId/results/$resultId").set(quizResult).await()
    }

    suspend fun getQuizResult(quizId: String): List<QuizResult> {
        var quizResult = listOf<QuizResult>()
        try {
            val querySnapshot = quizRef.document(quizId).collection("results")
                .whereGreaterThanOrEqualTo("score", -1)
                .whereLessThanOrEqualTo("score", 101).orderBy("score", Query.Direction.DESCENDING)
                .get().await()

            val users: List<User> = usersRef.whereEqualTo("type", "spbu")
                .orderBy("name", Query.Direction.ASCENDING).get().await().toObjects()

            quizResult = querySnapshot.toObjects()

            quizResult.forEach {
                Log.d("testing", "name: ${it.name}, score: ${it.score}")
            }

            val quizResultTemp = mutableListOf<QuizResult>()
            val quizResultTemp2 = mutableListOf<QuizResult>()
            // convert list users to mutablelist users
            val usersConvert = users.toMutableList()
            // mutablelist for object that same between mutablelist _users and _orders2 by applicantname OR name
            val usersSame = mutableListOf<User>()
            quizResult.forEach { result ->
                usersConvert.forEach inner@{ user ->
                    if (result.name == user.name) {
                        usersSame.add(user)
                        return@inner
                    }
                }
            }

            //remove object in collection _users which same object with object in collection _users2
            usersConvert.removeAll(usersSame)
            usersConvert.forEach {
                quizResultTemp2.add(
                    QuizResult(
                        name = it.name
                    )
                )
            }
            quizResultTemp.addAll(quizResult)
            quizResultTemp.addAll(quizResultTemp2)
            quizResult = quizResultTemp
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return quizResult
    }

    suspend fun hasJoinedQuiz(quizId: String): List<QuizResult> {
        var quizResult = listOf<QuizResult>()
        try {
            val querySnapshot = quizRef.document(quizId).collection("results")
                .whereGreaterThanOrEqualTo("score", -1)
                .whereLessThanOrEqualTo("score", 101).orderBy("score", Query.Direction.DESCENDING)
                .get().await()

            quizResult = querySnapshot.toObjects()
        } catch (e: FirebaseFirestoreException) {
            Log.e(tag, e.toString())
        }
        return quizResult
    }
}