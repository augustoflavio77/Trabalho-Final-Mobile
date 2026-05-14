package com.example.trabalho_final_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.PersonAddAlt1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trabalho_final_mobile.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit
) {
    val state = viewModel.registerUiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.resetRegisterState()
    }

    val handleSuccess: () -> Unit = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Conta criada com sucesso!",
                duration = SnackbarDuration.Short
            )
        }
        scope.launch {
            delay(1800L)
            onRegisterSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo / Ícone
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PersonAddAlt1,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Criar conta",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Preencha os dados abaixo para começar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            ModernTextField(
                value = state.name,
                onValueChange = { viewModel.onRegisterNameChange(it) },
                label = "Nome completo",
                placeholder = "Seu nome",
                leadingIcon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernTextField(
                value = state.email,
                onValueChange = { viewModel.onRegisterEmailChange(it) },
                label = "E-mail",
                placeholder = "voce@exemplo.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernTextField(
                value = state.phone,
                onValueChange = { viewModel.onRegisterPhoneChange(it) },
                label = "Telefone",
                placeholder = "(00) 00000-0000",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernTextField(
                value = state.password,
                onValueChange = { viewModel.onRegisterPasswordChange(it) },
                label = "Senha",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = state.isPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onRegisterPasswordVisibilityToggle() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModernTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onRegisterConfirmPasswordChange(it) },
                label = "Confirmar senha",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = state.isConfirmPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onRegisterConfirmPasswordVisibilityToggle() }
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorBanner(message = state.errorMessage)
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Cadastrar",
                onClick = { viewModel.register(handleSuccess) },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
