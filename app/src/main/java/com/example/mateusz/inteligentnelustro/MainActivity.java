package com.example.mateusz.inteligentnelustro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
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
    TextView news1,news2,news3,news4,txtSpeechInput,temp,location,conditions,news5,news6;
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";
    private News obj;
    Button b1,stop;
    List<String> test = new ArrayList<>();
    ImageView image;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    MediaPlayer mPlayer;




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
        news5 = (TextView) findViewById(R.id.news_5);
        news6 = (TextView) findViewById(R.id.news_6);
        temp = (TextView) findViewById(R.id.Temperature);
        location = (TextView)findViewById(R.id.Location) ;
        conditions = (TextView) findViewById(R.id.Condition);
        temp.setText("28°");
        location.setText("Kielce");
        conditions.setText("Słonecznie");
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        b1=(Button)findViewById(R.id.button);

        stop = (Button) findViewById(R.id.stop);

        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.stop();

            }
        });



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promptSpeechInput();

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
                    if(result.get(0).equals("zdjęcia")){
                        zdjęcia();
                    }
                    if(result.get(0).equals("muzyka")){
                        muzyka();
                    }
                    if(result.get(0).equals("stop")){
                        muzykaStop();
                    }
                    if(result.get(0).equals("ekran wiadomości")){
                       MediaView();
                    }
                    if(result.get(0).equals("help") || result.get(0).equals("pomoc")  ){
                        help();
                    }
                    if(result.get(0).toLowerCase().equals("pogoda")) {
                        pogoda();
                    }
                }
                break;
            }

        }
    }

    public void help(){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {
                obj = new News(finalUrl);
                obj.fetchXML();

                while (obj.parsingComplete) ;

                test = obj.getList();

                news1.post(new Runnable(){public void run(){news1.setText("1.'Ekran główny' - powrót do ekranu głównego");} });
                news2.post(new Runnable(){public void run(){news2.setText("2.'Ekran Wiadomości' -  przejście do ekranu wiadomośći");} });
                news3.post(new Runnable(){public void run(){news3.setText("3.'Pogoda' - przejście do ekranu z aktualną pogodą");} });
                news4.post(new Runnable(){public void run(){news4.setText("4.'Muzyka' ");} });
                news5.post(new Runnable(){public void run(){news5.setText("5.'Wiadomośći' - wyswietla nagłowki wiadomości ");} });
                news6.post(new Runnable(){public void run(){news6.setText("6.'Usuń' - usuwa nagłowki wiadomości ");} });

            }
        });
        thread.start();
    }

    public void pogoda(){
        Intent weatherIntent = new Intent(getApplicationContext(),WeatherActivity.class);
        startActivity(weatherIntent);
    }


public void wiadomosci(){
    wiadomosci2();

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
                news5.post(new Runnable(){public void run(){news5.setText("");} });
                news6.post(new Runnable(){public void run(){news6.setText("");} });

            }
        });
        thread.start();
    }

    public void zdjęcia(){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {
                
                image.post(new Runnable(){public void run(){image.setImageResource(R.drawable.jagoda);}});

            }
        });
        thread.start();
    }

    public void muzyka(){

                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
                mPlayer.start();

    }

    public void muzykaStop(){

        mPlayer.stop();

    }

    public void MediaView(){
        Intent startIntent = new Intent(getApplicationContext(),MediaActivity.class);
        startActivity(startIntent);
    }



}
