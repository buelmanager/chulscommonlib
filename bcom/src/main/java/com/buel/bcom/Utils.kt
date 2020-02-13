package com.buel.bcom

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and

object Utils {


    @SuppressLint("NewApi")
    fun setBackColor(
        context: Context,
        target: ImageView,
        color: Int
    ) {
        target.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(color))
    }

    @SuppressLint("NewApi")
    fun setBackColor(
        context: Context,
        target: Button,
        color: Int
    ) {
        target.backgroundTintList = ColorStateList.valueOf(context.resources.getColor(color))
    }

    fun setBackColor(
        context: Context,
        fabtn: FloatingActionButton,
        color: Int
    ) {
        fabtn.setBackgroundTintList(ColorStateList.valueOf(context.resources.getColor(color)))
    }

    fun getRandomMaterialColor(): Int? {
        var colorNum = 0
        val colorList: MutableList<Int> = ArrayList()
        colorList.add(R.color.material_500_amber)
        colorList.add(R.color.material_500_blue)
        colorList.add(R.color.material_500_cyan)
        colorList.add(R.color.material_500_deep_purple)
        colorList.add(R.color.material_500_indigo)
        colorList.add(R.color.material_500_light_blue)
        colorList.add(R.color.material_500_lime)
        colorList.add(R.color.material_500_pink)
        colorList.add(R.color.material_500_red)
        colorList.add(R.color.material_500_purple)
        val randNum =
            getRandom(colorList.size - 1.toFloat(), 0f) as Int
        colorNum = colorList[randNum]
        return colorNum
    }

    fun getRandom(range: Float, startsfrom: Float): Float {
        return (Math.random() * range).toFloat() + startsfrom
    }
    var VORDIPLOM_COLORS = intArrayOf(
        Color.rgb(140, 234, 255),
        Color.rgb(255, 140, 157),
        Color.rgb(192, 255, 140),
        Color.rgb(255, 247, 140),
        Color.rgb(255, 208, 140)
    )
    var VORDIPLOM_RED = intArrayOf(
        Color.rgb(255, 140, 157)
    )
    var VORDIPLOM_BLUE = intArrayOf(
        Color.rgb(140, 234, 255)
    )

    fun trim(str: String): String {
        return str.replace("\\p{Z}".toRegex(), "")
    }

    /**
     * removeAllFragments
     *
     * @param fragmentManager
     */
    fun removeAllFragments(fragmentManager: FragmentManager) { //Here we are clearing back stack fragment entries
        val backStackEntry = fragmentManager.backStackEntryCount
        if (backStackEntry > 0) {
            for (i in 0 until backStackEntry) {
                fragmentManager.popBackStackImmediate()
            }
        }
        //Here we are removing all the fragment that are shown here
        if (fragmentManager.fragments != null && fragmentManager.fragments.size > 0) {
            for (i in fragmentManager.fragments.indices) {
                val mFragment =
                    fragmentManager.fragments[i]
                if (mFragment != null) {
                    fragmentManager.beginTransaction().remove(mFragment).commit()
                    // this will clear the back stack and displays no animation on the screen
                    fragmentManager.popBackStackImmediate(
                        mFragment.tag,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }

    /**
     * 이메일 포맷 체크
     *
     * @param email
     * @return
     */
    fun checkEmail(email: String?): Boolean {
        val regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(email)
        return m.matches()
    }

    fun addZero(integer: Int): String {
        val tempNum: String
        tempNum = if (integer >= 10) integer.toString() else "0$integer"
        return tempNum
    }

    fun currentTimestamp(): String { /*Date and Time Pattern	          Result
        "yyyy.MM.dd G 'at' HH:mm:ss z"	  2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy"	              Wed, Jul 4, '01
        "h:mm a"	                      12:08 PM
        "hh 'o''clock' a, zzzz"	          12 o'clock PM, Pacific Daylight Time
        "K:mm a, z"	                      0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa"	  02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z"	  Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ"	                  010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"	  2001-07-04T12:08:56.235-0700*/
        val today = Calendar.getInstance().time
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddTHHmmss");
        val formatter =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(today)
    }

    /**
     * 현재의 view를 리턴
     *
     * @param context
     * @return
     */
    fun getRootView(context: Context): View {
        return (context as Activity).window.decorView
    }

    /**
     * ViewPager의 현재 fragment를 리턴
     *
     * @param pager
     * @param adapter
     * @return
     */
    fun getCurrentFragment(
        pager: ViewPager,
        adapter: FragmentPagerAdapter
    ): Fragment? {
        try {
            val m = adapter
                .javaClass
                .superclass
                ?.getDeclaredMethod(
                    "makeFragmentName", Int::class.javaPrimitiveType,
                    Long::class.javaPrimitiveType
                )
            val f = adapter.javaClass.superclass
                ?.getDeclaredField("mFragmentManager")
            f?.isAccessible = true
            val fm =
                f?.get(adapter) as FragmentManager
            m?.isAccessible = true
            var tag: String? = null
            tag = m?.invoke(
                null, pager.id,
                pager.currentItem.toLong()
            ) as String
            return fm.findFragmentByTag(tag)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri!!, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        val width = options.outWidth
        val height = options.outHeight
        val sampleRatio = getSampleRatio(width, height)
        options.inJustDecodeBounds = false
        options.inSampleSize = sampleRatio.toInt()
        val resizedBitmap =
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
        parcelFileDescriptor.close()
        return resizedBitmap
    }

    //handle exception
    val macAdress: String
        get() {
            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = nif.hardwareAddress ?: return ""
                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(Integer.toHexString((b and 0xFF.toByte()).toInt()) + ":")
                    }
                    if (res1.length > 0) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) { //handle exception
            }
            return ""
        }

    fun sendDirectCall(
        pNum: String,
        context: Activity
    ) { // 사용자의 OS 버전이 마시멜로우 이상인지 체크한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 확인한다.
             * Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다.
             */
            val permissionResult =
                context.checkSelfPermission(Manifest.permission.CALL_PHONE)
            /**
             * 패키지는 안드로이드 어플리케이션의 아이디이다.
             * 현재 어플리케이션이 CALL_PHONE에 대해 거부되어있는지 확인한다.
             */
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                if (context.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    val dialog =
                        AlertDialog.Builder(context)
                    dialog.setTitle("권한이 필요합니다.")
                        .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                        .setPositiveButton(
                            "네"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // CALL_PHONE 권한을 Android OS에 요청한다.
                                context.requestPermissions(
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    1000
                                )
                            }
                        }
                        .setNegativeButton(
                            "아니요"
                        ) { dialog, which ->
                            Toast.makeText(
                                context,
                                "기능을 취소했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .create().show()
                } else { // CALL_PHONE 권한을 Android OS에 요청한다.
                    context.requestPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        1000
                    )
                }
            } else { // 즉시 실행
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$pNum"))
                context.startActivity(intent)
            }
        } else { // 즉시 실행
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$pNum"))
            context.startActivity(intent)
        }
    }

    fun hideKeyboard(activity: Activity) {
        val view = getRootView(activity)
        if (view != null) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun gone(vararg views: View) {
        for (view in views) {
            view.gone()
        }
    }

    fun dpFromPx(context: Context, px: Float): Float {
        return px / context.getResources().getDisplayMetrics().density
    }



    fun getSampleRatio(width: Int, height: Int): Float {
        val targetWidth = 1280
        val targetHeight = 1280

        val ratio: Float

        if (width > height) {
            //landscape
            if (width > targetWidth) {
                ratio = width.toFloat() / targetWidth.toFloat()
            } else {
                ratio = 1f
            }
        } else {
            //portarit
            if (height > targetHeight) {
                ratio = height.toFloat() / targetHeight.toFloat()
            } else {
                ratio = 1f
            }
        }
        return Math.round(ratio).toFloat()
    }

    fun setLanguage( context: Context, lang:String){
        val locale = Locale(lang)
        var config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, null)
    }
}

