package com.example.myapplication.ui.settings
import android.content.Context
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

class SettingsFragment : Fragment() {
    private lateinit var listviewsettings: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity())[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // get a reference to the ListView
        listviewsettings = view.findViewById(R.id.list_view_settings)

        // set adapter
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, viewModel.getListItems())

        //  Link adapter to the ListView
        listviewsettings.adapter = adapter

        // listener
        listviewsettings.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            viewModel.setParameters(adapter.getItem(position), requireContext())
        }
        return view
    }
}