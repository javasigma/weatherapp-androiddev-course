package com.example.weatherapp20231141

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp20231141.ui.theme.Weatherapp20231141Theme

// Modern Colors (MSN Weather inspired)
val MsnBlue = Color(0xFF0078D4)
val DarkBlue = Color(0xFF003DA5)
val LightGray = Color(0xFFF5F5F5)
val DarkGray = Color(0xFF333333)
val AccentBlue = Color(0xFF50B4F2)

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
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MsnBlue
            ) {
                AppDestinations.entries.forEach {
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = it.icon,
                                contentDescription = it.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(it.label, fontSize = 12.sp) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MsnBlue,
                            selectedTextColor = MsnBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(LightGray)
        ) {
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
    val icon: androidx.compose.material.icons.rounded.Home,
) {
    HOME("Home", androidx.compose.material.icons.rounded.Home() as androidx.compose.material.icons.rounded.Home),
    LOCATIONS("Saved", androidx.compose.material.icons.rounded.Home() as androidx.compose.material.icons.rounded.Home),
    PROFILE("Profile", androidx.compose.material.icons.rounded.Home() as androidx.compose.material.icons.rounded.Home),
}

enum class AppDestinationsFixed(
    val label: String,
    val icon: androidx.compose.material.icons.filled.Home,
) {
    HOME("Home", Icons.Filled.Home),
    LOCATIONS("Saved", Icons.Filled.LocationOn),
    PROFILE("Profile", Icons.Filled.Person),
}

@Composable
fun HomeScreen(viewModel: LocationViewModel) {
    var cityInput by rememberSaveable { mutableStateOf("") }
    val allWeatherData by viewModel.allWeatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val savedLocations by viewModel.savedLocations.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Weather",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Search Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    placeholder = { Text("Search city...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MsnBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Button(
                    onClick = {
                        if (cityInput.isNotEmpty()) {
                            viewModel.searchWeather(cityInput)
                            cityInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MsnBlue),
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Search", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MsnBlue,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Empty state
        if (allWeatherData.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🔍",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        "Search for a city",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = DarkGray,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Weather cards list
        LazyColumn {
            items(allWeatherData) { weather ->
                val isSaved = savedLocations.any { it.name == weather.name }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    weather.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = DarkGray
                                    )
                                )
                                Text(
                                    weather.description.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // Temperature display
                            Box(
                                modifier = Modifier
                                    .background(
                                        MsnBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${weather.temp.toInt()}°",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 32.sp,
                                        color = MsnBlue
                                    )
                                )
                            }
                        }

                        // Weather details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherDetailItem("💧", "Humidity", "${weather.humidity}%")
                            Spacer(modifier = Modifier.width(16.dp))
                            WeatherDetailItem("🌡️", "Temperature", "${weather.temp}°C")
                        }

                        // Save button
                        Button(
                            onClick = {
                                if (isSaved) {
                                    viewModel.removeLocation(weather.name)
                                } else {
                                    viewModel.saveLocation(weather)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSaved) Color(0xFF4CAF50) else AccentBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (isSaved) "✓ Saved" else "+ Save",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(icon: String, label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp, modifier = Modifier.padding(bottom = 4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray,
                fontSize = 11.sp
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                fontSize = 14.sp
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun SavedLocationsScreen(viewModel: LocationViewModel) {
    val savedLocations by viewModel.savedLocations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Saved Locations",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Refresh button
        Button(
            onClick = { viewModel.refreshSavedLocations() },
            colors = ButtonDefaults.buttonColors(containerColor = MsnBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Updating..." else "🔄 Refresh",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MsnBlue,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        if (savedLocations.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "📍",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        "No saved locations",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = DarkGray,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Go to Home and save a city",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn {
                items(savedLocations) { location ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        location.name,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp,
                                            color = DarkGray
                                        )
                                    )
                                    Text(
                                        location.description.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        ),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            MsnBlue.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${location.temp.toInt()}°",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 32.sp,
                                            color = MsnBlue
                                        )
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                WeatherDetailItem("💧", "Humidity", "${location.humidity}%")
                                Spacer(modifier = Modifier.width(16.dp))
                                WeatherDetailItem("🌡️", "Temperature", "${location.temp}°C")
                            }

                            Button(
                                onClick = { viewModel.removeLocation(location.name) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE53935)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "🗑️ Remove",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "👤",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            "Profile",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )
        )
        Text(
            "User Settings",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Weatherapp20231141Theme {
        Weatherapp20231141App()
    }
}
