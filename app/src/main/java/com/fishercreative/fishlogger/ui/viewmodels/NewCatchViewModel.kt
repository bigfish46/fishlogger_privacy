package com.fishercreative.fishlogger.ui.viewmodels

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.FishLoggerApp
import com.fishercreative.fishlogger.data.models.Catch
import com.fishercreative.fishlogger.data.models.CloudCover
import com.fishercreative.fishlogger.data.models.RetrievalMethod
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import com.fishercreative.fishlogger.utils.StateUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class NewCatchViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "NewCatchViewModel"
    private val catchDao = FishLoggerApp.database.catchDao()
    
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
    
    var city by mutableStateOf("")
        private set
        
    var state by mutableStateOf("")
        private set
    
    var nearestCity by mutableStateOf("")
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
    
    var retrievalMethod by mutableStateOf(RetrievalMethod.OTHER)
        private set

    fun onScreenShown() {
        resetForm()
    }

    fun updateDate(value: LocalDate) {
        date = value
    }

    fun updateTime(value: LocalTime) {
        time = value
    }

    fun updateSpecies(value: String) {
        species = value
    }

    fun updateLengthInches(value: Int) {
        lengthInches = value
    }

    fun updateWeightPounds(value: Int) {
        weightPounds = value
    }

    fun updateWeightOunces(value: Int) {
        weightOunces = value
    }

    fun updateTemperature(value: Double) {
        temperature = value
    }

    fun updateCloudCover(value: CloudCover) {
        cloudCover = value
    }

    fun updateLocation(value: Location) {
        location = value
        updateNearestCityFromLocation(value)
    }

    fun updateCity(value: String) {
        city = value
        updateNearestCity()
    }
    
    fun updateState(value: String) {
        state = value
        updateNearestCity()
    }
    
    private fun updateNearestCity() {
        nearestCity = StateUtils.formatCityState(city, state)
    }

    fun updateWaterBody(value: String) {
        waterBody = value
    }

    fun updateBaitType(value: String) {
        baitType = value
    }

    fun updateBaitColor(value: String) {
        baitColor = value
    }

    fun updateWaterTurbidity(value: WaterTurbidity) {
        waterTurbidity = value
    }

    fun updateWaterTemperature(value: Double) {
        waterTemperature = value
    }

    fun updateWaterDepth(value: Double) {
        waterDepth = value
    }

    fun updateFishingDepth(value: Double) {
        fishingDepth = value
    }
    
    fun updateRetrievalMethod(value: RetrievalMethod) {
        retrievalMethod = value
    }

    fun saveCatch() {
        viewModelScope.launch {
            try {
                val catch = Catch(
                    id = UUID.randomUUID().toString(),
                    date = date,
                    time = time,
                    species = species,
                    lengthInches = lengthInches,
                    weightPounds = weightPounds,
                    weightOunces = weightOunces,
                    temperature = temperature,
                    cloudCover = cloudCover,
                    latitude = location?.latitude,
                    longitude = location?.longitude,
                    nearestCity = nearestCity,
                    waterBody = waterBody,
                    baitType = baitType,
                    baitColor = baitColor,
                    waterTurbidity = waterTurbidity,
                    waterTemperature = waterTemperature,
                    waterDepth = waterDepth,
                    fishingDepth = fishingDepth,
                    retrievalMethod = retrievalMethod
                )
                
                catchDao.insertCatch(catch)
                _saveResult.emit(SaveResult.Success)
                resetForm()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving catch", e)
                _saveResult.emit(SaveResult.Error("Failed to save catch: ${e.message}"))
            }
        }
    }

    private fun resetForm() {
        date = LocalDate.now()
        time = LocalTime.now()
        species = ""
        lengthInches = 0
        weightPounds = 0
        weightOunces = 0
        temperature = 0.0
        cloudCover = CloudCover.CLEAR
        location = null
        city = ""
        state = ""
        nearestCity = ""
        waterBody = ""
        baitType = ""
        baitColor = ""
        waterTurbidity = WaterTurbidity.CLEAR
        waterTemperature = 0.0
        waterDepth = 0.0
        fishingDepth = 0.0
        retrievalMethod = RetrievalMethod.OTHER
    }

    private fun updateNearestCityFromLocation(location: Location) {
        val geocoder = Geocoder(getApplication())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        city = address.locality ?: address.subAdminArea ?: ""
                        state = address.adminArea ?: ""
                        updateNearestCity()
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    city = address.locality ?: address.subAdminArea ?: ""
                    state = address.adminArea ?: ""
                    updateNearestCity()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location address", e)
        }
    }
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
} 