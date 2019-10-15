package com.pertamina.spbucare.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pertamina.spbucare.model.Order
import com.pertamina.spbucare.network.NetworkState
import com.pertamina.spbucare.repository.OrderRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class OrderViewModel(
    private val repo: OrderRepository,
    private val auth: FirebaseAuth,
    userType: String?
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState
    private val order = MutableLiveData<Order>()
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean>
        get() = _isEmptyList

    init {
            if (userType != null && userType.isNotEmpty()) {
                Log.d("testing", "getOrders called")
                getOrders(userType)
            }
    }

    fun createOrder(orderParamp: Order, isManual: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            if (isManual) {
                repo.createOrder(orderParamp.userId, orderParamp)
            } else {
                if (orderParamp.type.equals("pembatalan kiriman", true)) {
                    orderParamp.orderConfirmation.confirmSR = true
                    orderParamp.orderConfirmation.confirmOH = true
                } else if (orderParamp.type.equals("emergency MS2 manual", true)) {
                    orderParamp.orderConfirmation.confirmSR = true
                    orderParamp.orderConfirmation.confirmOH = true
                    orderParamp.orderConfirmation.confirmSSGA = true
                    orderParamp.orderConfirmation.confirmPN = true
                    orderParamp.orderConfirmation.complete = true
                }
                orderParamp.userId = auth.currentUser?.uid.toString()
                repo.createOrder(auth.currentUser?.uid.toString(), orderParamp)
            }
        }
    }

    fun updateOrder(orderParamp: Order, userType: String?, status: String) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            orderParamp.modifiedOn = Calendar.getInstance().time
            if (status.equals("menerima")) { // approved
                if (orderParamp.type.equals("pembatalan kiriman", true)) {
                    orderParamp.orderConfirmation.confirmSSGA = true
                    orderParamp.orderConfirmation.confirmPN = true
                    orderParamp.orderConfirmation.complete = true
                    orderParamp.open = false
                } else {
                    when (userType) {
                        "sales_retail" -> orderParamp.orderConfirmation.confirmSR = true
                        "sales_services" -> orderParamp.orderConfirmation.confirmSSGA = true
                        "oh" -> orderParamp.orderConfirmation.confirmOH = true
                        "patra_niaga" -> {
                            orderParamp.orderConfirmation.confirmPN = true
                            orderParamp.orderConfirmation.complete = true
                            orderParamp.open = false
                        }
                    }
                }
            } else { // rejected
                orderParamp.orderConfirmation.complete = true
                orderParamp.open = false
            }
            repo.updateOrder(orderParamp.userId, orderParamp)
        }
    }

    fun getOrders(userType: String?): LiveData<List<Order>> {
        _networkState.postValue(NetworkState.RUNNING)
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            val orderList = repo.getOrders(userType, auth.currentUser?.uid.toString())
            _isEmptyList.postValue(orderList.size == 0)
            _orders.postValue(orderList)
            _networkState.postValue(NetworkState.SUCCESS)
        }
        return _orders
    }

    fun refreshOrderConfirm(cookieType: String?) = getOrders(cookieType)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(OrderViewModel::class.java.simpleName, "An error happened: $e")
        _networkState.postValue(NetworkState.FAILED)
    }

    fun removeItem(position: Int) {
        val orders = _orders.value?.toMutableList()
        orders?.removeAt(position)
        _orders.postValue(orders)
    }

    fun restoreItem(item: Order, position: Int) {
        val orders = _orders.value?.toMutableList()
        orders?.add(position, item)
        _orders.postValue(orders)
    }

    fun getItem(position: Int): Order = _orders.value!!.get(position)
}