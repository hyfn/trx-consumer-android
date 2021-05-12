package com.trx.consumer.screens.settings.option

import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.screens.settings.SettingsModel

class SettingsOptionViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: SettingsModel, listener: SettingsOptionListener) {

        lblTitle.apply {
            text = itemView.context.getString(model.title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, model.textSize.toFloat())
            setTextColor(ContextCompat.getColor(itemView.context, model.textColor))
        }

        lblSubtitle.text = model.subtitle

        if (model.subtitle.isEmpty()) {
            lblTitle.apply {
                typeface = ResourcesCompat.getFont(itemView.context, R.font.atcarquette_regular)
                lblSubtitle.visibility = View.GONE
            }
        } else {
            lblTitle.apply {
                typeface = ResourcesCompat.getFont(itemView.context, R.font.atcarquette_bold)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.toFloat())
                setTextColor(ContextCompat.getColor(itemView.context, R.color.greyDark))
                isAllCaps = true
            }
        }

        btnSelect.action { listener.doTapSettings(model) }
    }
}
