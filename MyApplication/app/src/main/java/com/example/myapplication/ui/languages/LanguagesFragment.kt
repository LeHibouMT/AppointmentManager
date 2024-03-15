package com.example.myapplication.ui.languages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R

class LanguagesFragment : Fragment() {
    private lateinit var viewModel: LanguagesViewModel
    private lateinit var listviewlanguages: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // recuperate an instance
        viewModel = ViewModelProvider(this)[LanguagesViewModel::class.java]

        // inflate the layout
        val view = inflater.inflate(R.layout.fragment_languages, container, false)

        // get a reference to the ListView
        listviewlanguages = view.findViewById(R.id.list_view_languages)

        // set adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, viewModel.getListItems())

        //  Link adapter to the ListView
        listviewlanguages.adapter = adapter

        // listener
        listviewlanguages.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val itemClicked = adapter.getItem(position)
            viewModel.setLang(itemClicked, requireContext())
        }
        return view
    }
}