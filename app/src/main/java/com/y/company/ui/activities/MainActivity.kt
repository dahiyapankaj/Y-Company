package com.y.company.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.y.company.R
import com.y.company.adapters.ProductListAdapter
import com.y.company.data.filters.Filters
import com.y.company.data.firestore.ResponseState
import com.y.company.ui.fragments.FilterDialogFragment
import com.y.company.utils.FirebaseUtil
import com.y.company.viewmodels.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val RC_SIGN_IN = 9001
const val LIMIT = 50

class MainActivity : BaseActivity(), View.OnClickListener, FilterDialogFragment.FilterListener,
    ProductListAdapter.OnProductSelectedListener {
    private lateinit var userId: String
    private val TAG = MainActivity::class.java.simpleName


    private var mToolbar: Toolbar? = null
    private var mCurrentSearchView: TextView? = null
    private var mCurrentSortByView: TextView? = null
    private var mProductsRecycler: RecyclerView? = null
    private var mEmptyView: ViewGroup? = null
    private var mQuery: Query? = null

    private var mFilterDialog: FilterDialogFragment? = null
    private var mAdapter: ProductListAdapter? = null
    private lateinit var tvCartCount: AppCompatTextView
    private lateinit var ivCart: ImageView
    private lateinit var ivMyOrders: ImageView
    private val viewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = FirebaseUtil.auth?.currentUser?.email.toString()
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)
        mQuery = viewModel.getProductsQuery()
        initView()
        initRecyclerView()
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

    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mCurrentSearchView = findViewById(R.id.text_current_search)
        mCurrentSortByView = findViewById(R.id.text_current_sort_by)
        mProductsRecycler = findViewById(R.id.recycler_products)
        mEmptyView = findViewById(R.id.view_empty)
        tvCartCount = findViewById(R.id.tv_cart_count)
        ivCart = findViewById(R.id.iv_cart)
        ivMyOrders = findViewById(R.id.iv_order)

        findViewById<View>(R.id.filter_bar).setOnClickListener(this)
        findViewById<View>(R.id.button_clear_filter).setOnClickListener(this)

        ivCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        ivMyOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        // Initializing Filter Dialog
        mFilterDialog = FilterDialogFragment()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartItemCount(userId)
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }
        mAdapter = object : ProductListAdapter(mQuery, this) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    mProductsRecycler?.visibility = View.GONE
                    mEmptyView!!.visibility = View.VISIBLE
                } else {
                    mProductsRecycler?.visibility = View.VISIBLE
                    mEmptyView!!.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException?) {
                // Show a snackbar on errors
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error: check logs for info.", Snackbar.LENGTH_LONG
                ).show()
            }
        }
        mProductsRecycler?.layoutManager = LinearLayoutManager(this)
        mProductsRecycler?.adapter = mAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.filter_bar -> onFilterClicked()
            R.id.button_clear_filter -> onClearFilterClicked()
        }
    }


    override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn()
            return
        }

        // Apply filters
        onFilter(viewModel.getFilters()!!)

        // Start listening for Firestore updates
        mAdapter?.startListening()
    }


    override fun onFilter(filters: Filters?) {
        val query = viewModel.getFilteredQuery(filters)
        mAdapter?.setQuery(query)

        // Set header
        mCurrentSearchView?.text = Html.fromHtml(filters?.getSearchDescription(this))
        mCurrentSortByView?.text = filters?.getOrderDescription(this)

        // Save filters
        viewModel.setFilters(filters)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                FirebaseUtil.authUI?.signOut(this)
                startSignIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

    private fun onAddItemsClicked() {
        // Get a reference to the products collection
        viewModel.addRandomProducts()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            viewModel.setIsSigningIn(false)
            if (resultCode != RESULT_OK && shouldStartSignIn()) {
                startSignIn()
            }
        }
    }

    private fun onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog?.show(supportFragmentManager, FilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        mFilterDialog?.resetFilters()
        onFilter(Filters.default)
    }

    override fun onProductSelected(product: DocumentSnapshot?) {
        // Go to the details page for the selected product
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra(ProductDetailActivity.KEY_PRODUCT_ID, product?.id)
        startActivity(intent)
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.getIsSigningIn() && FirebaseUtil.auth?.currentUser == null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent: Intent = FirebaseUtil.authUI
            ?.createSignInIntentBuilder()
            ?.setAvailableProviders(
                listOf(
                    EmailBuilder().build()
                )
            )
            ?.setIsSmartLockEnabled(false)
            ?.build()!!
        startActivityForResult(intent, RC_SIGN_IN)
        viewModel.setIsSigningIn(true)
    }
}