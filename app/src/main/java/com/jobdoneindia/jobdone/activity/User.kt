package com.jobdoneindia.jobdone.activity

class User {
    var uid : String? = null
    var username : String? = null
    var url: String? = null
    var phoneNumber: String? = null
    var Location:ArrayList<Double>? = null
    var status : String? = null
    var Profession: String? = null
    var chatList: String? = null
    var tag1: String? = null
    var tag2: String? = null
    var tag3: String? = null

    constructor(
        uid:String?,
        username: String?,
        url: String?,
        phoneNumber: String?,
        Location:ArrayList<Double>?,
        status: String?,
        Profession: String?,
        chatList: String?,
        tag1: String?,
        tag2: String?,
        tag3: String?,
    ){
        this.uid = uid
        this.username = username
        this.url = url
        this.phoneNumber = phoneNumber
        this.Location = Location
        this.status = status
        this.Profession = Profession
        this.chatList = chatList
        this.tag1 = tag1
        this.tag2 = tag2
        this.tag3 = tag3
    }

    constructor(){

    }

}