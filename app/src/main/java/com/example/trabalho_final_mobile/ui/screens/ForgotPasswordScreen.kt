package com.example.trabalho_final_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trabalho_final_mobile.ui.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    val state = viewModel.forgotPasswordUiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Ícone
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.LockReset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Recuperar senha",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Informe seu e-mail e enviaremos as instruções para você redefinir sua senha",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            ModernTextField(
                value = state.email,
                onValueChange = { viewModel.onForgotPasswordEmailChange(it) },
                label = "E-mail",
                placeholder = "voce@exemplo.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ErrorBanner(message = state.errorMessage)
            }

            if (state.successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                SuccessBanner(message = state.successMessage)
            }

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Enviar instruções",
                onClick = { viewModel.sendPasswordRecovery(onBackToLogin) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecondaryButton(
                text = "Voltar ao login",
                onClick = onBackToLogin
            )
        }
    }
}
