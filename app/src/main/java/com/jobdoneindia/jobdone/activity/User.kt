package com.jobdoneindia.jobdone.activity

import android.media.Image

class User {
    var Username : String? = null
    var uid : String? = null

    constructor(){}

    constructor(
        uid:String?,
        name: String?,
    ){
        this.uid = uid
        this.Username = name

    }



}