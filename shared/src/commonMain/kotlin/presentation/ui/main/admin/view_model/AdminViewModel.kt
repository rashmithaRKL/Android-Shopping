package presentation.ui.main.admin.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.core.BaseViewModel
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.core.ViewEvent
import business.core.ViewSingleAction
import business.core.ViewState
import presentation.ui.main.admin.model.Product

class AdminViewModel : BaseViewModel<AdminViewModel.Event, AdminViewModel.State, AdminViewModel.Action>() {
    
    private val _state = mutableStateOf(State())
    override val state: MutableState<State> = _state

    override fun setInitialState(): State = State()

    override fun onTriggerEvent(event: Event) {
        when (event) {
            is Event.AddProduct -> {
                addProduct(event.name, event.price, event.description)
            }
            is Event.EditProduct -> {
                editProduct(event.product)
            }
            is Event.DeleteProduct -> {
                deleteProduct(event.product)
            }
            Event.LoadProducts -> {
                loadProducts()
            }
        }
    }

    private fun addProduct(name: String, price: Double, description: String) {
        val newProduct = Product(
            id = (state.value.products.size + 1).toString(),
            name = name,
            price = price,
            description = description
        )
        setState { copy(products = products + newProduct) }
    }

    private fun editProduct(product: Product) {
        setState { 
            copy(products = products.map {
                if (it.id == product.id) product else it
            })
        }
    }

    private fun deleteProduct(product: Product) {
        setState { 
            copy(products = products.filter { it.id != product.id })
        }
    }

    private fun loadProducts() {
        setState {
            copy(
                isLoading = false,
                products = listOf(
                    Product("1", "Sample Product 1", 99.99, "Description 1"),
                    Product("2", "Sample Product 2", 149.99, "Description 2")
                )
            )
        }
    }

    sealed interface Event : ViewEvent {
        data class AddProduct(val name: String, val price: Double, val description: String) : Event
        data class EditProduct(val product: Product) : Event
        data class DeleteProduct(val product: Product) : Event
        data object LoadProducts : Event
    }

    data class State(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = true,
        val progressBarState: ProgressBarState = ProgressBarState.Idle,
        val errorQueue: Queue<UIComponent> = Queue(mutableListOf())
    ) : ViewState

    sealed interface Action : ViewSingleAction
}
