package com.buel.bcom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

/**
 * Created by blue7 on 2018-05-12.
 */

public class SToastUtil {

    /**
     * 토스트에 색상이 필요할 경우
     * @param context
     * @param msg
     */
    public static void toastE(Context context , String msg){

        SuperActivityToast.create((Activity)context , new Style() , Style.TYPE_STANDARD)
                .setProgressBarColor(Color.WHITE)
                .setText(msg)
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK))
                .setAnimations(Style.ANIMATIONS_POP)
                .show();
    }

    public static void toastE(Context context , int res){
        SuperActivityToast.create((Activity)context , new Style() , Style.TYPE_STANDARD)
                .setProgressBarColor(Color.WHITE)
                .setText(context.getString(res))
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PINK))
                .setAnimations(Style.ANIMATIONS_POP)
                .show();
    }

    /**
     * 단순하게 토스트만 호출하는경우
     * @param context
     * @param msg
     */

    public static void toast(Context context , String msg){
       /* SuperActivityToast.create(context , new Style() , Style.TYPE_STANDARD)
                .setProgressBarColor(Color.WHITE)
                .setText(msg)
                .setDuration(Style.DURATION_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                .setAnimations(Style.ANIMATIONS_POP)
                .show();*/
    }
}
