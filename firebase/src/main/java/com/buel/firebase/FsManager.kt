package com.buel.firebase

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.buel.firebase.`interface`.IFirebase
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.log
import java.io.ByteArrayOutputStream

object FsManager : IFirebase {

    //release :: EC:ED:6B:4F:C2:7C:D8:ED:BC:48:25:03:B3:F3:B5:B2:7C:18:F6:4A
    //--------------------------------------------------------------------------------------------
    val TAG = "FIREBASE_FsManager"
    val ADMIN = "admin"
    val INFO = "info_page"
    val MAIN_PAGER = "main_pager"
    val APPS = "apps"
    val CONFIG_DATA = "config_data"
    val BOOKER = "booker"

    val USER = "user"

    //통합 게시판에대한 true/false
    var isCommon: Boolean = false

    var FIREBASE_TABLE_NAME = ""
    var FIREBASE_COM_TABLE_NAME = "common"

    private lateinit var db: FirebaseFirestore
    private lateinit var collectRef: DocumentReference
    private lateinit var comCollectRef: DocumentReference

    fun set(table:String) {
        FIREBASE_TABLE_NAME = table
        db = FirebaseFirestore.getInstance()
        collectRef = db.collection(FIREBASE_TABLE_NAME).document(FIREBASE_TABLE_NAME)
        comCollectRef = db.collection(FIREBASE_COM_TABLE_NAME).document(FIREBASE_COM_TABLE_NAME)
    }

    @SuppressLint("LongLogTag")
    override fun <T> write(
        collectName: String,
        dataModel: T,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {
        if (dataModel == null || collectName.isBlank()) return

        var docRef: DocumentReference =
            if (isCommon) comCollectRef else collectRef
        docRef.collection(collectName).document()
            .set(dataModel)
            .addOnSuccessListener {
                log.e(
                    TAG,
                    "$TAG  [ write > addonsuccesslistener > $collectName ]" + "addOnSuccessListener !! : $dataModel"
                )
                onFirestoreComplete.onSuccess(true)
            }
            .addOnFailureListener { exception ->
                onFirestoreComplete.onSuccess(false)
                error(exception)
            }
    }

    @SuppressLint("LongLogTag")
    fun readRealtime(tableName: String, onFirestoreComplete: OnSuccessListener<QuerySnapshot>) {

        var logStr: String = """        """.trimIndent()
        var docRef: DocumentReference =
            if (isCommon) comCollectRef else collectRef
        docRef.collection(tableName).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                log.e("Current data: ${snapshot.documents}")
                logStr += " - querySnapshot.size() : ${snapshot.size()}\n"
                for (document in snapshot) {
                    logStr += "  ${document.id} => ${document.data} \n"
                }
                log.d("$TAG [ READ > ADD_ON_SUCCESS_LISTENER !! > $tableName ]", logStr)
                onFirestoreComplete.onSuccess(snapshot)
            } else {
                log.d( "Current data: null")
            }
        }
    }

    @SuppressLint("LongLogTag")
    override fun read(tableName: String, onFirestoreComplete: OnSuccessListener<QuerySnapshot>) {


        var logStr: String = """        """.trimIndent()
        var docRef: DocumentReference =
            if (isCommon) comCollectRef else collectRef

        docRef.collection(tableName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                logStr += " - querySnapshot.size() : ${querySnapshot.size()}\n"
                for (document in querySnapshot) {
                    logStr += "  ${document.id} => ${document.data} \n"
                }
                log.d("$TAG [ READ > ADD_ON_SUCCESS_LISTENER !! > ${docRef.path + "/" + tableName} ]", logStr)
                onFirestoreComplete.onSuccess(querySnapshot)
            }
            .addOnFailureListener { exception ->
                error(exception)
            }
    }

    override fun delete(
        tableName: String,
        uid: String,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {
        var docRef: DocumentReference =
            if (isCommon) comCollectRef else collectRef
        docRef.collection(tableName)
            .document(uid)
            .delete()
            .addOnSuccessListener {
                onFirestoreComplete.onSuccess(true)
            }
            .addOnFailureListener { exception ->
                onFirestoreComplete.onSuccess(false)
                error(exception)
            }
    }

    /**
     * 수정은 맵형태로 받는다.
     */
    @SuppressLint("LongLogTag")
    fun modify(
        collectName: String, dataModel: java.util.HashMap<String, Any>,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {
        val map = dataModel


        var docRef: DocumentReference =
            if (isCommon) comCollectRef else collectRef

        val userRef = docRef.collection(collectName)
            .document((map["uid"] as String?)!!)


        userRef.update(map as Map<String, Any>)
            .addOnCompleteListener { task ->
                onFirestoreComplete.onSuccess(true)
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    @SuppressLint("LongLogTag")
    override fun error(exception: Exception) {
        log.e(TAG, exception.message)
    }

    /**
     * userModel 에 해당 하는 관리자의 프로필 사진을 서버 스토리지에 저장
     *
     * @param userModel
     * @param bitmap
     * @param onCompleteListener
     */
    fun setFirestoreUpload(
        type: String,
        uid: String,
        bitmap: Bitmap?,
        onFirestoreComplete: OnSuccessListener<Uri>
    ) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        var table = if (isCommon) FIREBASE_COM_TABLE_NAME else FIREBASE_TABLE_NAME

        FirebaseStorage.getInstance()
            .reference
            .child(table)
            .child(type)
            .child(uid)
            .putBytes(data)
            .addOnCompleteListener { task ->
                task.result!!.storage
                    .downloadUrl.addOnSuccessListener { uri ->
                    onFirestoreComplete.onSuccess(uri)
                }
            }
    }

    /**
     * userModel 에 해당 하는 관리자의 프로필 사진을 서버 스토리지에서 삭제
     *
     * @param userModel
     * @param bitmap
     * @param onCompleteListener
     */
    @SuppressLint("LongLogTag")
    fun setFirestoreDelete(
        type: String,
        uid: String,
        onFirestoreComplete: OnSuccessListener<Boolean>
    ) {

        try {
            var table = if (isCommon) FIREBASE_COM_TABLE_NAME else FIREBASE_TABLE_NAME

            FirebaseStorage.getInstance()
                .reference
                .child(table)
                .child(type)
                .child(uid)
                .delete()
                .addOnSuccessListener {
                    onFirestoreComplete.onSuccess(true)
                }.addOnFailureListener {
                    onFirestoreComplete.onSuccess(false)
                }
        } catch (exception: Exception) {
            log.e(TAG, exception.toString())
        }
    }


    enum class FIRESTORE_TYPE {
        TIME_LINE_POST
    }

}