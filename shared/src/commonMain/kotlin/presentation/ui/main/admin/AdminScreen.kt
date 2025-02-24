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
import presentation.ui.main.admin.model.Product
import common.database.entity.ProductEntity

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

    var searchQuery by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.onTriggerEvent(AdminViewModel.Event.SearchProducts(it))
                },
                placeholder = { Text("Search products...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                        items(state.products) { productEntity ->
                            // Convert ProductEntity to Product
                            val product = Product(
                                id = productEntity.id.toString(),
                                name = productEntity.name,
                                price = productEntity.price,
                                description = productEntity.description
                            )
                            
                            ProductItem(
                                product = product,
                                onEdit = { editedProduct ->
                                    // Convert back to ProductEntity for the ViewModel
                                    val editedEntity = ProductEntity(
                                        id = editedProduct.id.toLongOrNull() ?: 0,
                                        name = editedProduct.name,
                                        price = editedProduct.price,
                                        description = editedProduct.description
                                    )
                                    viewModel.onTriggerEvent(AdminViewModel.Event.EditProduct(editedEntity))
                                },
                                onDelete = { productToDelete ->
                                    // Convert to ProductEntity for the ViewModel
                                    val entityToDelete = ProductEntity(
                                        id = productToDelete.id.toLongOrNull() ?: 0,
                                        name = productToDelete.name,
                                        price = productToDelete.price,
                                        description = productToDelete.description
                                    )
                                    viewModel.onTriggerEvent(AdminViewModel.Event.DeleteProduct(entityToDelete))
                                }
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
}
