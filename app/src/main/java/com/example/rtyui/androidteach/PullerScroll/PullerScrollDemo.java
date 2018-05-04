package com.example.rtyui.androidteach.PullerScroll;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.rtyui.androidteach.R;

public class PullerScrollDemo extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puller_pullerscrollview_demo);

        LinearLayout linearLayout = (LinearLayout) ((ScrollView)findViewById(R.id.lst)).getChildAt(0);
        final View view = LayoutInflater.from(this).inflate(R.layout.item, null, false);
        final View view1 = LayoutInflater.from(this).inflate(R.layout.item, null, false);
        ViewPager viewPager = (ViewPager) LayoutInflater.from(this).inflate(R.layout.viewpager, linearLayout, false);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (position == 0){
                    if (view.getParent() != container)
                        container.addView(view);
                    return view;
                }else{
                    if (view1.getParent() != container)
                        container.addView(view1);
                    return view1;
                }
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                if (position == 0){
                    if (view.getParent() == container)
                        container.removeView(view);
                }else{
                    if (view1.getParent() == container)
                        container.removeView(view1);
                }
            }
        });
        linearLayout.addView(viewPager, 0);
    }
}
