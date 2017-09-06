package avater.avaterprojects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 作者:Avater
 * 日期： 2017-09-06.
 * 说明：
 */

public class TimeLineView extends View {

    private float mWidth;
    private float mHeight;
    private float mCycleRadiusSize = 30;
    private int mCycleRadiusColor = Color.RED;
    private Paint mPaint;
    private Paint mCirclePaint;
    private boolean isFirst = true;
    private boolean isLast = false;

    public boolean isLast() {
        return isLast;
    }

    public void setMiddle() {
        this.isFirst = false;
        this.isLast = false;
        invalidate();
    }

    public void setLast(boolean last) {
        isLast = last;
        this.isFirst = !isLast;
        invalidate();
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
        this.isLast = !isFirst;
        invalidate();
    }

    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCycleRadiusColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width_mode = MeasureSpec.getMode(widthMeasureSpec);
        int width_size = MeasureSpec.getSize(widthMeasureSpec);
        int height_mode = MeasureSpec.getMode(heightMeasureSpec);
        int height_size = MeasureSpec.getSize(heightMeasureSpec);
        if (width_mode == MeasureSpec.EXACTLY) {
            mWidth = width_size;
        } else {
            try {
                throw new Exception("请之指明宽度");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (height_mode == MeasureSpec.EXACTLY) {
            mHeight = height_size;
        } else {
            try {
                throw new Exception("请之指明高度");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG", "mWidth == " + mWidth + ", mHeight == " + mHeight);
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.e("TAG", " w == " + w + ", h == " + h + ", oldw == " + oldw + ", pldh == " + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLineFirst(canvas);
        drawCycleSecond(canvas);
    }

    private void drawCycleSecond(Canvas canvas) {
        canvas.drawCircle(mWidth / 2, mHeight / 2, mCycleRadiusSize, mCirclePaint);
    }

    private void drawLineFirst(Canvas canvas) {
        if (isFirst) {
            canvas.drawLine(mWidth / 2, mHeight / 2 - mCycleRadiusSize, mWidth / 2, mHeight, mPaint);
        } else if (isLast) {
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight / 2, mPaint);
        } else
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mPaint);
    }
}
