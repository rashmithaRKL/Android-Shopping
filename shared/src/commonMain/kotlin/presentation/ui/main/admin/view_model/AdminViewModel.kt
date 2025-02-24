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
import common.database.AppDatabase
import common.database.entity.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel : BaseViewModel<AdminViewModel.Event, AdminViewModel.State, AdminViewModel.Action>() {
    
    private val _state = mutableStateOf(State())
    override val state: MutableState<State> = _state
    private lateinit var database: AppDatabase

    override fun setInitialState(): State {
        loadProducts()
        return State()
    }

    fun initializeDatabase(context: android.content.Context) {
        database = AppDatabase.getDatabase(context)
    }

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
            is Event.SearchProducts -> {
                searchProducts(event.query)
            }
            Event.LoadProducts -> {
                loadProducts()
            }
        }
    }

    private fun loadProducts() {
        setState { copy(isLoading = true) }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = database.productDao().getAllProducts().first()
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            products = products,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            isLoading = false,
                            errorQueue = errorQueue.add(
                                UIComponent.DialogSimple(
                                    title = "Error",
                                    description = e.message ?: "Unknown error occurred"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        setState { copy(isLoading = true) }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = if (query.isBlank()) {
                    database.productDao().getAllProducts().first()
                } else {
                    database.productDao().searchProducts(query).first()
                }
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            products = products,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            isLoading = false,
                            errorQueue = errorQueue.add(
                                UIComponent.DialogSimple(
                                    title = "Search Error",
                                    description = e.message ?: "Failed to search products"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addProduct(name: String, price: Double, description: String) {
        val newProduct = ProductEntity(
            name = name,
            price = price,
            description = description
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.productDao().insertProduct(newProduct)
                loadProducts()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            errorQueue = errorQueue.add(
                                UIComponent.DialogSimple(
                                    title = "Error",
                                    description = "Failed to add product: ${e.message}"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun editProduct(product: ProductEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.productDao().updateProduct(product)
                loadProducts()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            errorQueue = errorQueue.add(
                                UIComponent.DialogSimple(
                                    title = "Error",
                                    description = "Failed to update product: ${e.message}"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteProduct(product: ProductEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.productDao().deleteProduct(product)
                loadProducts()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setState { 
                        copy(
                            errorQueue = errorQueue.add(
                                UIComponent.DialogSimple(
                                    title = "Error",
                                    description = "Failed to delete product: ${e.message}"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    sealed interface Event : ViewEvent {
        data class AddProduct(val name: String, val price: Double, val description: String) : Event
        data class EditProduct(val product: ProductEntity) : Event
        data class DeleteProduct(val product: ProductEntity) : Event
        data class SearchProducts(val query: String) : Event
        data object LoadProducts : Event
    }

    data class State(
        val products: List<ProductEntity> = emptyList(),
        val isLoading: Boolean = true,
        val progressBarState: ProgressBarState = ProgressBarState.Idle,
        val errorQueue: Queue<UIComponent> = Queue(mutableListOf())
    ) : ViewState

    sealed interface Action : ViewSingleAction
}
