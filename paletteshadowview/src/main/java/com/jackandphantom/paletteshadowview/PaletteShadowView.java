package com.jackandphantom.paletteshadowview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class PaletteShadowView extends AppCompatImageView {

    private Paint paint = new Paint();
    private Bitmap mBitmap , realBitmap;
    private int minimumWidth = 400;
    private Paint shadowPaint = new Paint();
    private RectF shadowRect;
    private int DEFAULT_PADDING = 40;
    private Palette mPalette;
    private int shadowColor = -1;
    private AsyncTask mAsyncTask;
    private static final int DEFAULT_OFFSET = 5;
    private static final int DEFAULT_SHADOW_RADIUS = 20;
    private int shadowRadius;
    private static final int MSG = 0x101;
    private int offsetX , offsetY;
    private ShadowHandler mShadowHandler;
    private int roundedRadius = 0;
    private Drawable initialDrawable;

    public PaletteShadowView(Context context) {
        super(context);
    }

    public PaletteShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PaletteShadowView);
        initialDrawable = a.getDrawable(R.styleable.PaletteShadowView_paletteSrc);
        shadowRadius = a.getInt(R.styleable.PaletteShadowView_paletteShadowRadius, shadowRadius);
        shadowColor = a.getColor(R.styleable.PaletteShadowView_paletteShadowColor, -1);
        offsetX = a.getInt(R.styleable.PaletteShadowView_paletteOffsetX, DEFAULT_OFFSET);
        offsetY = a.getInt(R.styleable.PaletteShadowView_paletteOffsetY, DEFAULT_OFFSET);
        roundedRadius = a.getInt(R.styleable.PaletteShadowView_paletteRoundRadius, 0);
        a.recycle();
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setDither(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setDither(true);
        shadowPaint.setShadowLayer(shadowRadius,DEFAULT_OFFSET,DEFAULT_OFFSET, Color.LTGRAY);
        mShadowHandler = new ShadowHandler(PaletteShadowView.this);
        if (initialDrawable != null) {
            getDrawableToBitmap(initialDrawable);
            if (shadowColor != -1){
             mShadowHandler.sendEmptyMessage(MSG);
            }
            else
            initShadowColor(mBitmap);
        }
    }

    /*
      Measure the view with minimum area so when we use wrap content it will use minimum width value
      as a width and height of the image
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = minimumWidth + getPaddingLeft() + getPaddingRight();
        int desiredHeight = minimumWidth + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));

    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            //Match parent
            result = specSize;
        }
        else if (specMode == MeasureSpec.UNSPECIFIED) {
            if (realBitmap != null) {
                result = specSize;
            }
        }
        else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                //wrap content
                result = minimumWidth;
            }
        }

        if (result < desiredSize){
            result = desiredSize;
        }
        return result;
    }

    /*
     * Create the rectangle used for making the shadow and also initialize the shadow and round bitmap
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        shadowRect = new RectF(DEFAULT_PADDING, DEFAULT_PADDING , getWidth() - DEFAULT_PADDING, getHeight() - DEFAULT_PADDING);
        if (realBitmap == null) {
            realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
            initShadowColor(mBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (realBitmap != null) {
            canvas.drawRoundRect(shadowRect, roundedRadius,roundedRadius, shadowPaint);
            canvas.drawBitmap(realBitmap, 0, 0, null);
        }
        if (shadowColor != -1)
            mAsyncTask.cancel(true);

    }
    //Few ways to render the image on the view
    @Override
    public void setImageResource(int resId) {
        try {
            mBitmap = BitmapFactory.decodeResource(getResources(), resId);
            realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
            initShadowColor(mBitmap);
        }catch (NullPointerException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        try {
            getDrawableToBitmap(drawable);
            realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
            initShadowColor(mBitmap);
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        try {
            mBitmap = bm;
            realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
            initShadowColor(bm);
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
            initShadowColor(mBitmap);
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    private void getDrawableToBitmap(Drawable drawable) {

        //return if drawable is null that means it doen't have a bitmap
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);
        mBitmap = bitmap;
    }
    /*
    * Create the round bitmap depends on the given radius, if radius higher then corner of the view will
    * form the round shape
    */
    private Bitmap createRoundedBitmap (Bitmap bitmap , int radius) {
        if (getWidth() <= 0 || getHeight() <= 0 )
            return null;
        Bitmap targetBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, getWidth() - (DEFAULT_PADDING * 2), getHeight() - (DEFAULT_PADDING * 2), false);
        Canvas canvas = new Canvas(targetBitmap);
        RectF rectF = new RectF(DEFAULT_PADDING, DEFAULT_PADDING, getWidth() - (DEFAULT_PADDING),
                getHeight() - (DEFAULT_PADDING));

        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode( PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(smallBitmap, DEFAULT_PADDING, DEFAULT_PADDING,paint);
        paint.setXfermode(null);
        return targetBitmap;

    }

    Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(@Nullable Palette palette) {
            if (palette != null) {
                mPalette = palette;
                if (mPalette.getDominantSwatch() != null) {
                    shadowColor = mPalette.getDominantSwatch().getRgb();
                    mShadowHandler.sendEmptyMessage(MSG);
                }
            }
        }
    };

    private void initShadowColor(Bitmap bitmap) {
        if (bitmap != null)
            mAsyncTask = Palette.from(bitmap).generate(paletteAsyncListener);
    }
    //Getter setter method for controlling the different variable in order to change properties like radius ,
    // shadow etc.
    public void setShadowRadius(int radius) {
        shadowRadius = radius;
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setShadowColor(int color) {
        this.shadowColor = color;
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setShadowOffest(int offsetX , int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public int getShadowColor() {
        return this.shadowColor;
    }

    public int getShadowRadius() {
        return this.shadowRadius;
    }

    public void setRoundedRadius(int radius) {
        if (mBitmap == null) return;
        this.roundedRadius = radius;
        realBitmap = createRoundedBitmap(mBitmap, roundedRadius);
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public int getRoundedRadius() {
        return roundedRadius;
    }

    public int[] getVibrantColor() {
        if (mPalette == null || mPalette.getVibrantSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getVibrantSwatch().getTitleTextColor();
        arry[1] = mPalette.getVibrantSwatch().getBodyTextColor();
        arry[2] = mPalette.getVibrantSwatch().getRgb();
        return arry;
    }

    public int[] getDarkVibrantColor() {
        if (mPalette == null || mPalette.getDarkVibrantSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getDarkVibrantSwatch().getTitleTextColor();
        arry[1] = mPalette.getDarkVibrantSwatch().getBodyTextColor();
        arry[2] = mPalette.getDarkVibrantSwatch().getRgb();
        return arry;
    }

    public int[] getLightVibrantColor() {
        if (mPalette == null || mPalette.getLightVibrantSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getLightVibrantSwatch().getTitleTextColor();
        arry[1] = mPalette.getLightVibrantSwatch().getBodyTextColor();
        arry[2] = mPalette.getLightVibrantSwatch().getRgb();
        return arry;
    }

    public int[] getMutedColor() {
        if (mPalette == null || mPalette.getMutedSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getMutedSwatch().getTitleTextColor();
        arry[1] = mPalette.getMutedSwatch().getBodyTextColor();
        arry[2] = mPalette.getMutedSwatch().getRgb();
        return arry;
    }

    public int[] getDarkMutedColor() {
        if (mPalette == null || mPalette.getDarkMutedSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getDarkMutedSwatch().getTitleTextColor();
        arry[1] = mPalette.getDarkMutedSwatch().getBodyTextColor();
        arry[2] = mPalette.getDarkMutedSwatch().getRgb();
        return arry;
    }

    public int[] getLightMutedColor() {
        if (mPalette == null || mPalette.getLightMutedSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getLightMutedSwatch().getTitleTextColor();
        arry[1] = mPalette.getLightMutedSwatch().getBodyTextColor();
        arry[2] = mPalette.getLightMutedSwatch().getRgb();
        return arry;
    }

    public int[] getDominantColor() {
        if (mPalette == null || mPalette.getDominantSwatch() == null) return null;
        int[] arry = new int[3];
        arry[0] = mPalette.getDominantSwatch().getTitleTextColor();
        arry[1] = mPalette.getDominantSwatch().getBodyTextColor();
        arry[2] = mPalette.getDominantSwatch().getRgb();
        return arry;
    }

    public void setLightMutedColor() {
        if (mPalette == null || mPalette.getLightMutedSwatch() == null) return ;
        shadowColor = mPalette.getLightMutedSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setDarkMutedColor() {
        if (mPalette == null || mPalette.getDarkMutedSwatch() == null) return ;

        shadowColor = mPalette.getDarkMutedSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setMutedColor() {
        if (mPalette == null || mPalette.getMutedSwatch() == null) return ;
        shadowColor = mPalette.getMutedSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setLightVibrantColor() {
        if (mPalette == null || mPalette.getLightVibrantSwatch() == null) return ;
        shadowColor = mPalette.getLightVibrantSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setDarkVibrantColor() {
        if (mPalette == null || mPalette.getDarkVibrantSwatch() == null) return;
        shadowColor = mPalette.getDarkVibrantSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setVibrantColor() {
        if (mPalette == null || mPalette.getMutedSwatch() == null) return;
        shadowColor = mPalette.getMutedSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    public void setDominantColor() {
        if (mPalette == null || mPalette.getDominantSwatch() == null) return;
        shadowColor = mPalette.getDominantSwatch().getRgb();
        mShadowHandler.sendEmptyMessage(MSG);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mShadowHandler.removeCallbacksAndMessages(null);
    }

    private static class ShadowHandler extends Handler {
        private final WeakReference<PaletteShadowView> weakReference;

        private ShadowHandler(PaletteShadowView pletteImage) {
            this.weakReference = new WeakReference<>(pletteImage);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() != null) {
                PaletteShadowView pletteImage = weakReference.get();
                if (pletteImage.offsetX < DEFAULT_OFFSET) pletteImage.offsetX = DEFAULT_OFFSET;
                if (pletteImage.offsetY < DEFAULT_OFFSET) pletteImage.offsetY = DEFAULT_OFFSET;
                if (pletteImage.shadowRadius < DEFAULT_SHADOW_RADIUS) pletteImage.shadowRadius = DEFAULT_SHADOW_RADIUS;
                pletteImage.shadowPaint.setShadowLayer(pletteImage.shadowRadius,pletteImage.offsetX,pletteImage.offsetY, pletteImage.shadowColor);
                pletteImage.invalidate();
            }
        }
    }

}
