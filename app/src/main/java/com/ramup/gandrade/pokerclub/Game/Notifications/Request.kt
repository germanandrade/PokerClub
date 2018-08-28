package com.ramup.gandrade.pokerclub.Game.Notifications


data class Request(val to: String, val data: Data) {

}

data class Data(var dbId: String, var token: String, var requestType: RequestType, var extra: Int?)