package com.example.projekt_aplikacje_2.create_lobby

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.LobbyStatusUpdateMessage
import com.example.projekt_aplikacje_2.communication.messages.MapBoundsContent
import com.example.projekt_aplikacje_2.databinding.ActivityCreateLobbyBinding
import com.example.projekt_aplikacje_2.lobby.LobbyActivity
import com.example.projekt_aplikacje_2.lobby.LobbySettings
import com.example.projekt_aplikacje_2.lobby.LobbyStatus
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.utils.*

class CreateLobbyActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCreateLobbyBinding
    private lateinit var lobbyPagerAdapter : LobbySettingsPagerAdapter

    private lateinit var gpsManager: GpsManager
    private val settings = LobbySettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Setup view binding */
        binding = ActivityCreateLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /* Setup lobby settings pager */
        lobbyPagerAdapter = LobbySettingsPagerAdapter()
        binding.lobbySettingsPager.adapter = lobbyPagerAdapter
        // Turn off changing page by swiping
        binding.lobbySettingsPager.isUserInputEnabled = false
        gpsManager = GpsManager.getGpsManager(this)
    }

    private fun createLobbyFromSettings() {

        Log.i("LOBBY SETTINGS", settings.toString())

        if(!checkSettings()) {
            UIFunctions.showAlert(this, "Nie prawidłowe ustawienia!")
            return
        }

        fun onSuccess(lobby: LobbyStatusUpdateMessage?, lon: Double, lat: Double, mapBounds: MapBoundsContent) {
            runOnUiThread { lobbyPagerAdapter.endFragment.unlockFinishButton() }

            if(lobby == null) {
                runOnUiThread { UIFunctions.showAlert(this, "Serwer nie odpowiada...") }
                return
            }

            val status = lobby.content


            Log.i("LOBBY_CREATOR", status.toString())
            Log.i("LOBBY_CREATOR", settings.toString())
            runOnUiThread { goToLobby(status, lon, lat, mapBounds) }
        }

        fun onFailure(exception: Exception) {
            runOnUiThread {
                UIFunctions.showAlert(this, "Serwer nie odpowiada: $exception")
                lobbyPagerAdapter.endFragment.unlockFinishButton()
            }
        }

        lobbyPagerAdapter.endFragment.blockFinishButton()
        gpsManager.getCurrentLocation {
            Log.d("GPS", "$it")
            Socket.createLobby(settings, lon =it.longitude, lat =it.latitude, onSuccess =::onSuccess, onFailure =::onFailure)
        }
    }

    private fun checkSettings(): Boolean {
        // TODO todo
        if(settings.dwarves_amount > 100 || settings.dwarves_amount <=0){
            return false
        }
        else if(settings.players_amount > 100 || settings.players_amount <= 0){
            return false
        }
        else if(settings.mobile_max_speed > 15 || settings.mobile_max_speed <= 0){
            return false
        }
        else if(settings.web_speed > 15 || settings.web_speed <= 0){
            return false
        }

        return true
    }

    private fun goToLobby(status: LobbyStatus, lon: Double, lat: Double, mapBounds: MapBoundsContent){
        val intent = Intent(this, LobbyActivity::class.java).apply {
            putExtra(LOBBY_STATUS_KEY, status)
            putExtra(LOBBY_CREATOR_KEY, true)
            putExtra(LAT, lat)
            putExtra(LON, lon)
            putExtra(MAP_BOUNDS_CONTENT_KEY, mapBounds)
        }

        startActivity(intent)
        finish()
    }

    /* PAGER SECTION */
    inner class LobbySettingsPagerAdapter() : FragmentStateAdapter(supportFragmentManager, lifecycle) {

        val endFragment = AdvancedLobbySettingsFragment(settings, ::goToBasicLobbySettings, ::createLobbyFromSettings)
        val stepsFragments = listOf(
            BasicLobbySettingsFragment(settings, ::goToAdvancedLobbySettings),
            endFragment
        )



        override fun getItemCount(): Int = stepsFragments.size
        override fun createFragment(position: Int): Fragment = stepsFragments[position]
    }

    private fun goToBasicLobbySettings() {
        binding.lobbySettingsPager.currentItem = 0
    }

    private fun goToAdvancedLobbySettings() {
        binding.lobbySettingsPager.currentItem = 1
    }
}