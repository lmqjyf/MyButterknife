package com.bitcoin.juwan.mybutterknife.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.mybutterknife.MyButterKnife;
import com.bitcoin.juwan.mybutterknife.R;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description :
 */
public class TestFragment extends Fragment {
    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fragment_text)
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.third_fragment, container, false);
        MyButterKnife.init(this, inflate);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击了", Toast.LENGTH_SHORT).show();
            }
        });
        return inflate;
    }
}
