package com.y.company.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.y.company.R
import com.y.company.adapters.OrderListAdapter
import com.y.company.data.firestore.ResponseState
import com.y.company.utils.FirebaseUtil
import com.y.company.viewmodels.OrdersViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrdersActivity : BaseActivity() {

    private val viewModel by viewModel<OrdersViewModel>()
    private lateinit var imvBackIcon: ImageView
    private lateinit var ivMyOrders: ImageView
    private lateinit var ivCart: ImageView
    private lateinit var tvCartCount: TextView
    private var rvOrders: RecyclerView? = null
    private var mEmptyView: ViewGroup? = null
    private var mFirestore: FirebaseFirestore? = null
    private lateinit var userId: String
    private var mAdapter: OrderListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        userId = FirebaseUtil.auth?.currentUser?.email.toString()
        mFirestore = FirebaseUtil.firestore
        initView()
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
                        tvCartCount.text = it.item.toString()
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

        viewModel.ordersLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        initRecyclerView()
                        hideProgressBar()
                    }
                    is ResponseState.Error -> {
                        rvOrders?.visibility = View.GONE
                        mEmptyView?.visibility = View.VISIBLE
                        showToast(it.failure.message)
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun initView() {
        ivCart = findViewById(R.id.iv_cart)
        tvCartCount = findViewById(R.id.tv_cart_count)
        ivMyOrders = findViewById(R.id.iv_order)
        rvOrders = findViewById(R.id.rvCart)
        mEmptyView = findViewById(R.id.view_empty)
        imvBackIcon = findViewById(R.id.imvBackIcon)
        ivMyOrders.visibility = View.GONE

        imvBackIcon.setOnClickListener {
            imvBackIcon.setOnClickListener {
                val i = Intent(this, MainActivity::class.java)
                // set the new task and clear flags
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }
        ivCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrdersData(userId)
        viewModel.getCartItemCount(userId)
    }

    private fun initRecyclerView() {
        if (viewModel.ordersList.isNullOrEmpty()) {
            rvOrders?.visibility = View.GONE
            mEmptyView?.visibility = View.VISIBLE
            return
        }
        rvOrders?.visibility = View.VISIBLE
        mEmptyView?.visibility = View.GONE
        mAdapter = OrderListAdapter(viewModel.ordersList)
        rvOrders?.layoutManager = LinearLayoutManager(this)
        rvOrders?.adapter = mAdapter
    }
}