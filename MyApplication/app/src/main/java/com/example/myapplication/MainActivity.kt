package com.example.myapplication
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.settings.SettingsViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: SettingsViewModel

    companion object {
        fun changeLocaleTo(locale : String){
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_calendar, R.id.nav_appointments, R.id.nav_languages, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val savedTheme = sharedPreferences.getInt("saved_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedTheme)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        val prefm = getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
        val muspref = prefm.getBoolean("my_music",false)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        if (muspref){
            viewModel.startMusic()
        }
    }

    override fun onResume() {
        super.onResume()
        if (getSharedPreferences("my_prefs2", Context.MODE_PRIVATE).getBoolean("my_music",false)) {
            viewModel.startMusic()
        }
    }

    override fun onPause() {
        super.onPause()

        if (getSharedPreferences("my_prefs2", Context.MODE_PRIVATE).getBoolean("my_music",false)){
            viewModel.stopMusic()
        }
    }

    override fun onStop() {
        super.onStop()
        if (getSharedPreferences("my_prefs2", Context.MODE_PRIVATE).getBoolean("my_music",false)){
            viewModel.stopMusic()
        }
    }

}