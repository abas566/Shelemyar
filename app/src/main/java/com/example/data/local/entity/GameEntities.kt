package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val team1Name: String = "گروه اول",
    val team2Name: String = "گروه دوم",
    val gameMode: String, // "WITHOUT_JOKER" or "WITH_JOKER"
    val targetScore: Int,
    val yasaEnabled: Boolean = true,
    val isFinished: Boolean = false,
    val winnerTeam: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "rounds",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["gameId"])]
)
data class RoundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val roundNumber: Int,
    val hakimTeam: String, // "TEAM_1" or "TEAM_2"
    val bid: Int,
    val hakimEarnedPoints: Int,
    val team1ScoreDelta: Int,
    val team2ScoreDelta: Int,
    val status: String,
    val isYasa: Boolean,
    val isShelem: Boolean,
    val isNegativeShelem: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
