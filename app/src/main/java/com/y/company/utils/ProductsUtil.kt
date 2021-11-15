package com.y.company.utils

import android.content.Context
import com.y.company.R
import com.y.company.models.Product
import java.util.*

/**
 * Utilities for Products.
 */
object ProductsUtil {
    private val NAME_FIRST_WORDS = arrayOf(
        "Philips",
        "Sony",
        "Lloyd",
        "Voltas",
        "Whirlpool",
        "Samsung",
    )
    private val URL_IMAGES = arrayOf(
        "https://as2.ftcdn.net/v2/jpg/01/23/33/35/1000_F_123333533_L57e12a294BwNUmapWZq7TkMlCJHRQad.jpg",
        "https://as2.ftcdn.net/v2/jpg/03/22/79/53/1000_F_322795364_KxbwRK7rn8Y3qC4YaNjYXoRYnjVm0awA.jpg",
        "https://as2.ftcdn.net/v2/jpg/02/77/26/19/1000_F_277261958_vjKKEphxJDzLOc8YLJb2l3nGW5rKdAwO.jpg",
        "https://as1.ftcdn.net/v2/jpg/01/63/18/72/1000_F_163187219_ALi0Er3ZIBukevYJAqAr6y4FKq0zi5yi.jpg",
        "https://as2.ftcdn.net/v2/jpg/00/24/20/29/1000_F_24202989_PX6scfO9G96FSx9SUkDnVlU1LM94yS5D.jpg",
        "https://image.shutterstock.com/shutterstock/photos/1668941440/display_1500/stock-photo--d-render-of-home-appliances-collection-set-1668941440.jpg"
    )
    private val NAME_SECOND_WORDS = arrayOf(
        "LCD",
        "TV",
        "Washing Machine",
        "Microwave",
        "Refrigerator",
        "Drills",
        "AC",
        "Mobile",
        "Hydraulic Jack"
    )
    private val tempProduct = Product()

    /**
     * Create a random Product POJO.
     */
    fun getRandom(context: Context): Product {
        val product = Product()
        val random = Random()

        // Cities (first element is 'Any')
        var cities = context.resources.getStringArray(R.array.offers)
        cities = cities.copyOfRange(1, cities.size)

        // Categories (first element is 'Any')
        var categories = context.resources.getStringArray(R.array.categories)
        categories = categories.copyOfRange(1, categories.size)
        val prices = intArrayOf(1299, 1699, 3455, 6990, 1499, 15999, 499, 799)
        product.name = getRandomName(random)
        tempProduct.name = product.name
        product.offer = getRandomString(cities, random)
        product.category = getRandomString(categories, random)
        product.price = getRandomInt(prices, random)
        product.avgRating = getRandomRating(random)
        product.noOfRatings = random.nextInt(20)
        product.photo = getRandomImageUrl(random)
        return product
    }

    /**
     * Get a random image.
     */
    private fun getRandomImageUrl(random: Random): String {
        val image: String = when {
            tempProduct.name.contains("tv", true) -> {
                URL_IMAGES[0]
            }
            tempProduct.name.contains("lcd", true) -> {
                URL_IMAGES[1]
            }
            tempProduct.name.contains("microwave", true) -> {
                URL_IMAGES[2]
            }
            tempProduct.name.contains("AC", false) -> {
                URL_IMAGES[3]
            }
            tempProduct.name.contains("Washing machine", true) -> {
                URL_IMAGES[4]
            }
            else -> {
                URL_IMAGES[5]
            }
        }
        return image
    }

    private fun getRandomRating(random: Random): Double {
        val min = 1.0
        return min + random.nextDouble() * 4.0
    }

    private fun getRandomName(random: Random): String {
        return (getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random))
    }

    private fun getRandomString(array: Array<String>, random: Random): String {
        val ind = random.nextInt(array.size)
        return array[ind]
    }

    private fun getRandomInt(array: IntArray, random: Random): Int {
        val ind = random.nextInt(array.size)
        return array[ind]
    }
}