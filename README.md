> 自定义折线图的需求在 APP 开发中好像很常见了，至少我碰到了两三次这样类似的需求。关于自定义 View 相关的一些知识总是看了又忘，忘了又看，寻思着还是记下来好一点，恩，烂笔头！下面上效果图：

![效果图.png](http://upload-images.jianshu.io/upload_images/2591553-a8e42fa8149c7954.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 我的自定义 view  
*  **1.新建 `Zhexiantu` 继承之 `View` ，重写下面两个构造函数,并在里面做一些初始化操作。**
```
public Zhexiantu(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

 public Zhexiantu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Zhexiantu);
        myBg = array.getColor(R.styleable.Zhexiantu_my_bg, 0xFFF7F7F7);  //默认值必须
        linechartColor = array.getColor(R.styleable.Zhexiantu_linechart_color,0xFF90E7FD);
        linechartSize = (int) array.getDimension(R.styleable.Zhexiantu_linechart_size,3);
        linechartBg = array.getColor(R.styleable.Zhexiantu_linechart_bg,0xFFFFFFFF);
        padding = (int) array.getDimension(R.styleable.Zhexiantu_linechart_padding,16);

        init();
        array.recycle(); //取完值，记得回收
    }
```

第二个构造函数中相比第一个构造函数多了一些自定义属性取值的操作，自定义属性需要在 `res/values` 文件夹下的 `styles.xml` 文件下定义属性，eg:
```
<!--自定义属性-->
    <declare-styleable name="Zhexiantu">
        <attr name="my_bg" format="color"/>
        <attr name="linechart_color" format="color"/>
        <attr name="linechart_size" format="dimension"/>
        <attr name="linechart_bg" format="color"/>
        <attr name="linechart_padding" format="dimension"/>
    </declare-styleable>
```
以上两步都完成就可以在我们自己的自定义控件上使用自定义属性了，eg:
```
  <com.lisheny.lenovo.myviewstudy.canvas.Zhexiantu
            android:id="@+id/zhexiantu"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:linechart_color="@color/color2"
            app:linechart_bg="@color/color13"/>
```
另外，附上 `Paint` 属性设置的详解 [http://wuxiaolong.me/2016/08/20/Paint/](http://wuxiaolong.me/2016/08/20/Paint/ "http://wuxiaolong.me/2016/08/20/Paint/")
*  **2.重写 `onMeasure` 方法，计算获取画布的宽高并计算尺 X、Y轴的区间间隔**
```
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
```
通过 `int mode = MeasureSpec.getMode(heightMeasureSpec);` 获取测量模式，根据不同的测量模式将宽高设置为合理的相应值。关于这三种测量模式的介绍：

1. `UNSPECIFIED` ==》父容器没有对当前View有任何限制，当前View可以任意取尺寸  
2. `EXACTLY`   ==》当前的尺寸就是当前View应该取的尺寸
3. `AT_MOST` ==》当前尺寸是当前View能取的最大尺寸

理解否？不理解没事，我们知道什么情况下调用什么模式就行：

1. xml 中 设置 `wrap_content` 时，会走 `AT_MOST` 模式；
2. xml 中 设置 固定尺寸（如100dp） 时，会走 `EXACTLY` 模式；
3. 这种使用比较少，xml 中 设置 比重 ` layout_weight="1"` 时，会走 `EXACTLY` 模式；

注：`onMeasure` 函数会多次调用

* **3.接下来就是重写 `onDraw` 函数绘制 View**

```
@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制画布颜色
        canvas.drawColor(myBg);

        drawTextAndGrid(canvas, textPaint, gridPaint);
        drawLineChart(canvas);
        drawDots(canvas);
    }
```

绘制步骤：
1. 绘制画布颜色，背景
2. 绘制X、Y轴的坐标值和表格
3. 绘制折线
4. 绘制小圆点
5. 完成！！！

**特别的：** 计算Y轴的刻度
```
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
```
其中：总刻度 = 最大Y刻度值（max） - 最小Y刻度值(min)

* **最后：折线图的绘制总的来说很简单，主要点是载入数据的刻度值要和Y轴相对应，理解计算出Y轴坐标的公式，折线图的绘制就简单了。**
