package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.trx.consumer.R

class SmallGroupViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_group_player_small_group_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey("TEST") }?.apply {
            val topRight: TextView = view.findViewById(R.id.text1)
            topRight.text = "Participant Top Right Page" + getInt("TEST").toString()

            val bottomLeft: TextView = view.findViewById(R.id.text3)
            bottomLeft.text = "Participant Bottom Left Page " + getInt("TEST").toString()

            val bottomRight: TextView = view.findViewById(R.id.text4)
            bottomRight.text = "Participant Bottom Right Page " + getInt("TEST").toString()
        }
    }
}
