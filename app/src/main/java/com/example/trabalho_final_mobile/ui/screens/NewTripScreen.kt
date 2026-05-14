package com.example.trabalho_final_mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trabalho_final_mobile.data.local.TripEntity
import com.example.trabalho_final_mobile.ui.viewmodel.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(
    viewModel: TripViewModel,
    editingTrip: TripEntity? = null,
    onSuccess: () -> Unit
) {
    val state = viewModel.formUiState

    LaunchedEffect(editingTrip) {
        if (editingTrip != null) {
            viewModel.loadTripForEdit(editingTrip)
        } else {
            viewModel.resetForm()
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDatePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onStartDateChange(dateFormatter.format(Date(millis)))
                    }
                    showStartDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDatePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onEndDateChange(dateFormatter.format(Date(millis)))
                    }
                    showEndDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = if (editingTrip != null) "Editar viagem" else "Planejar viagem",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (editingTrip != null)
                "Atualize os detalhes da sua viagem"
            else
                "Conte-nos para onde você vai",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // === Seção Destino ===
        SectionLabel(text = "DESTINO")
        Spacer(modifier = Modifier.height(8.dp))
        ModernTextField(
            value = state.destination,
            onValueChange = { viewModel.onDestinationChange(it) },
            label = "Para onde?",
            placeholder = "Ex: Florianópolis, SC",
            leadingIcon = Icons.Outlined.LocationOn
        )

        Spacer(modifier = Modifier.height(24.dp))

        // === Seção Tipo de Viagem ===
        SectionLabel(text = "TIPO DE VIAGEM")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TripTypeCard(
                modifier = Modifier.weight(1f),
                label = "Lazer",
                icon = Icons.Rounded.BeachAccess,
                selected = state.type == "Lazer",
                accentColor = MaterialTheme.colorScheme.primary,
                accentContainer = MaterialTheme.colorScheme.primaryContainer,
                onClick = { viewModel.onTypeChange("Lazer") }
            )
            TripTypeCard(
                modifier = Modifier.weight(1f),
                label = "Negócios",
                icon = Icons.Outlined.BusinessCenter,
                selected = state.type == "Negócios",
                accentColor = MaterialTheme.colorScheme.secondary,
                accentContainer = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { viewModel.onTypeChange("Negócios") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === Seção Datas ===
        SectionLabel(text = "PERÍODO")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernTextField(
                modifier = Modifier.weight(1f),
                value = state.startDate,
                onValueChange = {},
                label = "Início",
                placeholder = "dd/mm/aaaa",
                leadingIcon = Icons.Outlined.CalendarMonth,
                readOnly = true,
                onClick = { showStartDatePicker = true }
            )
            ModernTextField(
                modifier = Modifier.weight(1f),
                value = state.endDate,
                onValueChange = {},
                label = "Fim",
                placeholder = "dd/mm/aaaa",
                leadingIcon = Icons.Outlined.CalendarMonth,
                readOnly = true,
                onClick = { showEndDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === Seção Orçamento ===
        SectionLabel(text = "ORÇAMENTO")
        Spacer(modifier = Modifier.height(8.dp))
        ModernTextField(
            value = state.budget,
            onValueChange = { viewModel.onBudgetChange(it) },
            label = "Quanto pretende gastar?",
            placeholder = "0,00",
            leadingIcon = Icons.Outlined.AttachMoney,
            keyboardType = KeyboardType.Decimal
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            ErrorBanner(message = state.errorMessage)
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = if (editingTrip != null) "Salvar alterações" else "Criar viagem",
            onClick = {
                if (editingTrip != null) {
                    viewModel.updateTrip(editingTrip, onSuccess)
                } else {
                    viewModel.saveTrip(onSuccess)
                }
            },
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTypeCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accentColor: Color,
    accentContainer: Color,
    onClick: () -> Unit
) {
    val containerColor = if (selected) accentContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (selected) accentColor else MaterialTheme.colorScheme.outline

    Surface(
        onClick = onClick,
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
