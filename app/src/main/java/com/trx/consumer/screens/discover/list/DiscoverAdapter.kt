package com.trx.consumer.screens.discover.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.DiscoverModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.discover.DiscoverViewState
import com.trx.consumer.screens.videoworkout.VideoWorkoutListener
import com.trx.consumer.screens.videoworkout.VideoWorkoutViewHolder
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class DiscoverAdapter(
    private val listener: VideoWorkoutListener,
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
                TYPE_VIDEO_LIST -> VideoWorkoutViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_video_workout_table, parent, false)
                )
                else -> DiscoverEmptyListViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_discover_empty_list, parent, false)
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
            is VideoWorkoutViewHolder -> {
                when (item) {
                    is VideoModel -> holder.setup(item, listener)
                    is VideosModel -> holder.setup(item, listener)
                }
            }
            is DiscoverEmptyListViewHolder -> {
                holder.setup(item as DiscoverEmptyListViewState)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VideoModel -> TYPE_VIDEO_LIST
            is VideosModel -> TYPE_VIDEO_LIST
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(model: DiscoverModel) {
        val items: List<Any> = when (model.state) {
            DiscoverViewState.WORKOUTS -> {
                val workouts = model.workouts
                if (workouts.isEmpty()) listOf(DiscoverEmptyListViewState.WORKOUTS) else workouts
            }
            DiscoverViewState.COLLECTIONS -> {
                val videos = model.collections
                if (videos.isEmpty()) listOf(DiscoverEmptyListViewState.COLLECTIONS) else videos
            }
            DiscoverViewState.PROGRAMS -> {
                val videos = model.programs
                if (videos.isEmpty()) listOf(DiscoverEmptyListViewState.PROGRAMS) else videos
            }
        }
        updateVideos(items)
    }

    fun updateVideos(videos: List<Any>) {
        this.items.clear()
        this.items.addAll(videos)
        this.notifyDataSetChanged()
    }
}
