package com.example.mateusz.inteligentnelustro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView news1,news2,news3,news4,txtSpeechInput;
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";
    private News obj;
    Button b1,speakButton;
    List<String> test = new ArrayList<>();
    ImageView image;
    private final int REQ_CODE_SPEECH_INPUT = 100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageView1);
        news1 = (TextView) findViewById(R.id.news_1);
        news2 = (TextView) findViewById(R.id.news_2);
        news3 = (TextView) findViewById(R.id.news_3);
        news4 = (TextView) findViewById(R.id.news_4);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        b1=(Button)findViewById(R.id.button);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //wiadomosci();
                promptSpeechInput();

/*
                obj = new News(finalUrl);
                obj.fetchXML();

                while(obj.parsingComplete);

                test = obj.getList();

                news1.setText(test.get(1));
                news2.setText(test.get(2));
                news3.setText(test.get(3));
                news4.setText(test.get(4));

*/

            }
        });


    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    if(result.get(0).equals("News") || result.get(0).equals("wiadomości") ){
                        wiadomosci();
                    }
                    if(result.get(0).equals("delete") || result.get(0).equals("Usuń")){
                        wiadomosci2();
                    }
                    if(result.get(0).equals("Jagoda")){
                        jagoda();
                    }

                }
                break;
            }

        }
    }


public void wiadomosci(){

    Thread thread = new Thread( new Runnable(){
        @Override
        public void run() {
            obj = new News(finalUrl);
            obj.fetchXML();

            while (obj.parsingComplete) ;

            test = obj.getList();

           news1.post(new Runnable(){public void run(){news1.setText(test.get(1));} });
            news2.post(new Runnable(){public void run(){news2.setText(test.get(2));} });
            news3.post(new Runnable(){public void run(){news3.setText(test.get(3));} });
            news4.post(new Runnable(){public void run(){news4.setText(test.get(4));} });

        }
    });
thread.start();
}



    public void wiadomosci2(){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {


                news1.post(new Runnable(){public void run(){news1.setText("");} });
                news2.post(new Runnable(){public void run(){news2.setText("");} });
                news3.post(new Runnable(){public void run(){news3.setText("");} });
                news4.post(new Runnable(){public void run(){news4.setText("");} });

            }
        });
        thread.start();
    }

    public void jagoda(){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {


                image.post(new Runnable(){public void run(){image.setImageResource(R.drawable.jagoda);}});

            }
        });
        thread.start();
    }



}
