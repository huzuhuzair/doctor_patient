package com.aamir.icarepro.data.models.service

import com.aamir.icarepro.data.models.login.UserProfile

data class HomeResponse(
    val categories: List<Category>,
    val banners: List<Category>,
    val popular_docs: List<UserProfile>
)