package com.example.myapplication.ui.calendar
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.ui.appointments.Appointment
import com.example.myapplication.ui.appointments.AppointmentsViewModel


class CalendarFragment : Fragment() {
    private lateinit var calendarView   : CalendarView
    private lateinit var viewModel  : AppointmentsViewModel
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

    @SuppressLint("Range")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = AppointmentsViewModel.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        calendarView.minDate = 0
        calendarView.date = System.currentTimeMillis()
        calendarView.setOnDateChangeListener { _ , year, month, dayOfMonth ->

            val dialogView = layoutInflater.inflate(R.layout.add_appointment, null)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle(R.string.addrdv)

            val appointmentName = dialogView.findViewById<EditText>(R.id.appointment_name)

            val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
            datePicker.init(year, month, dayOfMonth, null)
            val timePicker = dialogView.findViewById<TimePicker>(R.id.time_picker)
            timePicker.setIs24HourView(true)
            timePicker.hour = 0
            timePicker.minute = 0

            val appointmentAddress = dialogView.findViewById<EditText>(R.id.appointment_address)


            importContactName = dialogView.findViewById(R.id.appointment_contact_name)
            importContactNumber = dialogView.findViewById(R.id.appointment_contact_number)
            val importContactButton = dialogView.findViewById<Button>(R.id.importContactButton)
            importContactButton.setOnClickListener {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_CONTACTS),
                    viewModel.REQUEST_CONTACTS_PERMISSION)
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.openContacts(pickContactLauncher)
                }
            }





            dialogBuilder.setPositiveButton(R.string.okmsg) { _, _ ->
                val name = if (appointmentName.text.toString() =="") getString(R.string.rdvnoname) else appointmentName.text.toString()
                val date = "${datePicker.year}-${datePicker.month + 1}-${datePicker.dayOfMonth}"
                val time = "${timePicker.hour}:${timePicker.minute}"
                val address = appointmentAddress.text.toString()
                val contactName = importContactName.text.toString()
                val contactNumber = importContactNumber.text.toString()
                val idx = viewModel.indexAppointments()
                val appointment = Appointment(idx, name, date, time, address, contactName, contactNumber)
                viewModel.addAppointment(appointment)
                viewModel.createNotificationChannel(requireContext())
                viewModel.showNotification(requireContext(), appointment)
                pickedContactName = ""
                pickedContactNumber = ""
            }
            dialogBuilder.setNegativeButton(R.string.cancelmsg) { dialog,_  ->
                dialog.cancel()
            }

            val dialog = dialogBuilder.create()
            dialog.show()
        }

        return view
    }
}
