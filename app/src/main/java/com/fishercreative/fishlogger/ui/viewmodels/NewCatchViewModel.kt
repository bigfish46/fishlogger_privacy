package com.fishercreative.fishlogger.ui.viewmodels

import android.app.Application
import android.location.Location
import android.location.Geocoder
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
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import com.fishercreative.fishlogger.data.models.RetrievalMethod
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

sealed interface SaveResult {
    object Success : SaveResult
    data class Error(val message: String) : SaveResult
}

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

    fun saveCatch() {
        Log.d(TAG, "saveCatch() called")
        
        if (species.isBlank()) {
            Log.w(TAG, "Cannot save: Species is blank")
            viewModelScope.launch {
                _saveResult.emit(SaveResult.Error("Please select a species"))
            }
            return
        }

        val catchId = UUID.randomUUID().toString()
        Log.d(TAG, "Creating catch object with ID: $catchId, species: $species")
        
        try {
            val fishCatch = Catch(
                id = catchId,
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
            
            Log.d(TAG, "Catch data prepared, saving to local database...")
            
            viewModelScope.launch {
                try {
                    catchDao.insertCatch(fishCatch)
                    Log.d(TAG, "Local save successful")
                    _saveResult.emit(SaveResult.Success)
                } catch (e: Exception) {
                    val errorMsg = "Failed to save catch: ${e.message}"
                    Log.e(TAG, errorMsg, e)
                    _saveResult.emit(SaveResult.Error(errorMsg))
                }
            }
        } catch (e: Exception) {
            val errorMsg = "Error preparing save operation: ${e.message}"
            Log.e(TAG, errorMsg, e)
            viewModelScope.launch {
                _saveResult.emit(SaveResult.Error(errorMsg))
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
        // Update nearest city using Geocoder
        updateNearestCityFromLocation(newLocation)
    }

    fun updateNearestCity(city: String) {
        nearestCity = city
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

    fun updateRetrievalMethod(method: RetrievalMethod) {
        retrievalMethod = method
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
                        nearestCity = address.locality ?: address.subAdminArea ?: ""
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    nearestCity = address.locality ?: address.subAdminArea ?: ""
                }
            }
        } catch (e: Exception) {
            Log.e("NewCatchViewModel", "Error getting location address", e)
        }
    }
} 