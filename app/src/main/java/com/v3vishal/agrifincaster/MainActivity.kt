package com.v3vishal.agrifincaster

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.google.firebase.firestore.firestore

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
    object Settings : BottomNavScreen("settings", "Settings", android.R.drawable.ic_dialog_info)
}



@Composable
fun LoginPage(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)

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
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
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
                        BasicText("Hindi [DISABLED]",
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                    MenuItem(modifier = Modifier.clip(RoundedCornerShape(6.dp)), onClick = { /* TODO handle click */ }) {
                        BasicText("Tamil [DISABLED]",
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp))
                    }
                }
            }
        }
        //login button
        Button(onClick = { navController.navigate(Screen.Home.route) }) {
            Text("Login")
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
            label = { Text("Password") },
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
            onClick = { navController.navigate(Screen.Login.route) },
        ) {
            Text("Register")
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    // Show Bottom Nav Bar for inner navigation after login
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavHost(bottomNavController)
        }
    }
}

// Bottom Navigation Bar Composable
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Weather,
        BottomNavScreen.Crops,
        BottomNavScreen.Reports,
        BottomNavScreen.Settings
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
        composable(BottomNavScreen.Settings.route) { SettingsScreen() }
        composable(BottomNavScreen.Weather.route) { WeatherScreen() }
        composable(BottomNavScreen.Crops.route) { CropsScreen() }
        composable(BottomNavScreen.Reports.route) { ReportsScreen() }
    }
}

@Composable
fun HomeScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    Text("Home Screen", modifier = Modifier
        .fillMaxSize()
        .background(color = Color(bghex)), textAlign = TextAlign.Center)
}

@Composable
fun SettingsScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    Text("Settings Screen", modifier = Modifier
        .fillMaxSize()
        .background(color = Color(bghex)), textAlign = TextAlign.Center)
}

@Composable
fun WeatherScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    Box(modifier = Modifier.fillMaxSize().background(color = Color(bghex))) {
        Text("Weather Forecast Details", textAlign = TextAlign.Center)
    }

}

@Composable
fun CropsScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    Text("Crops Screen", modifier = Modifier
        .fillMaxSize()
        .background(color = Color(bghex)), textAlign = TextAlign.Center)
}

@Composable
fun ReportsScreen() {
    val hexC = "#eecf8c"
    val bghex = android.graphics.Color.parseColor(hexC)
    Text("Reports Screen", modifier = Modifier
        .fillMaxSize()
        .background(color = Color(bghex)), textAlign = TextAlign.Center)
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
        WeatherScreen()
    }
}
