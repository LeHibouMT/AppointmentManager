package com.example.myapplication.ui.bdd
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.ui.appointments.Appointment

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 5
        const val DATABASE_NAME = "MyDatabase.db"
        const val TABLE_NAME = "MyTable"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_CONTACT_NAME = "contactName"
        const val COLUMN_CONTACT_NUMBER = "contactNumber"

        private var instance: MyDatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): MyDatabaseHelper {
            if (instance == null) {
                instance = MyDatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_NAME TEXT, $COLUMN_DATE TEXT, $COLUMN_TIME TEXT, $COLUMN_ADDRESS TEXT, $COLUMN_CONTACT_NAME TEXT, $COLUMN_CONTACT_NUMBER TEXT);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllAppointments(): Cursor? {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_DATE ASC, $COLUMN_TIME ASC, $COLUMN_ID ASC, $COLUMN_NAME ASC, $COLUMN_ADDRESS ASC, $COLUMN_CONTACT_NAME ASC, $COLUMN_CONTACT_NUMBER ASC")
    }

    fun addAppointment(appointment: Appointment) {
        val values = ContentValues().apply {
            put(COLUMN_ID, appointment.vid)
            put(COLUMN_NAME, appointment.vname)
            put(COLUMN_DATE, appointment.vdate)
            put(COLUMN_TIME, appointment.vtime)
            put(COLUMN_ADDRESS, appointment.vaddress)
            put(COLUMN_CONTACT_NAME, appointment.vcontactname)
            put(COLUMN_CONTACT_NUMBER, appointment.vcontactnumber)
        }
        writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun removeAppointment(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun editAppointment(id: Int, name : String?, date : String, time : String?, address : String?, contactName: String?, contactNumber : String ?) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DATE, date)
            put(COLUMN_TIME, time ?: "00:00")
            put(COLUMN_ADDRESS, address)
            put(COLUMN_CONTACT_NAME, contactName)
            put(COLUMN_CONTACT_NUMBER, contactNumber)
        }

        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }


}