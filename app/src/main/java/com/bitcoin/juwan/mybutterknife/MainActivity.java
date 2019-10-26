package com.bitcoin.juwan.mybutterknife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.mybutterknife.fragment.ThirdActivity;
import com.bitcoin.juwan.mybutterknife.test.SecondActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_text)
    TextView textView;

    @BindView(R.id.main_button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyButterKnife.init(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));
            }
        });
    }

}
