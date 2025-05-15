package com.fishercreative.fishlogger.ui.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.fishercreative.fishlogger.data.models.Catch
import com.fishercreative.fishlogger.data.models.CloudCover
import com.fishercreative.fishlogger.data.models.RetrievalMethod
import com.fishercreative.fishlogger.data.models.WaterTurbidity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class EditCatchViewModel(application: Application) : NewCatchViewModel(application) {
    private val _loadResult = MutableSharedFlow<LoadResult>()
    val loadResult = _loadResult.asSharedFlow()

    override fun onScreenShown() {
        // Don't reset form on screen shown for edit mode
    }

    fun loadCatch(catchId: String) {
        viewModelScope.launch {
            try {
                val catch = catchDao.getCatchById(catchId)
                if (catch != null) {
                    updateFromCatch(catch)
                    _loadResult.emit(LoadResult.Success)
                } else {
                    _loadResult.emit(LoadResult.Error("Catch not found"))
                }
            } catch (e: Exception) {
                _loadResult.emit(LoadResult.Error("Failed to load catch: ${e.message}"))
            }
        }
    }

    private fun updateFromCatch(catch: Catch) {
        updateDate(catch.date)
        updateTime(catch.time)
        updateSpecies(catch.species)
        updateLengthInches(catch.lengthInches)
        updateWeightPounds(catch.weightPounds)
        updateWeightOunces(catch.weightOunces)
        updateTemperature(catch.temperature)
        updateCloudCover(catch.cloudCover)
        catch.latitude?.let { lat ->
            catch.longitude?.let { lon ->
                val location = Location("").apply {
                    latitude = lat
                    longitude = lon
                }
                updateLocation(location)
            }
        }
        updateWaterBody(catch.waterBody)
        updateBaitType(catch.baitType)
        updateBaitColor(catch.baitColor)
        updateWaterTurbidity(catch.waterTurbidity)
        updateWaterTemperature(catch.waterTemperature)
        updateWaterDepth(catch.waterDepth)
        updateFishingDepth(catch.fishingDepth)
        updateRetrievalMethod(catch.retrievalMethod)
        updatePhotoUri(catch.photoUri)
    }

    fun updateCatch(catchId: String) {
        viewModelScope.launch {
            try {
                val catch = Catch(
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
                    retrievalMethod = retrievalMethod,
                    photoUri = photoUri
                )
                
                catchDao.updateCatch(catch)
                _saveResult.emit(SaveResult.Success)
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Error("Failed to update catch: ${e.message}"))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                EditCatchViewModel(application)
            }
        }
    }
}

sealed class LoadResult {
    object Success : LoadResult()
    data class Error(val message: String) : LoadResult()
} 