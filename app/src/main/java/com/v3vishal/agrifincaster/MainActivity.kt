package com.v3vishal.agrifincaster

import android.media.Image
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
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
sealed class BottomNavScreen(val route: String, val label: String, val icon: Int) {
    object Home : BottomNavScreen("home", "Home", R.mipmap.hicon_foreground)
    object Weather : BottomNavScreen("weather", "Weather", R.mipmap.wficon_foreground)
    object Crops : BottomNavScreen("crops", "Crops", R.mipmap.cricon_foreground)
    object Reports : BottomNavScreen("reports", "Reports", android.R.drawable.ic_menu_report_image)
    object Finances : BottomNavScreen("finances", "Finances", android.R.drawable.ic_input_add)
}

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
        Image(painter = painterResource(id = R.mipmap.agfc_logo_foreground), contentDescription = "Logo")
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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        temporaryMessage = "Welcome ${email}!"
                        navController.navigate(Screen.Home.route)
                    } else {
                        temporaryMessage = "Invalid login details. Try again!"
                    }
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

@Composable
fun HomeScreen(navController: NavController) {
    val bottomNavController = rememberNavController()

    Scaffold(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(bghex))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            "Finances Manager",
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
            onClick = { /* TODO: add weather open-meteo api code */
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

@Composable
fun WeatherScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    var location by remember { mutableStateOf("") }
    var winfo by remember { mutableStateOf("   Weather information will be displayed here") }

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
            onValueChange = { location = it },
            label = { Text("Enter Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: add weather open-meteo api code */
                winfo = if(winfo == "   Weather information will be displayed here") "(GPS) Chikkegowdanapalya, Bengaluru Rural, Bengaluru, Karnataka: \n\n25deg Celsius, 80% Precipitation - Good to grow Sugarcane!" else "Uttar Pradesh, Ghaziabad, Loni, West Ghat: \n\n24deg Celsius, 50% precipitation - Good to grow Paddy and Beans!"
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Forecast")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(winfo, textAlign = TextAlign.Center)
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
            onClick = { /* TODO: add weather open-meteo api code */ },
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
        Text("       Location: (GPS) Report: Wheat - 3 Acres")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: add weather open-meteo api code */
                crinfo = if(crinfo == "    Crop Information will be displayed here") "             Crop: Wheat \n           Area: 3 Acres \n                Ideal Production: 33kg\n              Ideal Yield: 11kg/acre (GPS)" else TODO()
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
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) { LoginPage(navController) }
        composable(Screen.Registration.route) { RegistrationPage(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    AgriFincasterTheme {
        FinancesScreen()
    }
}
