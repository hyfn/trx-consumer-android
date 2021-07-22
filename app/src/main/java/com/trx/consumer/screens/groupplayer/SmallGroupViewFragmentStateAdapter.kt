package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.trx.consumer.managers.LogManager


class SmallGroupViewFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    var fragments = ArrayList<SmallGroupViewFragment>()
    var activity: FragmentActivity = FragmentActivity()
    lateinit var groupHandler: GroupPlayerHandler

    init {
        activity = fragmentActivity
    }

    override fun getItemCount(): Int {
        return Math.ceil(groupHandler.participants.count().toDouble() / 3).toInt()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SmallGroupViewFragment()
        fragment.arguments = Bundle().apply {
            putInt("position", position);
        }

        fragments.add(fragment)
        LogManager.log("Fragment added count: " + fragments.count())

        return fragment
    }

    fun reloadFragments(page: Int) {
        if(fragments.count() > page)
            fragments[page].loadParticipants(page)
    }

}
