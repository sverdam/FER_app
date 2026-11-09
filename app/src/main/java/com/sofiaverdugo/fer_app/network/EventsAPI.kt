package com.sofiaverdugo.fer_app.network

import retrofit2.http.GET

interface EventsAPI {
    @GET("events?populate[photo]=true&populate[audiences]=true&populate[levels]=true&sort=publishedAt:desc&pagination[pageSize]=100")
    suspend fun getEvents(): StrapiListResponse<EventDto>
}