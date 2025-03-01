package com.example.hackgteeny.data

data class WikiArticle(
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val category: String,
    val fullArticleUrl: String,
    val isBookmarked: Boolean = false,
    val isLiked: Boolean = false
) 