package com.jobdoneindia.jobdone.activity

class User {
    var Username : String? = null
    var uid : String? = null
    var phoneNumber: String? = null
    var location:ArrayList<String>? = null

    constructor(
        uid:String?,
        name: String?,
        phoneNumber: String?,
        location:ArrayList<String>?
    ){
        this.uid = uid
        this.Username = name
        this.phoneNumber = phoneNumber
        this.location = location

    }



}