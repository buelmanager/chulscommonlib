package com.buel.sknmethodist.manager.firebase

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
import java.io.ByteArrayOutputStream

object FireStoreManager : IFirebase {

    //--------------------------------------------------------------------------------------------
    val TAG = "FIREBASE_FIRESTOREMANAGER"
    val ADMIN = "admin"
    val PAGE1 = "page1"
    val PAGE2 = "page2"
    val PAGE3 = "page3"
    val PAGE4 = "page4"
    val PAGE5 = "page5"
    val PAGE6 = "page6"
    val PAGE7 = "page7"
    val PAGE8 = "page8"

    val INFO = "info_page"
    val MAIN_PAGER = "main_pager"
    val CONFIG_DATA = "config_data"

    val USER = "user"

    //--------------------------------------------------------------------------------------------
    const val FIREBASE_TABLE_NAME = "view1"
    const val PARENT_TABLE_UID = FIREBASE_TABLE_NAME

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionReference: DocumentReference = db.collection(FIREBASE_TABLE_NAME).document(PARENT_TABLE_UID)

    @SuppressLint("LongLogTag")
    override fun <T> write(collectName: String, dataModel: T, onFirestoreComplete: OnSuccessListener<Boolean>) {
        if (dataModel == null || collectName.isBlank()) return

        collectionReference.collection(collectName).document()
                .set(dataModel)
                .addOnSuccessListener {
                    Log.e(TAG ,"$TAG  [ write > addonsuccesslistener > $collectName ]" +  "addOnSuccessListener !! : $dataModel")
                    onFirestoreComplete.onSuccess(true)
                }
                .addOnFailureListener { exception ->
                    onFirestoreComplete.onSuccess(false)
                    error(exception)
                }
    }

    @SuppressLint("LongLogTag")
    override fun read(tableName: String, onFirestoreComplete: OnSuccessListener<QuerySnapshot>) {

        var logStr: String = """        """.trimIndent()

        collectionReference.collection(tableName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    logStr += " - querySnapshot.size() : ${querySnapshot.size()} \n    │\n    │"
                    for (document in querySnapshot) {
                        logStr += "  ${document.id} => ${document.data} \n    │"
                    }
                    Log.e(TAG," [ READ > ADD_ON_SUCCESS_LISTENER !! > $tableName ]" +  logStr)
                    onFirestoreComplete.onSuccess(querySnapshot)
                }
                .addOnFailureListener { exception ->
                    //onFirestoreComplete.onSuccess(null)
                    error(exception)
                }
    }

    override fun delete(tableName: String, uid: String, onFirestoreComplete: OnSuccessListener<Boolean>) {
        collectionReference.collection(tableName)
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
    fun modify(collectName: String ,dataModel: java.util.HashMap<String, Any>,
               onFirestoreComplete: OnSuccessListener<Boolean>) {
        val map = dataModel

        val userRef = collectionReference.collection(collectName)
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
        Log.e(TAG, exception.message)
    }

    /**
     * 좋아요.
     */
    /*fun transaction(collectName: String, dataModel: CardTimeLineModel, onFirestoreComplete: OnSuccessListener<Boolean>) {
        if (dataModel == null || collectName.isBlank()) return

        val dataDocRef = collectionReference.collection(collectName).document(dataModel.uid!!)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(dataDocRef)

            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            val newPopulation = snapshot.getDouble("population")!! + 1
            transaction.update(dataDocRef, "population", newPopulation)

            val uid = snapshot.getString("uid")!!
            var starCnt = snapshot.getDouble("star_count")!!.toInt()
            var starMap: MutableMap<String, Boolean> = snapshot.getDouble("stars")!! as MutableMap<String, Boolean>

            val compare = starMap.any { uid.equals(it.value) }

            if (compare) {
                // Unstar the post and remove self from stars
                starCnt = starCnt - 1
                starMap.remove(uid)
            } else {
                // Star the post and add self to stars
                starCnt = starCnt + 1
                starMap[uid] = true
            }

            transaction.update(dataDocRef, "star_count", starCnt)
            transaction.update(dataDocRef, "stars", starMap)

            // Success
            null
        }.addOnSuccessListener { UL.d(TAG, "Transaction success!") }
                .addOnFailureListener { e -> UL.d(TAG, "Transaction failure.", e) }
    }*/

    /**
     * userModel 에 해당 하는 관리자의 프로필 사진을 서버 스토리지에 저장
     *
     * @param userModel
     * @param bitmap
     * @param onCompleteListener
     */
    fun setFirestoreUpload(type: String, uid: String, bitmap: Bitmap?, onFirestoreComplete: OnSuccessListener<Uri>) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        FirebaseStorage.getInstance()
                .reference
                .child(FIREBASE_TABLE_NAME)
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
    fun setFirestoreDelete(type: String, uid: String, onFirestoreComplete: OnSuccessListener<Boolean>) {

        try {
            FirebaseStorage.getInstance()
                    .reference
                    .child(FIREBASE_TABLE_NAME)
                    .child(type)
                    .child(uid)
                    .delete()
                    .addOnSuccessListener {
                        onFirestoreComplete.onSuccess(true)
                    }.addOnFailureListener {
                        onFirestoreComplete.onSuccess(false)
                    }
        }catch ( exception:Exception){
           Log.e(TAG, exception.toString())
        }
    }



    enum class FIRESTORE_TYPE {
        TIME_LINE_POST
    }

}