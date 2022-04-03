package com.example.projekt_aplikacje_2

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.*
import com.example.projekt_aplikacje_2.lobby.LobbyStatus
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.utils.*
import org.osmdroid.config.Configuration

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.util.*
import kotlin.collections.ArrayList

class GameActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView

    private lateinit var gpsManager: GpsManager
    private var gpsCallbackId = 0

    private lateinit var myMarker : Marker
    private lateinit var dwarfsMarkers : List<Marker>
    private var playersMarkers = mutableMapOf<String, Marker>()
    private lateinit var destMarker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Log.i("Game", "START AKTYWNOŚĆI GRA\n\n\n")
        setContentView(R.layout.activity_game)
        var mapBoundsContent = MapBoundsContent(1, 51.112225,51.105233, 17.08535, 17.07108)
        intent.extras?.let {
            mapBoundsContent = it.getSerializable(MAP_BOUNDS_CONTENT_KEY) as MapBoundsContent
        }

        initUI()
        initMap(mapBoundsContent)
        initGPS()
    }

    private fun initGPS() {
        gpsManager = GpsManager.getGpsManager(this)
        gpsManager.requestAllPermissions(this)

        gpsManager.getCurrentLocation {
            Log.i("Game", "Gps ok $it")
            myMarker.position = GeoPoint(it)
        }
        gpsCallbackId = gpsManager.addLocationChangeListener {
            Log.i("Game", "Gps ok $it")
            myMarker.position = GeoPoint(it)
            Socket.mobileMove(it.longitude, it.latitude, Date().time) { ex ->
                Log.e("Game", "Gps error $ex")
            }
        }
    }

    private fun setSocketListening() {
        Socket.inLoop(actions = mapOf(
            Header.MOBILE_MOVE_RESPONSE to { Log.i("Game", "Mobile Move Reponse $it"); mobileMoveResponseAction(it)},
            Header.PLAYERS_POINTS_UPDATE to { Log.i("Game", "Players Points Update $it") },
            Header.PICK_DWARF_RESPONSE to { Log.i("Game", "Pick Dwarf Response $it"); pickDwarfResponseAction(it) },
            Header.POSITION_DATA_UPDATE to { Log.i("Game", "Position Data Update $it"); positionDataUpdateAction(it)},
            Header.DWARF_LIST_DELIVERY to { Log.i("Game", "Dwarf List Delivery $it"); dwarfListDeliveryAction(it) },
        )
        ) { Log.e("Game", "FAIL! :(  {$it}") }
    }

    private fun dwarfListDeliveryAction(response: Any) {
        val msg = (response as DwarfListDelivery).content.dwarfs_list

        for(dwarf in msg) {
            val marker = Marker(map)
            marker.setAnchor(0.5f, 0.5f)
            marker.icon = ContextCompat.getDrawable(this, R.mipmap.dwarf)
            marker.position = GeoPoint(dwarf.lat, dwarf.lon)
            map.overlays.add(marker)
            marker.setOnMarkerClickListener { m, ma -> onDwarfClick(dwarf.id, m, ma) }
        }

    }

    private fun onDwarfClick(dwarfId: Int, marker: Marker, mapView: MapView) : Boolean {
        Log.i("Game", "KLIKAM NA $dwarfId")
        return true
    }

    private fun mobileMoveResponseAction(response: Any) {
        val msg = (response as MobileMoveResponse).content

        if(msg.response != "MOBILE_VALID_MOVE") {
            val was = destMarker.position.latitude
            destMarker.position = GeoPoint(msg.lat, msg.lon)
            map.invalidate()
            if(was == 0.0) {
                runOnUiThread {
                    UIFunctions.showAlert(
                        this,
                        "Nieprawidłowy ruch - ${msg.response} ${msg.lon} ${msg.lat}"
                    )
                }
            }
        } else {
            destMarker.position = GeoPoint(0.0, 0.0)
        }
    }

    private fun playersPointsUpdate(response: Any) {
        val msg = (response as PlayersPointsUpdate).content
    }

    private fun pickDwarfResponseAction(response: Any) {
        val msg = (response as PickDwarfResponse).content
    }

    private fun positionDataUpdateAction(response: Any) {
        val msg = (response as PositionDataUpdate).content
        for(player in msg.team0 + msg.team1 + msg.team2) {
            if (playersMarkers.containsKey(player.username))
            {
                playersMarkers[player.username]!!.position = GeoPoint(player.lat, player.lon)
            }
            else
            {
                val marker = Marker(map)
                marker.setAnchor(0.5f, 0.5f)
                marker.icon = ContextCompat.getDrawable(this, R.mipmap.standing_up_man)
                marker.position = GeoPoint(player.lat, player.lon)
                map.overlays.add(marker)
            }
        }
    }

    override fun onBackPressed() {
        exitGame()
    }

    fun settings() {
        Log.i("Ranking", "Click!")

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.in_game_ranking_popup, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.findViewById<ImageView>(R.id.back).setOnClickListener {
            mAlertDialog.dismiss()
            Log.i("Dialog ustawienia", "koniec")
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onResume() {
        super.onResume()
        setSocketListening()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        Socket.interrupt()
        map.onPause()
        gpsManager.removeLocationChangeListener(gpsCallbackId)
    }

    private fun initUI() {
        findViewById<LinearLayout>(R.id.pointsInfo).setOnClickListener { settings() }
        findViewById<ImageButton>(R.id.game_exit_button).setOnClickListener {
            exitGame()
        }
    }

    private fun initMap(mapBoundsContent: MapBoundsContent) {
        map = findViewById<MapView>(R.id.map)
        Configuration.getInstance().userAgentValue = "com.dwarf_runner"
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)
        map.minZoomLevel = 18.0
        val box = BoundingBox(mapBoundsContent.north, mapBoundsContent.east, mapBoundsContent.south, mapBoundsContent.west)
        map.addOnFirstLayoutListener { v, left, top, right, bottom ->
            map.zoomToBoundingBox(box, false)
            //map.invalidate()
        }
        map.setScrollableAreaLimitDouble(box)

        myMarker = Marker(map)
//        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        myMarker.icon = ContextCompat.getDrawable(this, R.drawable.person)
        myMarker.setOnMarkerClickListener { marker, mapView -> true }
        map.overlays.add(myMarker)
        dwarfsMarkers = ArrayList<Marker>()

        destMarker = Marker(map)
        destMarker.setOnMarkerClickListener { marker, mapView -> true }
        destMarker.icon = ContextCompat.getDrawable(this, R.drawable.osm_ic_follow_me)
        destMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(destMarker)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    fun exitGame() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Czy na pewno chcesz opuścić grę?")
            .setPositiveButton("Tak") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}