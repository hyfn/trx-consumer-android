package com.trx.consumer.screens.permissions

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.PermissionModel

class PermissionViewHolder(view: View) : CommonViewHolder(view) {

    private val lblPermissionName: CommonLabel = view.findViewById(R.id.lblPermissionName)
    private val scPermission: SwitchCompat = view.findViewById(R.id.scPermission)

    fun setup(model: PermissionModel, listener: PermissionsListener) {
        lblPermissionName.text = model.name
        scPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener.doPermissionEnabled(model.id)
            }
        }
    }
}
