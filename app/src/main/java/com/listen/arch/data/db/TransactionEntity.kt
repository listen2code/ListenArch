package com.listen.arch.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: String, // "EXPENSE" or "INCOME"
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColorHex: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val accountType: String = "WECHAT"
)
