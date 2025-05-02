package com.example.bookclub.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 40,
        @Query("startIndex") startIndex: Int = 0
    ): Response<GoogleBooksResponse>
    
    @GET("volumes")
    suspend fun getBooksByCategory(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): Response<GoogleBooksResponse>
}