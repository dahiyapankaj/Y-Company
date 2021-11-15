package com.y.company.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.y.company.data.firestore.Failure
import com.y.company.data.firestore.ResponseState
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.models.Order
import com.y.company.ui.activities.ProductDetailActivity
import com.y.company.utils.ApiConstants

class OrdersViewModel(
    val sharedPrefManager: SharedPrefManager, val fireStore: FirebaseFirestore
) :
    AndroidViewModel(Application()) {
    private var currencySymbol = MutableLiveData("")
    var cartItemsLiveData = MutableLiveData<ResponseState<Int>>()
    var ordersLiveData = MutableLiveData<ResponseState<ArrayList<Order>>>()
    var ordersList = ArrayList<Order>()

    init {
        currencySymbol.value = "$"
    }

    fun getCartItemCount(userId: String) {
        cartItemsLiveData.value = ResponseState.Loading
        fireStore.collection(ApiConstants.Endpoints.COLLECTION_CART_ITEMS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                cartItemsLiveData.value =
                    ResponseState.Success(documents.size())

            }
            .addOnFailureListener { exception ->
                cartItemsLiveData.value =
                    ResponseState.Error(Failure(code = "", message = "Error getting cart items"))

            }
    }

    fun getOrdersData(userId: String) {
        ordersLiveData.value = ResponseState.Loading
        fireStore.collection(ApiConstants.Endpoints.COLLECTION_ORDERS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                ordersList.clear()
                for (document in documents) {
                    Log.d("Tag", "${document.id} => ${document.data}")
                    val item = document.toObject<Order>()
                    item.orderId = document.id
                    ordersList.add(item)
                }
                ordersLiveData.value = ResponseState.Success(ordersList)
            }
            .addOnFailureListener { exception ->
                Log.w(ProductDetailActivity.TAG, "Error getting documents: ", exception)
                cartItemsLiveData.value =
                    ResponseState.Error(Failure(code = "", message = "Error getting your orders"))
            }

    }
}
