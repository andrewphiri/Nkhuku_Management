package and.drew.nkhukumanagement.prefs

import and.drew.nkhukumanagement.UserPreferences
import android.annotation.SuppressLint
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import java.util.Currency
import java.util.Locale

object UserPreferencesSerializer : Serializer<UserPreferences> {
    @SuppressLint("ConstantLocale")
    override val defaultValue: UserPreferences = UserPreferences
        .getDefaultInstance()
        .toBuilder()
        .setSymbol(Currency.getInstance(Locale("en", "ZM")).symbol)
        .setDisplayName(Currency.getInstance(Locale("en", "ZM")).displayName)
        .setNumericCode(967)
        .setCurrencyCode(Currency.getInstance(Locale("en", "ZM")).currencyCode)
        .setReceiveNotifications(true)
        .setCurrencyLocale(Locale("en", "ZM").toLanguageTag())
        .setSkipAccountSetup(false)
        .setLanguageLocale(Locale("en", "ZM").language)
        .setTraySize("30")
        .build()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        try {
            return UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        if (t == defaultValue) {
            // if t is the default value, do not write anything
            return
        }
        t.writeTo(output)
    }

}