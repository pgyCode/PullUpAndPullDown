package com.example.rtyui.androidteach.Test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.androidteach.R;

import java.net.URL;
import java.util.List;

/**
 * Created by rtyui on 2018/4/30.
 */

public class TestDemo extends Activity {

    private TextView txt;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        txt = findViewById(R.id.txt);
final String string = "<p><img alt=\"laugh\" height=\"23\" src=\"http://img3.imgtn.bdimg.com/it/u=3140599426,288343775&fm=26&gp=0.jpg\" " +
        "title=\"laugh\" width=\"23\" /> 简单的图文混排" +
        "<img alt=\"cheeky\" height=\"23\" src=\"http://img3.imgtn.bdimg.com/it/u=3140599426,288343775&fm=26&gp=0.jpg\" " +
        "title=\"cheeky\" width=\"23\" />这是展示内容</p>";

final Html.ImageGetter imgGetter = new Html.ImageGetter() {
    public Drawable getDrawable(String source) {
        Drawable drawable = null;
        URL url = null;
        try {
            url = new URL(source);
            drawable = Drawable.createFromStream(url.openStream(), "img");
        } catch (Exception e) {
            return null;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                .getIntrinsicHeight());
        return drawable;
    }
};

new Thread(new Runnable() {
    @Override
    public void run() {
        final Spanned text = Html.fromHtml(string, imgGetter, null);
        handler.post(new Runnable() {
            @Override
            public void run() {
                txt.setText(text);
            }
        });
    }
}).start();
    }



    public class NoUnderlineSpan extends UnderlineSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.my_blue));
            ds.setUnderlineText(false);
        }
    }
}
