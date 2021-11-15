package com.y.company.ui.activities

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.y.company.ui.CustomProgressLoader

abstract class BaseActivity : AppCompatActivity() {
    private var mProgressBar: DialogFragment? = null


    private fun showProgressPopup(shouldShow: Boolean) {
        if (shouldShow) {
            if (null == mProgressBar) {
                mProgressBar = CustomProgressLoader()
                mProgressBar?.show(supportFragmentManager, CustomProgressLoader::class.java.name)
            }
        } else {
            mProgressBar?.dismiss()
            mProgressBar = null
        }
    }

    protected fun showProgressBar() = showProgressPopup(true)
    protected fun hideProgressBar() = showProgressPopup(false)
    protected open fun showToast(message: String?) {
        if (message == null) return
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    protected fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}