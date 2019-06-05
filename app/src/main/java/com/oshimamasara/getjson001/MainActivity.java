package com.oshimamasara.getjson001;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private JSONObject jsonObject;
    private String text;
    private static final String TAG = "Main";
    private Object IOException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = findViewById(R.id.button);

        if (savedInstanceState == null) {
            //グルグルマーク
            //Fragment loadingScreenFragment = new LoadingScreenFragment();
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.add(R.id.fragment_container, loadingScreenFragment);
            //transaction.commit();

            // XML - JSON, SAVE Json File
            xmljson();

            // adapter処理を xmljson() と同じレベルで実行すると書き込みより読み込みが先に来て、アプリ 2回目以降でないとフィード表示されない
            // そのためボタンタップで表示プロっグラムを処理  他の手段 boolean?
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMenuItemsFromJson();
                    loadMenu();
                    btn.setVisibility(View.INVISIBLE);
                }
            });


            // AdMob
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        }
    }



    public List<Object> getRecyclerViewItems() {
        return mRecyclerViewItems;
    }

    private void loadMenu() {
        Fragment newFragment = new RecyclerViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Adds {@link MenuItem}'s from a JSON file.
     */
    private void addMenuItemsFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();

            JSONObject menuItemsJsonObject = new JSONObject(jsonDataString);

            JSONArray menuItemsJsonArray = menuItemsJsonObject.getJSONArray("items");
            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {
                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);

                String menuItemTitle = menuItemObject.getString("title");
                String menuPubDate = menuItemObject.getString("pubDate");
                String menuDescription = menuItemObject.getString("description");
                String menuItemLink = menuItemObject.getString("link");

                MenuItem menuItem = new MenuItem(menuItemTitle,menuPubDate,menuDescription,menuItemLink);
                mRecyclerViewItems.add(menuItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void xmljson() {
        Ion.with(getApplicationContext()).load("https://api.rss2json.com/v1/api.json?rss_url=http://rss.nytimes.com/services/xml/rss/nyt/Science.xml").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    //SAVE
                    text = obj.toString();
                    FileOutputStream fileOutputStream = openFileOutput("sample5.json", MODE_PRIVATE);
                    fileOutputStream.write(text.getBytes());
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Data Set OK", Toast.LENGTH_SHORT).show();


                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    private String readJsonDataFromFile() throws IOException {

        FileInputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try{

            String jsonDataString = null;

            inputStream = openFileInput("sample5.json");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(builder);
    }
}
