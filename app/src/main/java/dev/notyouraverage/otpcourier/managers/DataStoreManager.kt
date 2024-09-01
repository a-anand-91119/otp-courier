package dev.notyouraverage.otpcourier.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.notyouraverage.otpcourier.models.SmsCourierPreference
import kotlinx.coroutines.flow.map

const val SMS_COURIER_DATASTORE = "SMS_COURIER_DATASTORE"

val Context.smsCourierPreferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = SMS_COURIER_DATASTORE)

class DataStoreManager(private val context: Context) {

    companion object {
        val SECRET_PASSWORD = stringPreferencesKey("SECRET")
        val WHITELISTED_NUMBER = stringPreferencesKey("WHITELISTED_NUMBER")
    }

    suspend fun saveToDatastore(smsCourierPreference: SmsCourierPreference) {
        context.smsCourierPreferenceDataStore.edit {
            it[SECRET_PASSWORD] = smsCourierPreference.secretPassword
            it[WHITELISTED_NUMBER] = smsCourierPreference.whiteListedContactNumber
        }
    }

    fun getFromDatastore() = context.smsCourierPreferenceDataStore.data.map {
        SmsCourierPreference(
            secretPassword = it[SECRET_PASSWORD] ?: "",
            whiteListedContactNumber = it[WHITELISTED_NUMBER] ?: ""
        )
    }

    suspend fun clearDataStore() = context.smsCourierPreferenceDataStore.edit {
        it.clear()
    }

}