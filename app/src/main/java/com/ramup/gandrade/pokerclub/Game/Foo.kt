package com.ramup.gandrade.pokerclub.Game

import com.ramup.gandrade.pokerclub.Retrofit.RequestType

data class Foo(val to:String,val data:Data) {

}
data class Data(var requestType:RequestType,var extra:String?)