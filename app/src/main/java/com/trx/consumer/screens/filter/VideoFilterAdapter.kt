package com.trx.consumer.screens.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.VideoFilterModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class VideoFilterAdapter(
    private val listener: VideoFilterListener,
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
                TYPE_ROW -> VideoFilterViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_video_filter, parent, false)
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
            is VideoFilterViewHolder -> {
                holder.setup(item as VideoFilterModel, listener)
            }
        }
    }

    fun update(newItems: List<VideoFilterModel>) {
        this.items.clear()
        this.items.addAll(newItems)
        this.notifyDataSetChanged()
    }

    fun reset() {
       //TO-DO
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is VideoFilterModel -> TYPE_ROW
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size
}
