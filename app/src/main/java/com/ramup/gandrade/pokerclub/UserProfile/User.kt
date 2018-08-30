package com.ramup.gandrade.pokerclub.UserProfile

data class User(var name: String, var endavans: Int, var debt: Int, var id: String, var imageUrl: String? = null, var active: Boolean = true, var admin: Boolean = false, var lifeSavers: Int = 0) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf("Name" to name, "Endavans" to endavans, "Debt" to debt, "LifeSavers" to lifeSavers, "Active" to active, "Admin" to admin, "Id" to id)
    }

    constructor(map: Map<String, Any>?) : this(
            map!!["Name"] as String,
            (map["Endavans"] as Long).toInt(),
            (map["Debt"] as Long).toInt(),
            map["Id"] as String,
            map["ImgUrl"] as String?,
            map["Active"] as Boolean,
            map["Admin"] as Boolean,
            (map["LifeSavers"] as Long).toInt()
    )
}