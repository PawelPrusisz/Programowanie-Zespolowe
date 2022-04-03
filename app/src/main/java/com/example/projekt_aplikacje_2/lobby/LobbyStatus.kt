package com.example.projekt_aplikacje_2.lobby


import java.io.Serializable

data class LobbyStatus(
                         val lobby_id: Int = 0,
                         val lobby_name: String = "",
                         var gametype: GameType = GameType.SOLO,
                         var players_amount: Int = 0,
                         var map: Int = 0,
                         var endgame_cond: Int = 0,
                         var web_speed: Float = 0.0f,
                         var mobile_max_speed: Float = 0.0f,
                         var dwarves_amount: Int = 0,
                         var ready_players: Int = 0,
                         var teams: Map<String, List<String>> = mapOf("team0" to listOf(), "team1" to listOf(), "team2" to listOf())
                      ) : Serializable