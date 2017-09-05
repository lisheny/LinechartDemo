package com.lisheny.linechartdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinechartView linechartView = (LinechartView)findViewById(R.id.linechartview);
//        linechartView.setyText();
//        linechartView.setmData();
//        linechartView.setxText();
    }
}
