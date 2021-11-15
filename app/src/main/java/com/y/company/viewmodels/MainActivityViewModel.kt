package com.y.company.viewmodels

import android.app.Application
import android.text.Html
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.y.company.data.filters.Filters
import com.y.company.data.firestore.Failure
import com.y.company.data.firestore.ResponseState
import com.y.company.data.prefs.SharedPrefManager
import com.y.company.models.Product
import com.y.company.ui.activities.LIMIT
import com.y.company.utils.ApiConstants
import com.y.company.utils.ProductsUtil

class MainActivityViewModel(
    val sharedPrefManager: SharedPrefManager, val firestore: FirebaseFirestore
) :
    AndroidViewModel(Application()) {
    private var mIsSigningIn = false
    private var mFilters: Filters? = null
    var cartItemsLiveData = MutableLiveData<ResponseState<Int>>()

    init {
        mIsSigningIn = false
        mFilters = Filters.default
    }

    fun getIsSigningIn(): Boolean {
        return mIsSigningIn
    }

    fun setIsSigningIn(mIsSigningIn: Boolean) {
        this.mIsSigningIn = mIsSigningIn
    }

    fun getFilters(): Filters? {
        return mFilters
    }

    fun setFilters(mFilters: Filters?) {
        this.mFilters = mFilters
    }

    fun getProductsQuery():Query {
        return firestore.collection(ApiConstants.Endpoints.COLLECTION_PRODUCTS)
            .orderBy(ApiConstants.Endpoints.FILTER_AVG_RATING, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())

    }

    fun getCartItemCount(userId: String) {
        cartItemsLiveData.value = ResponseState.Loading
        firestore.collection(ApiConstants.Endpoints.COLLECTION_CART_ITEMS)
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

    fun getFilteredQuery(filters: Filters?): Query {
        // Construct query basic query
        var query: Query = firestore.collection(ApiConstants.Endpoints.COLLECTION_PRODUCTS)

        if (filters != null) {
            // Category (equality filter)
            if (filters.hasCategory()) {
                query = query.whereEqualTo(ApiConstants.Endpoints.FILTER_CATEGORY, filters.category)
            }

            // Offers (equality filter)
            if (filters.hasOffer()) {
                query = query.whereEqualTo(ApiConstants.Endpoints.FILTER_OFFER, filters.offer)
            }

            // Price (equality filter)
            if (filters.hasPrice()) {
                query = when (filters.price) {
                    1 -> {
                        query.whereGreaterThanOrEqualTo(ApiConstants.Endpoints.FILTER_PRICE, 0)
                            .whereLessThan(ApiConstants.Endpoints.FILTER_PRICE, 5000).orderBy(
                                ApiConstants.Endpoints.FILTER_PRICE
                            )
                    }
                    2 -> {
                        query.whereGreaterThanOrEqualTo(ApiConstants.Endpoints.FILTER_PRICE, 5000)
                            .whereLessThan(ApiConstants.Endpoints.FILTER_PRICE, 10000).orderBy(
                                ApiConstants.Endpoints.FILTER_PRICE
                            )
                    }
                    3 -> {
                        query.whereGreaterThan(ApiConstants.Endpoints.FILTER_PRICE, 10000).orderBy(
                            ApiConstants.Endpoints.FILTER_PRICE
                        )
                    }
                    else -> {
                        query.whereGreaterThan(ApiConstants.Endpoints.FILTER_PRICE, 0).orderBy(
                            ApiConstants.Endpoints.FILTER_PRICE
                        )
                    }
                }
            }

            // Sort by (orderBy with direction)
            if (filters.hasSortBy()) {
                query = filters.sortBy?.let {
                    filters.sortDirection?.let { it1 ->
                        query.orderBy(
                            it,
                            it1
                        )
                    }
                }!!
            }

            // Limit items
            query = query.limit(LIMIT.toLong())

        }
        return query
    }

    fun addRandomProducts() {
        val products = firestore.collection(ApiConstants.Endpoints.COLLECTION_PRODUCTS)
        for (i in 0..9) {
            // Get a random Product POJO
            val product: Product = ProductsUtil.getRandom(getApplication())
            // Add a new document to the product collection
            products.add(product)
        }
    }
}
