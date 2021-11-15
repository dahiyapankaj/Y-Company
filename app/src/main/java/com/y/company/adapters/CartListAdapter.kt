package com.y.company.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.y.company.R
import com.y.company.models.CartItem

/**
 * RecyclerView adapter for a list of Cart Items.
 */
class CartListAdapter(private val items: List<CartItem>, private val delegate: (CartItem, Int) -> Unit) :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = items[position]

        Glide.with(holder.imageView.context)
            .load(item.photo)
            .into(holder.imageView)
        holder.offerView.text = item.offer
         holder.nameView.text = item.name
         holder.categoryView.text = item.category
         holder.offerView.text = item.offer
         holder.priceView.text = "$${item.price}"

        // Click listener
         holder.removeView.setOnClickListener {
        delegate.invoke(items[position], position)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.item_image)
        var nameView: TextView = itemView.findViewById(R.id.item_name)
        var priceView: TextView = itemView.findViewById(R.id.item_price)
        var categoryView: TextView = itemView.findViewById(R.id.item_category)
        var offerView: TextView = itemView.findViewById(R.id.item_offer)
        var removeView: ImageView = itemView.findViewById(R.id.item_delete)

    }

    override fun getItemCount(): Int {
        return items.size
    }
}