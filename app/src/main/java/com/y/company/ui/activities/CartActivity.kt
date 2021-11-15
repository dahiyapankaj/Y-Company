package com.y.company.ui.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.y.company.R
import com.y.company.adapters.CartListAdapter
import com.y.company.data.firestore.ResponseState
import com.y.company.models.CartItem
import com.y.company.utils.FirebaseUtil
import com.y.company.viewmodels.CartViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


enum class OrderStatus(value: Int) {
    PENDING(0),
    CONFIRMED(1),
    DISPATCHED(2),
    DELIVERED(3)
}

class CartActivity : BaseActivity() {

    private val viewModel by viewModel<CartViewModel>()
    private lateinit var imvBackIcon: ImageView
    private lateinit var ivMyOrders: ImageView
    private lateinit var ivCart: ImageView
    private lateinit var tvCartCount: TextView
    private lateinit var txvProceedCheckout: TextView
    private lateinit var tvPayableAmount: TextView

    private lateinit var userId: String
    private var mCartItemsRecycler: RecyclerView? = null
    private var mEmptyView: ViewGroup? = null

    private var mAdapter: CartListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        userId = FirebaseUtil.auth?.currentUser?.email.toString()
        ivCart = findViewById(R.id.iv_cart)
        tvCartCount = findViewById(R.id.tv_cart_count)
        ivMyOrders = findViewById(R.id.iv_order)
        mCartItemsRecycler = findViewById(R.id.rvCart)
        txvProceedCheckout = findViewById(R.id.txvProceedCheckout)
        mEmptyView = findViewById(R.id.view_empty)
        imvBackIcon = findViewById(R.id.imvBackIcon)
        ivMyOrders = findViewById(R.id.iv_order)
        tvPayableAmount = findViewById(R.id.txvCartPrice)
        initViews()
        observeData()
    }

    private fun observeData() {
        viewModel.cartItemsLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        updateTotalValue()
                        initRecyclerView()
                        hideProgressBar()
                    }
                    is ResponseState.Error -> {
                        mCartItemsRecycler?.visibility = GONE
                        mEmptyView?.visibility = VISIBLE
                        showToast(it.failure.message)
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }

        viewModel.itemRemovedLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        showToast(getString(R.string.txt_item_removed))
                        initRecyclerView()
                        updateTotalValue()
                        hideProgressBar()
                    }
                    is ResponseState.Error -> {
                        showToast(it.failure.message)
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }

        viewModel.checkoutLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        initRecyclerView()
                        updateTotalValue()
                        showToast(getString(R.string.txt_order_placed))
                        this.finish()
                        hideProgressBar()
                    }
                    is ResponseState.Error -> {
                        showToast(it.failure.message)
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun initViews() {
        ivCart.visibility = GONE
        tvCartCount.visibility = GONE

        txvProceedCheckout.setOnClickListener {
            viewModel.placeOrder(userId)
        }

        ivMyOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        imvBackIcon.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            // set the new task and clear flags
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartItems(userId)
    }

    private fun updateTotalValue() {
        tvPayableAmount.text = viewModel.getTotalCartValue()
    }

    private fun initRecyclerView() {
        if (viewModel.itemList.isNullOrEmpty()) {
            mCartItemsRecycler?.visibility = GONE
            mEmptyView!!.visibility = VISIBLE

            txvProceedCheckout.isEnabled = false
            txvProceedCheckout.isClickable = false
            // setting colors dynamically to the background
            val unwrappedDrawable: Drawable = txvProceedCheckout.background
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
            DrawableCompat.setTint(
                wrappedDrawable,
                this.resources.getColor(R.color.light_grey)
            )
        } else {
            mCartItemsRecycler?.visibility = VISIBLE
            mEmptyView!!.visibility = GONE

            txvProceedCheckout.isEnabled = true
            txvProceedCheckout.isClickable = true
            // setting colors dynamically to the background
            val unwrappedDrawable: Drawable = txvProceedCheckout.background
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
            DrawableCompat.setTint(
                wrappedDrawable,
                this.resources.getColor(R.color.bg_yellow_v2_end)
            )
        }

        mAdapter = CartListAdapter(
            viewModel.itemList
        ) { item, position ->
            onItemRemoved(item, position)
        }
        mCartItemsRecycler?.layoutManager = LinearLayoutManager(this)
        mCartItemsRecycler?.adapter = mAdapter
    }

    private fun onItemRemoved(cartItem: CartItem, position: Int) {
        viewModel.removeItemFromCart(position)
    }
}