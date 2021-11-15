
package com.y.company.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.Query
import com.y.company.R
import com.y.company.data.filters.Filters
import com.y.company.utils.ApiConstants

/**
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment(), View.OnClickListener {
    internal interface FilterListener {
        fun onFilter(filters: Filters?)
    }

    private lateinit var mRootView: View
    private var mCategorySpinner: Spinner? = null
    private var mOfferSpinner: Spinner? = null
    private var mSortSpinner: Spinner? = null
    private var mPriceSpinner: Spinner? = null
    private var mFilterListener: FilterListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false)
        mCategorySpinner = mRootView.findViewById(R.id.spinner_category)
        mOfferSpinner = mRootView.findViewById(R.id.spinner_offers)
        mSortSpinner = mRootView.findViewById(R.id.spinner_sort)
        mPriceSpinner = mRootView.findViewById(R.id.spinner_price)
        mRootView.findViewById<View>(R.id.button_search).setOnClickListener(this)
        mRootView.findViewById<View>(R.id.button_cancel).setOnClickListener(this)
        return mRootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            mFilterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
        }
    }

    private fun onSearchClicked() {
        mFilterListener?.onFilter(filters)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    private val selectedCategory: String?
        private get() {
            val selected = mCategorySpinner?.selectedItem as String
            return if (getString(R.string.value_any_category) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedCity: String?
        private get() {
            val selected = mOfferSpinner!!.selectedItem as String
            return if (getString(R.string.value_any_offer) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedPrice: Int
        private get() {
            return when (mPriceSpinner!!.selectedItem as String) {
                getString(R.string.price_1) -> {
                    1
                }
                getString(R.string.price_2) -> {
                    2
                }
                getString(R.string.price_3) -> {
                    3
                }
                else -> {
                    -1
                }
            }
        }
    private val selectedSortBy: String?
        private get() {
            val selected = mSortSpinner!!.selectedItem as String
            if (getString(R.string.sort_by_rating) == selected) {
                return ApiConstants.Fields.FIELD_AVG_RATING
            }
            if (getString(R.string.sort_by_price) == selected) {
                return ApiConstants.Fields.FIELD_PRICE
            }
            return if (getString(R.string.sort_by_popularity) == selected) {
                ApiConstants.Fields.FIELD_POPULARITY
            } else null
        }
    private val sortDirection: Query.Direction?
        private get() {
            val selected = mSortSpinner?.selectedItem as String
            if (getString(R.string.sort_by_rating) == selected) {
                return Query.Direction.DESCENDING
            }
            if (getString(R.string.sort_by_price) == selected) {
                return Query.Direction.ASCENDING
            }
            return if (getString(R.string.sort_by_popularity) == selected) {
                Query.Direction.DESCENDING
            } else null
        }

    fun resetFilters() {
        mCategorySpinner?.setSelection(0)
        mOfferSpinner?.setSelection(0)
        mPriceSpinner?.setSelection(0)
        mSortSpinner?.setSelection(0)
    }

    private val filters: Filters
        get() {
            val filters = Filters()
            if (mRootView != null) {
                filters.category = selectedCategory
                filters.offer = selectedCity
                filters.price = selectedPrice
                filters.sortBy = selectedSortBy
                filters.sortDirection = sortDirection
            }
            return filters
        }

    companion object {
        const val TAG = "FilterDialog"
    }
}