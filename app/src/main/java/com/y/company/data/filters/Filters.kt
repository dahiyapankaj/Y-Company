package com.y.company.data.filters

import android.content.Context
import android.text.TextUtils
import com.google.firebase.firestore.Query
import java.lang.StringBuilder
import com.y.company.R
import com.y.company.utils.ApiConstants
import com.y.company.utils.ProductsUtil

/**
 * Class for passing filters around.
 */
class Filters {
    var category: String? = null
    var offer: String? = null
    var price = -1
    var sortBy: String? = null
    var sortDirection: Query.Direction? = null
    fun hasCategory(): Boolean {
        return !TextUtils.isEmpty(category)
    }

    fun hasOffer(): Boolean {
        return !TextUtils.isEmpty(offer)
    }

    fun hasPrice(): Boolean {
        return price > 0
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(context: Context): String {
        val desc = StringBuilder()
        if (category == null && offer == null) {
            desc.append("<b>")
            desc.append(context.getString(R.string.all_products))
            desc.append("</b>")
        }
        if (category != null) {
            desc.append("<b>")
            desc.append(category)
            desc.append("</b>")
        }
        if (category != null && offer != null) {
            desc.append(" in ")
        }
        if (offer != null) {
            desc.append("<b>")
            desc.append(offer)
            desc.append("</b>")
        }
        if (price > 0) {
            desc.append(" for ")
            desc.append("<b>")
            desc.append("$$price")
            desc.append("</b>")
        }
        return desc.toString()
    }

    fun getOrderDescription(context: Context): String {
        return when (sortBy) {
            ApiConstants.Fields.FIELD_PRICE -> {
                context.getString(R.string.sorted_by_price)
            }
            ApiConstants.Fields.FIELD_POPULARITY -> {
                context.getString(R.string.sorted_by_popularity)
            }
            else -> {
                context.getString(R.string.sorted_by_rating)
            }
        }
    }

    companion object {
        val default: Filters
            get() {
                val filters = Filters()
                filters.sortBy = ApiConstants.Fields.FIELD_AVG_RATING
                filters.sortDirection = Query.Direction.DESCENDING
                return filters
            }
    }
}