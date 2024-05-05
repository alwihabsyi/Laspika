package com.laspika.laspika.core.notification

import com.laspika.laspika.core.notification.NotificationConstants.CONTENT_TYPE
import com.laspika.laspika.core.notification.NotificationConstants.SERVER_KEY
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers(
        "Authorization: $SERVER_KEY",
        "Content-Type: $CONTENT_TYPE"
    )
    @POST("fcm/send")
    fun sendNotification(
        @Body pushNotification: PushNotification
    ): Call<NotificationResponse>
}