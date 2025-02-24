package presentation.ui.main.map.view_model

import business.core.BaseViewModel
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.core.ViewEvent
import business.core.ViewSingleAction
import business.core.ViewState
import business.datasource.network.common.JAlertResponse

class MapViewModel : BaseViewModel<MapViewModel.Event, MapViewModel.State, MapViewModel.Action>() {
    
    override fun setInitialState(): State = State()

    override fun onTriggerEvent(event: Event) {
        when (event) {
            is Event.OnLocationUpdate -> {
                updateLocation(event.latitude, event.longitude)
            }
            Event.OnError -> {
                setError {
                    UIComponent.DialogSimple(
                        title = "Location Error",
                        description = "Failed to get location. Please check your permissions and try again."
                    )
                }
            }
        }
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        setState { copy(latitude = latitude, longitude = longitude) }
    }

    sealed interface Event : ViewEvent {
        data class OnLocationUpdate(val latitude: Double, val longitude: Double) : Event
        data object OnError : Event
    }

    data class State(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val progressBarState: ProgressBarState = ProgressBarState.Idle,
        val errorQueue: Queue<UIComponent> = Queue(listOf())
    ) : ViewState

    sealed interface Action : ViewSingleAction
}
