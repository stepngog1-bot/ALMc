package ir.almasglass.sales.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore by preferencesDataStore(name = "almas_session")

class SessionManager(private val context: Context) {
    private val KEY_CUSTOMER_ID = longPreferencesKey("logged_in_customer_id")

    val loggedInCustomerId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOMER_ID]?.takeIf { it > 0 }
    }

    suspend fun setLoggedIn(customerId: Long) {
        context.dataStore.edit { it[KEY_CUSTOMER_ID] = customerId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_CUSTOMER_ID) }
    }
}

fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
