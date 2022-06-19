package com.akstudios.KSTWV.utils

import android.app.Activity
import android.app.Dialog
import com.akstudios.KSTWV.R
import javax.inject.Inject

class LoadingDialog @Inject constructor(){
     var dialogView: Dialog? = null
    fun showDialog(activity: Activity) {
        dialogView = Dialog(activity, R.style.CustomAlertDialog)
        dialogView?.setCanceledOnTouchOutside(false)
        dialogView?.setContentView(R.layout.loading_dialog)
        dialogView?.show()
    }

    fun dismissDialog() {
        dialogView?.dismiss()
    }
}