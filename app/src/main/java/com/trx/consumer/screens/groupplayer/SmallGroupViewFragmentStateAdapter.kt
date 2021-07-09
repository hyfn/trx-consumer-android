package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SmallGroupViewFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private lateinit var participants: ArrayList<String>

    override fun getItemCount(): Int {
        return participants.count()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SmallGroupViewFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("TEST", position + 1)
        }
        return fragment
    }

    fun setParticipants(participants: ArrayList<String>) {
        this.participants = participants
        notifyDataSetChanged()
    }
}
