/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yuku.ouch

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import yuku.ouch.ui.theme.MyTheme

enum class TabId {
    AdoptMe,
    Profile,
    About,
}

data class Hamster(
    val id: String,
    val displayName: String,
    @DrawableRes val resId: Int,
)

val hamsters = listOf(
    Hamster("1", "Snowster", R.drawable.hamster_1),
    Hamster("2", "Tikus", R.drawable.hamster_2),
    Hamster("3", "Cheeko", R.drawable.hamster_3),
    Hamster("4", "Blaze", R.drawable.hamster_4),
    Hamster("5", "Florian", R.drawable.hamster_5),
    Hamster("6", "Slater", R.drawable.hamster_6),
    Hamster("7", "Noo Noo", R.drawable.hamster_7),
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "/") {
        composable("/") { MainScreen(navController) }
        composable(
            "detail/{hamsterId}",
            arguments = listOf(navArgument("hamsterId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailScreen(hamster = hamsters.first { it.id == backStackEntry.arguments?.getString("hamsterId") })
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedTabId by remember { mutableStateOf(TabId.AdoptMe) }
    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = selectedTabId == TabId.AdoptMe,
                    icon = { Icon(Icons.Filled.Mouse, "Adopt Me") },
                    label = { Text("Adopt Me") },
                    onClick = { selectedTabId = TabId.AdoptMe },
                )
                BottomNavigationItem(
                    selected = selectedTabId == TabId.Profile,
                    icon = { Icon(Icons.Filled.Person, "Profile") },
                    label = { Text("Profile") },
                    onClick = { selectedTabId = TabId.Profile },
                )
                BottomNavigationItem(
                    selected = selectedTabId == TabId.About,
                    icon = { Icon(Icons.Filled.Info, "About") },
                    label = { Text("About") },
                    onClick = { selectedTabId = TabId.About },
                )
            }
        }
    ) {
        when (selectedTabId) {
            TabId.AdoptMe -> AdoptMeTab(navController)
            TabId.Profile -> Text("Empty")
            TabId.About -> AboutTab()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdoptMeTab(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            content = {
                val chunks = hamsters.chunked(2)

                item {
                    Text(
                        "Ouch",
                        style = MaterialTheme.typography.h2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                items(chunks.size) { index ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (hamster in chunks[index]) {
                            HamsterItem(navController, hamster)
                        }
                    }
                }

                item {
                    Box(modifier = Modifier.height(80.dp))
                }
            }
        )
    }
}

@Composable
fun RowScope.HamsterItem(navController: NavHostController, hamster: Hamster) {
    Column(modifier = Modifier.weight(1f)) {
        Image(
            painter = painterResource(hamster.resId),
            contentDescription = null,
            modifier = Modifier
                .height(160.dp)
                .padding(8.dp)
                .clickable(
                    onClick = {
                        navController.navigate("detail/${hamster.id}")
                    }
                )
                .fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        Text(
            hamster.displayName,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DetailScreen(hamster: Hamster) {
    var openDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            "Meet ${hamster.displayName}!",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Image(
            painter = painterResource(hamster.resId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .defaultMinSize(minHeight = 400.dp),
            contentScale = ContentScale.Crop,
        )
        Row {
            Icon(Icons.Filled.CalendarToday, contentDescription = null)
            Box(modifier = Modifier.width(8.dp))
            Text("Age: ${hamster.displayName.hashCode() % 10 + 2} months")
        }

        Box(modifier = Modifier.height(16.dp))

        Button(onClick = { openDialog = true }) {
            Text("Adopt ${hamster.displayName}")
        }

        if (openDialog) {
            AlertDialog(
                confirmButton = {
                    Button(onClick = { openDialog = false }) {
                        Text("OK")
                    }
                },
                text = { Text("Adoption is in progress...") },
                onDismissRequest = { openDialog = false },
            )
        }

        Box(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AboutTab() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ouch", style = MaterialTheme.typography.h2)
        Text("A project for the Android Dev Challenge", style = MaterialTheme.typography.subtitle1)
        Text("That is the sound you make if your hamster bites you", style = MaterialTheme.typography.caption)
    }
}
