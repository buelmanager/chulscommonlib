package com.buel.sknmethodist.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class IntroPageActivity : AppCompatActivity() {

    var isClickAble:Boolean = false
    private var webviewUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /*PageDataManager().start(OnSuccessListener {
            when (it) {
                PAGE_MANAGER_STATUS.COMPLETE -> {

                    val docShots: List<DocumentSnapshot> = PageData.admin_data as List<DocumentSnapshot>

                    //TODO 관리자인지 판단한다.
                    for (doc: DocumentSnapshot in docShots) {
                        if (AuthManager.firebaseAuth?.currentUser?.email == doc.data?.get("email")) {
                            AuthManager.level = AuthManager.USER_LEVEL.SYSTEM_LEVEL
                            UL.a("MENU_DRAWER_NEWS_PAGER", "this user is system level")
                        }
                    }

                    intro_ang_btn2.visibility = View.VISIBLE
                    isClickAble = true
                    //hideProgressDialog()

                    val device_version:Int = getInteger(getPackageManager().getPackageInfo(getPackageName(), 0).versionName)!!

                    val configs: List<DocumentSnapshot> = PageData.config_data as List<DocumentSnapshot>
                    var app_ver:Int = -1
                    var app_upgrade_url = ""

                    //TODO 앱업데이트 확인
                    for (doc: DocumentSnapshot in configs) {
                        app_ver = getInteger(doc.data?.get("app_ver") as String)!!
                        app_upgrade_url = doc.data?.get("app_upgrade_url") as String
                    }

                    UL.a("Intro" , device_version , app_ver)
                    if (device_version < app_ver) {

                        MaterialDailogUtil.Companion.simpleDoneDialog(
                                this,
                                "업그레이드가 필요합니다.",
                                "확인버튼을 클릭하여 업그레이드를 실행하여주세요. \n업데이트가 되지 않으면 삭제후 다시 설치해주세요.",
                                object : MaterialDailogUtil.OnDialogSelectListner {

                                    override fun onSelect(s: String) {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(app_upgrade_url)
                                        startActivity(intent)
                                        finish()
                                    }
                                })

                    }
                }
                PAGE_MANAGER_STATUS.START -> {
                    intro_ang_btn2.visibility = View.INVISIBLE
                    //showProgressDialog()
                }
            }
        })*/
    }
}