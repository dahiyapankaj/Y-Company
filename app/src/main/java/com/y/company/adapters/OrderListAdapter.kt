package com.y.company.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.y.company.R
import com.y.company.models.Order
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for a list of Order Items.
 */
class OrderListAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {

    @SuppressLint("SimpleDateFormat")
    private val formatDate = SimpleDateFormat("dd-MMM-yyyy")
    private val calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_order, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = orders[position]
        calendar.time = item.timestamp

        holder.tvOrderAmount.text = "$${item.totalValue}"
        holder.tvOrderId.text = item.orderId
        holder.tvOrderDate.text = formatDate.format(calendar.time)
        holder.tvOrderStatus.text = item.status


        holder.llProducts.removeAllViews()
        item.products?.forEach { product ->
            val productLayout =
                LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_cart, null) as RelativeLayout

            val imageView: ImageView = productLayout.findViewById(R.id.item_image)
            val tvName: TextView = productLayout.findViewById(R.id.item_name)
            val tvCategory: TextView = productLayout.findViewById(R.id.item_category)
            val tvPrice: TextView = productLayout.findViewById(R.id.item_price)
            val tvOffer: TextView = productLayout.findViewById(R.id.item_offer)
            val ivDelete: ImageView = productLayout.findViewById(R.id.item_delete)


            Glide.with(imageView.context)
                .load(product.photo)
                .into(imageView)
            tvOffer.text = product.offer
            tvName.text = product.name
            tvCategory.text = product.category
            tvPrice.text = "$${product.price}"
            ivDelete.visibility = View.GONE
            holder.llProducts.addView(productLayout)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvOrderId: TextView = itemView.findViewById(R.id.tv_order_id)
        var tvOrderAmount: TextView = itemView.findViewById(R.id.tv_order_amount)
        var tvOrderDate: TextView = itemView.findViewById(R.id.tv_order_date)
        var tvOrderStatus: TextView = itemView.findViewById(R.id.tv_order_status)
        var llProducts: LinearLayout = itemView.findViewById(R.id.view_products)
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}