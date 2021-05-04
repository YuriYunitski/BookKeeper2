package com.yunitski.bookkeeper2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Exchange extends AppCompatActivity implements View.OnClickListener {

    private Document doc;
    private Thread secThread;
    private Runnable runnable;
    private String dollar, euro;
    TextView t1, t2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        t1 = findViewById(R.id.dollar);
        t2 = findViewById(R.id.euro);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
//        dollar = "";
//        euro = "";
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        t1.setText(dollar);
        t2.setText(euro);

    }

    private void init(){
        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    private void getWeb(){
        try {
            doc = Jsoup.connect("https://www.cbr.ru/currency_base/daily/").get();

            Elements elements = doc.getElementsByTag("tbody");
            Element element = elements.get(0);
            Elements elementsFromTable = element.children();
            String s = elementsFromTable.get(11).toString();
            String[] st = s.split("<td>");
            String[] name = st[4].split("<");
            String[] value = st[5].split("<");
            String s2 = elementsFromTable.get(12).toString();
            String[] st2 = s2.split("<td>");
            String[] name2 = st2[4].split("<");
            String[] value2 = st2[5].split("<");
            dollar = name[0] + " " + value[0];
            euro = name2[0]  + " " + value2[0];
//            Log.d("MyLog", "title2 : " + name[0] + " " + value[0]);
//            Log.d("MyLog", "title3 : " + elements.get(3).text());
//            Log.d("MyLog", "title4 : " + elements.get(4).text());
//            Log.d("MyLog", "title5 : " + elements.get(5).text());
//            Log.d("MyLog", "title6 : " + elements.get(6).text());
//            Log.d("MyLog", "title7 : " + elements.get(7).text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        t1.setText(dollar);
        t2.setText(euro);
    }
    public void setV(){

        t1.setText(dollar);
        t2.setText(euro);
    }
}