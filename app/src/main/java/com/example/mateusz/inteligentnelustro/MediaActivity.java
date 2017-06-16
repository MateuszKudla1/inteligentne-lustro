package com.example.mateusz.inteligentnelustro;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.PowerManager;
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

public class MediaActivity extends AppCompatActivity implements RecognitionListener {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private News obj;
    private List<String> newsList = new ArrayList<>();
    private TextView news1,news2,news3,news4,news5,news6,news7;
    private String finalUrl="http://www.tvn24.pl/najwazniejsze.xml";
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final String KWS_SEARCH = "wakeup";
    private static final String KEYPHRASE = "smart mirror";
    private SpeechRecognizer recognizer;
    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();


        news1 = (TextView) findViewById(R.id.news1);
        news2 = (TextView) findViewById(R.id.news2);
        news3 = (TextView) findViewById(R.id.news3);
        news4 = (TextView) findViewById(R.id.news4);
        news5 = (TextView) findViewById(R.id.news5);
        news6 = (TextView) findViewById(R.id.news6);

        news();
        runRecognizerSetup();
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

                    if(result.get(0).equals("ekran główny")){
                        mainView();
                        recognizer.cancel();
                        recognizer.shutdown();
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




    public void news(){

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
                    Assets assets = new Assets(MediaActivity.this);
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
