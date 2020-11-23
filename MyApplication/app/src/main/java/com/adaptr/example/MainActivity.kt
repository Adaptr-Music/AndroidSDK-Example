package com.adaptr.example

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.adaptr.android.playersdk.*
import com.adaptr.android.playersdk.models.Play
import com.adaptr.android.playersdk.models.Station
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , PlayListener, UnhandledErrorListener,
    StateListener {

    var myPlayer:AdaptrPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Adaptr.initialize(applicationContext, "demo", "demo")

        Adaptr.getPlayerInstance(object : AvailabilityListener {
            override fun onPlayerAvailable(player: AdaptrPlayer) {

                myPlayer = player
                // Add our listeners
                player.addPlayListener(this@MainActivity)
                player.addStateListener(this@MainActivity)
                player.addUnhandledErrorListener(this@MainActivity)

                // Player is available lets see our provided playlists
                for (station in player.stationList) {
                    val stationButton = Button(this@MainActivity)
                    stationButton.text = station.name
                    stationButton.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    stationButton.tag = station
                    stationButton.setOnClickListener { view -> onClickStation(view) }
                    station_buttons.addView(stationButton)
                }

                bottom_navigation.setOnNavigationItemSelectedListener(
                    BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
                        myPlayer?.let {
                            when (item.itemId) {

                                R.id.action_next -> it.skip()
                                R.id.action_play_pause -> if (it.state != State.PLAYING) {
                                    it.play()
                                } else if (it.state == State.PLAYING) {
                                    it.pause()
                                }
                            }
                        }

                        false
                    })
            }

            override fun onPlayerUnavailable(e: Exception) {

                e.printStackTrace()
            }

        })



    }


    fun onClickStation(view: View) {
        // Change station
        val station = view.tag as Station
        myPlayer?.setActiveStation(station, false)
    }

    override fun onPlayStarted(play: Play?) {

        artist.text = play?.audioFile?.artist?.name
        tracktitle.text  = (play?.audioFile?.track?.title)
    }

    override fun onProgressUpdate(play: Play, elapsedTime: Float, duration: Float) {

        seekBar.progress = elapsedTime.toInt() * 1000
        seekBar.max = duration.toInt() * 1000
    }

    override fun onSkipStatusChanged(status: Boolean) {

    }

    override fun onUnhandledError(error: AdaptrException) {

    }

    override fun onStateChanged(state: State) {

        if (state == State.PLAYING) {
            val menu: Menu = bottom_navigation.menu
            val itm = menu.findItem(R.id.action_play_pause)
            itm.title = "Pause"
            itm.setIcon(android.R.drawable.ic_media_pause)
            //stateTv.setText("PLAYING");
        } else if (state == State.PAUSED) {
            val menu: Menu = bottom_navigation.menu
            val itm = menu.findItem(R.id.action_play_pause)
            itm.title = "Play"
            itm.setIcon(android.R.drawable.ic_media_play)
            //stateTv.setText("PAUSED");
        } else if (state == State.STALLED) {
            loading.visibility = View.VISIBLE
            //stateTv.setText("STALLED");
        } else if (state == State.WAITING_FOR_ITEM) {
            //stateTv.setText("WAITING_FOR_ITEM");
        } else if (state == State.READY_TO_PLAY || state == State.AVAILABLE_OFFLINE_ONLY) {
            //stateTv.setText("READY_TO_PLAY");
        }
    }


//    object BottomNavigationViewHelper {
//        @SuppressLint("RestrictedApi")
//        fun disableShiftMode(view: BottomNavigationView) {
//            val menuView = view.getChildAt(0) as BottomNavigationMenuView
//            for (i in 0 until menuView.childCount) {
//                val item = menuView.getChildAt(i) as BottomNavigationItemView
//
//                //item.setShiftingMode(false);
//                // set once again checked value, so view will be updated
//                item.setChecked(item.itemData.isChecked)
//            }
//        }
//    }
}