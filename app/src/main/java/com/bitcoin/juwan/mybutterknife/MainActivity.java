package com.bitcoin.juwan.mybutterknife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.annotations.onClick;
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
    }

    @Override
    protected void onDestroy() {
        MyButterKnife.unBind(this);
        super.onDestroy();
    }

    @onClick({R.id.main_text, R.id.main_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_text : {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
            }
            case R.id.main_button : {
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));
                break;
            }
        }
    }
}
