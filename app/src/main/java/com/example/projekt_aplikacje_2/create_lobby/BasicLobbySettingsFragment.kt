package com.example.projekt_aplikacje_2.create_lobby

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.example.projekt_aplikacje_2.R
import com.example.projekt_aplikacje_2.databinding.FragmentBasicLobbySettingsBinding
import com.example.projekt_aplikacje_2.lobby.GameType
import com.example.projekt_aplikacje_2.lobby.LobbySettings

class BasicLobbySettingsFragment(
    val settings: LobbySettings,
    val onNext : () -> Unit
) : Fragment() {

    private lateinit var binding : FragmentBasicLobbySettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicLobbySettingsBinding.inflate(inflater)


        /* Setup navigation events */
        binding.goToNextPageButton.setOnClickListener { navigateToNextStep() }


        /* Setup location spinner */
        val locations = resources.getStringArray(R.array.locations)
        binding.locationSpinner.adapter =
            ArrayAdapter(requireContext(), R.layout.item_location_spinner, R.id.locationLabel, locations)
        binding.locationSpinner.setPopupBackgroundResource(R.drawable.rounded_corners)

        /* Other events */
        binding.endConditionRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            switchTimeInput(checkedId == R.id.endConditionTime)
        }

        return binding.root
    }

    private fun switchTimeInput(visible : Boolean) {
        if (visible) {
            binding.gameEndConditionTimeContainer.visibility = View.VISIBLE
        } else {
            binding.gameEndConditionTimeContainer.visibility = View.INVISIBLE
        }
    }

    private fun navigateToNextStep() {
        // Save user input in settings map
        val gameType = when (binding.gameTypeRadioGroup.checkedRadioButtonId) {
            R.id.gameTypeTeamButton -> GameType.TEAM
            else -> GameType.SOLO
        }
        val playersCount = binding.playerCountInput.text.toString().toInt()
        val location = binding.locationSpinner.selectedItem.toString()
        val endCondition = when (binding.endConditionRadioGroup.checkedRadioButtonId) {
            R.id.endConditionTime -> binding.gameEndTimeInput.text.toString().toInt()
            else -> 0
        }

        settings.endgame_cond = endCondition
        settings.gametype = gameType
        settings.players_amount = playersCount
        settings.map = binding.locationSpinner.selectedItemPosition // TODO : Update location based on input

        // Call additional onNext function
        onNext()
    }


}