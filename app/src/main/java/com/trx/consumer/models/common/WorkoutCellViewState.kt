package com.trx.consumer.models.common

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class WorkoutCellViewState {

    VIEW, SOON, BOOKED, MATCH;

    @get:ColorRes
    val backgroundColor: Int
        get() {
            return when (this) {
                VIEW, BOOKED, MATCH -> R.color.greyLightExtra
                SOON -> R.color.black
            }
        }

    @get:StringRes
    val buttonTitle: Int
        get() {
            return when (this) {
                VIEW -> R.string.workout_cell_button_view_title
                SOON -> R.string.workout_cell_button_soon_title
                BOOKED -> R.string.workout_cell_button_booked_title
                MATCH -> R.string.workout_cell_button_match_title
            }
        }

    @get:ColorRes
    val buttonTitleColor: Int
        get() {
            return when (this) {
                SOON, BOOKED -> R.color.white
                VIEW, MATCH -> R.color.black
            }
        }

    @get:ColorRes
    val buttonBackgroundColor: Int
        get() {
            return when (this) {
                SOON -> R.color.greyDark
                VIEW, MATCH -> R.color.yellow
                BOOKED -> R.color.grey
            }
        }

    val buttonWidth: Float
        get() {
            return when (this) {
                SOON, MATCH -> 100f
                VIEW, BOOKED -> 68f
            }
        }

    @get:ColorRes
    val titleColor: Int
        get() {
            return when (this) {
                VIEW, BOOKED, MATCH -> R.color.black
                SOON -> R.color.white
            }
        }

    @get:ColorRes
    val subtitleColor: Int
        get() {
            return when (this) {
                VIEW, BOOKED, MATCH -> R.color.grey
                SOON -> R.color.yellow
            }
        }
}
