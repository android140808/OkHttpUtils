package avater.avaterprojects;

import android.app.Activity;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

public class MainActivity extends Activity implements View.OnClickListener {
    Button get;
    Button post;
    AvaterOkhttpUtils utils;
    ImageView imageview;
    ListView listview;
    ArrayList<Bean> beans = new ArrayList<>();
    Myadapter myadapter = new Myadapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setWindowStatusBarColor(this, Color.parseColor("#40E0D0"));
        setContentView(R.layout.activity_main);
        findView();
        initLister();
        init();
    }

    private void init() {
        utils = AvaterOkhttpUtils.getInstance();
        for (int i = 0; i < 30; i++) {
            beans.add(new Bean("Avater 2017-09-" + i));
        }
        listview.setAdapter(myadapter);
    }

    private void initLister() {
        get.setOnClickListener(this);
        post.setOnClickListener(this);
        imageview.setOnClickListener(this);
    }

    private void findView() {
        get = (Button) findViewById(R.id.get);
        post = (Button) findViewById(R.id.post);
        imageview = (ImageView) findViewById(R.id.imageview);
        listview = (ListView) findViewById(R.id.listview);
    }

    /**
     * 状态栏相关工具类
     */
    public void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = activity.getWindow();
                //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //设置透明状态栏,这样才能让 ContentView 向上
                //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
                ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null) {
                    //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                    ViewCompat.setFitsSystemWindows(mChildView, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String asString = AvaterOkhttpUtils.getAsString("https://www.baidu.com");
                            Log.e("TAG", "asString == " + asString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.post:
                AvaterOkhttpUtils.postAsyn("", new AvaterOkhttpUtils.AvaterResultCallBack<AvaterBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(AvaterBean response) {

                    }


                }, new AvaterOkhttpUtils.Param("username", "avater"));
                break;
            case R.id.imageview:
                AnimationSet set = new AnimationSet(true);
                TranslateAnimation tranAnim = new TranslateAnimation(
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 100,
                        Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, -800);
                set.addAnimation(tranAnim);
                set.setInterpolator(AnimationUtils.loadInterpolator(MainActivity.this,
                        android.R.anim.decelerate_interpolator));
                set.setDuration(800);
                set.setFillAfter(true);
                imageview.startAnimation(set);
                break;
        }
    }

    class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt.setText(beans.get(position).getTxt() + "");
            if (position == 0) {
                holder.tl.setFirst(true);
            } else if (position == beans.size() - 1) {
                holder.tl.setLast(true);
            } else
                holder.tl.setMiddle();
            return convertView;
        }
    }

    class ViewHolder {
        private TextView txt;
        private TimeLineView tl;

        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.txt);
            tl = (TimeLineView) view.findViewById(R.id.timeline);
        }
    }

}
