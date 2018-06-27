package com.alguojian.gaodemaputil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alguojian.map.BaseMapActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseMapActivity.start(this);
    }

}
