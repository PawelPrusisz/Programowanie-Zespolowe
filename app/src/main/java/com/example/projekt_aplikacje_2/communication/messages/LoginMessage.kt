package com.example.projekt_aplikacje_2.communication.messages



data class LoginMessageContent(val email: String = "", val password: String = "", val is_mobile: Boolean = true)


data class LoginMessage(val header: Header = Header.LOG_IN_REQUEST,
                        val client_id: Int = 0,
                        val content: LoginMessageContent)



data class LoginMessageResponseContent(val status: Int = -1,
                                       val user_nickname: String = "",
                                       val failure_reason: String = "")


data class LoginMessageResponse(val header: Header = Header.LOG_IN_RESPONSE,
                                val content: LoginMessageResponseContent)


/*
{
  "header": "LOG_IN_REQUEST",
  "client_id": 1,
  "content": {
    "email": "abc@abc.com",
    "password": "pass"
  }
}


{
  "header": "LOG_IN_RESPONSE",
  "content": {
    "status": 1,
    "user_nickname": null/"nickname",
    "failure_reason": null/"NOT_REGISTERED"/"WRONG_CREDENTIALS"/”DATA_LOST”//"UNKNOWN"
  }

 */