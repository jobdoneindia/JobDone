package com.jobdoneindia.jobdone.activity

class User {
    var Username : String? = null
    var uid : String? = null
    var phoneNumber: String? = null

    constructor(){}

    constructor(
        uid:String?,
        name: String?,
        phoneNumber: String?
    ){
        this.uid = uid
        this.Username = name
        this.phoneNumber = phoneNumber

    }



}