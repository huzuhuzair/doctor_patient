package com.aamir.icarepro.data

import com.aamir.icarepro.data.dataStore.DataStoreHelper

interface DataManager : DataStoreHelper {

    suspend fun setUserAsLoggedOut()

    fun updateUserInf(): HashMap<String, String>

    fun updateApiHeader(userId: Long?, accessToken: String)
}
