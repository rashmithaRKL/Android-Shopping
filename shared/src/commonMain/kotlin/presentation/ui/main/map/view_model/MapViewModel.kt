package presentation.ui.main.map.view_model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import business.core.BaseViewModel
import business.core.ProgressBarState
import business.core.Queue
import business.core.UIComponent
import business.core.ViewEvent
import business.core.ViewSingleAction
import business.core.ViewState

class MapViewModel : BaseViewModel<MapViewModel.Event, MapViewModel.State, MapViewModel.Action>() {
    
    override fun setInitialState(): State = State()

    override fun onTriggerEvent(event: Event) {
        when (event) {
            is Event.OnLocationUpdate -> {
                updateLocation(event.latitude, event.longitude)
            }
            Event.OnError -> {
                setError {
                    UI
