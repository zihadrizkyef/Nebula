package com.zr.nebula.data.item

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
internal data class Log(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var levelCode: String = Level.INFO.code,
    var message: String = "",
    var createdAt: Date = Date(),
) {
    override fun toString(): String {
        return "$levelCode : $message"
    }
}