package com.example.trabalho_final_mobile.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Repositório responsável por obter a localização atual do usuário e
 * converter as coordenadas em nome de cidade (reverse geocoding).
 *
 * Segue o padrão MVVM: o ViewModel consome este repositório, mantendo
 * a UI desacoplada de detalhes da API de localização do Android.
 */
class LocationRepository(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Verifica se as permissões de localização foram concedidas.
     */
    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    /**
     * Obtém a localização atual do usuário utilizando o FusedLocationProviderClient.
     * Retorna null caso a permissão não tenha sido concedida ou ocorra falha.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->
            val cts = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token
            )
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }

            continuation.invokeOnCancellation { cts.cancel() }
        }
    }

    /**
     * Converte coordenadas (latitude/longitude) em nome de cidade
     * usando o Geocoder (reverse geocoding).
     * Retorna null se não for possível resolver a cidade.
     */
    suspend fun getCityFromLocation(location: Location): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+ utiliza versão assíncrona
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) { addresses ->
                        val city = addresses.firstOrNull()?.let { addr ->
                            addr.locality ?: addr.subAdminArea ?: addr.adminArea
                        }
                        continuation.resume(city)
                    }
                }
            } else {
                // Versão síncrona para APIs anteriores (depreciada na 33+, mas funcional)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                addresses?.firstOrNull()?.let { addr ->
                    addr.locality ?: addr.subAdminArea ?: addr.adminArea
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
