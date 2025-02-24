package presentation.ui.main.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.ui.main.admin.view_model.AdminViewModel
import presentation.ui.main.admin.components.ProductItem
import presentation.ui.main.admin.components.AddProductDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onTriggerEvent(AdminViewModel.Event.LoadProducts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.products) { product ->
                        ProductItem(
                            product = product,
                            onEdit = { viewModel.onTriggerEvent(AdminViewModel.Event.EditProduct(it)) },
                            onDelete = { viewModel.onTriggerEvent(AdminViewModel.Event.DeleteProduct(it)) }
                        )
                    }
                }
            }

            if (showAddDialog) {
                AddProductDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, price, description ->
                        viewModel.onTriggerEvent(AdminViewModel.Event.AddProduct(name, price, description))
                        showAddDialog = false
                    }
                )
            }
        }
    }
}
