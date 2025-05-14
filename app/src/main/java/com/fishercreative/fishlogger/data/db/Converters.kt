package com.fishercreative.fishlogger.data.db

import androidx.room.TypeConverter
import com.fishercreative.fishlogger.data.models.CloudCover
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromCloudCover(value: CloudCover): String {
        return value.name
    }

    @TypeConverter
    fun toCloudCover(value: String): CloudCover {
        return CloudCover.valueOf(value)
    }

    @TypeConverter
    fun fromWaterTurbidity(value: WaterTurbidity): String {
        return value.name
    }

    @TypeConverter
    fun toWaterTurbidity(value: String): WaterTurbidity {
        return WaterTurbidity.valueOf(value)
    }
} 