package com.satyamthakur.bio_guardian

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.satyamthakur.bio_guardian.ui.model.BottomNavigationItem
import com.satyamthakur.bio_guardian.ui.navigation.BottomEndpoints
import com.satyamthakur.bio_guardian.ui.navigation.Endpoints
import com.satyamthakur.bio_guardian.ui.screens.AnimalIdentifiedScreen
import com.satyamthakur.bio_guardian.ui.screens.BioGuardianAppHomeScreen
import com.satyamthakur.bio_guardian.ui.screens.UploadImageScreen
import com.satyamthakur.bio_guardian.ui.theme.BioGuardianTheme
import com.satyamthakur.bio_guardian.ui.theme.accentColor
import com.satyamthakur.bio_guardian.ui.theme.onAccent
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BioGuardianTheme {
                val homeScreenNavController = rememberNavController()

                NavHost(
                    navController = homeScreenNavController,
                    startDestination = Endpoints.DASHBOARD
                ) {
                    composable(Endpoints.DASHBOARD) {
                        Dashboard(
                            homeScreenNavController = homeScreenNavController
                        )
                    }

                    // Define the route with a parameter for imageUrl
                    composable(
                        route = "${Endpoints.ANIMAL_DESC}/{imageUrl}/{animalName}",
                        arguments = listOf(
                            navArgument("imageUrl") { type = NavType.StringType },
                            navArgument("animalName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        // Retrieve the imageUrl argument
                        val encodedImageUrl  = backStackEntry.arguments?.getString("imageUrl") ?: ""
                        val encodedAnimalName = backStackEntry.arguments?.getString("animalName") ?: ""
                        // Pass it to the AnimalIdentifiedScreen
                        val imageUrl = URLDecoder.decode(encodedImageUrl, "UTF-8");
                        val animalName = URLDecoder.decode(encodedAnimalName, "UTF-8")
                        Log.d("BIOAPP", "Decoded URL : $imageUrl")

                        AnimalIdentifiedScreen(
                            navController = homeScreenNavController,
                            imageUrl = imageUrl,
                            animalName = animalName
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun Dashboard(
    homeScreenNavController: NavController
) {
    val bottomNavItems = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Upload",
            selectedIcon = Icons.Filled.CloudUpload,
            unselectedIcon = Icons.Outlined.CloudUpload
        )
    )

    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val bottomNavController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFDFB)
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = accentColor,
                    modifier = Modifier.height(64.dp)
                ) {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = onAccent,
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White
                            ),
                            selected = selectedItemIndex == index,
                            onClick = {
                                if (index == 0 && selectedItemIndex != 0) {
                                    bottomNavController.popBackStack()
                                    bottomNavController.navigate(BottomEndpoints.HOME_SCREEN)
                                }
                                if (index == 1 && selectedItemIndex != 1) {
                                    bottomNavController.popBackStack()
                                    bottomNavController.navigate(BottomEndpoints.UPLOAD)
                                }
                                selectedItemIndex = index
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedItemIndex == index)
                                        item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            alwaysShowLabel = false,
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomEndpoints.HOME_SCREEN
            ) {
                composable(BottomEndpoints.HOME_SCREEN) {
                    BioGuardianAppHomeScreen(paddingValues, homeScreenNavController)
                }
                composable(BottomEndpoints.UPLOAD) {
                    UploadImageScreen(paddingValues = paddingValues, homeScreenNavController)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BioGuardianTheme {
        Greeting("Android")
    }
}