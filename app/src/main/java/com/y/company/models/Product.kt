package com.y.company.models


data class Product(
    var id: String? = null,
    var name: String = "",
    var category: String = "",
    var photo: String = "",
    var offer: String = "",
    var price: Int= 0,
    var noOfRatings: Int = 0,
    var avgRating: Double=0.0
)