package com.zr.nebula.data.item

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
internal data class Log(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var levelCode: String = Level.INFO.code,
    var message: String = "",
    var createdAt: Date = Date(),
) {
    override fun toString(): String {
        return "$levelCode : $message"
    }
}