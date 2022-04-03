package com.example.projekt_aplikacje_2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekt_aplikacje_2.communication.Socket
import com.example.projekt_aplikacje_2.communication.messages.RegisterMessageResponse
import com.example.projekt_aplikacje_2.communication.messages.RegisterMessageResponseContent
import com.example.projekt_aplikacje_2.databinding.ActivityRegisterBinding
import com.example.projekt_aplikacje_2.utils.Player
import com.example.projekt_aplikacje_2.utils.UIFunctions
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.signUpButton.button.text = "Zarejestruj się"
        binding.signUpButton.button.setOnClickListener { registerInClick() }
    }

    private fun registerInClick() {
        val login = binding.putLogin.text.toString()
        val email = binding.putEmail.text.toString()

        val password = binding.putPassword.text.toString()
        val password2 = binding.confirmPassword.text.toString()

        Log.i("Hasło", password)
        Log.i("Hasło", password2)
        if (password == password2) {
            if(password != ""&& email != "" && login != "") {
                if(password.length >= 8){
                    if(password.length <= 32) {
                        if(email.contains("@")){
                            register(email, password, login)
                        }
                        else{
                            val toast = Toast.makeText(applicationContext, "Zły email", Toast.LENGTH_SHORT)
                            toast.show()
                        }
                    }
                    else{
                        val toast = Toast.makeText(applicationContext, "Hasło za długie", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
                else{
                    val toast = Toast.makeText(applicationContext, "Hasło za krótkie", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        } else {
            val toast = Toast.makeText(applicationContext, "Hasła się różnią", Toast.LENGTH_SHORT)
            toast.show()
        }

        Log.i("Login", login)
        Log.i("Email", email)
    }

    private fun register(email: String, pass: String, nickname: String) {
        Log.d("REJESTRACJA", "$email, $pass, $nickname")

        fun onSuccess(msg: RegisterMessageResponse?) {

            runOnUiThread {
                binding.signUpButton.button.isEnabled = true
            }

            if(msg == null) {
                runOnUiThread { UIFunctions.showAlert(this, "Brak odpowiedzi od serwera.") }
                return
            }

            if(msg.content.status != 1) {
                runOnUiThread { UIFunctions.showAlert(this, "Nie udało się zarejestrować - " + msg.content.failure_reason) }
                return
            }
            finish()
        }

        fun onFailure(exception: Exception) {
            Log.d("Rejestracja", "Nie zarejestrowano... $exception")
            runOnUiThread {
                val msg = if(exception is NoSuchElementException) "Nie się zarejestrować - serwer nie odpowiada!" else "Nie zalogowano\n$exception"
                runOnUiThread { UIFunctions.showAlert(this, msg) }
                binding.signUpButton.button.isEnabled = true
            }
        }
        Socket.register(email, pass,nickname, ::onSuccess, ::onFailure)
    }


}