package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.trx.consumer.R
import com.trx.consumer.managers.LogManager


class SmallGroupViewFragment() : Fragment() {
    var groupActivity: GroupPlayerActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        groupActivity = activity as GroupPlayerActivity?
        return inflater.inflate(R.layout.activity_group_player_small_group_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey("position") }?.apply {
            loadParticipants(getInt("position"))
        }
    }

    fun reloadParticipants(page: Int) {
        LogManager.log("Reloading participanats with count of " + groupActivity?.handler?.participants?.count())
//        var pages = Math.ceil((groupActivity?.handler?.participants?.count()?.toDouble() ?: 0.0) / 3).toInt()
//        var count = 0;
//        do {
//            LogManager.log("Reloading page: " + count.toString())
//            loadParticipants(count);
//            count++
//        } while(count < pages)
        loadParticipants(page)
    }

    fun loadParticipants(page: Int)
    {
        groupActivity?.runOnUiThread {
            var position = (page * 3);
            var count = 0;

            do {
                val container: FrameLayout?
                if (count == 0)
                    container = view?.findViewById(R.id.groupPlayRemoteLayout1)
                else if (count == 1)
                    container = view?.findViewById(R.id.groupPlayRemoteLayout2)
                else
                    container = view?.findViewById(R.id.groupPlayRemoteLayout3)

                val participant = groupActivity?.handler?.getParticipantByPosition(position);

                if (participant != null) {
                    participant.remoteMedia?.let { rm ->
                        if (rm.view.getParent() != null) {
                            participant.connection?.close()
                            (rm.view.getParent() as ViewGroup).removeView(rm.view)
                        }

                        rm.view.layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Gravity.CENTER
                        )
                        LogManager.log("Adding view on page "+ page +" for participant : " + count.toString())


                        participant.connection?.open()
                        container?.addView(rm.view)
                    }
                }

                position++
                count++
            } while (count < 3)
        }
    }
}
