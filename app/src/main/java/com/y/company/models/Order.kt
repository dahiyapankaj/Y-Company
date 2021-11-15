package com.y.company.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Order(
    var orderId: String? = null,
    var userId: String? = null,
    var products: List<Product>? = null,
    var totalValue: Double = 0.0,
    var status: String? = null,
    @ServerTimestamp
    var timestamp: Date? = null
)