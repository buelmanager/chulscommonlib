package com.buel.firebase.`interface`

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot

interface IFirebase {
    fun <T> write(collectName: String, dataModel: T, onFirestoreComplete: OnSuccessListener<Boolean>) {    }
    fun read(tableName: String, onFirestoreComplete: OnSuccessListener<QuerySnapshot>) {    }
    fun delete(tableName: String, uid:String, onFirestoreComplete: OnSuccessListener<Boolean>) {    }
    fun error(exception: Exception) {
    }
}