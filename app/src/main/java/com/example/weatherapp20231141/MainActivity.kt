package com.example.weatherapp20231141

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp20231141.ui.theme.Weatherapp20231141Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Weatherapp20231141Theme {
                Weatherapp20231141App()
            }
        }
    }
}

@Composable
fun Weatherapp20231141App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val locationViewModel: LocationViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach {
                    NavigationBarItem(
                        icon = { Icon(painterResource(it.icon), contentDescription = it.label) },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentDestination) {
                AppDestinations.HOME -> HomeScreen(locationViewModel)
                AppDestinations.LOCATIONS -> SavedLocationsScreen(locationViewModel)
                AppDestinations.PROFILE -> ProfileScreen()
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("首页", R.drawable.ic_home),
    LOCATIONS("我的地址", R.drawable.ic_location_pin),
    PROFILE("我的账号", R.drawable.ic_account_box),
}

@Composable
fun HomeScreen(viewModel: LocationViewModel) {
    var cityInput by rememberSaveable { mutableStateOf("") }
    val allWeatherData by viewModel.allWeatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val savedLocations by viewModel.savedLocations.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🌤️ WEATHER APP", style = MaterialTheme.typography.headlineLarge)

        // Search input
        TextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            label = { Text("Search city...") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                if (cityInput.isNotEmpty()) {
                    viewModel.searchWeather(cityInput)
                    cityInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("🔍 搜索")
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator()
        }

        // List of all weather data with Save button
        LazyColumn {
            items(allWeatherData) { weather ->
                val isSaved = savedLocations.any { it.name == weather.name }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(weather.name, style = MaterialTheme.typography.headlineSmall)
                        Text("🌡️ ${weather.temp}°C")
                        Text("💧 Humidity: ${weather.humidity}%")
                        Text("☁️ ${weather.description}")

                        Button(
                            onClick = {
                                if (isSaved) {
                                    viewModel.removeLocation(weather.name)
                                } else {
                                    viewModel.saveLocation(weather)
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(if (isSaved) "❌ 不保护" else "💾 保护")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedLocationsScreen(viewModel: LocationViewModel) {
    val savedLocations by viewModel.savedLocations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📍 Saved Locations", style = MaterialTheme.typography.headlineLarge)

        Button(
            onClick = { viewModel.refreshSavedLocations() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("🔄 更新")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (savedLocations.isEmpty()) {
            Text("No saved locations yet. Go to Home and save a city!")
        } else {
            LazyColumn {
                items(savedLocations) { location ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(location.name, style = MaterialTheme.typography.headlineSmall)
                            Text("🌡️ ${location.temp}°C")
                            Text("💧 Humidity: ${location.humidity}%")
                            Text("☁️ ${location.description}")

                            Button(
                                onClick = { viewModel.removeLocation(location.name) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("🗑️ Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("👤 Profile Screen", style = MaterialTheme.typography.headlineLarge)
        Text("User Settings")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Weatherapp20231141Theme {
        Weatherapp20231141App()
    }
}