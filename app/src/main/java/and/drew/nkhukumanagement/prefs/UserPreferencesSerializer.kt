package and.drew.nkhukumanagement.prefs

import and.drew.nkhukumanagement.UserPreferences
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences
        .getDefaultInstance()
        .toBuilder()
        .setSymbol("ZK")
        .setDisplayName("Zambian Kwacha")
        .setNumericCode(967)
        .setCurrencyCode("ZMW")
        .setReceiveNotifications(true)
        .setCurrencyLocale("en_ZM")
        .setSkipAccountSetup(false)
        .setLanguageLocale("en_US")
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