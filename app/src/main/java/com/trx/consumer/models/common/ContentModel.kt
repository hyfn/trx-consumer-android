package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.screens.content.ContentViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class ContentModel(
    var state: ContentViewState = ContentViewState.PLAIN,
    var title: String = "",
    var waiverModel: WaiverModel = WaiverModel(),
    var body: String = ""
) : Parcelable
