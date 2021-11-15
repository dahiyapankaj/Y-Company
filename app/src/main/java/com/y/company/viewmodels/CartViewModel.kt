package com.y.company.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.y.company.data.firestore.Failure
import com.y.company.data.firestore.ResponseState
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.models.CartItem
import com.y.company.models.Order
import com.y.company.models.Product
import com.y.company.ui.activities.OrderStatus
import com.y.company.ui.activities.ProductDetailActivity
import com.y.company.utils.ApiConstants

class CartViewModel(
    val sharedPrefManager: SharedPrefManager, val fireStore: FirebaseFirestore
) :
    AndroidViewModel(Application()) {
    private val totalCartValue = MutableLiveData(0)
    private var currencySymbol = MutableLiveData("")
    var cartItemsLiveData = MutableLiveData<ResponseState<ArrayList<CartItem>>>()
    var itemRemovedLiveData = MutableLiveData<ResponseState<Boolean>>()
    var checkoutLiveData = MutableLiveData<ResponseState<Boolean>>()
    var itemDocumentsList = ArrayList<DocumentSnapshot>()
    var itemList = ArrayList<CartItem>()

    init {
        currencySymbol.value = "$"
    }

    fun getCartItems(userId: String) {
        cartItemsLiveData.value = ResponseState.Loading
        fireStore.collection(ApiConstants.Endpoints.COLLECTION_CART_ITEMS)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                itemList.clear()
                itemDocumentsList.clear()
                for (document in documents) {
                    Log.d(ProductDetailActivity.TAG, "${document.id} => ${document.data}")
                    val item = document.toObject<CartItem>()
                    itemList.add(item)
                    itemDocumentsList.add(document)
                }
                cartItemsLiveData.value = ResponseState.Success(itemList)
            }
            .addOnFailureListener { exception ->
                cartItemsLiveData.value =
                    ResponseState.Error(Failure(code = "", message = "Error getting documents"))
                itemDocumentsList.clear()
                itemList.clear()
                Log.w(ProductDetailActivity.TAG, "Error getting documents: ", exception)
            }
    }

    fun removeItemFromCart(position: Int) {
        itemRemovedLiveData.value = ResponseState.Loading
        val doc = itemDocumentsList[position].reference
        doc.delete().addOnSuccessListener {
            Log.d("TAG", "DocumentSnapshot successfully deleted!")
            itemList.removeAt(position)
            itemDocumentsList.removeAt(position)
            itemRemovedLiveData.value = ResponseState.Success(true)

        }.addOnFailureListener { e ->
            Log.w("TAG", "Error deleting document", e)
            itemRemovedLiveData.value =
                ResponseState.Error(Failure(code = "", message = "Error removing documents"))
        }
    }


    fun placeOrder(userId: String) {
        checkoutLiveData.value = ResponseState.Loading
        val products = mutableListOf<Product>()
        var totalAmount = 0.0
        itemList.forEach {
            totalAmount += it.price
            products.add(
                Product(
                    id = it.productId,
                    price = it.price,
                    offer = it.offer,
                    photo = it.photo,
                    name = it.name,
                    category = it.category
                )
            )
        }
        val orderCollection = fireStore.collection(ApiConstants.Endpoints.COLLECTION_ORDERS)
        val order = Order(
            userId = userId,
            products = products,
            totalValue = totalAmount,
            status = OrderStatus.PENDING.name
        )
        orderCollection.add(order)

        val writeBatch = fireStore.batch()
        itemList.forEachIndexed { index, cartItem ->
            val doc = itemDocumentsList[index].reference
            doc.let { it1 -> writeBatch.delete(it1) }
        }
        writeBatch.commit().addOnSuccessListener {
            Log.d("TAG", "DocumentSnapshot successfully deleted!")
            itemList.clear()
            itemDocumentsList.clear()
            checkoutLiveData.value = ResponseState.Success(true)
        }.addOnFailureListener {
            itemRemovedLiveData.value =
                ResponseState.Error(Failure(code = "", message = "Error placing order"))
        }
    }

    fun getTotalCartValue(): String {
        totalCartValue.value = 0
        itemList.forEach {
            totalCartValue.value = totalCartValue.value?.plus(it.price)
        }
        return "${currencySymbol.value}${totalCartValue.value.toString()}"
    }
}
