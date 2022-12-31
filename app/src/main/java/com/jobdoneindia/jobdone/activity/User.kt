package com.jobdoneindia.jobdone.activity

class User {
    var uid : String? = null
    var username : String? = null
    var url: String? = null
    var phoneNumber: String? = null
    var Location:ArrayList<Double>? = null
    var status : String? = null
    var Profession: String? = null

    constructor(
        uid:String?,
        username: String?,
        url: String?,
        phoneNumber: String?,
        Location:ArrayList<Double>?,
        status: String?,
        Profession: String?,
    ){
        this.uid = uid
        this.username = username
        this.url = url
        this.phoneNumber = phoneNumber
        this.Location = Location
        this.status = status
        this.Profession = Profession

    }

    constructor(){

    }

}