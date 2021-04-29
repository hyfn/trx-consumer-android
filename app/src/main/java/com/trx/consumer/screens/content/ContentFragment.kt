package com.trx.consumer.screens.content

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentContentBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.params.ContentParamsModel

class ContentFragment : BaseFragment(R.layout.fragment_content) {

    private val viewModel: ContentViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentContentBinding::bind)

    override fun bind() {

        val model = NavigationManager.shared.params(this) as ContentParamsModel

        viewBinding.apply {
            btnPrimary.action { viewModel.doTapBtnPrimary() }
            btnBack.action { viewModel.doTapBack() }

            btnPrimary.setOnApplyWindowInsetsListener { view, windowInsets ->
                view.updatePadding(bottom = windowInsets.systemWindowInsetBottom)
                windowInsets
            }
        }

        viewModel.apply {
            viewModel.state = model.state

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)

            doLoadView(model)
        }
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    private val handleLoadView = Observer<ContentModel> { model ->
        val termsString = SpannableStringBuilder().append(stringFromHTML(model.waiverModel.covid))
            .append(stringFromHTML(model.waiverModel.terms))
            .append(stringFromHTML(model.body))

        viewBinding.apply {
            lblTitle.text = (model.title)

            if (termsString.length < 3) {
                lblMessage.text =
                    lblMessage.context.getString(R.string.content_message, model.title)
            } else {
                lblMessage.text = (termsString)
            }

            if (viewModel.state == ContentViewState.BOOK) {
                btnPrimary.visibility = View.VISIBLE
                btnPrimary.text = btnPrimary.context.getString(R.string.content_btnprimary_txt)
            }
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private fun stringFromHTML(htmlString: String): Spannable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toSpannable()
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(htmlString).toSpannable()
        }
    }
}
