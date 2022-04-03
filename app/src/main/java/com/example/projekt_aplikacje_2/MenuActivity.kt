package com.example.projekt_aplikacje_2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.projekt_aplikacje_2.databinding.ActivityMenuBinding
import com.example.projekt_aplikacje_2.MainActivity.Companion.RESULT_CLOSE
import com.example.projekt_aplikacje_2.MainActivity.Companion.RESULT_SIGN_OUT
import com.example.projekt_aplikacje_2.create_lobby.CreateLobbyActivity
import com.example.projekt_aplikacje_2.utils.Player

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setLayout()
    }

    private fun setLayout() {
        binding.buttonJoinGame.button.text = getString(R.string.join_game)
        binding.buttonJoinGame.button.setOnClickListener{ navigateToJoinGame() }
        binding.buttonNewGame.button.text = getString(R.string.new_game)
        binding.loginDeck.root.setOnClickListener{ rankingInit() }
        binding.loginDeck.loginTextView.text = Player.nickname
        binding.buttonSettings.button.text = getString(R.string.settings)
        binding.buttonSignOut.button.text = getString(R.string.sign_out)
        binding.buttonSettings.button.setOnClickListener { settings() }
        binding.buttonSignOut.button.setOnClickListener {
            val intent = Intent()
            setResult(RESULT_SIGN_OUT, intent)
            finish()
        }

        binding.buttonNewGame.button.setOnClickListener {
            navigateToCreateLobby()
        }

        binding.testButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

    fun rankingInit() {
        Log.i("Ranking", "Click!")

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.ranking_popup, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.findViewById<ImageView>(R.id.back).setOnClickListener{
            mAlertDialog.dismiss()
            Log.i("Dialog ustawienia", "koniec")
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDialogView.findViewById<TextView>(R.id.Ranking).text = "Ranking - " + Player.nickname
        mDialogView.findViewById<TextView>(R.id.avgMiejsceButton).text = "3.33"
        mDialogView.findViewById<TextView>(R.id.miejscebutton).text = "8"
        mDialogView.findViewById<TextView>(R.id.winratebutton).text = "21%"
        mDialogView.findViewById<TextView>(R.id.rankplacebutton).text = "37"

        val locationsArray = resources.getStringArray(R.array.locations)

        mDialogView.findViewById<Spinner>(R.id.SpinnerRanking).adapter = ArrayAdapter<String>(this, R.layout.item_location_spinner, R.id.locationLabel, locationsArray)


        mDialogView.findViewById<Spinner>(R.id.SpinnerRanking).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i("Spinner", locationsArray[p2])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    fun settings() {
        Log.i("Settings", "Click!")
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.settings_popup, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mDialogView.findViewById<ImageView>(R.id.back).setOnClickListener{
            mAlertDialog.dismiss()
            Log.i("Dialog ustawienia", "koniec")
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialogView.findViewById<LinearLayout>(R.id.buttonPotwierdzHaslo).findViewById<Button>(R.id.button).text = "Potwierdź"
        mDialogView.findViewById<LinearLayout>(R.id.buttonPotwierdzLogin).findViewById<Button>(R.id.button).text = "Potwierdź"
        mDialogView.findViewById<LinearLayout>(R.id.buttonPotwierdzHaslo).findViewById<Button>(R.id.button).setOnClickListener{
            val password = mDialogView.findViewById<EditText>(R.id.haslo).text.toString()
            val password2 = mDialogView.findViewById<EditText>(R.id.potwierdzHaslo).text.toString()
            Log.i("Haslo", password)
            Log.i("Haslo", password2)
            if(password.equals(password2))
            {
                Log.i("Czy hasla takie same", "Tak")
            }
            else
            {
                Log.i("Czy hasla takie same", "Nie")
            }
        }

        mDialogView.findViewById<LinearLayout>(R.id.buttonPotwierdzLogin).findViewById<Button>(R.id.button).setOnClickListener{
            val login = mDialogView.findViewById<EditText>(R.id.login).text.toString()
            Log.i("Login", login)
        }
    }
    private fun navigateToJoinGame() {
        val intent = Intent(this, JoinToGameActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        setResult(RESULT_CLOSE)
        finish()
    }

    private fun navigateToCreateLobby() {
        val intent = Intent(this, CreateLobbyActivity::class.java)
        startActivity(intent)
    }

}