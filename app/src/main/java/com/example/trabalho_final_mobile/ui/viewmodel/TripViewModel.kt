package com.example.trabalho_final_mobile.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalho_final_mobile.data.local.AppDatabase
import com.example.trabalho_final_mobile.data.local.TripEntity
import com.example.trabalho_final_mobile.data.location.LocationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TripFormUiState(
    val destination: String = "",
    val type: String = "Lazer", // "Lazer" ou "Negócios"
    val startDate: String = "",
    val endDate: String = "",
    val budget: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Estado da UI relativo à localização e à viagem atual encontrada
 * com base na cidade do usuário.
 */
data class LocationUiState(
    val isLoading: Boolean = false,
    val permissionGranted: Boolean = false,
    val city: String? = null,
    val currentTrip: TripEntity? = null,
    val message: String? = null
)

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val tripDao = AppDatabase.getDatabase(application).tripDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    // Repositório de localização (camada de dados do MVVM)
    private val locationRepository = LocationRepository(application.applicationContext)

    var formUiState by mutableStateOf(TripFormUiState())
        private set

    // Estado da UI relativo à localização e viagem atual
    var locationUiState by mutableStateOf(LocationUiState())
        private set

    // userId do usuário logado (definido após login)
    var loggedUserId by mutableStateOf(0)
        private set

    // trips do usuário logado como StateFlow
    var trips: StateFlow<List<TripEntity>> = tripDao
        .getTripsByUser(0)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        private set

    fun initUser(email: String) {
        viewModelScope.launch {
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                loggedUserId = user.id
                // recarrega o flow com o userId correto
                trips = tripDao
                    .getTripsByUser(user.id)
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
            }
        }
    }

    // ================== LOCALIZAÇÃO ==================

    /**
     * Atualiza o estado indicando se a permissão de localização foi concedida.
     * Deve ser chamado pela UI após o callback do launcher de permissões.
     */
    fun onLocationPermissionResult(granted: Boolean) {
        locationUiState = locationUiState.copy(
            permissionGranted = granted,
            message = if (!granted) "Permissão de localização negada." else null
        )
        if (granted) {
            requestCurrentLocationAndTrip()
        }
    }

    /**
     * Solicita a localização atual do usuário, faz reverse geocoding para obter a cidade
     * e busca no banco de dados uma viagem para aquela cidade dentro do intervalo de datas.
     */
    fun requestCurrentLocationAndTrip() {
        if (!locationRepository.hasLocationPermission()) {
            locationUiState = locationUiState.copy(
                permissionGranted = false,
                message = "Permissão de localização necessária."
            )
            return
        }

        locationUiState = locationUiState.copy(
            isLoading = true,
            permissionGranted = true,
            message = null,
            currentTrip = null
        )

        viewModelScope.launch {
            try {
                val location = locationRepository.getCurrentLocation()
                if (location == null) {
                    locationUiState = locationUiState.copy(
                        isLoading = false,
                        message = "Não foi possível obter a localização. Verifique se o GPS está ativado."
                    )
                    return@launch
                }

                val city = locationRepository.getCityFromLocation(location)
                if (city.isNullOrBlank()) {
                    locationUiState = locationUiState.copy(
                        isLoading = false,
                        message = "Não foi possível identificar a cidade."
                    )
                    return@launch
                }

                // Busca uma viagem para esta cidade no intervalo de datas atual
                val trip = findCurrentTripForCity(city)

                locationUiState = locationUiState.copy(
                    isLoading = false,
                    city = city,
                    currentTrip = trip,
                    message = if (trip == null) {
                        "Nenhuma viagem em andamento para $city."
                    } else null
                )
            } catch (e: Exception) {
                locationUiState = locationUiState.copy(
                    isLoading = false,
                    message = "Erro ao obter localização: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Busca, no banco de dados, uma viagem do usuário logado cuja cidade
     * corresponde (case-insensitive) e cuja data atual está entre startDate e endDate.
     */
    private suspend fun findCurrentTripForCity(city: String): TripEntity? {
        if (loggedUserId == 0) return null

        // Buscamos por todas as viagens do usuário cujo destino "contenha" a cidade
        // (LIKE com curingas) para suportar casos como "São Paulo - SP".
        val candidates = tripDao.findTripsByCity(loggedUserId, "%$city%")

        val today = stripTime(Date())
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            isLenient = false
        }

        return candidates.firstOrNull { trip ->
            try {
                val start = sdf.parse(trip.startDate)?.let { stripTime(it) }
                val end = sdf.parse(trip.endDate)?.let { stripTime(it) }
                start != null && end != null &&
                        !today.before(start) && !today.after(end)
            } catch (e: Exception) {
                false
            }
        }
    }

    /** Remove a parte de hora/minuto/segundo para comparar somente datas. */
    private fun stripTime(date: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.time
    }

    // --- Form ---
    fun onDestinationChange(value: String) {
        formUiState = formUiState.copy(destination = value, errorMessage = null)
    }

    fun onTypeChange(value: String) {
        formUiState = formUiState.copy(type = value, errorMessage = null)
    }

    fun onStartDateChange(value: String) {
        formUiState = formUiState.copy(startDate = value, errorMessage = null)
    }

    fun onEndDateChange(value: String) {
        formUiState = formUiState.copy(endDate = value, errorMessage = null)
    }

    fun onBudgetChange(value: String) {
        formUiState = formUiState.copy(budget = value, errorMessage = null)
    }

    fun resetForm() {
        formUiState = TripFormUiState()
    }

    fun loadTripForEdit(trip: TripEntity) {
        formUiState = TripFormUiState(
            destination = trip.destination,
            type = trip.type,
            startDate = trip.startDate,
            endDate = trip.endDate,
            budget = trip.budget.toString()
        )
    }

    fun saveTrip(onSuccess: () -> Unit) {
        val state = formUiState

        if (state.destination.isBlank() || state.startDate.isBlank() ||
            state.endDate.isBlank() || state.budget.isBlank()
        ) {
            formUiState = state.copy(errorMessage = "Todos os campos são obrigatórios.")
            return
        }

        val budgetValue = state.budget.replace(",", ".").toDoubleOrNull()
        if (budgetValue == null || budgetValue <= 0) {
            formUiState = state.copy(errorMessage = "Informe um orçamento válido.")
            return
        }

        formUiState = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                tripDao.insertTrip(
                    TripEntity(
                        destination = state.destination,
                        type = state.type,
                        startDate = state.startDate,
                        endDate = state.endDate,
                        budget = budgetValue,
                        totalSpent = 0.0,
                        userId = loggedUserId
                    )
                )
                formUiState = TripFormUiState()
                onSuccess()
            } catch (e: Exception) {
                formUiState = state.copy(
                    isLoading = false,
                    errorMessage = "Erro ao salvar viagem. Tente novamente."
                )
            }
        }
    }

    fun updateTrip(trip: TripEntity, onSuccess: () -> Unit) {
        val state = formUiState

        if (state.destination.isBlank() || state.startDate.isBlank() ||
            state.endDate.isBlank() || state.budget.isBlank()
        ) {
            formUiState = state.copy(errorMessage = "Todos os campos são obrigatórios.")
            return
        }

        val budgetValue = state.budget.replace(",", ".").toDoubleOrNull()
        if (budgetValue == null || budgetValue <= 0) {
            formUiState = state.copy(errorMessage = "Informe um orçamento válido.")
            return
        }

        formUiState = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                tripDao.updateTrip(
                    trip.copy(
                        destination = state.destination,
                        type = state.type,
                        startDate = state.startDate,
                        endDate = state.endDate,
                        budget = budgetValue
                    )
                )
                formUiState = TripFormUiState()
                onSuccess()
            } catch (e: Exception) {
                formUiState = state.copy(
                    isLoading = false,
                    errorMessage = "Erro ao atualizar viagem."
                )
            }
        }
    }

    fun deleteTrip(trip: TripEntity) {
        viewModelScope.launch {
            tripDao.deleteTrip(trip)
        }
    }
}
