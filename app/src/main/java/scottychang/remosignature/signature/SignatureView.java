package scottychang.remosignature.signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class SignatureView extends View {

    private static final String TAG = SignatureView.class.getSimpleName();

    //定義繪圖的基本參數：線的width, color；是否擷取狀態；簽名圖示bitmap
    private float mSignatureWidth = 8f; 
    private int mSignatureColor = Color.BLACK;
    private boolean mSignaturing = true;
    private Bitmap mSignature = null; 
 
    //定義防止線條有鋸齒的常數
    private static final boolean GESTURE_RENDERING_ANTIALIAS = true; 
    private static final boolean DITHER_FLAG = true; 
 
    private Paint mPaint = new Paint(); 
    private Path mPath = new Path(); 
    
    //矩形
    private final Rect mInvalidRect = new Rect(); 
 
    private float mX; 
    private float mY; 
 
    private float mCurveEndX; 
    private float mCurveEndY; 
 
    private int mInvalidateExtraBorder = 10;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public SignatureView(Context context, AttributeSet attrs, int defStyle) { 
        super(context, attrs, defStyle);

        init(context);
    } 
 
    public SignatureView(Context context) { 
        super(context); 
        init(context);
    } 
 
    public SignatureView(Context context, AttributeSet attrs) { 
        super(context, attrs);    
        init(context);
    } 

    private void init(Context context) {
        setWillNotDraw(false);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
        mPaint.setColor(mSignatureColor); 
        mPaint.setStyle(Paint.Style.STROKE); 
        mPaint.setStrokeJoin(Paint.Join.ROUND); 
        mPaint.setStrokeCap(Paint.Cap.ROUND); 
        mPaint.setStrokeWidth(mSignatureWidth); 
        mPaint.setDither(DITHER_FLAG); 
        mPath.reset(); 
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);

        Log.d("DEBUG", "X: " + mPosX + " Y: " + mPosY);

        if (mSignature != null) {
            w = mSignature.getWidth();
            h = mSignature.getHeight();
            Log.d(TAG, "??" + w+ " " + h);
            canvas.drawBitmap(mSignature, null, new Rect(0, 0, (int)w, (int)h), null);
        }
        canvas.drawPath(mPath, mPaint);


        canvas.restore();

    } 

    @Override 
    public boolean dispatchTouchEvent(MotionEvent event) { 
        if (mSignaturing) {
            processSignatureEvent(event);
            Log.d(VIEW_LOG_TAG, "dispatchTouchEvent"); 
            return true; 
        } else {
            boolean retVal = mScaleDetector.onTouchEvent(event);
            retVal = processDragEvent(event) || retVal;
            return retVal || super.onTouchEvent(event);
        }
    }
    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private boolean processDragEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(mActivePointerId);
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    //mPosX + dx > -getWidth();

                    mPosX = Math.max(mPosX + dx, getWidth()-w * mScaleFactor);
                    mPosY = Math.max(mPosY + dy, getHeight()-h * mScaleFactor);

                    if (mPosX > 0) mPosX = 0;
                    if (mPosY > 0) mPosY = 0;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = event.getX(newPointerIndex);
                    mLastTouchY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;

    }

    private boolean processSignatureEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                touchDown(event);
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");
                Rect rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

            case MotionEvent.ACTION_UP:
                touchUp(event, false);
                invalidate();
                return true;

            case MotionEvent.ACTION_CANCEL:
                touchUp(event, true);
                invalidate();
                return true;

        }

        return false;
    } 
 
    private void touchUp(MotionEvent event, boolean b) { 
        // TODO Auto-generated method stub 
    } 
 
    private Rect touchMove(MotionEvent event) { 
        Rect areaToRefresh = null; 
 
        final float x = event.getX();
        final float y = event.getY();
 
        final float previousX = mX; 
        final float previousY = mY; 
 
        areaToRefresh = mInvalidRect;
 
        // start with the curve end 
        final int border = mInvalidateExtraBorder; 
        areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border, 
                (int) mCurveEndX + border, (int) mCurveEndY + border); 
 
        float cX = mCurveEndX = (x + previousX) / 2; 
        float cY = mCurveEndY = (y + previousY) / 2; 
 
        mPath.quadTo((previousX - mPosX)/mScaleFactor,
                     (previousY - mPosY)/mScaleFactor,
                     (cX - mPosX)/mScaleFactor,
                     (cY - mPosY)/mScaleFactor);
 
        // union with the control point of the new curve 
        areaToRefresh.union((int) previousX - border, (int) previousY - border, 
                (int) previousX + border, (int) previousY + border); 
 
        // union with the end point of the new curve 
        areaToRefresh.union((int) cX - border, (int) cY - border, (int) cX 
                + border, (int) cY + border); 
 
        mX = x; 
        mY = y; 
 
        return areaToRefresh; 
 
    } 
 
    private void touchDown(MotionEvent event) { 
        float x = event.getX();
        float y = event.getY();
 
        mX = x; 
        mY = y; 
        mPath.moveTo((x - mPosX)/mScaleFactor, (y - mPosY)/mScaleFactor);
 
        final int border = mInvalidateExtraBorder; 
        mInvalidRect.set((int) x - border, (int) y - border, (int) x + border, 
                (int) y + border); 
 
        mCurveEndX = x;
        mCurveEndY = y;
    }  
 
    /** 
     * Erases the signature. 
     */ 
    public void clear() { 
        mSignature = null; 
        mPath.rewind(); 
        // Repaints the entire view. 
        invalidate(); 
    } 
 
    public boolean isSignaturing() {
        return mSignaturing;
    } 
 
    public void setIsSignaturing(boolean signaturing) {
        this.mSignaturing = signaturing;
    } 
 
    public void setSignatureBitmap(Bitmap signature) { 
        mSignature = signature; 
        invalidate(); 
    } 

    float w, h;

    public Bitmap getSignatureBitmap() { 
        if (mSignature != null) {
            return mSignature;
        } else if (mPath.isEmpty()) { 
            return null; 
        } else { 
            mSignature = Bitmap.createBitmap(getWidth(), getHeight(),
            Bitmap.Config.ARGB_8888); 
            Canvas c = new Canvas(mSignature);
            c.drawPath(mPath, mPaint); 
            return mSignature;
        } 
    } 
 
    public void setSignatureWidth(float width) { 
        mSignatureWidth = width; 
        mPaint.setStrokeWidth(mSignatureWidth); 
        invalidate(); 
    } 
 
    public float getSignatureWidth(){ 
        return mPaint.getStrokeWidth(); 
    } 
 
    public void setSignatureColor(int color) { 
        mSignatureColor = color; 
    } 
 
    /** 
     * @return the byte array representing the signature as a PNG file format 
     */ 
    public byte[] getSignaturePNG() { 
        return getSignatureBytes(CompressFormat.PNG, 0); 
    } 
 
    /** 
     * @param quality Hint to the compressor, 0-100. 0 meaning compress for small 
     *            size, 100 meaning compress for max quality. 
     * @return the byte array representing the signature as a JPEG file format 
     */ 
    public byte[] getSignatureJPEG(int quality) { 
        return getSignatureBytes(CompressFormat.JPEG, quality); 
    } 
 
    private byte[] getSignatureBytes(CompressFormat format, int quality) { 
        Log.d(TAG, "getSignatureBytes() path is empty: " + mPath.isEmpty());
        Bitmap bmp = getSignatureBitmap(); 
        if (bmp == null) { 
            return null; 
        } else { 
            ByteArrayOutputStream stream = new ByteArrayOutputStream(); 
 
            getSignatureBitmap().compress(format, quality, stream);

            Bitmap b = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);

            return stream.toByteArray(); 
        } 
    }

    //================================================================================
    // Scale
    //================================================================================

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(.3f, Math.min(mScaleFactor, 3.0f));

            Log.d(TAG, "Scale factor:" + mScaleFactor);
            invalidate();
            return true;
        }
    }

} 