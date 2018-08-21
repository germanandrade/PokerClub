package com.ramup.gandrade.pokerclub

data class User(var name:String,var endavans:Int, var debt:Int) {
    fun toMap(): Map<String, Any> {
        return mapOf("Name" to name ,"Endavans" to endavans,"Debt" to debt)
    }

    constructor(map: Map<String, Any>?) : this(map!!["Name"] as String, (map["Endavans"] as Long).toInt(), (map["Debt"] as Long).toInt())
}