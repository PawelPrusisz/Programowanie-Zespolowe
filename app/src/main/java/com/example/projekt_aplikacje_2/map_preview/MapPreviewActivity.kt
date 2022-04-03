package com.example.projekt_aplikacje_2.map_preview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projekt_aplikacje_2.R
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.MapBoundsContent
import com.example.projekt_aplikacje_2.databinding.ActivityMapPreviewBinding
import com.example.projekt_aplikacje_2.lobby.LobbyStatus
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.utils.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapPreviewActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var gpsManager: GpsManager
    private var gpsCallbackId = 0
    private lateinit var binding : ActivityMapPreviewBinding
    private lateinit var map: MapView

    private var mapBoundsContent = MapBoundsContent()

    private lateinit var myMarker : Marker

    private var startLon : Double = 0.0
    private var startLat : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Setup view binding */
        binding = ActivityMapPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapPreviewExitButton.setOnClickListener {
            finish()
        }

        intent.extras?.let {
            mapBoundsContent = it.getSerializable(MAP_BOUNDS_CONTENT_KEY) as MapBoundsContent
            startLon = it.getDouble(LON)
            startLat = it.getDouble(LAT)
        }

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
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        myMarker.icon = ContextCompat.getDrawable(this, R.drawable.person)
        myMarker.setOnMarkerClickListener { marker, mapView -> true }
        map.overlays.add(myMarker)

        val startMarker = Marker(map)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = ContextCompat.getDrawable(this, R.drawable.osm_ic_ic_map_ortho)
        startMarker.setOnMarkerClickListener { marker, mapView -> true }
        startMarker.position = GeoPoint(startLat, startLon)
        map.overlays.add(startMarker)
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
}