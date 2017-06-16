package com.example.mateusz.inteligentnelustro;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mateusz.inteligentnelustro.weather.Channel;
import com.example.mateusz.inteligentnelustro.weather.ForecastDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class WeatherActivity extends AppCompatActivity implements RecognitionListener {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    public String cityResult;
    public String cityR = "Kielce";
    private String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+cityR+"%2C%20pl%22)%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    private TextView temp,conditions,city,tvSunrise,tvSunset,tvDay1,tvDay2,tvDay3,tvDay4,tvDay5,tvDay6;
    private ImageView ivDay1,ivDay2,ivDay3,ivDay4,ivDay5,ivDay6;
    private final ImageView[] tab = {ivDay1,ivDay2,ivDay3,ivDay4,ivDay5,ivDay6};
    private final TextView[] tab2 = {tvDay1,tvDay2,tvDay3,tvDay4,tvDay5,tvDay6};
    public int[] imageResources = new int[7];
    private String[] tab3 = new String[7];
    private String[] tab4 = new String[7];
    private String  result;
    private Channel channel;
    private Drawable[] resources = new Drawable[7];
    private List<ForecastDay> fList;
    protected PowerManager.WakeLock mWakeLock;

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final String KWS_SEARCH = "wakeup";
    private static final String KEYPHRASE = "hello";
    private SpeechRecognizer recognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_weather);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();


        temp = (TextView) findViewById(R.id.temperature);
        conditions = (TextView) findViewById(R.id.tvConditions);
        city= (TextView) findViewById(R.id.tvCity);
        tvSunrise = (TextView) findViewById(R.id.sunrise);
        tvSunset = (TextView) findViewById(R.id.sunset);
        tvDay1 = (TextView) findViewById(R.id.day1);
        tvDay2 = (TextView) findViewById(R.id.day2);
        tvDay3 = (TextView) findViewById(R.id.day3);
        tvDay4 = (TextView) findViewById(R.id.day4);
        tvDay5 = (TextView) findViewById(R.id.day5);
        tvDay6 = (TextView) findViewById(R.id.day6);

        ivDay1 = (ImageView) findViewById(R.id.day1image);
        ivDay2 = (ImageView) findViewById(R.id.day2image);
        ivDay3 = (ImageView) findViewById(R.id.day3image);
        ivDay4 = (ImageView) findViewById(R.id.day4image);
        ivDay5 = (ImageView) findViewById(R.id.day5image);
        ivDay6 = (ImageView) findViewById(R.id.day6image);

        weather(url);
        runRecognizerSetup();

        temp.setOnClickListener(new View.OnClickListener() {
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
        recognizer.startListening(KWS_SEARCH);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    if(result.get(0).equals("ekran główny")){
                        mainView();
                        recognizer.cancel();
                        recognizer.shutdown();
                    }else {
                        cityResult = (String) result.get(0);
                        weatherFill(cityResult);
                    }

                }
                break;
            }

        }
    }

    public void mainView(){
        Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(startIntent);
    }





    public void weather(final String urlS){

        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {
                System.out.println(url);
                System.out.println(cityResult);
                System.out.println(cityR);
                result = Network.get(urlS);

                try {
                    JSONObject jo = new JSONObject(result);
                    JSONObject joW = jo.optJSONObject("query")
                            .optJSONObject("results")
                            .optJSONObject("channel");

                    channel = new Channel(joW);
                    fList = channel
                            .forecast
                            .getList();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                temp.post(new Runnable(){public void run(){temp.setText(channel.condition.getTemp()+"°c");}});
                conditions.post(new Runnable(){public void run(){conditions.setText(channel.condition.getText());}});
                city.post(new Runnable(){public void run(){city.setText(channel.location.getCity());}});
                tvSunset.post(new Runnable(){public void run(){tvSunset.setText("Zachód: "+channel.astronomy.getSunset());}});
                tvSunrise.post(new Runnable(){public void run(){tvSunrise.setText("Wschód "+channel.astronomy.getSunrise());}});


                for(int i = 1; i<7;i++){
                    ForecastDay fd = new ForecastDay();
                    TextView tv;
                    String data;
                    fd = fList.get(i);
                    data = " "+fd.getDayW()+ " " + fd.getHigh() + "°c " + fd.getText();
                    tab3[i-1] = data;
                    tab4[i - 1] ="@drawable/" + "a" + fd.getCode();
                    System.out.println(tab4[i-1]);
                    imageResources[i-1]  = getResources().getIdentifier(tab4[i-1], null, getPackageName());
                    resources[i-1] = getResources().getDrawable(imageResources[i-1]);


                }

                tvDay1.post(new Runnable(){public void run(){tvDay1.setText(tab3[0]);}});
                tvDay2.post(new Runnable(){public void run(){tvDay2.setText(tab3[1]);}});
                tvDay3.post(new Runnable(){public void run(){tvDay3.setText(tab3[2]);}});
                tvDay4.post(new Runnable(){public void run(){tvDay4.setText(tab3[3]);}});
                tvDay5.post(new Runnable(){public void run(){tvDay5.setText(tab3[4]);}});
                tvDay6.post(new Runnable(){public void run(){tvDay6.setText(tab3[5]);}});



                ivDay1.post(new Runnable(){public void run(){ivDay1.setImageDrawable(resources[0]);}});
                ivDay2.post(new Runnable(){public void run(){ivDay2.setImageDrawable(resources[1]);}});
                ivDay3.post(new Runnable(){public void run(){ivDay3.setImageDrawable(resources[2]);}});
                ivDay4.post(new Runnable(){public void run(){ivDay4.setImageDrawable(resources[3]);}});
                ivDay5.post(new Runnable(){public void run(){ivDay5.setImageDrawable(resources[4]);}});
                ivDay6.post(new Runnable(){public void run(){ivDay6.setImageDrawable(resources[5]);}});







            }
        });
        thread.start();
    }


    public void weatherFill(String data){
        System.out.println(data);
        cityR = data;
        String city2 = cityR;
        String url2 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+city2+"%2C%20pl%22)%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        weather(url2);

    }


    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(WeatherActivity.this);
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
        this.mWakeLock.release();
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