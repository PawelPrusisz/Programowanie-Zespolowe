package com.example.projekt_aplikacje_2.communication.messages



enum class Header {
    SERVER_HELLO,
    LOG_IN_REQUEST, LOG_IN_RESPONSE,
    REGISTER_REQUEST, REGISTER_RESPONSE,
    CREATE_LOBBY_REQUEST, CREATE_LOBBY_RESPONSE, LOBBY_STATUS_UPDATE,
    JOIN_LOBBY_REQUEST, JOIN_LOBBY_RESPONSE,
    LOBBY_LIST_REQUEST, LOBBY_LIST_DELIVERY,
    CHANGE_TEAM_REQUEST, CHANGE_TEAM_RESPONSE,
    PLAYER_IS_READY, PLAYER_IS_UNREADY, ACKNOWLEDGE,
    START_GAME_REQUEST, START_GAME_RESPONSE,
    QUIT_LOBBY_REQUEST, QUIT_LOBBY_RESPONSE,
    LOBBY_CREATOR_RIGHTS, POSITION_DATA_UPDATE,
    DWARF_LIST_DELIVERY, MOBILE_MOVE,
    PLAYERS_POINTS_UPDATE,PICK_DWARF_RESPONSE,
    MOBILE_MOVE_RESPONSE,PICK_DWARF_REQUEST,
    ERROR, MAP_BOUNDS;


    companion object {
        val msgTypes : Map<Header, Class<Any>> = mapOf(
            SERVER_HELLO to HandshakeMessage::class.java,
            LOG_IN_REQUEST to LoginMessage::class.java,
            REGISTER_RESPONSE to RegisterMessageResponse::class.java,
            REGISTER_REQUEST to RegisterMessage::class.java,
            LOG_IN_RESPONSE to LoginMessageResponse::class.java,
            CREATE_LOBBY_REQUEST to CreateLobbyMessage::class.java,
            CREATE_LOBBY_RESPONSE to CreateLobbyResponse::class.java,
            JOIN_LOBBY_REQUEST to JoinLobbyMessage::class.java,
            JOIN_LOBBY_RESPONSE to JoinLobbyMessageResponse::class.java,
            LOBBY_STATUS_UPDATE to LobbyStatusUpdateMessage::class.java,
            LOBBY_LIST_REQUEST to LobbyListMessage::class.java,
            LOBBY_LIST_DELIVERY to LobbyListDeliveryMessage::class.java,
            PLAYER_IS_READY to LobbyPlayerIsReady::class.java,
            PLAYER_IS_UNREADY to LobbyPlayerIsNotReady::class.java,
            ACKNOWLEDGE to LobbyAck::class.java,
            START_GAME_REQUEST to StartGame::class.java,
            START_GAME_RESPONSE to StartGameResponse::class.java,
            CHANGE_TEAM_REQUEST to ChangeTeamRequest::class.java,
            CHANGE_TEAM_RESPONSE to ChangeTeamResponse::class.java,
            QUIT_LOBBY_REQUEST to QuitLobbyRequest::class.java,
            QUIT_LOBBY_RESPONSE to QuitLobbyResponse::class.java,
            LOBBY_CREATOR_RIGHTS to LobbyCreatorMessage::class.java,
            POSITION_DATA_UPDATE to PositionDataUpdate::class.java,
            DWARF_LIST_DELIVERY to DwarfListDelivery::class.java,
            MOBILE_MOVE to MobileMove::class.java,
            MOBILE_MOVE_RESPONSE to MobileMoveResponse::class.java,
            PICK_DWARF_REQUEST to PickDwarfRequest::class.java,
            PICK_DWARF_RESPONSE to PickDwarfResponse::class.java,
            PLAYERS_POINTS_UPDATE to PlayersPointsUpdate::class.java,
            ERROR to ErrorMessage::class.java
        ) as Map<Header, Class<Any>>
    }
}
