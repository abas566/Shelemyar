package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.GameEntity
import com.example.data.local.entity.RoundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY updatedAt DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE isFinished = 0 ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveGameFlow(): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE isFinished = 0 ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getActiveGame(): GameEntity?

    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameByIdFlow(gameId: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Long): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Update
    suspend fun updateGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()
}

@Dao
interface RoundDao {
    @Query("SELECT * FROM rounds WHERE gameId = :gameId ORDER BY roundNumber ASC")
    fun getRoundsForGameFlow(gameId: Long): Flow<List<RoundEntity>>

    @Query("SELECT * FROM rounds WHERE gameId = :gameId ORDER BY roundNumber ASC")
    suspend fun getRoundsForGame(gameId: Long): List<RoundEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundEntity): Long

    @Update
    suspend fun updateRound(round: RoundEntity)

    @Delete
    suspend fun deleteRound(round: RoundEntity)

    @Query("DELETE FROM rounds WHERE id = :roundId")
    suspend fun deleteRoundById(roundId: Long)

    @Query("DELETE FROM rounds WHERE gameId = :gameId AND roundNumber = (SELECT MAX(roundNumber) FROM rounds WHERE gameId = :gameId)")
    suspend fun deleteLastRound(gameId: Long)

    @Query("DELETE FROM rounds WHERE gameId = :gameId")
    suspend fun deleteRoundsForGame(gameId: Long)
}
