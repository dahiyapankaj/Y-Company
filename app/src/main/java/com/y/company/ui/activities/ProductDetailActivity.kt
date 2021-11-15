package com.y.company.ui.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.y.company.R
import com.y.company.adapters.RatingAdapter
import com.y.company.data.firestore.ResponseState
import com.y.company.models.Product
import com.y.company.models.Rating
import com.y.company.ui.fragments.RatingDialogFragment
import com.y.company.ui.fragments.RatingDialogFragment.RatingListener
import com.y.company.utils.FirebaseUtil
import com.y.company.viewmodels.ProductDetailViewModel
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailActivity : BaseActivity(), View.OnClickListener,
    EventListener<DocumentSnapshot>, RatingListener {
    private val viewModel by viewModel<ProductDetailViewModel>()
    private var receivedProduct: Product? = null
    private lateinit var productId: String
    private lateinit var userId: String
    private lateinit var mImageView: ImageView
    private lateinit var mNameView: TextView
    private lateinit var mRatingIndicator: MaterialRatingBar
    private lateinit var mNumRatingsView: TextView
    private lateinit var mCityView: TextView
    private lateinit var mCategoryView: TextView
    private lateinit var mPriceView: TextView
    private lateinit var mEmptyView: ViewGroup
    private lateinit var mRatingsRecycler: RecyclerView
    private lateinit var tvAddToCart: AppCompatTextView
    private lateinit var tvCartCount: AppCompatTextView
    private lateinit var ivCart: ImageView
    private lateinit var ivBack: ImageView
    private lateinit var ivOrders: ImageView
    private lateinit var mRatingDialog: RatingDialogFragment
    private var mProductRegistration: ListenerRegistration? = null
    private var mRatingAdapter: RatingAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get restaurant ID from extras
        productId = intent.extras!!.getString(KEY_PRODUCT_ID)
            ?: throw IllegalArgumentException("Must pass extra $KEY_PRODUCT_ID")
        userId = FirebaseUtil.auth?.currentUser?.email.toString()
        setContentView(R.layout.activity_product_detail)
        initViews()
        initRatingsList()
        observeData()
    }

    private fun initRatingsList() {
        // RecyclerView
        mRatingAdapter = object : RatingAdapter(viewModel.getRatingsQuery(productId)) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    mRatingsRecycler.visibility = View.GONE
                    mEmptyView.visibility = View.VISIBLE
                } else {
                    mRatingsRecycler.visibility = View.VISIBLE
                    mEmptyView.visibility = View.GONE
                }
            }
        }
        mRatingsRecycler.layoutManager = LinearLayoutManager(this)
        mRatingsRecycler.adapter = mRatingAdapter
        mRatingDialog = RatingDialogFragment()
    }

    private fun observeData() {
        viewModel.addToCartLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        showToast(getString(R.string.message_added_to_cart))
                        tvAddToCart.isEnabled = false
                        tvAddToCart.isClickable = false
                        // setting colors dynamically to the background
                        val unwrappedDrawable: Drawable = tvAddToCart.background
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                        DrawableCompat.setTint(
                            wrappedDrawable,
                            this.resources.getColor(R.color.light_grey)
                        )

                        updateCartItemCount()
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

        viewModel.addRatingLiveData.observe(this) { response ->
            response?.let { it ->
                when (it) {
                    is ResponseState.Loading -> {
                        showProgressBar()
                    }
                    is ResponseState.Success -> {
                        mRatingsRecycler.smoothScrollToPosition(0)
                        hideKeyboard()
                        hideProgressBar()
                    }
                    is ResponseState.Error -> {
                        showToast(it.failure.message)
                        hideKeyboard()
                        hideProgressBar()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun initViews() {
        tvAddToCart = findViewById(R.id.add_to_cart)
        tvCartCount = findViewById(R.id.tv_cart_count)
        ivCart = findViewById(R.id.iv_cart)
        ivBack = findViewById(R.id.imvBackIcon)
        ivOrders = findViewById(R.id.iv_order)
        mImageView = findViewById(R.id.restaurant_image)
        mNameView = findViewById(R.id.restaurant_name)
        mRatingIndicator = findViewById(R.id.restaurant_rating)
        mNumRatingsView = findViewById(R.id.restaurant_num_ratings)
        mCityView = findViewById(R.id.restaurant_city)
        mCategoryView = findViewById(R.id.restaurant_category)
        mPriceView = findViewById(R.id.restaurant_price)
        mEmptyView = findViewById(R.id.view_empty_ratings)
        mRatingsRecycler = findViewById(R.id.recycler_ratings)
        findViewById<View>(R.id.restaurant_button_back).setOnClickListener(this)
        findViewById<View>(R.id.fab_show_rating_dialog).setOnClickListener(this)

        tvAddToCart.setOnClickListener {
            viewModel.addToCart(userId, productId, receivedProduct)
        }
        ivOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        ivBack.setOnClickListener {
            onBackArrowClicked(it)
        }
        ivCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        mRatingAdapter!!.startListening()
        mProductRegistration = viewModel.getProductRef(productId).addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()
        mRatingAdapter!!.stopListening()
        if (mProductRegistration != null) {
            mProductRegistration!!.remove()
            mProductRegistration = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.restaurant_button_back -> onBackArrowClicked(v)
            R.id.fab_show_rating_dialog -> onAddRatingClicked(v)
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartItemCount()
        enableAddToCartButton()
    }

    private fun updateCartItemCount() {
        // Get cart items for current user
        viewModel.getCartItemCount(userId)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        enableAddToCartButton()
    }

    private fun enableAddToCartButton() {
        tvAddToCart.isEnabled = true
        tvAddToCart.isClickable = true
        // setting colors dynamically to the background
        val unwrappedDrawable: Drawable = tvAddToCart.background
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
        DrawableCompat.setTint(
            wrappedDrawable,
            this.resources.getColor(R.color.bg_yellow_v2_end)
        )
    }

    /**
     * Listener for the Product document.
     */
    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "product:onEvent", e)
            return
        }
        onProductLoaded(snapshot?.toObject(Product::class.java))
    }

    private fun onProductLoaded(product: Product?) {
        receivedProduct = product
        mNameView.text = product!!.name
        mRatingIndicator.rating = product.avgRating.toFloat()
        mNumRatingsView.text = getString(R.string.fmt_num_ratings, product.noOfRatings)
        mCityView.text = product.offer
        mCategoryView.text = product.category
        mPriceView.text = "$${product.price}"

        // Background image
        Glide.with(mImageView.context)
            .load(product.photo)
            .into(mImageView)
    }

    private fun onBackArrowClicked(view: View?) {
        onBackPressed()
    }

    private fun onAddRatingClicked(view: View?) {
        mRatingDialog.show(supportFragmentManager, RatingDialogFragment.TAG)
    }

    override fun onRating(rating: Rating?) {
        // In a transaction, add the new rating and update the aggregate totals
        if (rating == null) {
            return
        }
        viewModel.addRating(rating, productId)
    }

    companion object {
        const val TAG = "ProductDetail"
        const val KEY_PRODUCT_ID = "key_product_id"
    }
}