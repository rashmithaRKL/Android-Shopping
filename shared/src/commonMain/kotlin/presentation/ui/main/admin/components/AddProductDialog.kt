package presentation.ui.main.admin.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import presentation.ui.main.admin.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, description: String) -> Unit
) {
    ProductDialog(
        title = "Add New Product",
        confirmButtonText = "Add",
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}
