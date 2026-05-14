package com.example.trabalho_final_mobile.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY id DESC")
    fun getTripsByUser(userId: Int): Flow<List<TripEntity>>

    /**
     * Busca todas as viagens do usuário cujo destino corresponda à cidade informada.
     * A comparação é case-insensitive (COLLATE NOCASE).
     * O filtro por intervalo de datas é feito no ViewModel pois as datas são armazenadas
     * como String no formato dd/MM/yyyy.
     */
    @Query(
        "SELECT * FROM trips " +
        "WHERE userId = :userId AND destination LIKE :city COLLATE NOCASE"
    )
    suspend fun findTripsByCity(userId: Int, city: String): List<TripEntity>
}
