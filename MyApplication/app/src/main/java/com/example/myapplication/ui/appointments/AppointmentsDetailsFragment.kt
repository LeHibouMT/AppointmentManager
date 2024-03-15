package com.example.myapplication.ui.appointments
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.ui.appointments.AppointmentsViewModel.Companion.niceTime2
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AppointmentsDetailsFragment: Fragment() {
    private lateinit var viewModel: AppointmentsViewModel
    private lateinit var selectedAppointment: Appointment
    private lateinit var detailsView: ScrollView
    private var pickedContactName: String? = ""
    private var pickedContactNumber: String? = ""
    private lateinit var importContactName: EditText
    private lateinit var importContactNumber: EditText

    private val pickContactLauncher = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { contactUri ->
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            )
            val cursor = requireContext().contentResolver.query(
                contactUri,
                projection,
                null,
                null,
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                    val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val hasNumberIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    val id = it.getString(idIndex)
                    pickedContactName = it.getString(nameIndex)
                    val pickedContactHasNumber = it.getString(hasNumberIndex).toInt()

                    if (pickedContactHasNumber == 1){
                        val cursor2 = requireContext().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                        cursor2?.use {
                            if (cursor2.moveToFirst()) {
                                val numberIndex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                pickedContactNumber = cursor2.getString(numberIndex)
                            }
                        }
                    }
                    if (pickedContactName != "") importContactName.setText(pickedContactName)
                    if (pickedContactNumber != "") importContactNumber.setText(pickedContactNumber)
                    Toast.makeText(context, getString(R.string.importedcontact), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = AppointmentsViewModel.getInstance(requireActivity().application)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_appointment_details, container, false)

        detailsView = view.findViewById(R.id.scroll_view_appointment_details)

        arguments?.let {selectedAppointment = it.getSerializable("selectedAppointment", Appointment::class.java)!!}

        selectedAppointment.let {
            detailsView.findViewById<TextView>(R.id.show_name).text = getString(R.string.name) + it.vname
            detailsView.findViewById<TextView>(R.id.show_date).text = getString(R.string.date) + it.vdate
            detailsView.findViewById<TextView>(R.id.show_time).text = getString(R.string.time) + niceTime2(it.vtime)
            detailsView.findViewById<TextView>(R.id.show_address).text = getString(R.string.address) + it.vaddress
            detailsView.findViewById<TextView>(R.id.show_ContactName).text = getString(R.string.contactname) + it.vcontactname
            detailsView.findViewById<TextView>(R.id.show_ContactNumber).text = getString(R.string.contactnumber) + it.vcontactnumber
        }

        // backButton
        val backButton = detailsView.findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AppointmentsFragment())
                .commit()
        }

        // mapButton
        val mapButton = detailsView.findViewById<Button>(R.id.importMapButton)
        mapButton.setOnClickListener {
            val map = "http://maps.google.co.in/maps?q=" + selectedAppointment.vaddress
            val gmmIntentUri = Uri.parse(map)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        // callButton
        val callButton = detailsView.findViewById<FloatingActionButton>(R.id.callButton)
        callButton.setOnClickListener {
            viewModel.onCallClick(requireContext(), selectedAppointment.vcontactnumber)
        }

        // shareButton
        val shareButton = detailsView.findViewById<FloatingActionButton>(R.id.shareButton)
        shareButton.setOnClickListener {
            viewModel.shareMethod(requireContext(),selectedAppointment)
        }

        // modifyButton
        val modifyButton = detailsView.findViewById<Button>(R.id.btn_modify)
        modifyButton.setOnClickListener {
            // dialogView
            val dialogView = layoutInflater.inflate(R.layout.add_appointment, null)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle(R.string.editmsg)

            // editText + button for appointment name
            dialogView.findViewById<EditText>(R.id.appointment_name).setText(selectedAppointment.vname)
            val appointmentName = dialogView.findViewById<EditText>(R.id.appointment_name)

            // datePicker
            val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
            val vd = selectedAppointment.vdate.split("-")
            datePicker.init(vd[0].toInt(),vd[1].toInt() -1, vd[2].toInt(),null)
            // timePicker
            val timePicker = dialogView.findViewById<TimePicker>(R.id.time_picker)
            timePicker.setIs24HourView(true)
            val vt = selectedAppointment.vtime.split(":")
            timePicker.hour = vt[0].toInt()
            timePicker.minute = vt[1].toInt()
            // buttons + listener
            dialogView.findViewById<EditText>(R.id.appointment_address).setText(selectedAppointment.vaddress)
            val appointmentAddress = dialogView.findViewById<EditText>(R.id.appointment_address)

            importContactName = dialogView.findViewById(R.id.appointment_contact_name)
            dialogView.findViewById<EditText>(R.id.appointment_contact_name).setText(selectedAppointment.vcontactname)
            importContactNumber = dialogView.findViewById(R.id.appointment_contact_number)
            dialogView.findViewById<EditText>(R.id.appointment_contact_number).setText(selectedAppointment.vcontactnumber)
            val importContactButton = dialogView.findViewById<Button>(R.id.importContactButton)
            importContactButton.setOnClickListener {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_CONTACTS),
                    viewModel.REQUEST_CONTACTS_PERMISSION)
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.openContacts(pickContactLauncher)
                }
            }

            // OK BUTTON
            dialogBuilder.setPositiveButton(R.string.okmsg) { _, _ ->
                val name = if (appointmentName.text.toString() == "") selectedAppointment.vname else appointmentName.text.toString()
                val date = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"
                val time = "${timePicker.hour}:${timePicker.minute}"
                val address = appointmentAddress.text.toString()
                val contactName = importContactName.text.toString()
                val contactNumber = importContactNumber.text.toString()
                viewModel.editAppointmentById(selectedAppointment.vid, name, date, time,address, contactName, contactNumber)
                viewModel.createNotificationChannel(requireContext())
                viewModel.showNotification(requireContext(), Appointment(selectedAppointment.vid, name, date, time,address, contactName, contactNumber))
                pickedContactName = ""
                pickedContactNumber = ""
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AppointmentsFragment())
                    .commit()
            }
            // DELETE BUTTON
            dialogBuilder.setNeutralButton(R.string.delatemsg) { _, _ ->
                pickedContactName = ""
                pickedContactNumber = ""
                viewModel.delete(selectedAppointment.vid)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AppointmentsFragment())
                    .commit()
            }
            // CANCEL BUTTON
            dialogBuilder.setNegativeButton(R.string.cancelmsg) { dialog, _ ->
                pickedContactName = ""
                pickedContactNumber = ""
                dialog.cancel()
            }

            val dialog = dialogBuilder.create()
            dialog.show()
        }
        return view
    }
}