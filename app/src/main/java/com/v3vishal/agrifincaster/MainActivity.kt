package com.v3vishal.agrifincaster

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.v3vishal.agrifincaster.ui.theme.AgriFincasterTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}
// Define the main screens
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registration : Screen("registration")
    object Home : Screen("home")
}

// Define the bottom navigation screens
sealed class BottomNavScreen(val route: String, val label: String, val icon: Int, val heading: String) {
    object Home : BottomNavScreen("home", "Home", R.mipmap.hicon_foreground, "AgriFinCaster Homepage")
    object Weather : BottomNavScreen("weather", "Weather", R.mipmap.wficon_foreground, "Weather Forecast Details")
    object Crops : BottomNavScreen("crops", "Crops", R.mipmap.cricon_foreground,"Crop Yield and Production Predictor")
    object Reports : BottomNavScreen("reports", "Reports", android.R.drawable.ic_menu_report_image,"Crop Reports")
    object Finances : BottomNavScreen("finances", "Finances", android.R.drawable.ic_input_add,"Finances Manager")
}

data class GeocodeResponse(
    val addresses: List<Address>?
)

data class Address(
    val address: AddressDetails?,
    val position: Position?
)

data class AddressDetails(
    val freeformAddress: String?
)

data class Position(
    val lat: Double?,
    val lon: Double?
)

@Composable
fun TemporaryMessage(message: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SnackbarHost(hostState = snackbarHostState)

    LaunchedEffect(message) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@Composable
fun LoginPage(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    val auth = Firebase.auth
    var temporaryMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //logo
        Image(painter = painterResource(id = R.drawable.agfclogohighres), contentDescription = "Logo")
        //username-field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        //password-field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        //lang selector
        Box(Modifier.height(50.dp)) {
            Menu(modifier = Modifier
                .align(Alignment.TopCenter)
                .width(240.dp), state = rememberMenuState(expanded = false)) {
                MenuButton(
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        BasicText("Options", style = TextStyle(fontSize = 16.sp, color = Color.Black))
                        Spacer(Modifier.width(4.dp))
                        Image(Icons.Rounded.KeyboardArrowDown, null)
                    }
                }

                MenuContent(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .padding(4.dp),
                    exit = fadeOut()
                ) {
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("English",
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Hindi",
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Tamil",
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                }
            }
        }
        //login button
        Button(onClick = {
            if((email.length != 0) or (password.length != 0)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            temporaryMessage = "Welcome ${email}!"
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            temporaryMessage = "Invalid login details. Try again!"
                        }
                    }
            } else {
                temporaryMessage = "Enter details to log in!"
            }
        }) {
            Text("Login")
        }

        // Display TemporaryMessage if message is not null
        temporaryMessage?.let { message ->
            TemporaryMessage(message)
            // Reset the message after it's displayed
            LaunchedEffect(message) {
                delay(5000) // Delay for 5 seconds
                temporaryMessage = null
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        //register button
        Button(onClick = { navController.navigate(Screen.Registration.route) }) {
            Text("Register")
        }
    }
}

@Composable
fun RegistrationPage(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    val auth = Firebase.auth
    var temporaryMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.mipmap.agfc_logo_foreground), contentDescription = "Logo")
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (Min. 6 Characters)") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        temporaryMessage = "Registered ${username}!"
                        navController.navigate(Screen.Login.route)
                    } else {
                        temporaryMessage = "Couldn't register. Try again!"
                    }
                } },
        ) {
            Text("Register")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(@Suppress("UNUSED_PARAMETER") navController: NavController) {
    val bottomNavController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AgriFincaster") },
                actions = {
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavHost(bottomNavController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Weather,
        BottomNavScreen.Crops,
        BottomNavScreen.Reports,
        BottomNavScreen.Finances
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {Image(painter = painterResource(screen.icon), contentDescription = screen.label)},
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

// Bottom NavHost for inner navigation within Home Screen
@Composable
fun BottomNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavScreen.Home.route) {
        composable(BottomNavScreen.Home.route) { HomeScreen() }
        composable(BottomNavScreen.Finances.route) { FinancesScreen() }
        composable(BottomNavScreen.Weather.route) { WeatherScreen() }
        composable(BottomNavScreen.Crops.route) { CropsScreen() }
        composable(BottomNavScreen.Reports.route) { ReportsScreen() }
    }
}

@Composable
fun HomeScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    val heading = BottomNavScreen.Home.heading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            heading,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.mipmap.agfc_logo_foreground),
            contentDescription = "AGFC Logo",
            modifier = Modifier.size(250.dp)
        )

        Text(
            "Hi there, Welcome to AgriFincaster!",
            style = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp)
        )

        Text("AgriFincaster helps you to obtain Weather info, predict production and yield for your crops, and manage your finances! Click on the bottom buttons to navigate through the app.",
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.padding(top = 8.dp)
        )

    }
}

