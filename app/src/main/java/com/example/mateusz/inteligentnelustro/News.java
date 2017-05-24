package com.example.mateusz.inteligentnelustro;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

/**
 * Created by Mateusz on 2017-05-23.
 */

public class News {


    private String rssUrl = "http://www.tvn24.pl/najnowsze.xml";
    private String title = "title";
    private String link = "link";
    List<String> list = new ArrayList<>();
    private String description = "description";
    public Boolean parsingComplete = true;
    private XmlPullParserFactory xmlFactoryObject;

    public News(String URL){
        this.rssUrl = URL;
    }
    public List<String> getList(){
        return list;
    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return link;
    }

    public String getDescription(){
        return description;
    }



    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(name.equals("title")){
                            list.add(text);
                            System.out.println(text);

                            title = text;
                        }


                        else{
                        }

                        break;
                }

                event = myParser.next();
            }

            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fetchXML(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    String response = Network.get(rssUrl);
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(new StringReader(response));

                    parseXMLAndStoreIt(myparser);

                }

                catch (Exception e) {
                }
            }
        });
        thread.start();
    }



}

