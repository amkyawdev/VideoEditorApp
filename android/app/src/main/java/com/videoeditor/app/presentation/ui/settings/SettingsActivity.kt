package com.videoeditor.app.presentation.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.videoeditor.app.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            // Load settings from DataStore
            val prefs = getSharedPreferences("settings", MODE_PRIVATE)
            
            binding.switchAutosave.isChecked = prefs.getBoolean("autosave", true)
            binding.switchHardwareAccel.isChecked = prefs.getBoolean("hardware_accel", true)
        }
    }

    private fun setupListeners() {
        binding.switchAutosave.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("autosave", isChecked)
        }

        binding.switchHardwareAccel.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("hardware_accel", isChecked)
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        getSharedPreferences("settings", MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }
}