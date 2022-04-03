package com.example.projekt_aplikacje_2

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt_aplikacje_2.communication.Sender
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.LobbyListDeliveryMessage
import com.example.projekt_aplikacje_2.communication.messages.LoginMessageResponse
import com.example.projekt_aplikacje_2.communication.messages.LoginMessageResponseContent
import com.example.projekt_aplikacje_2.databinding.ActivityMainBinding
import com.example.projekt_aplikacje_2.lobby.GameType
import com.example.projekt_aplikacje_2.lobby.LobbySettings
import com.example.projekt_aplikacje_2.location.GpsManager
import com.example.projekt_aplikacje_2.utils.Player
import com.example.projekt_aplikacje_2.utils.UIFunctions
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Socket.connect { exception -> runOnUiThread { Toast.makeText(this, "$exception", Toast.LENGTH_LONG).show(); finish() } }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initLayout()

        val gpsManager = GpsManager.getGpsManager(this)
        gpsManager.requestAllPermissions(this)

        gpsManager.getCurrentLocation { loc -> Log.i("Location", "${loc.latitude} : ${loc.longitude}") }
    }

    private fun signInClick() {
        val email = binding.editEmail.text.toString()
        val pass = binding.editPassword.text.toString()
        binding.signUpButton.button.isEnabled = false
        binding.signInButton.button.isEnabled = false
        //if(email.contains("@")){
            signIn(email, pass)
        // }
        //else{
        //    val toast = Toast.makeText(applicationContext, "Niepoprawny email", Toast.LENGTH_SHORT)
        //    toast.show()
        //}
    }

    private fun signIn(email: String, pass: String) {
        Log.d("LOGOWANIE", "$email, $pass")

        fun onSuccess(msg: LoginMessageResponse?) {

            runOnUiThread {
                binding.signUpButton.button.isEnabled = true
                binding.signInButton.button.isEnabled = true
            }

            if(msg == null) {
                runOnUiThread { UIFunctions.showAlert(this, "Brak odpowiedzi od serwera.") }
                return
            }

            if(msg.content.status != 1) {
                runOnUiThread { UIFunctions.showAlert(this, "Nie udało się zalogować - " + msg.content.failure_reason) }
                return
            }

            Player.nickname = msg.content.user_nickname
            val intent = Intent(this, MenuActivity::class.java)
            startActivityForResult(intent, 1)
        }

        fun onFailure(exception: Exception) {
            Log.d("LOGOWANIE", "Nie zalogowano... $exception")
            runOnUiThread {
                val msg = if(exception is NoSuchElementException) "Nie zalogowano - serwer nie odpowiada!" else "Nie zalogowano\n$exception"
                runOnUiThread { UIFunctions.showAlert(this, msg) }
                binding.signUpButton.button.isEnabled = true
                binding.signInButton.button.isEnabled = true
            }
        }

        Socket.logIn(email, pass, ::onSuccess, ::onFailure)
        //Socket.createLobby(LobbySettings(GameType.SOLO, 3, 3, 1, 3.0f, 3.0f, 2), {Log.i("LOGOEE", "OK -> ")}, {Log.i("LOGOEE", "Fail -> ")})
        //onSuccess(LoginMessageResponse(content = LoginMessageResponseContent(status = 1))) //!!!!!!!!!!!!!!!!!!!!!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CLOSE) {
            finish()
        } else if(resultCode == RESULT_SIGN_OUT) {

        }
    }

    private fun initLayout() {
        binding.signInButton.button.text = getString(R.string.sign_in)
        binding.signUpButton.button.text = getString(R.string.sign_up)

        binding.signInButton.button.setOnClickListener {
            signInClick()
        }

        binding.signUpButton.button.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    RegisterActivity::class.java
                ),
                1
            )
        }
    }

    companion object {
        const val RESULT_CLOSE = 10
        const val RESULT_SIGN_OUT = 20
    }
}