package com.ardaayvatas.currencyconverter;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetSymbols {
    public static ArrayList<String> money = new ArrayList<>();

    public GetSymbols()
    {
        new HTTPAsyncTask().execute("https://api.exchangerate.host/symbols?format=xml");
    }


    private class HTTPAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpGet(urls[0]);
            } catch (IOException | XmlPullParserException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }
    }

    private String HttpGet(String myUrl) throws IOException, XmlPullParserException {
        InputStream inputStream = null;
        String result ="";

        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();

        inputStream = conn.getInputStream();

        if (inputStream!=null)
        {
            result = convertInputStreamToString(inputStream);
        }
        else
            result = "Did not work!";

        return result;

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException, XmlPullParserException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(bufferedReader);

        int eventType = xpp.getEventType();
        String result ="";
        int x = 0;
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_DOCUMENT)
            {
                //result = "Document Begining";
            }else if (eventType == XmlPullParser.END_DOCUMENT){
                //result = result + System.getProperty("line.separator") + "End of Document";
            }else if (eventType == XmlPullParser.START_TAG){
                //result = result + System.getProperty("line.separator") + xpp.getName() + "11";
                if (xpp.getName().equals("code"))
                {
                    x = 1;
                }

                else
                {
                    x = 0;
                }

            }else if (eventType == XmlPullParser.END_TAG){
                //result = result + System.getProperty("line.separator") + "End tag: " + xpp.getName();
            }else if (eventType == XmlPullParser.TEXT){
                //result = result + System.getProperty("line.separator") + "Text: " + xpp.getText();
                if (x == 1)
                {

                    money.add(xpp.getText());
                    x=0;
                }
            }
            eventType = xpp.next();
        }
        inputStream.close();
        System.out.println(money);
        return result;
    }
}
