package com.example.trabalho_final_mobile.ui.screens

import android.Manifest
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalho_final_mobile.data.local.TripEntity
import com.example.trabalho_final_mobile.ui.viewmodel.TripViewModel
import kotlinx.coroutines.launch

sealed class MenuDestination {
    object Home : MenuDestination()
    object NewTrip : MenuDestination()
    object MyTrips : MenuDestination()
    object About : MenuDestination()
    data class EditTrip(val trip: TripEntity) : MenuDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    email: String = "",
    password: String = "",
    tripViewModel: TripViewModel = viewModel()
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentDestination: MenuDestination by remember { mutableStateOf(MenuDestination.Home) }

    LaunchedEffect(email) {
        tripViewModel.initUser(email)
    }

    BackHandler {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            currentDestination !is MenuDestination.Home -> currentDestination = MenuDestination.Home
            else -> (context as? Activity)?.finish()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModernDrawerContent(
                email = email,
                currentDestination = currentDestination,
                onDestinationChange = { dest ->
                    currentDestination = dest
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentDestination) {
                                is MenuDestination.NewTrip -> "Nova Viagem"
                                is MenuDestination.MyTrips -> "Minhas Viagens"
                                is MenuDestination.About -> "Sobre"
                                is MenuDestination.EditTrip -> "Editar Viagem"
                                else -> "Início"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = "Abrir menu",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (val dest = currentDestination) {
                    is MenuDestination.Home -> HomeContent(
                        email = email,
                        tripViewModel = tripViewModel,
                        onNewTripClick = { currentDestination = MenuDestination.NewTrip },
                        onMyTripsClick = { currentDestination = MenuDestination.MyTrips }
                    )
                    is MenuDestination.NewTrip -> NewTripScreen(
                        viewModel = tripViewModel,
                        onSuccess = { currentDestination = MenuDestination.MyTrips }
                    )
                    is MenuDestination.MyTrips -> MyTripsScreen(
                        viewModel = tripViewModel,
                        onEditTrip = { trip ->
                            currentDestination = MenuDestination.EditTrip(trip)
                        }
                    )
                    is MenuDestination.EditTrip -> NewTripScreen(
                        viewModel = tripViewModel,
                        editingTrip = dest.trip,
                        onSuccess = { currentDestination = MenuDestination.MyTrips }
                    )
                    is MenuDestination.About -> AboutContent()
                }
            }
        }
    }
}

// =================== DRAWER ===================
@Composable
fun ModernDrawerContent(
    email: String,
    currentDestination: MenuDestination,
    onDestinationChange: (MenuDestination) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            // Avatar + info do usuário
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = email.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Olá!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            DrawerItem(
                icon = Icons.Outlined.AddCircleOutline,
                label = "Nova Viagem",
                selected = currentDestination is MenuDestination.NewTrip,
                onClick = { onDestinationChange(MenuDestination.NewTrip) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem(
                icon = Icons.Outlined.Luggage,
                label = "Minhas Viagens",
                selected = currentDestination is MenuDestination.MyTrips,
                onClick = { onDestinationChange(MenuDestination.MyTrips) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            DrawerItem(
                icon = Icons.Outlined.Info,
                label = "Sobre",
                selected = currentDestination is MenuDestination.About,
                onClick = { onDestinationChange(MenuDestination.About) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

// =================== HOME ===================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    email: String,
    tripViewModel: TripViewModel,
    onNewTripClick: () -> Unit,
    onMyTripsClick: () -> Unit
) {
    val locationState = tripViewModel.locationUiState

    // Launcher para solicitar permissões de localização (fine + coarse)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        tripViewModel.onLocationPermissionResult(granted)
    }

    // Ao entrar na tela Home, solicita permissão (caso ainda não concedida)
    // e dispara a busca pela localização atual.
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Saudação
        Text(
            text = "Olá, viajante 👋",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Para onde vamos hoje?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===== Card de Viagem Atual (baseada na localização) =====
        CurrentTripCard(
            isLoading = locationState.isLoading,
            city = locationState.city,
            currentTrip = locationState.currentTrip,
            message = locationState.message,
            onRetry = { tripViewModel.requestCurrentLocationAndTrip() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card hero - chamada principal
        Surface(
            onClick = onNewTripClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FlightTakeoff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Planejar nova viagem",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Crie um novo destino agora mesmo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card secundário
        Surface(
            onClick = onMyTripsClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Luggage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Minhas Viagens",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Veja, edite ou exclua suas viagens",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dica de rodapé
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Use o menu lateral para navegar entre as funcionalidades",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// =================== CURRENT TRIP CARD (LOCALIZAÇÃO) ===================
@Composable
fun CurrentTripCard(
    isLoading: Boolean,
    city: String?,
    currentTrip: TripEntity?,
    message: String?,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Cabeçalho do card
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Viagem atual",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = when {
                            isLoading -> "Obtendo sua localização..."
                            city != null -> "Você está em: $city"
                            else -> "Localização não identificada"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!isLoading) {
                    TextButton(onClick = onRetry) {
                        Text("Atualizar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Buscando viagem para a sua cidade...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                currentTrip != null -> {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    CurrentTripDetails(trip = currentTrip)
                }
                else -> {
                    Text(
                        text = message
                            ?: "Nenhuma viagem em andamento para a sua cidade atual.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentTripDetails(trip: TripEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TripInfoRow(
            icon = Icons.Outlined.LocationOn,
            label = "Destino",
            value = trip.destination
        )
        TripInfoRow(
            icon = Icons.Outlined.CalendarMonth,
            label = "Data de início",
            value = trip.startDate
        )
        TripInfoRow(
            icon = Icons.Outlined.CalendarMonth,
            label = "Data final",
            value = trip.endDate
        )
        TripInfoRow(
            icon = if (trip.type.equals("Negócios", ignoreCase = true))
                Icons.Outlined.BusinessCenter
            else
                Icons.Rounded.BeachAccess,
            label = "Tipo",
            value = trip.type
        )
        TripInfoRow(
            icon = Icons.Outlined.AttachMoney,
            label = "Orçamento",
            value = "R$ %.2f".format(trip.budget)
        )
        TripInfoRow(
            icon = Icons.Outlined.AttachMoney,
            label = "Total de gastos",
            value = "R$ %.2f".format(trip.totalSpent)
        )
    }
}

@Composable
private fun TripInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =================== ABOUT ===================
@Composable
fun AboutContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.FlightTakeoff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Trabalho Final Mobile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Versão 1.0.0",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Sobre o aplicativo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aplicativo para gerenciar suas viagens de lazer e negócios de forma prática, " +
                            "rápida e organizada. Cadastre destinos, defina datas e acompanhe seu orçamento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Tecnologias",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Jetpack Compose · Material 3 · Room · ViewModel · Navigation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
