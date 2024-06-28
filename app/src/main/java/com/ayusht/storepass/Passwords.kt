package com.ayusht.storepass

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Passwords: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var appName: String = ""
    var appPassword: String = ""
    var userName: String = ""
}