package com.aamir.icarepro.data.dataStore

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*


import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AppDataStoreImpl
@Inject
constructor(
    context: Context,
    dataStoreName: String,
    private val gson: Gson
) : DataStoreHelper {


    private val dataStore: DataStore<Preferences> =
        context.createDataStore(
            name = dataStoreName
        )

    override suspend fun <T> setKeyValue(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }


    override fun <T> getKeyValue(key: Preferences.Key<T>): Flow<Any?> {

        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                preference[key]
            }
    }


    override suspend fun <T> getGsonValue(key: Preferences.Key<String>, type: Class<T>): Flow<T?> {

        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                fetchGson(gson = preference[key] ?: "", type = type, key = key.name)
            }

    }

    override suspend fun addGsonValue(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun logOut() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun getCurrentUserLoggedIn(): Flow<Boolean> {

        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                preference[DataStoreConstants.USER_LOGGED_IN] ?: false
            }

    }

    override suspend fun isDocumentUploaded(): Flow<Boolean> {

        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                preference[DataStoreConstants.USER_DOC_UPLOADED] ?: false
            }

    }



    private fun <T> fetchGson(gson: String, type: Class<T>, key: String): T? {
        return if (gson.isEmpty()) {
            null
        } else {
            try {
                this.gson.fromJson(gson, type)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Object storaged with key "
                            + key + " is instanceof other class"
                )
            }

        }
    }
}