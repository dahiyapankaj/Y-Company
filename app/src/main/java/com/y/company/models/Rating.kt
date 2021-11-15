package com.y.company.models

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Rating(
    var userId: String? = null,
    var userName: String? = null,
    var rating: Double = 0.0,
    var text: String? = null,
    @ServerTimestamp
    var timestamp: Date? = null
) {
    constructor(user: FirebaseUser?, rating_: Double?, text_: String?) : this(
        rating = rating_ ?: 0.0,
        text = text_,
        userName = user?.displayName,
        userId = user?.uid
    ) {
        if (TextUtils.isEmpty(userName)) {
            userName = user?.email
        }
    }

}