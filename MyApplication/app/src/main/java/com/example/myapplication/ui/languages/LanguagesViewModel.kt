package com.example.myapplication.ui.languages
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.MainActivity

class LanguagesViewModel : ViewModel() {
    private val listLanguages = MutableLiveData<List<String>>()

    init {
        listLanguages.value = listOf("English", "Français")
    }

    fun getListItems(): List<String> {
        return listLanguages.value.orEmpty()
    }

    fun setLang(lang : String?, context : Context) {
        when (lang)
        {
            "English" ->
            {
                MainActivity.changeLocaleTo("en")
                Toast.makeText(context, "Language set to : $lang", Toast.LENGTH_SHORT).show()
            }
            "Français" ->
            {
                MainActivity.changeLocaleTo("fr")
                Toast.makeText(context, "Langue choisie : $lang", Toast.LENGTH_SHORT).show()
            }
        }
    }
}