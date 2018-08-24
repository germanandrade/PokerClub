package com.ramup.gandrade.pokerclub.UserProfile

data class User(var name:String,var endavans:Int, var debt:Int,var active:Boolean=true, var admin:Boolean=false) {
    fun toMap(): Map<String, Any> {
        return mapOf("Name" to name ,"Endavans" to endavans,"Debt" to debt, "Active" to active, "Admin" to admin)
    }

    constructor(map: Map<String, Any>?) : this(map!!["Name"] as String, (map["Endavans"] as Long).toInt(), (map["Debt"] as Long).toInt(),active=map["Active"]as Boolean,admin=map["Admin"] as Boolean)
}