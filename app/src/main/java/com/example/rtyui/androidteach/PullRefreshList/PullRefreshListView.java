package com.example.rtyui.androidteach.PullRefreshList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rtyui.androidteach.R;


/**
 * Created by rtyui on 2018/4/26.
 */

public class PullRefreshListView extends ListView {

    private View header;
    private float move = 0;
    private float lastPosition = 0;

    private int HEADER_HEIGHT;
    private int CURRENT_HEIGHT;

    private ImageView imgHead = null;
    private TextView tipHead = null;

    private boolean can = true;

    private Animation animation = null;

    private PullRefreshInterface pullRefreshInterface;
    private float mPrevX;
    private AnimationDrawable animationDrawable;

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        initHead(context);
        setPadding(getPaddingLeft(), getPaddingTop() - 1, getPaddingRight(), getPaddingBottom());
        addHeaderView(header, null, false);
        setHeaderHeight(1);
    }

    public void setPullRefreshInterface(PullRefreshInterface pullRefreshInterface){
        this.pullRefreshInterface = pullRefreshInterface;
    }

    private void setHeaderHeight(int px){
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        layoutParams.height = px;
        header.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (can && this.getFirstVisiblePosition() == 0 ){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastPosition = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (lastPosition != 0){
                        float temp = ev.getY() - lastPosition;
                        if (temp > -60 && temp < 60) {
                            move += ev.getY() - lastPosition;
                        }
                        lastPosition = ev.getY();
                        if (move > 0) {
                            CURRENT_HEIGHT = (int) (Math.sqrt(move) / 0.065);
                            setHeaderHeight(CURRENT_HEIGHT);
                            if (CURRENT_HEIGHT < HEADER_HEIGHT)
                                headStatu0();
                            else
                                headStatu1();
                        }else{
                            setHeaderHeight(1);
                            move = 0;
                            lastPosition = 0;
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (move != 0) {
                        can = false;
                        if (CURRENT_HEIGHT > HEADER_HEIGHT) {
                            replyView(CURRENT_HEIGHT, HEADER_HEIGHT);
                        } else {
                            replyView(CURRENT_HEIGHT, 1);
                        }
                        move = 0;
                        lastPosition = 0;
                    }
                    break;
            }
        }
        System.out.println(getFirstVisiblePosition());
        return super.onTouchEvent(ev);
    }

    /**
     * dp转为px
     * @param dp dp值
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 回弹
     * @param distance 距离
     * @param origin 终点
     */
    private void replyView(final float distance, final int origin) {
        int length = (int) (distance - origin);
        long time = length > 0 ? length * 2 : -length * 2;
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(distance - origin, 0.0F).setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CURRENT_HEIGHT = (int) (origin + (Float) animation.getAnimatedValue());
                setHeaderHeight(CURRENT_HEIGHT);
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if (CURRENT_HEIGHT == HEADER_HEIGHT){
                    new MyAsyncTask().execute();
                    headStatu2();
                }
                else if(CURRENT_HEIGHT == 1)
                    can = true;
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anim.start();
    }

    private class MyAsyncTask extends AsyncTask<Void, Boolean, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = pullRefreshInterface.load_PullRefresh();
            publishProgress(result);
            if (result)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            return result;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if (animationDrawable != null)
                animationDrawable.stop();
            if (values[0]){
                headStatu3();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pullRefreshInterface.beforeLoad_PullRefresh();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            replyView(HEADER_HEIGHT, 1);
            pullRefreshInterface.afterLoad_PullRefresh(aBoolean);
        }
    }

    //下拉刷新
    private void headStatu0(){
        imgHead.setImageResource(R.drawable.down);
        tipHead.setText("下拉刷新");
    }

    //释放立即刷新
    private void headStatu1(){
        imgHead.setImageResource(R.drawable.up);
        tipHead.setText("释放立即刷新");
    }

    //正在刷新
    private void headStatu2(){
        imgHead.setImageResource(R.drawable.ss);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgHead.getDrawable();
        animationDrawable.start();
        tipHead.setText("正在刷新...");
    }

    //刷新成功
    private void headStatu3(){
        imgHead.setImageResource(R.drawable.succeed);
        tipHead.setText("刷新成功");
    }

    //初始化头部
    private void initHead(Context context){
        header = LayoutInflater.from(context).inflate(R.layout.pull_refresh_header, this, false);//我的头部
        imgHead = header.findViewById(R.id.img);//我的头部图标
        tipHead = header.findViewById(R.id.tip);//我的头部提示
        HEADER_HEIGHT = dp2px(100);//我的头部高度 100dp
        setBackgroundColor(getResources().getColor(R.color.blackB));//为了让背景颜色和我头部一样
    }

    //Viewpager
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (can && getFirstVisiblePosition() == 0){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevX = event.getX();
                    lastPosition = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    System.out.println(event.getX() - mPrevX);
                    // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                    if (Math.abs(event.getX() - mPrevX) > 60) {
                        return false;
                    }
            }
        }
        return super.onInterceptTouchEvent(event);
    }
}
