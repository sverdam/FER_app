package com.sofiaverdugo.fer_app.network

import retrofit2.http.GET

interface PostsAPI {
    @GET("posts?populate[thumbnail]=true&populate[attachments][populate]=*&populate[audiences]=true&populate[levels]=true&sort=publishedAt:desc&pagination[pageSize]=100")
    suspend fun getPosts(): StrapiListResponse<PostDto>
}