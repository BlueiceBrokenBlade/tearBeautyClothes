package com.imooc.tearbeautifulclothes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xhx on 2017-05-28.
 */

public class TearClothView extends View {
    private Paint mOutterPaint;//橡皮擦画笔
    private Path mPath;//绘制的路径
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;

    private MediaPlayer mp=MediaPlayer.create(getContext(),R.raw.si);//用于播放撕衣音效

    private Drawable downDrawable;
    private Drawable upDraable;
    private Bitmap downBitmap;//底层图片
    private Bitmap upBitmap;//覆盖图片

    private Paint mBackPaint;

    private volatile boolean mComplete=false;//擦除百分比达到阈值代表是否擦除完成
    private OnScratchCompleteListener mListener;

    /**
     * 返回一个接口，让用户决定擦除完毕显示操作
     */
    public interface OnScratchCompleteListener{
        void complete();
    }


    public void setOnScratchCompleteListener(OnScratchCompleteListener mListener){
        this.mListener=mListener;
    }

    public TearClothView(Context context) {
        this(context,null);
    }

    public TearClothView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TearClothView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        if(isInEditMode()){
            return ;
        }
        //自定义属性
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.TearClothView,defStyleAttr,0);
        int n=a.getIndexCount();
        for(int i=0;i<n;i++){
            int attr=a.getIndex(i);
            switch (attr){
                case R.styleable.TearClothView_srcdown:
                    downDrawable=a.getDrawable(attr);
//                    srcDow=a.getInteger(attr,0);
                case R.styleable.TearClothView_srcup:
                    upDraable=a.getDrawable(attr);

                    break;
            }
        }
        a.recycle();


        init();
    }

    public void setDownPicture(Integer drawable){
//        this.downBitmap=((BitmapDrawable)drawable).getBitmap();
        this.downBitmap=BitmapFactory.decodeResource(getResources(),drawable);
    }

    public void setUpPicture(Integer drawable){
        this.upBitmap=BitmapFactory.decodeResource(getResources(),drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width=getMeasuredWidth();
        int height=getMeasuredHeight();

        //初始化Bitmap
        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);//装载画布

        //设置绘制path画笔属性
        setUpOutPaint();

        //设置一个覆盖图层
        mCanvas.drawBitmap(upBitmap, 0, 0, null);

        Log.e("TAG","onMeasure");
    }

    /**
     * 橡皮擦画笔属性设置
     */
    private void setUpOutPaint() {
        mOutterPaint.setColor(Color.RED);
        mOutterPaint.setAntiAlias(true);//抗锯齿功能
        mOutterPaint.setDither(true);//抖动
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);//结合处样子 圆弧
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND);//画笔转弯风格
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setStrokeWidth(60);
    }

    /**
     * 初始化操作
     */
    public void init(){
        mOutterPaint=new Paint();
        mPath=new Path();

        //底层图片bitmap
        downBitmap=((BitmapDrawable)downDrawable).getBitmap();
        //覆盖图片bitmap
//        bitmapUp=BitmapFactory.decodeResource(getResources(),R.drawable.img1);
        upBitmap=((BitmapDrawable)upDraable).getBitmap();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();

        int x= (int) event.getX();
        int y= (int) event.getY();

        if(!mComplete) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = x;
                    mLastY = y;
                    mPath.moveTo(mLastX, mLastY);//初始轮廓点

                    break;
                case MotionEvent.ACTION_MOVE:
                    mp.start();
                    int dx = Math.abs(x - mLastX);
                    int dy = Math.abs(y - mLastY);

                    if (dx > 3 || dy > 3) {
                        mPath.lineTo(x, y);//初始点到（x,y）点的路径
                    }

                    mLastX = x;
                    mLastY = y;

                    break;
                case MotionEvent.ACTION_UP:
                    new Thread(mRunnable).start();
                    break;
                default:
                    break;
            }

            invalidate();//刷新view
        }
        return true;
    }

    //新启一个线程计算像素面积百分比
    private Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            int w=getWidth();
            int h=getHeight();

            float wipeArea=0;//擦去像素
            float totalArea=w*h;//总像素

            int[] mPixels=new int[w*h];

            //bitmap获得其像素方法参数：存储像素点信息数组，偏移量，步长（多少个像素换行）,起始X,起始Y，截取的宽度，截取的高度
            mBitmap.getPixels(mPixels,0,w,0,0,w,h);


            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    int index=i*w+j;
                    if(mPixels[index]==0){
                        wipeArea++;
                    }
                }
            }

            if(wipeArea>0&&totalArea>0){
                int percent=(int)(wipeArea*100/totalArea);
                Log.e("TAG",percent+"");
                if(percent>40){
                    mp.release();
                    mComplete=true;
                    postInvalidate();
                }
            }

        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制最底层图片
        canvas.drawBitmap(downBitmap,0,0,null);

        if(mComplete){
            if(mListener!=null){
                mListener.complete();
            }
        }

        if(!mComplete){
            drawPath();
            canvas.drawBitmap(mBitmap,0,0,null);
        }
        Log.e("TAG","ondraw");
    }

    private void drawPath(){
        //图层相交时显示模式
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mCanvas.drawPath(mPath,mOutterPaint);
    }

}
