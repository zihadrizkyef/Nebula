package com.zr.nebula.data.item

internal enum class Level(val code: String) {
    INFO("I"),
    DEBUG("D"),
    WARN("W"),
    ERROR("E");

    companion object {
        fun fromCode(code: String): Level {
            return Level.entries.find { it.code == code } ?: INFO
        }
    }
}