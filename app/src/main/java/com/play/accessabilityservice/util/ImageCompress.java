package com.play.accessabilityservice.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageCompress {

    /**
     * 设置水印图片在左上角
     *
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark, dp2px(paddingLeft), dp2px(paddingTop));
    }

    /**
     * 设置水印图片在右下角
     *
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskRightBottom(Bitmap src, Bitmap watermark, int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, src.getWidth() - watermark.getWidth() - dp2px(paddingRight), src.getHeight() - watermark.getHeight() - dp2px(paddingBottom));
    }

    /**
     * 设置水印图片到右上角
     *
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskRightTop(Bitmap src, Bitmap watermark, int paddingRight, int paddingTop) {
        return createWaterMaskBitmap(src, watermark, src.getWidth() - watermark.getWidth() - dp2px(paddingRight), dp2px(paddingTop));
    }

    /**
     * 设置水印图片到左下角
     *
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskLeftBottom(Bitmap src, Bitmap watermark, int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, dp2px(paddingLeft), src.getHeight() - watermark.getHeight() - dp2px(paddingBottom));
    }

    /**
     * 设置水印图片到中间
     *
     * @param src
     * @param watermark
     * @return
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }

    /**
     * 将View转成Bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap viewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * dp转pix
     *
     * @param dp
     * @return
     */
    public static int dp2px(float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}