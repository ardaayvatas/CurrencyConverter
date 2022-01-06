package com.ardaayvatas.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText tv;
    EditText editText;
    NumberPicker numberPicker;
    NumberPicker numberPickerTwo;
    String ilk;
    String iki;
    String carp="1";
    Float num ;
    GetSymbols getSymbols = new GetSymbols();
    String[] moneyx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberPicker = findViewById(R.id.numberPicker);
        numberPickerTwo = findViewById(R.id.numberPickerTwo);
        //moneyx = getSymbols.money.toArray(new String[0]);
        try {
            TimeUnit.MILLISECONDS.sleep(1850);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moneyx = new String[getSymbols.money.size()];
        for (int i = 0; i< getSymbols.money.size();i++)
        {
            moneyx[i] = getSymbols.money.get(i);
        }
        tv = findViewById(R.id.tv);
        editText = findViewById(R.id.editText);
        //1
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(moneyx.length-1);
        numberPicker.setValue(1);
        ilk = moneyx[1];
        //2
        numberPickerTwo.setMinValue(0);
        numberPickerTwo.setMaxValue(moneyx.length-1);
        numberPickerTwo.setValue(1);
        iki = moneyx[1];
        //1 and 2
        numberPicker.setDisplayedValues(moneyx);
        numberPickerTwo.setDisplayedValues(moneyx);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ilk = moneyx[newVal];
            }
        });

        numberPickerTwo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                iki = moneyx[newVal];
            }
        });


    }

    public void convert(View view) {
        if (checkNetworkConnection())
        {
            new HTTPAsyncTask().execute("https://api.exchangerate.host/convert?from="+ilk+"&to="+iki+"&format=xml");
        }
    }

    public boolean checkNetworkConnection()
    {
        ConnectivityManager connMgr =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected ;
        isConnected = networkInfo.isConnected();
        return isConnected;
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
        protected void onPostExecute(String result)
        {
            carp = editText.getText().toString();
            carp = carp.replace(',','.');
            num = Float.parseFloat(carp);
            //System.out.println(result);
            result = result.replace(',','.');
            num *= Float.parseFloat(result);
            tv.setText(String.format("%.2f",num));
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
                result = "Document Begining";
            }else if (eventType == XmlPullParser.END_DOCUMENT){
                result = result + System.getProperty("line.separator") + "End of Document";
            }else if (eventType == XmlPullParser.START_TAG){
                result = result + System.getProperty("line.separator") + xpp.getName() + "11";
                if (xpp.getName().equals("result"))
                {
                    x = 1;
                }

                else
                {
                    x = 0;
                }
            }else if (eventType == XmlPullParser.END_TAG){
                result = result + System.getProperty("line.separator") + "End tag: " + xpp.getName();
            }else if (eventType == XmlPullParser.TEXT){
                result = result + System.getProperty("line.separator") + "Text: " + xpp.getText();
                if (x == 1)
                {
                    result = xpp.getText();
                    break;
                }
            }
            eventType = xpp.next();
        }
        inputStream.close();
        return result;
    }
}