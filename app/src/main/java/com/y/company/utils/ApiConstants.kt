package com.y.company.utils

import java.net.HttpURLConnection

object ApiConstants {

    object ResponseCode {
        const val FAILURE = -1
        const val OK = HttpURLConnection.HTTP_OK
        const val UNAUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED
        const val DONE = HttpURLConnection.HTTP_CREATED
        const val NO_CONTENT = HttpURLConnection.HTTP_NO_CONTENT
        const val INTERNAL_SERVER_ERROR = HttpURLConnection.HTTP_INTERNAL_ERROR
    }

    object Endpoints {
        const val COLLECTION_PRODUCTS="products"
        const val COLLECTION_RATINGS="ratings"
        const val COLLECTION_CART_ITEMS="cart_items"
        const val COLLECTION_ORDERS="orders"
        const val FILTER_AVG_RATING="avgRating"
        const val FILTER_CATEGORY="category"
        const val FILTER_OFFER="offer"
        const val FILTER_PRICE="price"
        const val FILTER_TIMESTAMP="timestamp"
    }

    object Fields {
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_OFFER = "offer"
        const val FIELD_AVG_RATING = "avgRating"
    }

    enum class Status {
        SUCCESS,
        FAILURE,
        ERROR,
        LOADING
    }

     object MediaType {
        const val AUDIO = "audio/wav"
        const val IMAGE = "image/jpeg"
        const val VIDEO = "video/mp4"
        const val PDF = "application/pdf"
    }

    object AudioLanguage {
        const val ENGLISH = 1
        const val GERMAN = 2
    }
}