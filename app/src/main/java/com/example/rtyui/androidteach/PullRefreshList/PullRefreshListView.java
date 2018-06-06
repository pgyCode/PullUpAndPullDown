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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rtyui.androidteach.R;

/**
 * Created by rtyui on 2018/5/3.
 */

public class PullRefreshListView extends ListView {

    private final int HEADER_DP = 60;

    private View header;
    private float currentHeight = 0;
    private float lastPosition = 0;
    private PullRefreshInterface pullRefreshInterface;
    private boolean can = false;


    private ImageView imgHead;
    private TextView tipHead;

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHead(context);
        addHeaderView(header, null, false);
        setHeaderHeight();
        setVerticalScrollBarEnabled(false);
        setPadding(getPaddingLeft(), getPaddingTop() -1,  getPaddingRight(), getPaddingBottom());
    }

    public void setPullRefreshInterface(PullRefreshInterface pullRefreshInterface) {
        this.pullRefreshInterface = pullRefreshInterface;
        can = true;
    }

    //处理touch事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (currentHeight == 0)
            setSelector(R.drawable.item);
        else
            setSelector(R.drawable.item_out);
        if (can){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    System.out.println(getFirstVisiblePosition() + " " + currentHeight + " " + lastPosition);
                    if (getFirstVisiblePosition() == 0) {
                        if (lastPosition != 0) {
                            float temp = ev.getY() - lastPosition;
                            if (temp > -60 && temp < 60) {
                                currentHeight += temp > 0 ? calculateHeight(temp) : temp;
                            }
                            lastPosition = ev.getY();
                            if (currentHeight > 0) {
                                setHeaderHeight();
                                if (currentHeight > dp2px(HEADER_DP))
                                    headStatu1();
                                else
                                    headStatu0();
                                return true;
                            } else {
                                currentHeight = 0;
                                setHeaderHeight();
                            }
                        } else {
                            lastPosition = ev.getY();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    lastPosition = 0;
                    if (currentHeight != 0){
                        if (currentHeight > dp2px(HEADER_DP)){
                            new MyAsyncTask().execute();
                            headStatu2();
                            can = false;
                        }
                        else if (currentHeight > 0){
                            replyView(currentHeight, 0);
                        }else{
                            currentHeight = 0;
                            setHeaderHeight();
                        }
                        return true;
                    }
                    break;
            }
        }
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

    /*
     * 回弹
     * @param distance 距离
     * @param origin 终点
     */
    private void replyView(final float distance, final int origin) {
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(distance - origin, 0.0F).setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentHeight = (int) (origin + (Float) animation.getAnimatedValue());
                System.out.println(currentHeight + "w wewe ");
                setHeaderHeight();
                if (currentHeight == 0)
                    can = true;
            }
        });
        anim.start();
    }

    //初始化头部
    private void initHead(Context context){
        header = LayoutInflater.from(context).inflate(R.layout.pull_refresh_header, this, false);//我的头部
        imgHead = header.findViewById(R.id.img);//我的头部图标
        tipHead = header.findViewById(R.id.tip);//我的头部提示
    }

    private void setHeaderHeight(){
        ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        if (currentHeight <= 1)
            layoutParams.height = 1;
        else
            layoutParams.height = (int) currentHeight;
        header.setLayoutParams(layoutParams);
    }

    /**
     * 计算滑动高度与实际要移动的高度的关系
     * @param move 滑动高度
     * @return
     */
    private float calculateHeight(float move){
        return move / 2;
    }


    private class MyAsyncTask extends AsyncTask<Void, Boolean, Boolean> {

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
            if (values[0]){
                headStatu3();
            }else{
                replyView(currentHeight, 0);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            replyView(currentHeight, dp2px(HEADER_DP));
            pullRefreshInterface.beforeLoad_PullRefresh();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            replyView(currentHeight, 0);
            pullRefreshInterface.afterLoad_PullRefresh(aBoolean);
        }
    }
}
