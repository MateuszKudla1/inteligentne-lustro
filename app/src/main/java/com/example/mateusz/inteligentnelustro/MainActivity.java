package com.example.mateusz.inteligentnelustro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;

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


import com.example.mateusz.inteligentnelustro.weather.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static com.example.mateusz.inteligentnelustro.R.id.imageView;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements edu.cmu.pocketsphinx.RecognitionListener {
    TextView news1,news2,news3,news4,temp,location,conditions,news5,news6,bitstamp,rs;
    public String cityR = "Kielce";
    private String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+cityR+"%2C%20pl%22)%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";
    private String urlBs = "https://www.bitstamp.net/api/v2/ticker_hour/btcusd/";
    private News obj;
    Button b1,stop;
    List<String> test = new ArrayList<>();
    ImageView image,weatherImage;
    private String  result;
    Channel channel;
    String resource;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    MediaPlayer mPlayer;
    private int imageResource;
    private Drawable drawable;
    private static final String KWS_SEARCH = "wakeup";
    private static final String KEYPHRASE = "hello";
    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        weather(url);
        image = (ImageView) findViewById(R.id.imageView1);
        news1 = (TextView) findViewById(R.id.news_1);
        news2 = (TextView) findViewById(R.id.news_2);
        news3 = (TextView) findViewById(R.id.news_3);
        news4 = (TextView) findViewById(R.id.news_4);
        news5 = (TextView) findViewById(R.id.news_5);
        news6 = (TextView) findViewById(R.id.news_6);
        rs = (TextView) findViewById(R.id.result);
        bitstamp = (TextView) findViewById(R.id.bitstamp);
        //pogoda
        weatherImage = (ImageView) findViewById(R.id.imageView);
        temp = (TextView) findViewById(R.id.Temperature);
        location = (TextView)findViewById(R.id.Location) ;
        conditions = (TextView) findViewById(R.id.Condition);
        temp.setText("28°");
        location.setText("Kielce");
        conditions.setText("Słonecznie");
        //--------

        wiadomosci();
        bitStamp();
        runRecognizerSetup();
        location.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPlayer.stop();

            }
        });



        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promptSpeechInput();

            }
        });

        //pocketsphinx











//-----------------------------------------------
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {

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
        });
        thread.start();
    }










    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        recognizer.startListening(KWS_SEARCH);

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


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
                    if(result.get(0).equals("wiadomości")){
                       MediaView();
                        recognizer.cancel();
                        recognizer.shutdown();
                    }
                    if(result.get(0).equals("pokaż")){
                        wiadomosci();

                    }
                    if(result.get(0).equals("help") || result.get(0).equals("pomoc")  ){
                        help();
                    }
                    if(result.get(0).toLowerCase().equals("pogoda")) {
                        pogoda();
                        recognizer.cancel();
                        recognizer.shutdown();
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
                news2.post(new Runnable(){public void run(){news2.setText("2.'Wiadomości' -  przejście do ekranu wiadomośći");} });
                news3.post(new Runnable(){public void run(){news3.setText("3.'Pogoda' - przejście do ekranu z aktualną pogodą");} });
                news4.post(new Runnable(){public void run(){news4.setText("4.'Muzyka' ");} });
                news5.post(new Runnable(){public void run(){news5.setText("5.'Usuń' - usuwa nagłowki wiadomości ");} });
                news6.post(new Runnable(){public void run(){news6.setText("6.'Pokaż' - Pokazuje nagłowki wiadomości ");} });


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
            news5.post(new Runnable(){public void run(){news5.setText(test.get(5));} });
            news6.post(new Runnable(){public void run(){news6.setText(test.get(6));} });

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

    public void bitStamp() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                result = Network.get(urlBs);

                try {

                    JSONObject jo = new JSONObject(result);
                   final BitcointStamp bs = new BitcointStamp(jo);
                    bitstamp.post(new Runnable(){public void run(){bitstamp.setText("Bitstamp $"+bs.getLast());}});


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
        thread.start();
    }


    public void weather(final String urlS){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {

                result = Network.get(urlS);

                try {
                    JSONObject jo = new JSONObject(result);
                    JSONObject joW = jo.optJSONObject("query")
                            .optJSONObject("results")
                            .optJSONObject("channel");

                    channel = new Channel(joW);




                } catch (JSONException e) {
                    e.printStackTrace();
                }


                resource ="@drawable/" + "a" + channel.condition.getCode();
                imageResource = getResources().getIdentifier(resource, null, getPackageName());
                drawable = getResources().getDrawable(imageResource);




                weatherImage.post(new Runnable(){public void run(){weatherImage.setImageDrawable(drawable);}});
                temp.post(new Runnable(){public void run(){temp.setText(channel.condition.getTemp()+"°");}});
                conditions.post(new Runnable(){public void run(){conditions.setText(channel.condition.getText());}});
                location.post(new Runnable(){public void run(){location.setText(channel.location.getCity());}});












            }
        });
        thread.start();
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {


                } else {
                    switchSearch(KWS_SEARCH);
                }

            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }



    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }


    @Override
    public void onBeginningOfSpeech() {

    }


    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();
        rs.setText(text);
        if (text.equals(KEYPHRASE))
        {
            start();
            recognizer.cancel();



        }

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {


        }

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);

    }

    public void start(){
        promptSpeechInput();
    }

}
