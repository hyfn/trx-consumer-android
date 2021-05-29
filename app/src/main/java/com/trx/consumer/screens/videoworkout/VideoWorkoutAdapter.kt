package com.trx.consumer.screens.videoworkout

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
import com.trx.consumer.screens.discover.list.DiscoverEmptyListViewHolder
import com.trx.consumer.screens.discover.list.DiscoverEmptyListViewState
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class VideoWorkoutAdapter(
    private val listener: VideoWorkoutListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_ROW = 1
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_ROW -> VideoWorkoutViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.row_video_workout_collection, parent, false)
                )
                else -> DiscoverEmptyListViewHolder(
                    LayoutInflater
                        .from(parent.context)
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

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VideoModel -> TYPE_ROW
            is VideosModel -> TYPE_ROW
            else -> TYPE_EMPTY
        }
    }

    fun update(model: DiscoverModel) {
        val items: List<Any> = when (model.state) {
            DiscoverViewState.WORKOUTS -> {
                val workouts = model.workouts
                if (workouts.isEmpty()) listOf(DiscoverEmptyListViewState.WORKOUTS) else workouts
            }
            DiscoverViewState.COLLECTIONS -> {
                val videos = model.videos
                if (videos.isEmpty()) listOf(DiscoverEmptyListViewState.COLLECTIONS) else videos
            }
            DiscoverViewState.PROGRAMS -> {
                val videos = model.videos
                if (videos.isEmpty()) listOf(DiscoverEmptyListViewState.PROGRAMS) else videos
            }
        }
        update(items)
    }

    fun update(newItems: List<Any>) {
        items.apply {
            clear()
            addAll(newItems)
            notifyDataSetChanged()
        }
    }
}
