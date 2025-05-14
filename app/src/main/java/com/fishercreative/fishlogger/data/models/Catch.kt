package com.fishercreative.fishlogger.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

data class Catch(
    @DocumentId
    val id: String = "",
    val timestamp: Timestamp = Timestamp(Date()),
    val species: String = "",
    val lengthInches: Int = 0,
    val weightPounds: Int = 0,
    val weightOunces: Int = 0,
    val temperature: Double = 0.0,
    val cloudCover: CloudCover = CloudCover.CLEAR,
    val location: GeoPoint? = null,
    val waterBody: String = "",
    val baitType: String = "",
    val baitColor: String = "",
    val waterTurbidity: WaterTurbidity = WaterTurbidity.CLEAR,
    val waterTemperature: Double = 0.0,
    val waterDepth: Double = 0.0,
    val fishingDepth: Double = 0.0
) {
    val date: LocalDate
        get() = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    
    val time: LocalTime
        get() = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime()

    companion object {
        fun create(
            id: String = "",
            date: LocalDate,
            time: LocalTime,
            species: String = "",
            lengthInches: Int = 0,
            weightPounds: Int = 0,
            weightOunces: Int = 0,
            temperature: Double = 0.0,
            cloudCover: CloudCover = CloudCover.CLEAR,
            location: GeoPoint? = null,
            waterBody: String = "",
            baitType: String = "",
            baitColor: String = "",
            waterTurbidity: WaterTurbidity = WaterTurbidity.CLEAR,
            waterTemperature: Double = 0.0,
            waterDepth: Double = 0.0,
            fishingDepth: Double = 0.0
        ): Catch {
            val timestamp = date.atTime(time)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .let { Timestamp(Date.from(it)) }
            
            return Catch(
                id = id,
                timestamp = timestamp,
                species = species,
                lengthInches = lengthInches,
                weightPounds = weightPounds,
                weightOunces = weightOunces,
                temperature = temperature,
                cloudCover = cloudCover,
                location = location,
                waterBody = waterBody,
                baitType = baitType,
                baitColor = baitColor,
                waterTurbidity = waterTurbidity,
                waterTemperature = waterTemperature,
                waterDepth = waterDepth,
                fishingDepth = fishingDepth
            )
        }
    }
}

enum class CloudCover {
    CLEAR,
    PARTLY_CLOUDY,
    MOSTLY_CLOUDY,
    OVERCAST,
    RAIN,
    STORM
}

enum class WaterTurbidity {
    CLEAR,
    SLIGHTLY_STAINED,
    STAINED,
    MUDDY
} 