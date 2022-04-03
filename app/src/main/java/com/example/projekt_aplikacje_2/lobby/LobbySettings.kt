package com.example.projekt_aplikacje_2.lobby

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Type


data class LobbySettings(var gametype: GameType = GameType.SOLO,
                         var players_amount: Int = 0,
                         var map: Int = 0,
                         var endgame_cond: Int = 0,
                         var web_speed: Float = 0.0f,
                         var mobile_max_speed: Float = 0.0f,
                         var dwarves_amount: Int = 0) : Serializable


enum class GameType {
    @SerializedName("solo")
    SOLO,
    @SerializedName("team")
    TEAM;
}
