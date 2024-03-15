package com.example.myapplication.ui.settings
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.R

@SuppressLint("RestrictedApi")
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayer : MediaPlayer = MediaPlayer.create(getApplication(), R.raw.song)

    fun getListItems(): List<String> {
        return listOf(getApplication<Application>().getString(R.string.daymode),
            getApplication<Application>().getString(R.string.nightmode),
            getApplication<Application>().getString(R.string.music),
            getApplication<Application>().getString(R.string.notifications))
    }

    fun setParameters(th : String?, context: Context) {

        when (th)
        {
            getApplication<Application>().getString(R.string.daymode) ->
            {
                val pref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                pref.edit().putInt("saved_theme", AppCompatDelegate.MODE_NIGHT_NO).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(context, "$th : " + getApplication<Application>().getString(R.string.activated), Toast.LENGTH_SHORT).show()
            }
            getApplication<Application>().getString(R.string.nightmode) ->
            {
                val pref = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                pref.edit().putInt("saved_theme", AppCompatDelegate.MODE_NIGHT_YES).apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(context, "$th : " + getApplication<Application>().getString(R.string.activated), Toast.LENGTH_SHORT).show()
            }
            getApplication<Application>().getString(R.string.music) ->
            {
                val prefm = context.getSharedPreferences("my_prefs2", Context.MODE_PRIVATE)
                var muspref = prefm.getBoolean("my_music",false)
                if (muspref) {
                    stopMusic()
                    Toast.makeText(context, "$th : " + getApplication<Application>().getString(R.string.desactivated), Toast.LENGTH_SHORT).show()
                    muspref = false
                }
                else {
                    startMusic()
                    Toast.makeText(context, "$th : " + getApplication<Application>().getString(R.string.activated), Toast.LENGTH_SHORT).show()
                    muspref = true
                }
                prefm.edit()
                    .putBoolean("my_music", muspref)
                    .apply()
            }
            getApplication<Application>().getString(R.string.notifications) ->
            {
                val intent = Intent()
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                context.startActivity(intent)
            }
        }

    }

    fun startMusic() {
        this@SettingsViewModel.mediaPlayer.isLooping = true
        this@SettingsViewModel.mediaPlayer.start()
    }

    fun stopMusic() {
        this@SettingsViewModel.mediaPlayer.pause()
    }
}