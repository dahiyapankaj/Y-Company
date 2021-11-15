package com.y.company.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.y.company.R

class CustomProgressLoader : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.custom_progress_loader, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStyle(STYLE_NO_FRAME, R.style.CustomProgessLoaderTheme)
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }
}