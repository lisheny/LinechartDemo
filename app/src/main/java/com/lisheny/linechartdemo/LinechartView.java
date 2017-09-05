package com.lisheny.linechartdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * <pre>
 *     author : lisheny
 *     e-mail : 1020044519@qq.com
 *     time   : 2017/09/01
 *     desc   : 折线图绘制
 *     version: 1.0
 * </pre>
 */
public class LinechartView extends View {
    private Context mContext;
    //默认值
    private float mWidth = 560;
    private float mHeigt = 3 * mWidth / 5;
    private Paint mPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private Paint rectanglePaint;
    private Paint dotsPaint;
    private Paint lineChartPaint;

    private int myBg = 0xFFF9974E;
    private int linechartColor = 0xFF90E7FD;
    private int linechartBg = 0xFFFFFFFF;
    private int linechartSize = 3;
    //小圆半径
    private float radius = 4;
    //X轴区间间隔
    private float xDistance;
    //Y轴区间间隔
    private float yDistance;
    //边距
    private int padding = 16;
    //X轴坐标数据
    private int[] xText = {125, 250, 500, 1000, 2000, 4000, 8000};
    //Y轴坐标数据
    private int[] yText = {-20, 0, 20, 40, 60, 80, 100};
    //文字大小
    private float testSize = 12;
    //test 测试数据
    private float[] mData = {-20, 40, 20, 85, 97, 65, 75};

