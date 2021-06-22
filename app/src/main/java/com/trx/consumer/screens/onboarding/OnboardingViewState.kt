package com.trx.consumer.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class OnBoardingViewState {

    DEMAND, LIVE, VIRTUAL;

    val currentPage: Int
        get() = when (this) {
            DEMAND -> 0
            LIVE -> 1
            VIRTUAL -> 2
        }

    @get:StringRes
    val title: Int
        get() = when (this) {
            DEMAND -> R.string.onboarding_demand_title
            LIVE -> R.string.onboarding_live_title
            VIRTUAL -> R.string.onboarding_virtual_title
        }

    @get:StringRes
    val description: Int
        get() = when (this) {
            DEMAND -> R.string.onboarding_demand_description
            LIVE -> R.string.onboarding_live_description
            VIRTUAL -> R.string.onboarding_virtual_description
        }

    @get:DrawableRes
    val image: Int
        get() = when (this) {
            DEMAND -> R.drawable.img_media_onboarding_003
            LIVE -> R.drawable.img_media_onboarding_002
            VIRTUAL -> R.drawable.img_media_onboarding_001
        }

    @get:StringRes
    val listItemOne: Int
        get() = when (this) {
            DEMAND -> R.string.onboarding_demand_list_text_one
            LIVE -> R.string.onboarding_live_list_text_one
            VIRTUAL -> R.string.onboarding_virtual_list_text_one
        }

    @get:StringRes
    val listItemTwo: Int
        get() = when (this) {
            DEMAND -> R.string.onboarding_demand_list_text_two
            LIVE -> R.string.onboarding_live_list_text_two
            VIRTUAL -> R.string.onboarding_virtual_list_text_two
        }

    @get:StringRes
    val listItemThree: Int
        get() = when (this) {
            DEMAND -> R.string.onboarding_demand_list_text_three
            LIVE -> R.string.onboarding_live_list_text_three
            VIRTUAL -> R.string.onboarding_virtual_list_text_three
        }

    companion object {
        fun getStateFromPage(page: Int): OnBoardingViewState {
            return values().firstOrNull { it.currentPage == page } ?: DEMAND
        }
    }
}
