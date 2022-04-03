package com.example.projekt_aplikacje_2.communication
import android.util.Log
import com.example.projekt_aplikacje_2.communication.messages.*
import com.example.projekt_aplikacje_2.lobby.GameType
import com.example.projekt_aplikacje_2.lobby.LobbySettings
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.lang.RuntimeException

object Socket {
    private lateinit var sender : Sender
    private var interrupt : Boolean = false
    private var ID = 0

    fun connect(onFailure: (Exception) -> Unit) {
        Log.i("SOCKET", "Łączenie....")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sender = Sender()
                Log.i("SOCKET", "OK")
                handshake()
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", "NIE UDAŁO SIĘ POŁĄCZYĆ!\n\n$e")
                onFailure(e)
            }
        }
    }

    private fun handshake() {
        val msg = sender.receiveMessage(HandshakeMessage::class.java)
        if (msg != null) {
            ID = msg.content
            Log.i("SOCKET", "ID => $ID")
        } else {
            Log.i("SOCKET", "Handshake failed")
            throw RuntimeException()
        }
    }

    fun pickDwarf(dwarf_id: Int, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = PickDwarfRequest(Header.PICK_DWARF_REQUEST, client_id = ID, content=dwarf_id)
                sender.sendMessage(msg)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun mobileMove(lon: Double, lat: Double, timestamp: Long, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val content = MobileMoveContent(lon, lat, timestamp)
                val msg = MobileMove(Header.MOBILE_MOVE, client_id = ID, content=content)
                sender.sendMessage(msg)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun logIn(email: String, password: String, onSuccess: (LoginMessageResponse?) -> Unit, onFailure: (Exception) -> Unit) {
        if(ID == 0) {
            Log.e("SOCKET", "Nie połączono się jeszcze z serwerem!")
            onFailure(java.lang.Exception("Nie połączono się jeszcze z serwerem!"))
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msgContent = LoginMessageContent(email, password)
                val msg = LoginMessage(client_id = ID, content = msgContent)
                sender.sendMessage(msg)
                val response = sender.receiveMessage(LoginMessageResponse::class.java)
                onSuccess(response)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }
    fun register(email: String, password: String, nickname: String, onSuccess: (RegisterMessageResponse?) -> Unit, onFailure: (Exception) -> Unit) {
        if(ID == 0) {
            Log.e("SOCKET", "Nie połączono się jeszcze z serwerem!")
            onFailure(java.lang.Exception("Nie połączono się jeszcze z serwerem!"))
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msgContent = RegisterMessageContent(email, password, nickname)
                val msg = RegisterMessage(client_id = ID, content = msgContent)
                sender.sendMessage(msg)
                val response = sender.receiveMessage(RegisterMessageResponse::class.java)
                onSuccess(response)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }


    fun createLobby(settings: LobbySettings, lon: Double = 50.0, lat: Double = 50.0, onSuccess: (LobbyStatusUpdateMessage?, Double, Double, MapBoundsContent) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = CreateLobbyMessage(client_id = ID, content = settings)
                sender.sendMessage(msg)
                Log.d("SOCKET", "Create Lobby Response")
                val response = sender.receiveMessage(CreateLobbyResponse::class.java)
                if(response != null) {
                    if (response.content != -1) {
                        val msg2 = JoinLobbyMessage(client_id = ID, content = JoinLobbyMessageContent(lon=lon, lat=lat, lobby_id = response.content, team_id = if (settings.gametype == GameType.SOLO) 0 else 1))
                        sender.sendMessage(msg2)
                        Log.d("SOCKET", "Lobby Creator Message")
                        sender.receiveMessage(LobbyCreatorMessage::class.java)
                        val response2 = sender.receiveMessage(JoinLobbyMessageResponse::class.java)
                        if(response2 != null && response2.content.response) {
                            Log.d("SOCKET", "MapBounds")
                            val response4 = sender.receiveMessage(MapBoundsMessage::class.java)
                            if(response4 != null) {
                                Log.d("SOCKET", "LobbyStatusUpdateMessage")
                                val response3 =
                                    sender.receiveMessage(LobbyStatusUpdateMessage::class.java)
                                Log.d("SOCKET", response3.toString())
                                onSuccess(
                                    response3,
                                    response2.content.lon,
                                    response2.content.lat,
                                    response4.content
                                )
                            } else {
                                throw Exception("Nie udało się dołączyć do lobby.")
                            }
                        } else {
                            throw Exception("Nie udało się dołączyć do lobby.")
                        }
                    } else {
                        throw Exception("Nie udało się dołączyć do lobby.")
                    }
                } else {
                    throw Exception("Serwer nie odpowiada...")
                }
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun getLobbies(gametype: GameType? = GameType.SOLO, map: Int? = 1, onSuccess: (LobbyListDeliveryMessage?) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            sender.resetScanner()
            try {
                val msg = LobbyListMessage(client_id = ID, content = LobbyListMessageContent(gametype = gametype, map = map))
                sender.sendMessage(msg)
                val response = sender.receiveMessage(LobbyListDeliveryMessage::class.java)
                if(response != null) {
                    onSuccess(response)
                } else {
                    throw Exception("Serwer nie odpowiada...")
                }
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun inLoop(actions: Map<Header, (Any) -> Unit>, onFailure: (Exception) -> Unit) {
        interrupt = false
        CoroutineScope(Dispatchers.IO).launch {
            var jsonMsg : JsonElement

            while(!interrupt) {
                try {

                    try {
                        jsonMsg = sender.receiveMessage()
                    } catch (e: Exception) {
                        Log.i("SOCKET", "brak wiadmomości...")
                        continue
                    }

                    if(interrupt) {
                        break
                    }

                    val header = sender.getHeader(jsonMsg)
                    val msgType = Header.msgTypes[header]

                    val msg = sender.toObject(jsonMsg, msgType!!)
                    actions[header]?.let { it(msg) }
                } catch (e: IllegalStateException) {

                } catch (e: Exception) {
                    onFailure(e)
                }
            }

        }
    }

    fun setPlayerReadiness(ready: Boolean, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if(ready) {
                    val msg = LobbyPlayerIsReady(client_id = ID)
                    sender.sendMessage(msg)
                } else {
                    val msg = LobbyPlayerIsNotReady(client_id = ID)
                    sender.sendMessage(msg)
                }
                onSuccess(ready)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun quitLobby(onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = QuitLobbyRequest(client_id = ID)
                sender.sendMessage(msg)
                val response = sender.receiveMessage(QuitLobbyResponse::class.java)
                if(response != null) {
                    onSuccess(response.content)
                } else {
                    throw Exception("Serwer nie odpowiada...")
                }

            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun changeTeam(team: Int, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = ChangeTeamRequest(client_id = ID, content = team)
                sender.sendMessage(msg)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun startGame(onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = StartGame(client_id = ID)
                sender.sendMessage(msg)
            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun joinLobby(lobbyId: Int, teamId: Int, lon: Double = 0.0, lat: Double = 0.0, onSuccess: (JoinLobbyMessageResponseContent?, MapBoundsContent) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val msg = JoinLobbyMessageContent(lobbyId, teamId, lon, lat)
                sender.sendMessage(JoinLobbyMessage(client_id = ID, content = msg))
                val response = sender.receiveMessage(JoinLobbyMessageResponse::class.java)
                if(response != null) {
                    val response2 = sender.receiveMessage(MapBoundsMessage::class.java)
                    if(response2 != null) {
                        onSuccess(response.content, response2.content)
                    } else {
                        throw Exception("Serwer nie odpowiada...")
                    }
                } else {
                    throw Exception("Serwer nie odpowiada...")
                }

            } catch (e: java.lang.Exception) {
                Log.e("SOCKET", e.toString())
                onFailure(e)
            }
        }
    }

    fun interrupt() {
        interrupt = true
    }

}