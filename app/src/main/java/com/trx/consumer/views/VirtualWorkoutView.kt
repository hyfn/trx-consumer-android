package com.trx.consumer.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.trx.consumer.R
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.RowVirtualWorkoutBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.dpToPx
import com.trx.consumer.extensions.load
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutCellViewState
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener
import java.util.Locale

class VirtualWorkoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs) {

    private val binding = RowVirtualWorkoutBinding.inflate(LayoutInflater.from(context), this, true)

    fun loadView(model: WorkoutModel, listener: VirtualWorkoutViewListener) {
        loadViewState(model.cellViewStatus)
        binding.apply {
            imgProfile.load(model.imageUrl)
            lblTitle.text = model.title
            lblSubtitle.text = model.subtitle

            btnPrimary.action { listener.doTapPrimaryVirtualWorkout(model) }
            btnSelect.action { listener.doTapSelectVirtualWorkout(model) }
        }
    }

    fun loadView(model: TrainerModel) {
        loadViewState(WorkoutCellViewState.VIEW)
        binding.apply {
            imgProfile.load(model.profilePhoto)
            lblTitle.text = context.getString(
                R.string.virtual_workout_view_trainer_title,
                model.firstName
            )
            lblSubtitle.text = context.getString(
                R.string.virtual_workout_view_trainer_subtitle,
                model.firstName
            ).toUpperCase(Locale.getDefault())
        }
    }

    fun loadViewMatchMe() {
        loadViewState(WorkoutCellViewState.MATCH)
        binding.apply {
            imgProfile.setImageResource(R.drawable.ic_img_tab_profile)
            lblTitle.text = context.getString(R.string.virtual_workout_view_match_me_title)
            lblSubtitle.text = context.getString(R.string.virtual_workout_view_match_me_subtitle)
        }
    }

    private fun loadViewState(state: WorkoutCellViewState) {
        state.apply {
            with(binding) {
                viewMain.bgColor(backgroundColor)
                lblTitle.textColor(titleColor)
                lblSubtitle.textColor(subtitleColor)
                with(btnPrimary) {
                    text = context.getString(buttonTitle)
                    textColor(buttonTitleColor)
                    bgColor(buttonBackgroundColor)
                    val layoutParams = layoutParams
                    layoutParams.width = dpToPx(buttonWidth)
                    setLayoutParams(layoutParams)
                }
            }
        }
    }
}