    public LinechartView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public LinechartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LinechartView);
        myBg = array.getColor(R.styleable.LinechartView_my_bg, 0xFFF7F7F7);  //默认值必须
        linechartColor = array.getColor(R.styleable.LinechartView_linechart_color,0xFF90E7FD);
        linechartSize = (int) array.getDimension(R.styleable.LinechartView_linechart_size,3);
        linechartBg = array.getColor(R.styleable.LinechartView_linechart_bg,0xFFFFFFFF);
        padding = (int) array.getDimension(R.styleable.LinechartView_linechart_padding,16);

        init();
        array.recycle(); //取完值，记得回收
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(myBg);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(sp2px(testSize));

        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(0xFFF0F0F0);

        rectanglePaint = new Paint();
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setAntiAlias(true);
        rectanglePaint.setColor(linechartBg);

        dotsPaint = new Paint();
        dotsPaint.setStyle(Paint.Style.FILL);
        dotsPaint.setAntiAlias(true);
        dotsPaint.setColor(0xFF90E7FD);

        lineChartPaint = new Paint();
        lineChartPaint.setStyle(Paint.Style.FILL);
        lineChartPaint.setAntiAlias(true);
        lineChartPaint.setColor(linechartColor);
        lineChartPaint.setStrokeWidth(dp2px(linechartSize));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        switch (mode) {
            case MeasureSpec.AT_MOST:                             //(wrap_content)
                mWidth = MeasureSpec.getSize(widthMeasureSpec);
                mHeigt = 3 * mWidth / 5;
                break;
            case MeasureSpec.EXACTLY:                             //固定尺寸（如100dp）
                mWidth = MeasureSpec.getSize(widthMeasureSpec);
                mHeigt = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.UNSPECIFIED:                         //比重 (layout_weight="1")
                mWidth = MeasureSpec.getSize(widthMeasureSpec);
                mHeigt = 3 * mWidth / 5;
                break;
        }

        //X轴区间间隔
        xDistance = (mWidth - 4 * dp2px(padding)) / (xText.length - 1);
        //Y轴区间间隔
        yDistance = (mHeigt - 2 * dp2px(padding)) / (yText.length - 1);
        setMeasuredDimension((int) mWidth, (int) mHeigt);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制画布颜色
        canvas.drawColor(myBg);

        drawTextAndGrid(canvas, textPaint, gridPaint);
        drawLineChart(canvas);
        drawDots(canvas);
    }

    /**
     * test 矩形
     *
     * @param canvas
     */
    private void drawRectangle(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeigt, mPaint);
    }

    /**
     * 绘文字和网格
     *
     * @param canvas
     */
    private void drawTextAndGrid(Canvas canvas, Paint textPaint, Paint gridPaint) {
        //白色背景矩形
        canvas.drawRect(2 * dp2px(padding),
                dp2px(padding) + dp2px(5),
                mWidth - 2 * dp2px(padding),
                (dp2px(5) + dp2px(padding) + (yText.length - 1) * yDistance),
                rectanglePaint);


        //X轴文字
        for (int i = 0; i < xText.length; i++) {
            if (i == 0) {
                canvas.drawText(
                        String.valueOf(xText[i]) + " hz",
                        (2 * dp2px(padding) + i * xDistance),
                        dp2px(padding),
                        textPaint);
            } else {
                canvas.drawText(
                        String.valueOf(xText[i]),
                        (2 * dp2px(padding) + i * xDistance),
                        dp2px(padding),
                        textPaint);
            }

            //网格绘制
            canvas.drawLine(
                    (2 * dp2px(padding) + i * xDistance),
                    dp2px(padding) + dp2px(5),
                    (2 * dp2px(padding) + i * xDistance),
                    (mHeigt - dp2px(padding) + dp2px(5)),
                    gridPaint);
        }

        //Y轴左边文字绘制
        for (int i = 0; i < yText.length; i++) {
            canvas.drawText(
                    String.valueOf(yText[i]),
                    dp2px(padding),
                    (dp2px(10) + dp2px(padding) + i * yDistance),
                    textPaint);
            canvas.drawText(
                    String.valueOf(yText[i]),
                    (mWidth - dp2px(padding)),
                    (dp2px(10) + dp2px(padding) + i * yDistance),
                    textPaint);

            //网格绘制
            canvas.drawLine(
                    2 * dp2px(padding),
                    (dp2px(5) + dp2px(padding) + i * yDistance),
                    mWidth - 2 * dp2px(padding),
                    (dp2px(5) + dp2px(padding) + i * yDistance),
                    gridPaint);
        }

    }

    /**
     * 绘制折线图
     *
     * @param canvas
     */
    private void drawLineChart(Canvas canvas) {
        if (mData.length > xText.length) {
            Log.e(getClass().getSimpleName(), "填入数据与坐标设置长度不符");
            return;
        }

        for (int i = 0; i < mData.length; i++) {
            if ((i + 1) == mData.length) {
                return;
            }
            canvas.drawLine(
                    (2 * dp2px(padding) + i * xDistance),
                    getYcoordinate(mData[i]),
                    (2 * dp2px(padding) + (i + 1) * xDistance),
                    getYcoordinate(mData[i + 1]),
                    lineChartPaint
            );
        }
    }

    /**
     * 绘制小圆点
     *
     * @param canvas
     */
    private void drawDots(Canvas canvas) {
        if (mData.length > xText.length) {
            Log.e(getClass().getSimpleName(), "填入数据与坐标设置长度不符");
            return;
        }

        for (int i = 0; i < mData.length; i++) {
            dotsPaint.setColor(0xFF90E7FD);
            canvas.drawCircle(
                    (2 * dp2px(padding) + i * xDistance),
                    getYcoordinate(mData[i]),
                    dp2px(radius),
                    dotsPaint
            );
            dotsPaint.setColor(0xFFFFFFFF);
            canvas.drawCircle(
                    (2 * dp2px(padding) + i * xDistance),
                    getYcoordinate(mData[i]),
                    dp2px(radius - 1),
                    dotsPaint
            );
        }

    }

    private int dp2px(float dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据输入数据值的到对应Y轴坐标
     *
     * 这里需求：Y轴坐标增长 随屏幕之上而下增加  注:变量尽量用浮点型，避免精度丢失
     * 公式：Y = Y轴开始值 + 每份坐标刻度所占Y轴长度的量{Y轴长度/总刻度} * 当前刻度值{value-min}
     * 即：Y = Y轴开始值{ dp2px(padding) + dp2px(5)} + 每份坐标刻度所占Y轴长度的量{(yDistance * (yText.length - 1)) / (yText[yText.length-1]-yText[0])} * 当前刻度值{(value - yText[0])}
     *
     * @param value
     * @return
     */
    private float getYcoordinate(float value) {
        return dp2px(padding) + dp2px(5) + (yDistance * (yText.length - 1)) / (yText[yText.length - 1] - yText[0]) * (value - yText[0]);
    }

    /**
     * 设置X轴坐标
     * @param xText
     */
    public void setxText(int[] xText) {
        this.xText = xText;
    }

    /**
     * 设置Y轴坐标
     * @param yText
     */
    public void setyText(int[] yText) {
        this.yText = yText;
    }

    /**
     * 载入数据
     * @param mData
     */
    public void setmData(float[] mData) {
        this.mData = mData;
    }

}
