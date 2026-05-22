package com.example.weatherapp20231141

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeatherData(
    val name: String,
    val temp: Double,
    val humidity: Int,
    val description: String
)

class LocationViewModel : ViewModel() {
    private val _allWeatherData = MutableStateFlow<List<WeatherData>>(emptyList())
    val allWeatherData: StateFlow<List<WeatherData>> = _allWeatherData

    private val _savedLocations = MutableStateFlow<List<WeatherData>>(emptyList())
    val savedLocations: StateFlow<List<WeatherData>> = _savedLocations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiKey = "4ab38d73be70ea079580dc16b1e54ba2"

    fun searchWeather(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.apiService.getWeather(
                    cityName = cityName,
                    apiKey = apiKey,
                    units = "metric"
                )

                val weatherData = WeatherData(
                    name = response.name,
                    temp = response.main.temp,
                    humidity = response.main.humidity,
                    description = response.weather[0].description
                )

                // Add to list (avoid duplicates)
                val currentList = _allWeatherData.value.toMutableList()
                currentList.removeIf { it.name == weatherData.name }
                currentList.add(0, weatherData)
                _allWeatherData.value = currentList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveLocation(weatherData: WeatherData) {
        val currentList = _savedLocations.value.toMutableList()
        if (!currentList.any { it.name == weatherData.name }) {
            currentList.add(weatherData)
            _savedLocations.value = currentList
        }
    }

    fun removeLocation(cityName: String) {
        _savedLocations.value = _savedLocations.value.filter { it.name != cityName }
    }

    fun refreshSavedLocations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedLocations = _savedLocations.value.map { location ->
                    val response = RetrofitInstance.apiService.getWeather(
                        cityName = location.name,
                        apiKey = apiKey,
                        units = "metric"
                    )
                    WeatherData(
                        name = response.name,
                        temp = response.main.temp,
                        humidity = response.main.humidity,
                        description = response.weather[0].description
                    )
                }
                _savedLocations.value = updatedLocations
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
