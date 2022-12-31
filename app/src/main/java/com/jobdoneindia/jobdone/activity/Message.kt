package com.jobdoneindia.jobdone.activity

import java.sql.Timestamp

class Message {
        var message: String? = null
        var senderId: String? = null
        var receiverId: String? = null
        var timestamp: Long? = null
        var message_type: String? = null
        var status: String? = null
        var ukey: String? = null
          var isseen : Boolean? = null

        constructor(){}

        constructor(message: String?, senderId: String?, receiverId:String?, timestamp: Long, message_type: String, status: String, ukey: String  , isseen: Boolean?){
            this.message = message
            this.senderId = senderId
            this.receiverId = receiverId
            this.timestamp = timestamp
            this.message_type = message_type
            this.status = status
            this.ukey = ukey
            this.isseen = isseen
        }
    }
