package dev.notyouraverage.otpcourier.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.notyouraverage.otpcourier.managers.DataStoreManager
import dev.notyouraverage.otpcourier.models.SmsCourierPreference
import kotlinx.coroutines.flow.first

suspend fun loadSavedPreferences(
    preferencesDataStore: DataStore<Preferences>,
    onResult: (SmsCourierPreference) -> Unit
) {
    val preferences = preferencesDataStore.data.first()
    val secretPassword = preferences[DataStoreManager.SECRET_PASSWORD]
    val whiteListedContactNumber = preferences[DataStoreManager.WHITELISTED_NUMBER]
    onResult(
        SmsCourierPreference(
            secretPassword = secretPassword ?: "",
            whiteListedContactNumber = whiteListedContactNumber ?: ""
        )
    )
}

suspend fun savePreferences(
    secretPassword: String,
    whiteListedContactNumber: String,
    datastoreManager: DataStoreManager
) {
    datastoreManager.saveToDatastore(
        SmsCourierPreference(
            secretPassword = secretPassword,
            whiteListedContactNumber = whiteListedContactNumber
        )
    )
}

