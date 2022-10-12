package com.jobdoneindia.jobdone.activity

import java.sql.Timestamp

class Message {
        var message: String? = null
        var senderId: String? = null
        var timestamp: Long? = null
        var message_type: String? = null

        constructor(){}

        constructor(message: String?, senderId: String?, timestamp: Long, message_type: String){
            this.message = message
            this.senderId = senderId
            this.timestamp = timestamp
            this.message_type = message_type
        }
    }
