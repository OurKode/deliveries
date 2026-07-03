// SPDX-License-Identifier: GPL-3.0-or-later
package dev.itsvic.parceltracker.api

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import com.squareup.moshi.JsonClass
import dev.itsvic.parceltracker.BINDERBYTE_API_KEY
import dev.itsvic.parceltracker.R
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
    override val acceptsPostCode: Boolean = courierCode == "jne"
    override val requiresPostCode: Boolean = courierCode == "jne"
    override val requiresApiKey: Boolean = true
    override val apiKeyPreference: Preferences.Key<String> = BINDERBYTE_API_KEY

    override val postalCodeLabel: Int
        get() = if (courierCode == "jne") R.string.jne_number else super.postalCodeLabel

    override val postalCodeLabelFlavor: Int
        get() = if (courierCode == "jne") R.string.specify_jne_number_flavor_text else super.postalCodeLabelFlavor

    override val specifyPostalCodeLabel: Int
        get() = if (courierCode == "jne") R.string.specify_jne_number else super.specifyPostalCodeLabel

    override val postalCodeErrorLabel: Int
        get() = if (courierCode == "jne") R.string.jne_number_error_text else super.postalCodeErrorLabel

    override val postalCodeIcon: Int
        get() = if (courierCode == "jne") R.drawable.outline_deployed_code_account_24 else super.postalCodeIcon
    
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
            service.trackParcel(key, courierCode, trackingId, if (courierCode == "jne") postalCode else null)
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

        val properties = buildMap {
            put(R.string.property_last_update, summary.date)
            val detail = data.detail
            if (detail != null) {
                detail.origin?.takeIf { it.isNotBlank() }?.let { put(R.string.property_origin, it) }
                detail.destination?.takeIf { it.isNotBlank() }?.let { put(R.string.property_destination, it) }
                detail.shipper?.takeIf { it.isNotBlank() }?.let { put(R.string.property_sender, it) }
                detail.receiver?.takeIf { it.isNotBlank() }?.let { put(R.string.property_receiver, it) }
            }
        }

        return Parcel(
            id = trackingId,
            history = history,
            currentStatus = status,
            properties = properties,
            description = summary.desc
        )
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
            @Query("awb") awb: String,
            @Query("number") number: String?
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
        val detail: BinderbyteDetail?,
        val history: List<BinderbyteHistory>
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteSummary(
        val awb: String,
        val courier: String,
        val service: String,
        val status: String,
        val date: String,
        val desc: String?
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteDetail(
        val origin: String?,
        val destination: String?,
        val shipper: String?,
        val receiver: String?
    )

    @JsonClass(generateAdapter = true)
    internal data class BinderbyteHistory(
        val date: String,
        val desc: String,
        val location: String
    )
}
