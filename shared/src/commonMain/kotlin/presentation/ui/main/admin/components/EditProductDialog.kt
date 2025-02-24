package presentation.ui.main.admin.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import presentation.ui.main.admin.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, description: String) -> Unit
) {
    ProductDialog(
        title = "Edit Product",
        confirmButtonText = "Save",
        initialProduct = product,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}
