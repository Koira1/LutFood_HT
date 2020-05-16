package com.example.lutfood_ht;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ScrapeMenus {

    Document doc;
    Element link;
    String name;






    public ScrapeMenus() throws IOException {
        asd();
        Log.d("asdasdasddasdas", "asdasdasdads");
    }



    private void asd(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://kolmekivea.fi/cloud-kitchen/").get();
                    Log.d("Success", "Success");
                } catch (IOException e) {
                    Log.d("Not Success", "Not success");
                    e.printStackTrace();
                }
                link = doc.select("h4").first();
                name = link.text();
                Log.d("name", "name: " + name);

            }
        }).start();
    }
}
