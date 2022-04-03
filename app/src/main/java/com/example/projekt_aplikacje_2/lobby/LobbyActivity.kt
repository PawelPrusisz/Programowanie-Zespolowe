package com.example.projekt_aplikacje_2.lobby

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.projekt_aplikacje_2.GameActivity
import com.example.projekt_aplikacje_2.R
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.*
import com.example.projekt_aplikacje_2.databinding.ActivityLobbyBinding
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.map_preview.MapPreviewActivity
import com.example.projekt_aplikacje_2.utils.*
import org.w3c.dom.Text
import java.lang.Error

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLobbyBinding
    private lateinit var adapter : PlayerListAdapter

    private lateinit var status : LobbyStatus
    private var creator: Boolean = false
    private var myTeam : Int = 1
    private lateinit var locationsArray: Array<String>

    private lateinit var gpsManager: GpsManager
    private var gpsCallbackId = 0

    private var startLon = 0.0;
    private var startLat = 0.0;
    private var mapBoundsContent = MapBoundsContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Setup view binding */
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lobbySettingsChangeButton.setOnClickListener{lobbySettings()}

        intent.extras?.let {
            status = it.getSerializable(LOBBY_STATUS_KEY) as LobbyStatus
            creator = it.getBoolean(LOBBY_CREATOR_KEY)
            startLon = it.getDouble(LON)
            startLat = it.getDouble(LAT)
            mapBoundsContent = it.getSerializable(MAP_BOUNDS_CONTENT_KEY) as MapBoundsContent
        }

        Log.i("LOBBY", status.toString())

        //status.team1 = listOf("AAA", "BBB", "CCC", "DDD")
        //status.team2 = listOf("EEE", "FFF", "GGG", "HHH")

        /* Setup players list */
        adapter = PlayerListAdapter()
        binding.lobbyPlayersList.adapter = adapter
        locationsArray = resources.getStringArray(R.array.locations)


        setUI()
        updateUI()

        /* Setup location */
        gpsManager = GpsManager.getGpsManager(this)
        gpsManager.requestAllPermissions(this)

        binding.lobbyLocationDiode.setOnClickListener {
            gpsManager.getCurrentLocation { loc ->
                Log.i("Location", "${loc.latitude} : ${loc.longitude}")
            }
        }
    }



    private fun setUI() {
        binding.lobbyReadyButton.setOnClickListener {
            binding.lobbyReadyButton.isEnabled = false

            fun onSuccess(ready: Boolean) {
                Log.i("LOBBY", "READY : $ready")
                runOnUiThread { binding.lobbyReadyButton.isEnabled = true }
                if(ready) {
                    runOnUiThread { binding.lobbyReadyButton.text = "Gotowy!" }
                } else {
                    runOnUiThread { binding.lobbyReadyButton.text = "Gotowy?" }
                }
            }

            Socket.setPlayerReadiness(binding.lobbyReadyButton.isChecked, ::onSuccess) {
                Log.e("LOBBY", it.toString())
                runOnUiThread {
                    UIFunctions.showAlert(this, "Serwer nie odpowiada!")
                    binding.lobbyReadyButton.isEnabled = true
                }
            }
        }

        binding.lobbyTeamChangeButton.setOnClickListener {
            binding.lobbyTeamChangeButton.isEnabled = false

            Socket.changeTeam(if(myTeam == 1) 2 else 1) {
                Log.e("LOBBY", it.toString())
                runOnUiThread {
                    UIFunctions.showAlert(this, "Serwer nie odpowiada!")
                    binding.lobbyTeamChangeButton.isEnabled = true
                }
            }
        }

        binding.lobbyStartButton.setOnClickListener {
            binding.lobbyStartButton.isEnabled = false

            Socket.startGame {
                UIFunctions.showAlert(this, "Serwer nie odpowiada!")
                binding.lobbyStartButton.isEnabled = true
            }
        }

        binding.lobbyShowMapButton.setOnClickListener {
            val intent = Intent(this, MapPreviewActivity::class.java)
            intent.putExtra(MAP_BOUNDS_CONTENT_KEY, mapBoundsContent)
            intent.putExtra(LON, startLon)
            intent.putExtra(LAT, startLat)
            startActivity(intent)
        }
    }

    fun updateUI() {
        binding.lobbyOwnerHeader.text = status.lobby_name

        if (status.gametype == GameType.TEAM) {
            binding.lobbyGameTypeHeader.text = "Tryb gry: Drużynowa"
            binding.lobbyTeamLabel.visibility = View.VISIBLE
            binding.lobbyTeamChangeButton.visibility = View.VISIBLE
            binding.lobbyTeamLabel.text = "Twoja drużyna: "  + if(myTeam == 1) "Zieloni" else "Pomarańczowi"
        } else {
            binding.lobbyGameTypeHeader.text = "Tryb gry: Solo"
            binding.lobbyTeamLabel.visibility = View.GONE
            binding.lobbyTeamChangeButton.visibility = View.GONE
        }

        binding.lobbyLocationHeader.text = locationsArray[status.map]

        binding.lobbyPlayersCount.text = "${status.ready_players} / ${status.players_amount}"

        val color1 = ResourcesCompat.getColor(resources, R.color.green, null)
        val color2 = ResourcesCompat.getColor(resources, R.color.orange, null)

        if(status.gametype == GameType.TEAM) {
            val playersList =
                status.teams["team1"]!!.map { it to color1 } +  status.teams["team2"]!!.map { it to color2 }
            adapter.submitList(playersList)
        } else {
            val playersList =
                status.teams["team0"]!!.map { it to color1 }
            adapter.submitList(playersList)
        }

        //status.map

        binding.lobbyStartButton.visibility = if (creator) View.VISIBLE else View.INVISIBLE
    }

    fun checkPlayerLocation(location : Location) {
        Log.i("Location", "Player at location ${location.latitude} : ${location.longitude}")
        setLocationDiode(isOnMap(location))
    }

    fun isOnMap(location: Location) : Boolean {
        // Todo : Check if player is on map
        return location.latitude != 0.0
    }

    fun setLocationDiode(isOnMap : Boolean) {

        val colorId = if (isOnMap) R.color.green
        else R.color.orange

        binding.lobbyLocationDiode.imageTintList =
            ResourcesCompat.getColorStateList(resources, colorId, null)
    }

    private fun lobbySettings() {
        Log.i("Ranking", "Click!")

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.game_settings, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.findViewById<ImageView>(R.id.back).setOnClickListener{
            mAlertDialog.dismiss()
            Log.i("Dialog ustawienia", "koniec")
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialogView.findViewById<TextView>(R.id.gameEndRuleButton).text = if(status.endgame_cond > 0) "${status.endgame_cond} minut" else "krasnale"
        mDialogView.findViewById<TextView>(R.id.numberOfDwarfsbutton).text = "${status.dwarves_amount}"
        mDialogView.findViewById<TextView>(R.id.maxwebplayerspeedbutton).text = "${status.web_speed} km/h"
        mDialogView.findViewById<TextView>(R.id.maxmobileplayerspeedbutton).text = "${status.mobile_max_speed} km/h"
    }

    override fun onPause() {
        super.onPause()
        Socket.interrupt()
        gpsManager.removeLocationChangeListener(gpsCallbackId)
        Log.i("LIFECYCLE", "PAUSE")
    }

    override fun onResume() {
        super.onResume()
        setSocketListening()
        Log.i("LIFECYCLE", "Resume")
        gpsCallbackId = gpsManager.addLocationChangeListener {
            checkPlayerLocation(it)
        }
    }

    private fun setSocketListening() {
        Socket.inLoop(actions = mapOf(
            Header.JOIN_LOBBY_RESPONSE to { Log.i("LOBBY", "JOIN") },
            Header.LOBBY_STATUS_UPDATE to {
                Log.i("LOBBY", "UPDATE - $it")
                status = (it as LobbyStatusUpdateMessage).content
                runOnUiThread { updateUI() }
            },
            Header.ACKNOWLEDGE to {Log.i("LOBBY", "ACKNOWLEDGE")},
            Header.CHANGE_TEAM_RESPONSE to ::changeTeamSuccess,
            Header.START_GAME_RESPONSE to {
                val msg = (it as StartGameResponse).content
                Log.i("LOBBY", "Start Game  - $msg")
                if(msg) {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra(MAP_BOUNDS_CONTENT_KEY, mapBoundsContent)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread{ UIFunctions.showAlert(this, "Nie można wystartować gry!") }
                }
            },
            Header.ERROR to {
                val msg = (it as ErrorMessage).content
                Log.e("LOBBY", "ERROR MSG -- $msg")
            },
            Header.QUIT_LOBBY_RESPONSE to {
                Log.e("LOBBY", "Quit OK")
            },
            Header.LOBBY_CREATOR_RIGHTS to {
                creator = true
                Log.e("LOBBY", "Prawa zalozyciela lobby")
                runOnUiThread { updateUI() }
            }

        )
        ) { Log.e("LOBBY", "FAIL!!! {$it}") }
    }

    private fun changeTeamSuccess(ok_r: Any) {
        val ok = (ok_r as ChangeTeamResponse).content
        Log.i("LOBBY", "CHANGE TEAM : $ok")
        myTeam = if(myTeam == 1) 2 else 1
        runOnUiThread { binding.lobbyTeamChangeButton.isEnabled = true }

        if(ok) {
            runOnUiThread {
                Toast.makeText(this, "Zmieniono team!", Toast.LENGTH_LONG).show()
                binding.lobbyTeamLabel.text = "Twoja drużyna: "  + if(myTeam == 1) "Zieloni" else "Pomarańczowi"
            }
        } else {
            runOnUiThread { Toast.makeText(this, "Nie zmieniono drużyny!", Toast.LENGTH_LONG).show() }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Socket.quitLobby({ Log.i("LOBBY", "WYSZLIŚMY Z LOBBY $it") }, { })
    }


}