/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.y.company.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.y.company.R
import com.y.company.models.Product
import com.y.company.utils.ProductsUtil
import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * RecyclerView adapter for a list of Products.
 */
open class ProductListAdapter(query: Query?, private val mListener: OnProductSelectedListener) :
    FirestoreAdapter<ProductListAdapter.ViewHolder?>(query) {
    interface OnProductSelectedListener {
        fun onProductSelected(productDocument: DocumentSnapshot?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_product, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.item_image)
        var nameView: TextView = itemView.findViewById(R.id.item_name)
        var ratingBar: MaterialRatingBar = itemView.findViewById(R.id.item_rating)
        var numRatingsView: TextView = itemView.findViewById(R.id.item_num_ratings)
        var priceView: TextView = itemView.findViewById(R.id.item_price)
        var categoryView: TextView = itemView.findViewById(R.id.item_category)
        var offerView: TextView = itemView.findViewById(R.id.item_offer)
        fun bind(snapshot: DocumentSnapshot, listener: OnProductSelectedListener?) {
            val product = snapshot.toObject(Product::class.java)
            val resources = itemView.resources

            // Load image
            Glide.with(imageView.context)
                .load(product!!.photo)
                .into(imageView)
            nameView.text = product.name
            ratingBar.rating = product.avgRating.toFloat()
            offerView.text = product.offer
            categoryView.text = product.category
            numRatingsView.text = resources.getString(
                R.string.fmt_num_ratings,
                product.noOfRatings
            )
            priceView.text = "$${product.price}"

            // Click listener
            itemView.setOnClickListener { listener?.onProductSelected(snapshot) }
        }

    }
}