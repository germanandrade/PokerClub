package com.ramup.gandrade.pokerclub.Retrofit

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi
{
    @Headers("Content-Type:application/json","Authorization:key=AAAAZ813xEc:APA91bHTm8_zfC-N7ywnx4TcT4rW1Uh9jjFJlTgRj2_mBpD-iKxAZw1Di87Tr11xPrTuu-aRFBznjFVW5GQt1FaSRqIxP8SaL0Rnt6wq3YgEGwazI8eVWivtWlm2Ki_jdnE7R3q9mchtwSg_RgsvpItsbHyCu9GZ8g")
    @FormUrlEncoded
    @POST("/fcm/send")
    fun sendNotification(@Field("to")adminToken:String,type:RequestType,extra:Int?){
    }
}

enum class RequestType {
    BUY, DEPOSIT, WITHDRAW,PAY
}
