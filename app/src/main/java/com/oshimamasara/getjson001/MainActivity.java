package com.oshimamasara.getjson001;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
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

    // Admob
    public static final int NUMBER_OF_ADS = 1;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn = findViewById(R.id.button);

        if (savedInstanceState == null) {

            // XML - JSON, SAVE Json File
            xmljson();

            // adapter処理を xmljson() と同じレベルで実行すると書き込みより読み込みが先に来て、アプリ 2回目以降でないとフィード表示されない
            // そのためボタンタップで表示プロっグラムを処理  他の手段 boolean?
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNativeAds();
                    addMenuItemsFromJson();
                    loadMenu();

                    btn.setVisibility(View.INVISIBLE); //タップでボタン非表示に
                }
            });


            // AdMob
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        }
    }

    private void loadNativeAds() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // 広告表示
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }


    // 広告の表示サイクルに関するプログラム
    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 5; // 表示箇所
        for (UnifiedNativeAd ad : mNativeAds) {
            mRecyclerViewItems.add(index, ad);
            index = index + offset;
        }
        loadMenu();
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
