package com.example.myapplication3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CommonLayoutView extends RelativeLayout {

    private RelativeLayout loadingOverlay;
    private TextView tvHelp;

    public CommonLayoutView(Context context) {
        super(context);
        init(context);
    }

    public CommonLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_common_layout, this, true);

        loadingOverlay = findViewById(R.id.loading_overlay);
        tvHelp = findViewById(R.id.tv_help);

        loadingOverlay.setVisibility(View.GONE);

        tvHelp.setOnClickListener(v ->
                Toast.makeText(context, "这是帮助提示", Toast.LENGTH_SHORT).show()
        );
    }

    public void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
