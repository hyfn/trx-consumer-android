package com.trx.consumer.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.trx.consumer.R
import com.trx.consumer.managers.LogManager

open class BaseDialogFragment(@LayoutRes private val layoutResId: Int) : AppCompatDialogFragment() {

    // region Objects
    private val name = this.javaClass.simpleName

    // region Setup
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    open fun bind() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseDialogFragmentStyle)
    }

    override fun onResume() {
        super.onResume()
        LogManager.log("fragmentName: $name")
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        requireDialog().window?.setLayout(width, height)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : AppCompatDialog(requireContext(), theme) {
            override fun onBackPressed() {
                this@BaseDialogFragment.dismiss()
            }
        }
    }

    //endregion

    //region animation
    fun topInAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.slide_in_top)
    }

    fun topOutAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.slide_out_top)
    }

    fun bottomInAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
    }

    fun bottomOutAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
    }

    //endregion
}