@Composable
fun FinancesScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    val heading = BottomNavScreen.Finances.heading
    var cpkg by remember { mutableStateOf("") }
    var prkg by remember { mutableStateOf("") }
    var exps by remember { mutableStateOf("") }
    var fnrp by remember { mutableStateOf("   Finances Report will be displayed here") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(16.dp)
    ) {
        Text(
            heading,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cpkg,
            onValueChange = { cpkg = it },
            label = { Text("Enter Crop Price per kg") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = prkg,
            onValueChange = { prkg = it },
            label = { Text("Enter Crop Production in kg / Leave blank for (GPS)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = exps,
            onValueChange = { exps = it },
            label = { Text("Enter Expenses / Leave blank for Auto Predict") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: add finance manager code */
                fnrp = if(fnrp == "   Finances Report will be displayed here") "Total Revenue: Rs. 1650\nExpenses: Rs. 1000\nNet Revenue: Rs.650" else "Total Revenue: Rs. 2500\nExpenses: Rs.1200\nNet Revenue: Rs. 1300"
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)

        ) {
            Text("Register Info")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(fnrp)
    }
}
fun makeApiRequest(context: Context, url: String, onResponse: (List<Pair<String, Pair<Double, Double>>>) -> Unit, onError: (VolleyError) -> Unit) {
    val request = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            val resultsArray = response.getJSONArray("results")
            val locations = mutableListOf<Pair<String, Pair<Double, Double>>>()

            for (i in 0 until resultsArray.length()) {
                val result = resultsArray.getJSONObject(i)
                val address = result.getJSONObject("address").getString("freeformAddress")
                val position = result.getJSONObject("position")
                val lat = position.getDouble("lat")
                val lon = position.getDouble("lon")
                locations.add(address to (lat to lon))
            }
            onResponse(locations)
        },
        { error -> onError(error) }
    )
    Volley.newRequestQueue(context).add(request)
}
fun getWeatherData(context: Context, url: String, onResponse: (String) -> Unit, onError: (VolleyError) -> Unit) {
    val request = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            val currentTemp = response.getJSONObject("current").getDouble("temperature_2m")
            val daily = response.getJSONObject("daily")
            val dates = daily.getJSONArray("time")
            val tempMax = daily.getJSONArray("temperature_2m_max")
            val tempMin = daily.getJSONArray("temperature_2m_min")
            val daylightDuration = daily.getJSONArray("daylight_duration")
            val precipitationHours = daily.getJSONArray("precipitation_hours")

            // Formatting the data
            val dailyForecast = StringBuilder("Current Temperature: $currentTemp°C\n\n7-Day Forecast:\n")
            for (i in 0 until dates.length()) {
                dailyForecast.append(
                    "Date: ${dates.getString(i)}\n" +
                            "Max Temp: ${tempMax.getDouble(i)}°C, Min Temp: ${tempMin.getDouble(i)}°C\n" +
                            "Daylight: ${daylightDuration.getDouble(i)} seconds, Precipitation Hours: ${precipitationHours.getInt(i)} hours\n\n"
                )
            }

            onResponse(dailyForecast.toString())
        },
        { error -> onError(error) }
    )
    Volley.newRequestQueue(context).add(request)
}

@Composable
fun WeatherScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    var location by remember { mutableStateOf("") }
    var winfo by remember { mutableStateOf("   Weather information will be displayed here") }
    var addressList by remember { mutableStateOf(listOf<Pair<String, Pair<Double, Double>>>()) }
    var selecAddress by remember { mutableStateOf<String?>(null) }
    var selectedLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var wfield by remember { mutableStateOf("Enter Location") }
    var dailyForecast by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(16.dp)
    ) {
        Text(
            "Weather Forecast Details",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                if (location.length >= 3) {
                    val url =
                        "https://api.tomtom.com/search/2/geocode/$location.json?key=k6cSq5FLgC72KvbQLOVOWZLq7JAiAmtY&countrySet=IN"
                    makeApiRequest(
                        context = context,
                        url = url,
                        onResponse = { results ->
                            addressList = results
                        },
                        onError = { /* Handle error */ }
                    )
                } else {
                    addressList = emptyList()
                }
            },
            label = { Text(wfield) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = selecAddress == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Define a max height for visibility
            ) {
                items(addressList) { address ->
                    Text(
                        text = address.first,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selecAddress = address.first
                                selectedLatLng = address.second
                                location = address.first
                                addressList = emptyList()
                            }
                            .padding(8.dp),
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedLatLng?.let { latLng ->
                    val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=${latLng.first}&longitude=${latLng.second}&current=temperature_2m&daily=temperature_2m_max,temperature_2m_min,daylight_duration,precipitation_hours&timezone=auto"
                    getWeatherData(
                        context = context,
                        url = weatherUrl,
                        onResponse = { data ->
                            winfo = "${selecAddress}\nLatitude: ${latLng.first}, Longitude: ${latLng.second}\n\nCurrent Temperature:\n${data.split("\n\n")[0]}"
                            dailyForecast = data.split("\n\n").drop(1)
                        },
                        onError = { /* Handle error here */ }
                    )
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Forecast")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(winfo, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f) // Make this scrollable list fill remaining space
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(dailyForecast) { forecast ->
                Text(
                    text = forecast,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
fun CropsScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    var crtype by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(16.dp)
    ) {
        Text(
            "Crop Production Yield Predictor",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = crtype,
            onValueChange = { crtype = it },
            label = { Text("Enter Crop Type/Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = area,
            onValueChange = { area = it },
            label = { Text("Enter Crop Area (in Acres)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: add weather open-meteo api code */

            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register Info")
        }
    }
}

@Composable
fun ReportsScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    var crinfo by remember { mutableStateOf("    Crop Information will be displayed here") }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(bghex))
        .padding(16.dp)) {
        Text("Location Not Registered.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: add weather open-meteo api code */
                //crinfo = if(crinfo == "    Crop Information will be displayed here") "             Crop: Wheat \n           Area: 3 Acres \n                Ideal Production: 33kg\n              Ideal Yield: 11kg/acre (GPS)" else TODO()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Report")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(crinfo, textAlign = TextAlign.Center)
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    val user = Firebase.auth.currentUser

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    NavHost(
        navController,
        startDestination = if (user != null) "home" else "login"
    ) {
        composable("login") { LoginPage(navController) }
        composable("home") { HomeScreen(navController) }
        composable("registration") { RegistrationPage(navController) }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    MainScreen(navController)
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AgriFincasterTheme {
        val navController = rememberNavController()
        WeatherScreen()
    }
}
