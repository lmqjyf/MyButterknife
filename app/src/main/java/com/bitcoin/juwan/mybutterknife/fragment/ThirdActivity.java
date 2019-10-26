package com.bitcoin.juwan.mybutterknife.fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.mybutterknife.MyButterKnife;
import com.bitcoin.juwan.mybutterknife.R;

public class ThirdActivity extends AppCompatActivity {


    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        MyButterKnife.init(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, TestFragment.newInstance()).commit();
    }
}
