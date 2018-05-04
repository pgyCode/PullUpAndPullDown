package com.example.rtyui.androidteach.PullRefreshList;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.rtyui.androidteach.R;

import java.util.Random;

public class PullerRefreshListDemo extends Activity{


    private PullRefreshListView pullRefreshListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puller_pullrefreshlistview_demo);

        pullRefreshListView = findViewById(R.id.lst);

        pullRefreshListView.setAdapter(new MyAdapter());

        pullRefreshListView.setPullRefreshInterface(new PullRefreshInterface() {
            @Override
            public void beforeLoad_PullRefresh() {
                Toast.makeText(PullerRefreshListDemo.this, "开始之前", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean load_PullRefresh() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random random = new Random();
                return random.nextBoolean();
            }

            @Override
            public void afterLoad_PullRefresh(boolean result) {
                if (result)
                    Toast.makeText(PullerRefreshListDemo.this, "加载成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(PullerRefreshListDemo.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
        initViewpager();
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(PullerRefreshListDemo.this).inflate(R.layout.item, pullRefreshListView, false);
            view.setBackgroundColor(255 * 256 * 256 * 256 + i * 20 % 256 * 256 * 256 + (i + 100) * 20 % 256 * 256 + (i + 200) * 20 % 256);
            return view;
        }
    }

    private void initViewpager(){
        final View view = LayoutInflater.from(this).inflate(R.layout.item, null, false);
        final View view1 = LayoutInflater.from(this).inflate(R.layout.item1, null, false);
        ViewPager viewPager = (ViewPager) LayoutInflater.from(this).inflate(R.layout.viewpager, pullRefreshListView, false);
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
        pullRefreshListView.addHeaderView(viewPager);
    }
}
