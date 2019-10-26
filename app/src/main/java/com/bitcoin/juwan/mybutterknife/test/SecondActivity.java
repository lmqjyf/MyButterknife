package com.bitcoin.juwan.mybutterknife.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.mybutterknife.MyButterKnife;
import com.bitcoin.juwan.mybutterknife.R;

public class SecondActivity extends AppCompatActivity {


    @BindView(R.id.second_text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MyButterKnife.init(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MyButterKnife.unBind(this);
        super.onDestroy();
    }
}
