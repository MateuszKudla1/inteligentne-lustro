package com.example.mateusz.inteligentnelustro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView news1,news2,news3,news4;
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";
    private News obj;
    Button b1;
    List<String> test = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        news1 = (TextView) findViewById(R.id.news_1);
        news2 = (TextView) findViewById(R.id.news_2);
        news3 = (TextView) findViewById(R.id.news_3);
        news4 = (TextView) findViewById(R.id.news_4);

        b1=(Button)findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj = new News(finalUrl);
                obj.fetchXML();

                while(obj.parsingComplete);

                test = obj.getList();

                news1.setText(test.get(1));
                news2.setText(test.get(2));
                news3.setText(test.get(3));
                news4.setText(test.get(4));
            }
        });


    }







}
