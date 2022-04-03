package com.example.projekt_aplikacje_2.communication.messages

import com.example.projekt_aplikacje_2.lobby.GameType
import com.example.projekt_aplikacje_2.lobby.LobbyInfo
import com.example.projekt_aplikacje_2.lobby.LobbySettings
import com.example.projekt_aplikacje_2.lobby.LobbyStatus
import java.io.Serializable


data class CreateLobbyMessage(val header: Header = Header.CREATE_LOBBY_REQUEST,
                              val client_id: Int = 0,
                              val content: LobbySettings)

data class CreateLobbyResponse(val header: Header = Header.CREATE_LOBBY_RESPONSE, val content: Int = 0)


data class JoinLobbyMessage(val header: Header = Header.JOIN_LOBBY_REQUEST, val client_id: Int = 0, val content: JoinLobbyMessageContent)

data class JoinLobbyMessageContent(val lobby_id : Int = 0, val team_id : Int = 0, val lon : Double = 0.0, val lat : Double = 0.0)

data class JoinLobbyMessageResponse(val header: Header = Header.JOIN_LOBBY_RESPONSE,
                                    val content: JoinLobbyMessageResponseContent)

data class JoinLobbyMessageResponseContent(val response: Boolean = false, val lon : Double = 0.0, val lat : Double = 0.0)

data class LobbyStatusUpdateMessage(val header: Header = Header.LOBBY_STATUS_UPDATE,
                                    val content: LobbyStatus)


data class LobbyListMessage(val header: Header = Header.LOBBY_LIST_REQUEST,
                            val client_id: Int = 0,
                            val content: LobbyListMessageContent)


data class LobbyListMessageContent(val gametype: GameType? = GameType.SOLO, val map: Int? = 1,
                                   val include_full: Boolean = true, val lobby_range_beginning: Int = 0,
                                   val lobby_range_end: Int = 50)


data class LobbyListDeliveryMessage(val header: Header = Header.LOBBY_LIST_DELIVERY,
                                    val content: LobbyListDeliveryMessageContent)


data class LobbyListDeliveryMessageContent(val lobbys_amount: Int = 0, val total_lobbys_amount: Int = 0,
                                           val lobbys_list: List<LobbyInfo> = listOf())

data class LobbyPlayerIsReady(val header: Header = Header.PLAYER_IS_READY, val client_id: Int = 0, val content: Map<Any, Any> = mapOf())
data class LobbyPlayerIsNotReady(val header: Header = Header.PLAYER_IS_UNREADY, val client_id: Int = 0, val content: Map<Any, Any> = mapOf())
data class LobbyAck(val header: Header = Header.ACKNOWLEDGE, val content: String = "")
data class StartGame(val header: Header = Header.START_GAME_REQUEST, val client_id: Int = 0, val content: Map<Any, Any> = mapOf())
data class StartGameResponse(val header: Header = Header.START_GAME_REQUEST, val content: Boolean = false)
data class ChangeTeamRequest(val header: Header = Header.CHANGE_TEAM_REQUEST, val client_id: Int = 0, val content: Int = 0)
data class ChangeTeamResponse(val header: Header = Header.CHANGE_TEAM_RESPONSE, val content: Boolean = false)
data class QuitLobbyRequest(val header: Header = Header.QUIT_LOBBY_REQUEST, val client_id: Int = 0, val content: Map<Any, Any> = mapOf())
data class QuitLobbyResponse(val header: Header = Header.QUIT_LOBBY_RESPONSE, val content: Boolean = true)
data class ErrorMessage(val header: Header = Header.ERROR, val content : String = "")
data class LobbyCreatorMessage(val header: Header = Header.LOBBY_CREATOR_RIGHTS, val content: Map<Any, Any> = mapOf())
data class MapBoundsMessage(val header: Header = Header.MAP_BOUNDS, val content: MapBoundsContent)
data class MapBoundsContent(val map_id: Int = 0, val north: Double = 0.0, val south: Double = 0.0, val east: Double = 0.0, val west: Double = 0.0) : Serializable

/*

{
  "header": "MAP_BOUNDS",
  "content":
  {
      "map_id": $,
      "north": 12.345,
      "south": 12.345,
      "east": 12.345,
      "west": 12.345
  }
}

{
  "header": "LOBBY_LIST_DELIVERY",
  "content":
  {
    "lobbys_amount": $,
    "total_lobbys_amount:": $,
    "lobbys_list": [
      {
        "lobby_id": $,
        "lobby_name": "name",
        "map": $,
        "gametype": "solo/team",
        "curr_players": $,
        "max_players": $
      }
    ]
  }
}



{
  "header": "LOBBY_LIST_REQUEST",
  "client_id": $,
  "content":
  {
    "gametype": $,
    "map": $,
    "include_full": $,
    "lobby_range_beginning": $,
    "lobby_range_end": $,
  }
}



{
  "header": "CREATE_LOBBY_REQUEST",
"client_id": $,
  "content":
  {
    "gametype": $,
    "players_amount": $,
    "map": $,
    "endgame_cond": $,
    "web_speed": $,
    "mobile_max_speed": $,
    "dwarves_amount": $
  }
}


{
  "header": "JOIN_LOBBY_RESPONSE",
  "content": true/false
}

{
  "header": "LOBBY_STATUS_UPDATE",
  "content": {
    "gametype": $,
    "players_amount": $,
    "map": $,
    "endgame_cond": $,
    "web_speed": $,
    "mobile_max_speed": $,
    "dwarves_amount": $,
    "ready_players": $,
    "team1": [ If “gametype” == “solo” then “team2” array is empty and all players are listed in “team1”
      "player1",
      "player2"
    ],
    "team2": [
      "player1",
      "player2"
    ]
  }
}



 */