package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.ClassModel
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.screens.content.ContentViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class ContentParamsModel(
    var state: ContentViewState = ContentViewState.PLAIN,
    var model: ContentModel = ContentModel(),
    var classModel: ClassModel = ClassModel()
) : Parcelable {
    constructor(state: ContentViewState, model: ContentModel) : this() {
        this.state = state
        this.model = model
    }

    constructor(state: ContentViewState, title: String, body: String) : this() {
        this.state = state
        this.model.title = title
        this.model.body = body
    }
}
