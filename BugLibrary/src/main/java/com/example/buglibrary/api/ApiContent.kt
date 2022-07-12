package com.example.buglibrary.api

import com.example.buglibrary.data.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


interface ApiContent {
//    @POST("/contentroute/getfaqs")
//    fun fetchFaq(
//        @Body body: Map<String, String>
//    ): Observable<BudDataResponse<Faq>>


    @POST("/contactroute/getcontentmenu")
    fun fetchContact(
        @Body body: Map<String, String>
    ): Response<ResultResponse<ContactMenu>>

    @POST("/contentroute/getaboutusforapp")
    suspend fun fetchAboutUs(
        @Body body: Map<String, String>
    ): Response<ObjResponse<PrivacyTerms>>

    @POST("notificatonroute/bud/getnotification")
    suspend fun fetchNotification(@Body body: Map<String, String>): Response<ResultResponse<NotificationModel>>

    @POST("/notificatonroute/bud/removenotification")
    suspend fun deleteNotification(@Body requestBody: RequestBody): Response<ResultResponse<NotificationModel>>

    /* @POST("/loginroute/uploadimage")
     fun uploadFeedBackImage(
         @Body body: Map<String, String>
     ): Observable<ObjResponse<Any>>

     @POST("/campaignsroute/addreview")
     fun sendFeedback(
         @Body body: RequestBody
     ): Observable<ObjResponse<Any>>
 */
    @POST
    suspend fun fetchFeedback(
        @Url url: String,
        @Body body: RequestBody
    ): Response<ObjResponse<Any>>

    @POST
    suspend fun fetchFeedbackDcaa(
        @Url url: String,
        @Body body: Map<String, String>
    ): Response<List<FeedbackResult>>

    @GET
    suspend fun fetchFeedbackSubject(
        @Url url: String
    ): Response<FeedBackSubjectsResult>

    @POST
    suspend fun termToUse(
        @Url url: String,
        @Body body: Map<String, String>
    ): Response<ObjResponse<PrivacyTerms>>


    @POST("/favouriteroute/bud/getfavourite")
    fun fetchFav(
        @Body body: Map<String, String>
    ): Response<ResultResponse<Poi>>

    @POST("userroute/findupdateuser")
    suspend fun userDetail(@Body body: Map<String, String>): Response<Any>

    @GET
    suspend fun fetchIpDetail(@Url url: String): Response<IpDetail>
}



