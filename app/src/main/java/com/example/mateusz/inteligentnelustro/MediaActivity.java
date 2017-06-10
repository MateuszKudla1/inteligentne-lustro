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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaActivity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    TextView txtSpeechInput;
    Button b1,stop;
    private News obj;
    private List<String> newsList = new ArrayList<>();
    TextView news1,news2,news3,news4,news5,news6,news7;
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media);


        wiadomosci();
        news1 = (TextView) findViewById(R.id.news1);
        news2 = (TextView) findViewById(R.id.news2);
        news3 = (TextView) findViewById(R.id.news3);
        news4 = (TextView) findViewById(R.id.news4);
        news5 = (TextView) findViewById(R.id.news5);
        news6 = (TextView) findViewById(R.id.news6);
        news7 = (TextView) findViewById(R.id.news7);

        news1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promptSpeechInput();

            }
        });

        news2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                MainView();

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
                    //txtSpeechInput.setText(result.get(0));


                    if(result.get(0).equals("ekran główny")){
                        MainView();
                    }

                }
                break;
            }

        }
    }


    public void MainView(){
        Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(startIntent);
    }

    public void wiadomosci(){

        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {

                obj = new News(finalUrl);
                obj.fetchXML();

                while (obj.parsingComplete) ;

                newsList = obj.getList();

                news1.post(new Runnable() {
                    public void run() {
                        news1.setText(newsList.get(1));
                    }
                });
                news2.post(new Runnable() {
                    public void run() {
                        news2.setText(newsList.get(2));
                    }
                });
                news3.post(new Runnable() {
                    public void run() {
                        news3.setText(newsList.get(3));
                    }
                });
                news4.post(new Runnable() {
                    public void run() {
                        news4.setText(newsList.get(4));
                    }
                });
                news5.post(new Runnable() {
                    public void run() {
                        news5.setText(newsList.get(5));
                    }
                });
                news6.post(new Runnable() {
                    public void run() {
                        news6.setText(newsList.get(6));
                    }
                });
                news7.post(new Runnable() {
                    public void run() {
                        news7.setText(newsList.get(7));
                    }
                });
            }
        });

        thread.start();


    }


}
