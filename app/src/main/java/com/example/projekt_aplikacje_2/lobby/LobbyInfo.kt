package com.example.projekt_aplikacje_2.lobby

data class LobbyInfo(val lobby_id: Int = 0,
                     val lobby_name: String = "",
                     val map: Int = 0,
                     val gametype: GameType = GameType.SOLO,
                     val curr_players: Int = 0,
                     val players_amount: Int = 0)
