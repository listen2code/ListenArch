package com.listen.arch.data.backup

import com.listen.arch.data.db.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TransactionBackupManager {

    fun exportToJson(transactions: List<TransactionEntity>): String {
        val jsonArray = JSONArray()
        transactions.forEach { tx ->
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("type", tx.type)
                put("categoryId", tx.categoryId)
                put("categoryName", tx.categoryName)
                put("categoryIcon", tx.categoryIcon)
                put("categoryColorHex", tx.categoryColorHex)
                put("amount", tx.amount)
                put("note", tx.note)
                put("accountType", tx.accountType)
                put("timestamp", tx.timestamp)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    fun importFromJson(jsonStr: String): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        val jsonArray = JSONArray(jsonStr)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val tx = TransactionEntity(
                id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                type = obj.optString("type", "EXPENSE"),
                categoryId = obj.optString("categoryId", "c_other_exp"),
                categoryName = obj.optString("categoryName", "其他"),
                categoryIcon = obj.optString("categoryIcon", "c_other_exp"),
                categoryColorHex = obj.optString("categoryColorHex", "#6B7280"),
                amount = obj.optDouble("amount", 0.0),
                note = obj.optString("note", ""),
                accountType = obj.optString("accountType", "WECHAT"),
                timestamp = obj.optLong("timestamp", System.currentTimeMillis())
            )
            list.add(tx)
        }
        return list
    }

    fun exportToCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,类型,分类,金额,账户,备注,时间\n")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        transactions.forEach { tx ->
            val typeStr = if (tx.type == "EXPENSE") "支出" else "收入"
            val timeStr = sdf.format(Date(tx.timestamp))
            val cleanNote = tx.note.replace(",", " ")
            sb.append("${tx.id},$typeStr,${tx.categoryName},${tx.amount},${tx.accountType},$cleanNote,$timeStr\n")
        }
        return sb.toString()
    }
}
