package com.example.projekt_aplikacje_2.communication.messages



data class RegisterMessageContent(val email: String = "",
                                  val password: String = "",
                                  val nickname: String = "")


data class RegisterMessage(val header: Header = Header.REGISTER_REQUEST,
                           val client_id: Int = 0,
                           val content: RegisterMessageContent)



data class RegisterMessageResponseContent(val status: Int = -1,
                                          val failure_reason: String = "")


data class RegisterMessageResponse(val header: Header = Header.REGISTER_RESPONSE,
                                   val content: RegisterMessageResponseContent)


/*
{
  "header": "REGISTER_REQUEST",
  "client_id": 1,
  "content": {
	"email": "abc@abc.com",
	"password": "pass",
	"nickname": "nick"
  }
}



{
  "header": "REGISTER_RESPONSE",
	"content": {
  	"status": 1,
  	"failure_reason": null/"EMAIL_TAKEN"/”DATA_LOST”/”UNKNOWN”
  }
}


 */