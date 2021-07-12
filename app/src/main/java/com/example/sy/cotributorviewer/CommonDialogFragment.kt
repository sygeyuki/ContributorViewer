package com.example.sy.cotributorviewer

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CommonDialogFragment() : DialogFragment() {
    companion object {
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_POSITIVE = "positive"
        const val KEY_NEGATIVE = "negative"

        fun create(title: String?, message: String?, positive: String?, negative: String?): CommonDialogFragment {
            return CommonDialogFragment().apply {
                val bundle = Bundle()
                bundle.putString(KEY_TITLE, title)
                bundle.putString(KEY_MESSAGE, message)
                bundle.putString(KEY_POSITIVE, positive)
                bundle.putString(KEY_NEGATIVE, negative)
                arguments = bundle
            }
        }
    }

    var onPositiveListener:DialogInterface.OnClickListener? = null
    var onNegativeListener:DialogInterface.OnClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.apply {
            setTitle(arguments?.getString(KEY_TITLE))
            setMessage(arguments?.getString(KEY_MESSAGE))
            setPositiveButton(arguments?.getString(KEY_POSITIVE), onPositiveListener)
            setNegativeButton(arguments?.getString(KEY_NEGATIVE), onNegativeListener)
            //Backキー押下はキャンセル扱いとする
            setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onNegativeListener?.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
                    true
                }
                false
            })
            setCancelable(false)
        }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
}