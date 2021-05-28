package com.trx.consumer.screens.discover.list

import androidx.annotation.StringRes
import com.trx.consumer.R

enum class DiscoverEmptyListViewState(@StringRes val text: Int) {

    WORKOUTS(R.string.discover_workouts_empty_label),
    COLLECTIONS(R.string.discover_collections_empty_label),
    PROGRAMS(R.string.discover_programs_empty_label);
}
