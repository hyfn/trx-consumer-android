package com.trx.consumer.screens.virtualworkout

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.views.VirtualWorkoutView

class VirtualWorkoutViewHolder(view: View) : CommonViewHolder(view) {

    private val virtualWorkoutView: VirtualWorkoutView = view.findViewById(R.id.virtualWorkoutView)

    fun setup(model: WorkoutModel, listener: VirtualWorkoutViewListener) {
        virtualWorkoutView.loadView(model, listener)
    }

}
