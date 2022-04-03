package com.example.projekt_aplikacje_2.communication.messages

data class PositionDataUpdate(val header: Header = Header.POSITION_DATA_UPDATE, val content: PositionDataUpdateContent)
data class PositionDataUpdateContent(val team0: ArrayList<PositionDataUserUpdateContent>, val team1: ArrayList<PositionDataUserUpdateContent>, val team2: ArrayList<PositionDataUserUpdateContent>)
data class PositionDataUserUpdateContent(val username: String = "", val lon: Double = 0.0, val lat: Double = 0.0)

data class DwarfListDelivery(val header: Header = Header.DWARF_LIST_DELIVERY, val content: DwarfListDeliveryContent)
data class DwarfListDeliveryContent(val dwarfs_list: ArrayList<DwarfListDeliveryContentItem>)
data class DwarfListDeliveryContentItem(val lon: Double = 0.0, val lat: Double = 0.0, val id: Int = 0)


data class MobileMove(val header: Header = Header.MOBILE_MOVE, val client_id: Int = 0, val content:MobileMoveContent )
data class MobileMoveContent(val lon: Double = 0.0, val lat: Double = 0.0, val timestamp: Long = 0)

data class PlayersPointsUpdate(val header: Header = Header.PLAYERS_POINTS_UPDATE, val content:PlayersPointsUpdateContent)
data class PlayersPointsUpdateContent(val teams:PlayersPointsUpdateContentTeams)
data class PlayersPointsUpdateContentTeams(val team:PlayersPointsUpdateContentTeamsTeam)
data class PlayersPointsUpdateContentTeamsTeam(val fields: ArrayList <PlayersPointsUpdateContentTeamsTeamField>)
data class PlayersPointsUpdateContentTeamsTeamField(val username: String, val points: Int)

data class  PickDwarfResponse(val header: Header = Header.PICK_DWARF_RESPONSE, val content:PickDwarfResponseContent)
data class  PickDwarfResponseContent(val status: Boolean, val points: Int)

data class  MobileMoveResponse(val header: Header = Header.MOBILE_MOVE_RESPONSE, val content:MobileMoveResponseContent)
data class  MobileMoveResponseContent(val response: String, val lon: Double = 0.0, val lat: Double = 0.0, val punishment_time: Double = 0.0)

data class  PickDwarfRequest(val header: Header = Header.PICK_DWARF_REQUEST, val client_id: Int = 0, val content: Int)


/*
{
  "header": "PICK_DWARF_REQUEST",
  "client_id": 123,
  "content": $(dwarf_id)
}

{
  "header": "MOBILE_MOVE_RESPONSE",
  "content": {
     "response": "MOBILE_VALID_MOVE"/"SPEED_BAN"/"POSITION_BAN",
     "lon": 12.345/null,
     "lat": 12.345/null,
     "punishment_time": 12.345/null
  }
}


{
  "header": "PICK_DWARF_RESPONSE",
  "content": {
      "status": 1,
      "points": $/null
  }
}


{
    "header": "PLAYERS_POINTS_UPDATE",
    "content": {
     "teams": {
            "team1": [
                {
                    "username": "username1",
                    "points": $
                },
                {
                    "username": "username2",
                    "points": $
                }
            ],
           "team2": [
                {
                    "username": "username3",
                    "points": $
                },
                {
                    "username": "username4",
                    "points": $
                }
           ]
         }
    }
}
*/