package com.fishercreative.fishlogger.ui.viewmodels

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.data.models.Catch
import com.fishercreative.fishlogger.data.models.CloudCover
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : Error()
}

class NewCatchViewModel : ViewModel() {
    private val db = Firebase.firestore
    
    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult = _saveResult.asSharedFlow()
    
    var date by mutableStateOf(LocalDate.now())
        private set
    
    var time by mutableStateOf(LocalTime.now())
        private set
    
    var species by mutableStateOf("")
        private set
    
    var lengthInches by mutableStateOf(0)
        private set
    
    var weightPounds by mutableStateOf(0)
        private set
    
    var weightOunces by mutableStateOf(0)
        private set
    
    var temperature by mutableStateOf(0.0)
        private set
    
    var cloudCover by mutableStateOf(CloudCover.CLEAR)
        private set
    
    var location by mutableStateOf<Location?>(null)
        private set
    
    var waterBody by mutableStateOf("")
        private set
    
    var baitType by mutableStateOf("")
        private set
    
    var baitColor by mutableStateOf("")
        private set
    
    var waterTurbidity by mutableStateOf(WaterTurbidity.CLEAR)
        private set
    
    var waterTemperature by mutableStateOf(0.0)
        private set
    
    var waterDepth by mutableStateOf(0.0)
        private set
    
    var fishingDepth by mutableStateOf(0.0)
        private set

    fun saveCatch() {
        viewModelScope.launch {
            try {
                val catch = Catch.create(
                    id = UUID.randomUUID().toString(),
                    date = date,
                    time = time,
                    species = species,
                    lengthInches = lengthInches,
                    weightPounds = weightPounds,
                    weightOunces = weightOunces,
                    temperature = temperature,
                    cloudCover = cloudCover,
                    location = location?.let { GeoPoint(it.latitude, it.longitude) },
                    waterBody = waterBody,
                    baitType = baitType,
                    baitColor = baitColor,
                    waterTurbidity = waterTurbidity,
                    waterTemperature = waterTemperature,
                    waterDepth = waterDepth,
                    fishingDepth = fishingDepth
                )
                
                db.collection("catches")
                    .document(catch.id)
                    .set(catch)
                    .await()
                
                resetForm()
                _saveResult.emit(SaveResult.Success)
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Error("Failed to save catch: ${e.message}"))
            }
        }
    }

    // Update functions
    fun updateDate(newDate: LocalDate) {
        date = newDate
    }

    fun updateTime(newTime: LocalTime) {
        time = newTime
    }

    fun updateSpecies(newSpecies: String) {
        species = newSpecies
    }

    fun updateLengthInches(length: Int) {
        lengthInches = length
    }

    fun updateWeightPounds(pounds: Int) {
        weightPounds = pounds
    }

    fun updateWeightOunces(ounces: Int) {
        weightOunces = ounces
    }

    fun updateTemperature(temp: Double) {
        temperature = temp
    }

    fun updateCloudCover(cover: CloudCover) {
        cloudCover = cover
    }

    fun updateLocation(newLocation: Location) {
        location = newLocation
    }

    fun updateWaterBody(body: String) {
        waterBody = body
    }

    fun updateBaitType(type: String) {
        baitType = type
    }

    fun updateBaitColor(color: String) {
        baitColor = color
    }

    fun updateWaterTurbidity(turbidity: WaterTurbidity) {
        waterTurbidity = turbidity
    }

    fun updateWaterTemperature(temp: Double) {
        waterTemperature = temp
    }

    fun updateWaterDepth(depth: Double) {
        waterDepth = depth
    }

    fun updateFishingDepth(depth: Double) {
        fishingDepth = depth
    }

    fun resetForm() {
        date = LocalDate.now()
        time = LocalTime.now()
        species = ""
        lengthInches = 0
        weightPounds = 0
        weightOunces = 0
        temperature = 0.0
        cloudCover = CloudCover.CLEAR
        location = null
        waterBody = ""
        baitType = ""
        baitColor = ""
        waterTurbidity = WaterTurbidity.CLEAR
        waterTemperature = 0.0
        waterDepth = 0.0
        fishingDepth = 0.0
    }
} 