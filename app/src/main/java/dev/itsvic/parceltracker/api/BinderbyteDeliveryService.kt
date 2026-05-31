// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.api

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.squareup.moshi.JsonClass
import dev.itsvic.parceltracker.BINDERBYTE_API_KEY
import dev.itsvic.parceltracker.dataStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

open class BinderbyteDeliveryService(
    val courierCode: String,
    override val nameResource: Int
) : DeliveryService {
    override val acceptsPostCode: Boolean = false
    override val requiresPostCode: Boolean = false
    override val requiresApiKey: Boolean = true
    override val apiKeyPreference: Preferences.Key<String> = BINDERBYTE_API_KEY
    
    override fun acceptsFormat(trackingId: String): Boolean {
        return trackingId.isNotBlank()
    }

    override suspend fun getParcel(
        context: Context,
        trackingId: String,
        postalCode: String?
    ): Parcel {
        val key = context.dataStore.data.first()[apiKeyPreference]
        if (key.isNullOrEmpty()) {
            throw APIKeyMissingException()
        }

        val resp = try {
            service.trackParcel(key, courierCode, trackingId)
        } catch (_: HttpException) {
            throw ParcelNonExistentException()
        }

        if (resp.status != 200 || resp.data == null) {
            throw ParcelNonExistentException()
        }

        val data = resp.data
        val summary = data.summary
        
        val status = when (summary.status.uppercase()) {
            "DELIVERED" -> Status.Delivered
            "ON PROCESS" -> Status.InTransit
            "RETURNED" -> Status.ReturnedToSender
            "PENDING" -> Status.Preadvice
            "FAILED" -> Status.DeliveryFailure
            else -> logUnknownStatus("Binderbyte ($courierCode)", summary.status)
        }

        val history = data.history.map {
            ParcelHistoryItem(
                description = it.desc,
                time = parseBinderbyteDate(it.date),
                location = it.location.ifBlank { "Unknown location" }
            )
        }

        return Parcel(trackingId, history, status)
    }

    private fun parseBinderbyteDate(dateStr: String): LocalDateTime {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(dateStr, formatter)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.binderbyte.com/v1/")
        .client(api_client)
        .addConverterFactory(api_factory)
        .build()

    private val service = retrofit.create(API::class.java)

    private interface API {
        @GET("track")
        suspend fun trackParcel(
            @Query("api_key") apiKey: String,
            @Query("courier") courier: String,
            @Query("awb") awb: String
        ): BinderbyteResponse
    }

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteResponse(
        val status: Int,
        val message: String,
        val data: BinderbyteData?
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteData(
        val summary: BinderbyteSummary,
        val history: List<BinderbyteHistory>
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteSummary(
        val awb: String,
        val courier: String,
        val service: String,
        val status: String,
        val date: String
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteHistory(
        val date: String,
        val desc: String,
        val location: String
    )
}
