package com.example.myapplication.ui.appointments
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class AppointmentsFragment : Fragment() {

    private lateinit var viewModel: AppointmentsViewModel
    private lateinit var listViewAppointments: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = AppointmentsViewModel.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // inflate the layout
        val view = inflater.inflate(R.layout.fragment_appointments, container, false)

        // get a reference to the ListView
        listViewAppointments = view.findViewById(R.id.list_view_appointments)

        // set adapter
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_appointments_informations,
            viewModel.mapmap())
        //  Link adapter to the ListView
        listViewAppointments.adapter = adapter
        listViewAppointments.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val itemClicked = viewModel.appointmentsList[position]

            val bundle = Bundle().apply {
                putSerializable("selectedAppointment", itemClicked)
            }
            val appointmentsDetailsFragment = AppointmentsDetailsFragment().apply {
                arguments = bundle
            }


            listViewAppointments.visibility = View.GONE

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, appointmentsDetailsFragment)
                .commit()
        }
        return view
    }
}