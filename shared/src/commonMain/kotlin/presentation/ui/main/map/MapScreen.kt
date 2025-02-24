package presentation.ui.main.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import common.MapComponent
import org.jetbrains.compose.resources.painterResource
import presentation.ui.main.map.view_model.MapViewModel
import shoping_by_kmp.shared.generated.resources.Res
import shoping_by_kmp.shared.generated.resources.location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onTriggerEvent(MapViewModel.Event.OnLocationUpdate(37.7749, -122.4194))
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.location),
                    contentDescription = "My Location"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapComponent(
                onLatitude = { latitude ->
                    viewModel.onTriggerEvent(MapViewModel.Event.OnLocationUpdate(latitude, state.value.longitude))
                },
                onLongitude = { longitude ->
                    viewModel.onTriggerEvent(MapViewModel.Event.OnLocationUpdate(state.value.latitude, longitude))
                }
            )
        }
    }
}
