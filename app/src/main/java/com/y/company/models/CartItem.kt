package com.y.company.models


data class CartItem(
    var productId: String? = null,
    var userId: String? = null,
    var name: String = "",
    var category: String = "",
    var photo: String = "",
    var offer: String = "",
    var price: Int= 0
){}