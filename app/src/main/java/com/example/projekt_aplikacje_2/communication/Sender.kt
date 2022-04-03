package com.example.projekt_aplikacje_2.communication

import android.util.Log
import com.example.projekt_aplikacje_2.communication.messages.Header
import com.google.gson.*
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class Sender(
    private val serverAddress: String = Server.ADDRESS,
    private val serverPort: Int = Server.PORT
) {
    private val TIME_OUT = 3 * 1000
    val socket: Socket = Socket()
    private val gson : Gson
    private val input : Scanner

    init {
        Log.i("SENDER", "Init... $serverAddress : $serverPort")
        socket.connect(InetSocketAddress(serverAddress, serverPort), TIME_OUT)
        socket.soTimeout = TIME_OUT
        input = Scanner(socket.getInputStream())

        val builder = GsonBuilder()
        //builder.registerTypeAdapter(GameType::class.java, GameTypeAdapter())
        gson = builder.create()
        Log.i("SENDER", "OK")
    }

    fun resetScanner() {
        try {
            socket.getInputStream().reset()
            socket.getInputStream().skip(2048)
        } catch (e: Exception) {

        }

    }

    fun<T> sendMessage(message: T) {
        val json = gson.toJson(message) + '\n'
        Log.i("SENDER", "Send:\n$json")
        socket.outputStream.write(json.toByteArray())
        socket.getOutputStream().flush()
        Log.i("SENDER", "Send - OK.")
    }

    fun <T> receiveMessage(msgType: Class<T>): T? {
        Log.i("SENDER", "Waiting for message")

        var t : JsonElement? = null
        val buffer = StringBuffer()
        var ch: Int
        var run = true
        try {
            while (run) {
                ch = socket.getInputStream().read()
//                if (ch == -1) {
//                    break
//                }
                buffer.append(ch.toChar())

                try {
                    val str = buffer.toString()
//                  Log.i("SENDER", "Read:\n'$str'")
                    t = JsonParser.parseString(str)

                    if(str.startsWith("{") && str.endsWith("}\n") && t != null && t != JsonNull.INSTANCE) {
                        run = false
                    }

                } catch (e: JsonParseException) { }
                catch(e: JsonSyntaxException) { }
            }
        } catch (e: Exception) {
            //handle exception
        }

        Log.i("SENDER", "Read:\n$t")
        return gson.fromJson(t, msgType)

    }

    fun receiveMessage() : JsonElement {
        Log.i("SENDER", "Waiting for message")

        var t : JsonElement? = null
        val buffer = StringBuffer()
        var ch: Int
        var run = true
        try {
            while (run) {
                ch = socket.getInputStream().read()
//                if (ch == -1) {
//                    break
//                }
                buffer.append(ch.toChar())

                try {
                    val str = buffer.toString()
//                  Log.i("SENDER", "Read:\n'$str'")
                    t = JsonParser.parseString(str)

                    if (str.startsWith("{") && str.endsWith("}\n") && t != null && t != JsonNull.INSTANCE) {
                        run = false
                    }

                } catch (e: JsonParseException) {
                } catch (e: JsonSyntaxException) {
                }
            }
        } catch (e: Exception) {
            //handle exception
        }


        Log.i("SENDER", "Read:\n$t")
        return t ?: JsonParser.parseString("null")
    }

    fun toObject(json: JsonElement, type: Class<Any>): Any {
        return gson.fromJson(json, type)
    }

    fun getHeader(jsonMsg : JsonElement) : Header {
        return gson.fromJson(jsonMsg.asJsonObject.get("header"), Header::class.java)
    }

    fun closeSocket() {
        socket.close()
    }
}