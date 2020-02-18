package tech.zettervall.notes.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public abstract class DensityUtil {

    /**
     * Convert from Density Independent Pixels to Pixels.
     */
    public static int convertDpToPx(Context context, int dp){
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
