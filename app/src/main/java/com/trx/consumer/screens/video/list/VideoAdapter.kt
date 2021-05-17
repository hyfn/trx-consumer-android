package com.trx.consumer.screens.video.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class VideoAdapter(
    private val listener: VideoListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_VIDEO_LIST = 1
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_VIDEO_LIST -> VideoViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_video_detail, parent, false)
                )
                else -> EmptyViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
                )
            }
        } catch (e: Exception) {
            LogManager.log(e)
            return EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is VideoViewHolder -> {
                holder.setup(item as WorkoutModel, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is WorkoutModel -> TYPE_VIDEO_LIST
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateVideos(newWorkouts: List<WorkoutModel>) {
        this.items.clear()
        this.items.addAll(newWorkouts)
        this.notifyDataSetChanged()
    }
}