fun Context.getDrawbleId(name:String):Int{
    return resources.getIdentifier(name,"drawable",packageName)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msgRes: Int) {
    Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show()
}

fun Context.getInflateView(layoutId: Int): View {
    val mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return mInflater.inflate(layoutId, null)
}

fun Context.getWindowManager(): WindowManager {
    return getSystemService(Context.WINDOW_SERVICE) as WindowManager
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}




/**
 * 디버깅용
 */
fun Bundle.printTostring(): String {
    var str = ""
    for (key in this.keySet()) {
        str += (key + "=" + this.get(key).toString()) + "," //To Implement
    }
    return str
}

fun Bundle.checkNullParam(vararg params: String?):Boolean {
    for (param in params) {
        if (getString(param).isNullOrEmpty()) {
            Log.e(TAG ,"require invalid >> param : $param is null")
            return false
        }
    }
    return true
}

fun Bundle.put(key: String, value: String): Bundle {
    putString(key, value)
    return this
}

fun Bundle.put(key: String, value: Int): Bundle {
    putInt(key, value)
    return this
}

val TAG: String? = "BUEL_UTILS"
val df: DecimalFormat = DecimalFormat("#.#")

fun Float.formatF(): String {
    return df.format(this)
}

fun androidx.recyclerview.widget.RecyclerView.linearHorizon(pCon: Context) {
    setHasFixedSize(true)
    itemAnimator = null
    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(pCon, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
}

fun androidx.recyclerview.widget.RecyclerView.grid(pCon: Context, spanCount: Int) {
    setHasFixedSize(true)
    itemAnimator = null
    layoutManager = androidx.recyclerview.widget.GridLayoutManager(pCon, spanCount)
}

fun androidx.recyclerview.widget.RecyclerView.setItemDecoration(itemDeco: androidx.recyclerview.widget.RecyclerView.ItemDecoration) {
    removeItemDecoration(itemDeco)
    addItemDecoration(itemDeco)
}

fun List<String>.randomOrIndex(index: Int): String {
    if (index == 0)
        return random()
    return getOrElse(index - 1) { "" }
}

fun getAppVersion(context: Context): String {
    // application version
    var versionName = ""
    try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = info.versionName
    } catch (e: Exception) {
        Log.e("aimanager", e.toString())
    }
    return versionName
}

/**
 * 문자열의 숫자를 뽑아 리턴
 *
 * @param str 숫자가 뒤에 들어가도록...
 * @return 리턴되는 숫자
 */
fun getInteger(str: String): Int? {
    var tempStr = ""
    try {
        val tempNum = str.toInt()
        if (tempNum >= 0) {
            return tempNum
        }
    } catch (e: java.lang.Exception) {

    }
    //charAt를 이용하여 숫자가 아니면 넘기는 식으로 해서 뽑아 낼 수 있다.
    for (i in 0 until str.length) { // 48 ~ 57은 아스키 코드로 0~9이다.
        if (48 <= str[i].toInt() && str[i].toInt() <= 57) tempStr += str[i]
    }
    return if (str.length == 0) {
        0
    } else Integer.valueOf(tempStr)
}

fun Context.getPackage():String
{
    return applicationContext.packageName
}

interface onStatus {
    fun onComplete()
}

