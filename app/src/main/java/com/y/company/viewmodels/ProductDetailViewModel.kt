package com.y.company.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.y.company.data.firestore.Failure
import com.y.company.data.firestore.ResponseState
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.models.CartItem
import com.y.company.models.Product
import com.y.company.models.Rating
import com.y.company.ui.activities.ProductDetailActivity
import com.y.company.utils.ApiConstants

class ProductDetailViewModel(
    val sharedPrefManager: SharedPrefManager, val fireStore: FirebaseFirestore
) :
    AndroidViewModel(Application()) {
    private var currencySymbol = MutableLiveData("")
    var cartItemsLiveData = MutableLiveData<ResponseState<Int>>()
    var addToCartLiveData = MutableLiveData<ResponseState<Boolean>>()
    var addRatingLiveData = MutableLiveData<ResponseState<Boolean>>()

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

    fun addToCart(userId: String, productId: String, receivedProduct: Product?) {
        addToCartLiveData.value = ResponseState.Loading
        val item = fireStore.collection(ApiConstants.Endpoints.COLLECTION_CART_ITEMS)
        val cartItem = CartItem(
            userId = userId,
            productId = productId,
            name = receivedProduct?.name.toString(),
            category = receivedProduct?.category.toString(),
            photo = receivedProduct?.photo.toString(),
            offer = receivedProduct?.offer.toString(),
            price = receivedProduct?.price ?: 0
        )
        item.add(cartItem).addOnSuccessListener {
            addToCartLiveData.value = ResponseState.Success(true)
        }.addOnFailureListener {
            addToCartLiveData.value =
                ResponseState.Error(Failure(code = "", message = "Error adding to cart"))
        }
    }

    fun addRating(rating: Rating, productId: String) {
        addRatingLiveData.value = ResponseState.Loading
        getRatingTask(getProductRef(productId), rating).addOnSuccessListener {
            Log.d(ProductDetailActivity.TAG, "Rating added")
            addRatingLiveData.value = ResponseState.Success(true)
        }.addOnFailureListener { e ->
            Log.w(ProductDetailActivity.TAG, "Add rating failed", e)
            addRatingLiveData.value =
                ResponseState.Error(Failure(code = "", message = "Failed to add rating"))
        }
    }

    private fun getRatingTask(
        productRef: DocumentReference,
        rating: Rating
    ): Task<Void> {
        // Create reference for new rating, for use inside the transaction
        val ratingRef = productRef.collection(ApiConstants.Endpoints.COLLECTION_RATINGS)
            .document()

        // In a transaction, add the new rating and update the aggregate totals
        return fireStore.runTransaction { transaction ->
            val product = transaction[productRef]
                .toObject(Product::class.java)

            // Compute new number of ratings
            val newNumRatings = product!!.noOfRatings + 1

            // Compute new average rating
            val oldRatingTotal = product.avgRating *
                    product.noOfRatings
            val newAvgRating = (oldRatingTotal + rating.rating) /
                    newNumRatings

            // Set new product info
            product.noOfRatings = newNumRatings
            product.avgRating = newAvgRating

            // Commit to Firestore
            transaction[productRef] = product
            transaction[ratingRef] = rating
            null
        }
    }

    fun getProductRef(productId: String): DocumentReference {
        return fireStore.collection(ApiConstants.Endpoints.COLLECTION_PRODUCTS).document(productId)

    }

    fun getRatingsQuery(productId: String): Query {
        return getProductRef(productId)
            .collection(ApiConstants.Endpoints.COLLECTION_RATINGS)
            .orderBy(ApiConstants.Endpoints.FILTER_TIMESTAMP, Query.Direction.DESCENDING)
            .limit(50)
    }
}
