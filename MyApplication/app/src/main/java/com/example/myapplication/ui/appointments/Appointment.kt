package com.example.myapplication.ui.appointments
import java.io.Serializable


class Appointment(id : Int, name : String?, date : String, time : String?, address : String?, contactName : String?, contactNumber : String?) :
    Serializable {
    var vid = id
    var vname = name
    var vdate = date
    var vtime = time ?: "00:00"
    var vaddress = address ?: ""
    var vcontactname = contactName ?: ""
    var vcontactnumber = contactNumber ?: ""

    fun setName(n : String?){
        vname = n
    }

    fun setDate(d : String){
        vdate = d
    }

    fun setTime(t : String?){
        vtime = t ?: "00:00"
    }

    fun setAddress(a : String?){
        vaddress = a ?: ""
    }

    fun setContactName(cn : String?){
        vcontactname = cn ?: ""
    }

    fun setContactNumber(cn : String?){
        vcontactnumber = cn ?: ""
    }
}