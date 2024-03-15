package com.example.myapplication.ui.appointments
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.ui.bdd.MyDatabaseHelper


@SuppressLint("Range")
class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {
    private var databaseHelper: MyDatabaseHelper = MyDatabaseHelper.getInstance(application.applicationContext)
    var appointmentsList = mutableListOf<Appointment>()
    val REQUEST_CONTACTS_PERMISSION = 1
    private var NOTIFICATION_ID = 100
    private var REQUEST_CODE = 200
    private val CHANNEL_ID = "channel_01"

    init {

        val appointments = databaseHelper.getAllAppointments()

        if (appointments != null) {
            while (appointments.moveToNext()) {
                val id = appointments.getInt(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_ID))
                val name = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_NAME))
                val date = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_DATE))
                val time = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_TIME))
                val address = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS))
                val contactName = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_CONTACT_NAME))
                val contactNumber = appointments.getString(appointments.getColumnIndex(MyDatabaseHelper.COLUMN_CONTACT_NUMBER))
                appointmentsList.add((Appointment(id, name, date, time, address,contactName,contactNumber)))
            }
        }
    }

    fun addAppointment(appointment: Appointment) {
        databaseHelper.addAppointment(appointment)
        appointmentsList.add(appointment)
        sortAppointmentsByDate()
    }

    private fun removeAppointmentById(id: Int) {
        val iterator = appointmentsList.iterator()
        while (iterator.hasNext()) {
            val appointment = iterator.next()
            if (appointment.vid == id) {
                iterator.remove()
                databaseHelper.removeAppointment(id)
                return
            }
        }
    }

    private fun sortAppointmentsByDate() {
        appointmentsList.sortWith(compareBy({ it.vdate }, { it.vtime }, {it.vid}))
    }

    fun indexAppointments() : Int {
        val sortedList = appointmentsList.sortedBy { it.vid }
        var nextId = 0
        for (appointment in sortedList) {
            if (appointment.vid != nextId) {
                return nextId
            }
            nextId++
        }
        return nextId
    }

    fun editAppointmentById(i : Int, n : String?, d : String, t : String?, a : String?, cna : String?, cnu : String?){
        val index = appointmentsList.indexOfFirst { it.vid == i }
        appointmentsList[index].setName(n)
        appointmentsList[index].setDate(d)
        appointmentsList[index].setTime(t)
        appointmentsList[index].setAddress(a)
        appointmentsList[index].setContactName(cna)
        appointmentsList[index].setContactNumber(cnu)
        databaseHelper.editAppointment(i,n,d,t,a,cna,cnu)
        sortAppointmentsByDate()
    }

    fun delete(i : Int) {
        databaseHelper.removeAppointment(i)
        removeAppointmentById(i)
        sortAppointmentsByDate()
    }

    fun mapmap() : List<String>{
        return appointmentsList.map {"${it.vname} - ${it.vdate}"}
    }

    companion object {
        private fun niceTime(s : String) : String {
            return if (s.length>1) s else "0$s"
        }

        fun niceTime2(s : String) : String {
            return niceTime(s.split(":")[0]) + ":" + niceTime(s.split(":")[1])
        }

        @Volatile
        private var INSTANCE: AppointmentsViewModel? = null

        fun getInstance(application: Application): AppointmentsViewModel =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppointmentsViewModel(application).also { INSTANCE = it }
            }
    }

    fun openContacts(pickContactLauncher: ActivityResultLauncher<Void?>) {
        pickContactLauncher.launch(null)
    }

    fun shareMethod(context : Context, appointment : Appointment) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Appointment Details")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            getApplication<Application>().getString(R.string.name) + " : ${appointment.vname}\n" +
                    getApplication<Application>().getString(R.string.date) + " : ${appointment.vdate}\n" +
                    getApplication<Application>().getString(R.string.address) + " : ${appointment.vaddress}\n" +
                    getApplication<Application>().getString(R.string.time) + " : " + niceTime2(appointment.vtime)
        )
        context.startActivity(Intent.createChooser(shareIntent, getApplication<Application>().getString(R.string.shareappointment)))
    }

    fun onCallClick(context : Context, phoneNumber : String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(callIntent)
    }

    fun createNotificationChannel(context : Context) {
        val name = "Notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = "Notification"
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context : Context, appointment: Appointment) {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notificationManager = NotificationManagerCompat.from(context)
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.owl)
                .setContentTitle(getApplication<Application>().getString(R.string.name) + " : ${appointment.vname}\n")
                .setContentText(getApplication<Application>().getString(R.string.date) + " : ${appointment.vdate}\n")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }
}