package com.trx.consumer.screens.photos

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.load

class PhotosViewHolder (view: View) : CommonViewHolder(view) {

    private val imgTrainerPhoto: CommonImageView = view.findViewById(R.id.imgTrainerPhoto)

    fun setup(model: String, listener: PhotosViewListener) {
        imgTrainerPhoto.load(model)
        imgTrainerPhoto.action { listener.doTapSelectPhotos(model)}
    }
}