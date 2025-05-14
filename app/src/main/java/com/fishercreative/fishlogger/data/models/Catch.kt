package com.fishercreative.fishlogger.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "catches")
data class Catch(
    @PrimaryKey
    var id: String = "",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now(),
    var species: String = "",
    var lengthInches: Int = 0,
    var weightPounds: Int = 0,
    var weightOunces: Int = 0,
    var temperature: Double = 0.0,
    var cloudCover: CloudCover = CloudCover.CLEAR,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var waterBody: String = "",
    var baitType: String = "",
    var baitColor: String = "",
    var waterTurbidity: WaterTurbidity = WaterTurbidity.CLEAR,
    var waterTemperature: Double = 0.0,
    var waterDepth: Double = 0.0,
    var fishingDepth: Double = 0.0,
    var createdAt: Long = System.currentTimeMillis(),
    var needsSync: Boolean = true
)

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