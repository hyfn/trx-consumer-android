package com.trx.consumer.screens.settings.option

import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.SettingsModel

class SettingsOptionViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblSubtitle: CommonLabel = view.findViewById(R.id.lblSubtitle)
    private val btnSelect: CommonButton = view.findViewById(R.id.btnSelect)

    fun setup(model: SettingsModel, listener: SettingsOptionListener) {
        if (model.subtitle.isEmpty()) {
            lblTitle.apply {
                font(R.font.atcarquette_regular)
                textColor(model.titleTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                isAllCaps = false
            }
            lblSubtitle.isVisible = false
        } else {
            lblTitle.apply {
                font(R.font.atcarquette_bold)
                textColor(R.color.greyDark)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, model.titleTextSize.toFloat())
                isAllCaps = true
            }
            lblSubtitle.isVisible = true
        }

        lblTitle.text = itemView.context.getString(model.title)
        lblSubtitle.text = model.subtitle

        btnSelect.action { listener.doTapSettings(model) }
    }
}
