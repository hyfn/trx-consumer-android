package com.trx.consumer.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.trx.consumer.R

enum class OnBoardingViewState {

    VIRTUAL, LIVE, DEMAND;

    val currentPage: Int
        get() = when (this) {
            VIRTUAL -> 0
            LIVE -> 1
            DEMAND -> 2
        }

    @get:StringRes
    val title: Int
        get() = when (this) {
            VIRTUAL -> R.string.onboarding_virtual_title
            LIVE -> R.string.onboarding_live_title
            DEMAND -> R.string.onboarding_demand_title
        }

    @get:StringRes
    val description: Int
        get() = when (this) {
            VIRTUAL -> R.string.onboarding_virtual_description
            LIVE -> R.string.onboarding_live_description
            DEMAND -> R.string.onboarding_demand_description
        }

    @get:DrawableRes
    val image: Int
        get() = when (this) {
            VIRTUAL -> R.drawable.img_media_onboarding_001
            LIVE -> R.drawable.img_media_onboarding_002
            DEMAND -> R.drawable.img_media_onboarding_003
        }

    @get:StringRes
    val listItemOne: Int
        get() = when (this) {
            VIRTUAL -> R.string.onboarding_virtual_list_text_one
            LIVE -> R.string.onboarding_live_list_text_one
            DEMAND -> R.string.onboarding_demand_list_text_one
        }

    @get:StringRes
    val listItemTwo: Int
        get() = when (this) {
            VIRTUAL -> R.string.onboarding_virtual_list_text_two
            LIVE -> R.string.onboarding_live_list_text_two
            DEMAND -> R.string.onboarding_demand_list_text_two
        }

    @get:StringRes
    val listItemThree: Int
        get() = when (this) {
            VIRTUAL -> R.string.onboarding_virtual_list_text_three
            LIVE -> R.string.onboarding_live_list_text_three
            DEMAND -> R.string.onboarding_demand_list_text_three
        }

    companion object {
        fun getStateFromPage(page: Int): OnBoardingViewState {
            return values().firstOrNull { it.currentPage == page } ?: VIRTUAL
        }
    }
}
