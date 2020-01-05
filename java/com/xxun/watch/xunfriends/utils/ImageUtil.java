package com.xxun.watch.xunfriends.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageUtil {

    public static Bitmap getMaskBitmap(Bitmap mask, Bitmap original) {

        original = Bitmap.createScaledBitmap(original, mask.getWidth(), mask.getHeight(), true);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    public static Bitmap getMaskBitmap(Bitmap mask, Drawable drawable) {

        BitmapDrawable bb = (BitmapDrawable) drawable;
        Bitmap original = bb.getBitmap();
        return getMaskBitmap(mask, original);
    }

    public static void setMaskImage(ImageView view, int maskResId, Drawable drawable) {

        try {
            Bitmap mask = BitmapFactory.decodeResource(view.getResources(), maskResId);
            view.setImageBitmap(ImageUtil.getMaskBitmap(mask, drawable));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } catch (Exception e) {
        }
    }
}
