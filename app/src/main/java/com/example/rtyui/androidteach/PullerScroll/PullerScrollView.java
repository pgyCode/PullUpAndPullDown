package com.example.rtyui.androidteach.PullerScroll;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by rtyui on 2018/4/28.
 * 效果：ScrollView头部底部弹性
 * tip0:弹性尺寸为 move/2
 * tip1:可以双指同时拉动 这时候执行的事件 只有 一次down 一次up
 * tip2:可以在动画回弹时继续拉动
 * tip3:可以在滑动屏幕的过程中不用抬起直接拉动
 */

public class PullerScrollView extends ScrollView {
    private float lastPosition;//上次位置
    private float move;//移动距离

    private ValueAnimator anim;//动画

    private View inner;//内部唯一孩子
    private Rect normal;//用于存储开始滑动时的位置

    private boolean animStop = true;//动画是不是没有处于运行 true:没有运行 false:运行
    private boolean isScrolledToTop = true;//是不是到达顶部
    private boolean isScrolledToBottom = false;//是不是到达底部
    private float mPrevX;//记录开始位置，处理viewpager

    public PullerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //完成布局时
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inner = getChildAt(0);
        normal = new Rect();
        //去掉顶部底部阴影效果
        setHorizontalFadingEdgeEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    //处理touch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!animStop){
                    lastPosition = ev.getY();
                    animStop = true;
                }else if (isScrolledToTop || isScrolledToBottom){
                    if (lastPosition != 0){
                        float temp = ev.getY() - lastPosition;
                        if (temp > -60 && temp < 60) {
                            move += temp;
                        }
                        lastPosition = ev.getY();
                        if ((move > 0 && isScrolledToTop) || (move < 0 && isScrolledToBottom)) {
                                setLayout(move);
                        }else{
                            setLayout(0);
                            move = 0;
                            lastPosition = 0;
                        }
                    }else{
                        lastPosition = ev.getY();
                        normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                        animStop = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                replyView(move, 0);
                animStop = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    //设置scrollView中唯一孩子的位置
    private void setLayout(float move){
        inner.layout(normal.left, normal.top + (int) calculateHeight(move), normal.right, normal.bottom + (int) calculateHeight(move));
    }

    /**
     * 回弹
     * @param distance 距离
     * @param origin 终点
     */
    private void replyView(final float distance, final int origin) {
        // 设置动画
        anim = ObjectAnimator.ofFloat(distance - origin, 0.0F).setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!animStop) {
                    if (distance > 0) {
                        move = (float) animation.getAnimatedValue();
                        setLayout(move);
                    } else {
                        move = (float) animation.getAnimatedValue();
                        setLayout(move);
                    }
                }
            }
        });
        anim.start();
    }


    //重写，获取是否处于顶部或者底部
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY == 0) {
            isScrolledToTop = clampedY;
            isScrolledToBottom = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = clampedY;
        }
    }

    //处理Viewpager
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    System.out.println(event.getX() - mPrevX);
                    if (Math.abs(event.getX() - mPrevX) > 60) {
                        return false;
                    }
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("scrollView - dispatch");
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 计算滑动高度与实际要移动的高度的关系
     * @param move 滑动高度
     * @return
     */
    private float calculateHeight(float move){
        return move / 2;
    }
}
