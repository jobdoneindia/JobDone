package com.jobdoneindia.jobdone.activity

class User {
    var uid : String? = null
    var username : String? = null
    var url: String? = null
    var phoneNumber: String? = null
    var Location:ArrayList<Double>? = null

    constructor(
        uid:String?,
        username: String?,
        url: String?,
        phoneNumber: String?,
        Location:ArrayList<Double>?,
    ){
        this.uid = uid
        this.username = username
        this.url = url
        this.phoneNumber = phoneNumber
        this.Location = Location

    }

    constructor(){

    }

}