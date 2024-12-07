package com.zr.nebula.item

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date
import java.util.UUID

open class Log : RealmObject() {
    @PrimaryKey
    var id = UUID.randomUUID().toString()
    var levelCode = Level.INFO.code
    var message = ""
    var createdAt = Date()

    override fun toString(): String {
        return "$levelCode : $message"
    }

}