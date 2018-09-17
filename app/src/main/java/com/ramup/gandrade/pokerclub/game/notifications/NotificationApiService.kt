package com.ramup.gandrade.pokerclub.game.notifications

import BASE_URL
import android.support.annotation.Keep
import com.ramup.gandrade.pokerclub.model.Request
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

@Keep
interface NotificationApiService {

    @Headers("Content-Type:application/json", "Authorization:key=AAAAZ813xEc:APA91bHTm8_zfC-N7ywnx4TcT4rW1Uh9jjFJlTgRj2_mBpD-iKxAZw1Di87Tr11xPrTuu-aRFBznjFVW5GQt1FaSRqIxP8SaL0Rnt6wq3YgEGwazI8eVWivtWlm2Ki_jdnE7R3q9mchtwSg_RgsvpItsbHyCu9GZ8g")
    @POST("send")
    fun sendNotification(@Body body: Request): Observable<FCMResponse>

    companion object Factory {
        fun create(): NotificationApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(NotificationApiService::class.java);
        }
    }
}

