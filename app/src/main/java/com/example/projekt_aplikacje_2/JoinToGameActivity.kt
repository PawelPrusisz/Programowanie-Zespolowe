package com.example.projekt_aplikacje_2
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.*
import com.example.projekt_aplikacje_2.communication.messages.LobbyListDeliveryMessage
import com.example.projekt_aplikacje_2.communication.messages.LobbyListDeliveryMessageContent
import com.example.projekt_aplikacje_2.lobby.*
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.utils.*
import java.lang.Exception

class JoinToGameActivity : AppCompatActivity(){
    private var sortSolo = 0
    private var sortTeam = 0
    private var sortLocate = 0

    private lateinit var soloButton: ImageButton
    private lateinit var teamButton: ImageButton
    private lateinit var locateButton: ImageButton

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArrayList : List<LobbyInfo>

    private lateinit var adapter : JoinToGameAdapter

    private lateinit var mainHandler: Handler

    private lateinit var locationsArray: Array<String>
    private lateinit var gpsManager: GpsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_to_game)

        newRecyclerView=findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager=LinearLayoutManager(this)
        newArrayList = arrayListOf()
        locationsArray = resources.getStringArray(R.array.locations)
        adapter = setAdapter(ArrayList<LobbyInfo>(), locationsArray)

        soloButton=findViewById(R.id.soloButton)
        soloButton.setOnClickListener{
            if(sortSolo == 0){
                getLobbies(GameType.SOLO, null)
                sortSolo = 1
            }
            else{
                sortSolo = 0
                sortTeam = 0
                sortLocate = 0
                getLobbies(null, null)
            }
        }

        teamButton=findViewById(R.id.teamButton)
        teamButton.setOnClickListener{
            if(sortTeam == 0){
                getLobbies(GameType.TEAM, null)
                sortTeam = 1
            }
            else{
                sortSolo = 0
                sortTeam = 0
                sortLocate = 0
                getLobbies(null, null)
            }
        }

        locateButton=findViewById(R.id.locateButton)
        locateButton.setOnClickListener{
            if(sortLocate == 0){
                adapter.sortPlace()
                sortLocate = 1
            }
            else{
                sortSolo = 0
                sortTeam = 0
                sortLocate = 0
                getLobbies(null, null)
            }
        }

        getLobbies(null, null)

        mainHandler = Handler(Looper.getMainLooper())

        gpsManager = GpsManager.getGpsManager(this)
    }

    override fun onPause() {
        super.onPause()
        stop()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    private fun start() {
        mainHandler.post(object : Runnable {
            override fun run() {
                if(sortSolo == 1){
                    getLobbies(GameType.SOLO, null)
                }
                else if(sortTeam == 1){
                    getLobbies(GameType.TEAM, null)
                }
                else
                {
                    getLobbies(null, null)
                }
                mainHandler.postDelayed(this, 5000)
            }
        })
    }

    private fun stop() {
        mainHandler.removeCallbacksAndMessages(null)
    }


    private fun getLobbies(gametype : GameType?, map : Int?) {

        fun onSuccess(msg: LobbyListDeliveryMessage?) {
            getLobbiesLoading = false
            if(msg == null) {
                runOnUiThread { UIFunctions.showAlert(this, "Brak  odpowiedzi od serwera.") }
            }
            else
            {
                val cnt: LobbyListDeliveryMessageContent = msg.content
                val list: List<LobbyInfo> = cnt.lobbys_list
                newArrayList = list
                Log.i("VIEW_EXISTING_GAMES", list.toString())
                runOnUiThread {
                    adapter.replace(list)
                }
            }
        }

        fun onFailure(exception: Exception) {
            getLobbiesLoading = false
            Log.d("VIEW_EXISTING_GAMES", "Coś poszło nie tak:  $exception")
//            runOnUiThread {
//                UIFunctions.showAlert(this, "SERVER NIE DZIALA")
//            }
        }

        if(!getLobbiesLoading) {
            getLobbiesLoading = true
            Socket.getLobbies(gametype, map, ::onSuccess, ::onFailure)
        }
    }


    private fun setAdapter(l: ArrayList<LobbyInfo>, locationsArray: Array<String>): JoinToGameAdapter {
        val adapter=JoinToGameAdapter(l,locationsArray )
        newRecyclerView.adapter=adapter

        fun onSuccess(response: JoinLobbyMessageResponseContent?, mapBoundsContent: MapBoundsContent) {
            loading = false

            if(response == null) {
                runOnUiThread { UIFunctions.showAlert(this, "Serwer nie odpowiada...") }
                start()
                return
            }

            Log.i("LOBBY_CREATOR", response.toString())
            if(response.response) {
                runOnUiThread { goToLobby(LobbyStatus(), response.lon, response.lat, mapBoundsContent) }
            } else {
                runOnUiThread { UIFunctions.showAlert(this, "Nie można dołączyć do lobby") }
            }
        }

        fun onFailure(exception: Exception) {
            runOnUiThread {
                UIFunctions.showAlert(this, "Serwer nie odpowiada: $exception")
                loading = false
                start()
            }
        }

        adapter.setOnItemClickListener(object : JoinToGameAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                if(loading) {
                    return
                }

                loading = true
                stop()
                val team = if(newArrayList[position].gametype == GameType.TEAM) 1 else 0
                gpsManager.getCurrentLocation {
                    Log.d("GPS", "$it")
                    Socket.joinLobby(newArrayList[position].lobby_id, team, lon=it.longitude, lat=it.latitude, onSuccess=::onSuccess, onFailure=::onFailure)
                }

            }
        })
        return adapter
    }

    private fun goToLobby(status: LobbyStatus, lon: Double, lat: Double, mapBoundsContent: MapBoundsContent){
        val intent = Intent(this, LobbyActivity::class.java).apply {
            putExtra(LOBBY_STATUS_KEY, status)
            putExtra(LOBBY_CREATOR_KEY, false)
            putExtra(LAT, lat)
            putExtra(LON, lon)
            putExtra(MAP_BOUNDS_CONTENT_KEY, mapBoundsContent)
        }
        startActivity(intent)
    }

    private var loading = false
    private var getLobbiesLoading = false
}