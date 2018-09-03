package com.dou.shugo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dou.shugo.shugo_annotation.SHugo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();
    }

    @SHugo
    private void test() {
        System.out.println("test");
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
