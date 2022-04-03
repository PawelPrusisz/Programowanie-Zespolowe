package com.example.projekt_aplikacje_2.create_lobby

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projekt_aplikacje_2.R
import com.example.projekt_aplikacje_2.databinding.FragmentAdvancedLobbySettingsBinding
import com.example.projekt_aplikacje_2.databinding.FragmentBasicLobbySettingsBinding
import com.example.projekt_aplikacje_2.lobby.LobbySettings
import com.example.projekt_aplikacje_2.utils.*

class AdvancedLobbySettingsFragment(
    val settings: LobbySettings,
    val onBack : () -> Unit,
    val onFinish : () -> Unit
) : Fragment() {

    private lateinit var binding : FragmentAdvancedLobbySettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvancedLobbySettingsBinding.inflate(inflater)

        /* Setup navigation events */
        binding.goToPreviousPageButton.setOnClickListener { onBack() }
        binding.finishButton.setOnClickListener { navigateToFinish() }

        return binding.root
    }

    fun blockFinishButton() {
        binding.finishButton.isEnabled = false
    }

    fun unlockFinishButton() {
        binding.finishButton.isEnabled = true
    }

    private fun navigateToFinish() {
        // Save user input in settings map

        val webPlayersSpeed = binding.webPlayersSpeedInput.text.toString().toFloat()
        val mobilePlayersSpeedLimit = binding.mobilePlayersSpeedLimitInput.text.toString().toFloat()
        val numberOfDwarfs = binding.numberOfDwarfsInput.text.toString().toInt()
        val punishment = binding.punishmentInput.text.toString()

        settings.web_speed = webPlayersSpeed
        settings.mobile_max_speed = mobilePlayersSpeedLimit
        settings.dwarves_amount = numberOfDwarfs
//        settings = punishment TODO: Punishment setting

        // Call additional onFinish function
        onFinish()
    }

}