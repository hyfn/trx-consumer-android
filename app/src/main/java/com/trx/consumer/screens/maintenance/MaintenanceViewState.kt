package com.trx.consumer.screens.maintenance

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class MaintenanceViewState(
    @DrawableRes val imageLogo: Int,
    @StringRes val title: Int,
    @StringRes val message: Int,
    @StringRes val btnTitle: Int
) {
    MAINTENANCE(
        imageLogo = R.drawable.ic_img_icon_maintenance,
        title = R.string.maintenance_title,
        message = R.string.maintenance_message,
        btnTitle = R.string.maintenance_button_primary_title
    ),
    UPDATE(
        imageLogo = R.drawable.ic_img_icon_maintenance,
        title = R.string.update_title,
        message = R.string.update_message,
        btnTitle = R.string.update_button_primary_title
    )
}
