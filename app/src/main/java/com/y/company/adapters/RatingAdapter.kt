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

import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.y.company.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.google.firebase.firestore.Query
import com.y.company.models.Rating
import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * RecyclerView adapter for a bunch of Ratings.
 */
open class RatingAdapter(query: Query?) : FirestoreAdapter<RatingAdapter.ViewHolder?>(query) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rating, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position).toObject(Rating::class.java))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView: TextView = itemView.findViewById(R.id.rating_item_name)
        var ratingBar: MaterialRatingBar = itemView.findViewById(R.id.rating_item_rating)
        var textView: TextView = itemView.findViewById(R.id.rating_item_text)
        fun bind(rating: Rating?) {
            nameView.text = rating!!.userName
            ratingBar.rating = rating.rating.toFloat()
            textView.text = rating.text
        }

    }
}