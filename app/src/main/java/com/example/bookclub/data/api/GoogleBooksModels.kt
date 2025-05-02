package com.example.bookclub.data.api

data class GoogleBooksResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<VolumeInfo>?
)

data class VolumeInfo(
    val id: String,
    val volumeInfo: BookInfo,
    val searchInfo: SearchInfo? = null
)

data class BookInfo(
    val title: String,
    val authors: List<String>? = null,
    val description: String? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val averageRating: Float? = null,
    val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class SearchInfo(
    val textSnippet: String? = null
)