package com.ramup.gandrade.pokerclub.game.notifications

import java.io.Serializable

data class Request(val to: String, val data: Data)

data class Data(var name: String, var dbId: String, var token: String, var requestType: String, var extra: Int?, var success: Boolean? = null) : Serializable {
    constructor(map: Map<String, String>?) :
            this(
                    map!!["name"] as String,
                    map["dbId"] as String,
                    map["token"] as String,
                    map["requestType"] as String,
                    when {
                        map["extra"] == null -> null
                        else -> (map["extra"] as String).toInt()
                    },
                    when {
                        map["success"] == null -> null
                        ((map["success"] as String).equals("true")) -> true
                        ((map["success"] as String).equals("false")) -> false
                        else -> null
                    }
            )


    override fun toString(): String {
        return "$name wishes $requestType ${extra ?: ""}"
    }
}