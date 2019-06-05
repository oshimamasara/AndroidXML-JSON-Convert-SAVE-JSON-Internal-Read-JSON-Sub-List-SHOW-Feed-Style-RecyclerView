package com.oshimamasara.getjson001;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * A simple activity showing the use of menu items in
 * a {@link RecyclerView} widget.
 */
public class MainActivity extends AppCompatActivity {

    // List of MenuItems that populate the RecyclerView.
    private List<Object> mRecyclerViewItems = new ArrayList<>();
    private JSONObject jsonObject;
    private String text;
    private static final String TAG = "Main";
    private Object IOException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Create new fragment to display a progress spinner while the data set for the
            // RecyclerView is populated.
            Fragment loadingScreenFragment = new LoadingScreenFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, loadingScreenFragment);
            // Commit the transaction.
            transaction.commit();
            // XML - JSON, SAVE Json File
            xmljson();
            // Update the RecyclerView item's list with menu items.
            addMenuItemsFromJson();
            Log.i(TAG,"リスト"+mRecyclerViewItems);
            loadMenu();


            // AdMob
            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        }
    }

    public List<Object> getRecyclerViewItems() {
        return mRecyclerViewItems;
    }

    private void loadMenu() {
        // Create new fragment and transaction
        Fragment newFragment = new RecyclerViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * Adds {@link MenuItem}'s from a JSON file.
     */
    private void addMenuItemsFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            Log.i(TAG, "READDDDDDDDDDD:" + jsonDataString);

            //JSONArray menuItemsJsonArray = new JSONArray(jsonDataString);
            JSONObject menuItemsJsonObject = new JSONObject(jsonDataString);
            Log.i(TAG, "OBJECT;:" + menuItemsJsonObject);

            //JSONArray menuItemsJsonArray = new JSONArray();
            //menuItemsJsonArray = menuItemsJsonArray.put(menuItemsJsonObject);  // JSONObject - JSONArray
            //Log.i(TAG, "ARRAY:" + menuItemsJsonArray);

            JSONArray menuItemsJsonArray = menuItemsJsonObject.getJSONArray("items");






            //JSONObject obj = new JSONObject(jsonDataString);
            //JSONArray menuItemsJsonArray = menuItemsJsonArray1.getJSONArray("items");


            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {
                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);
                //String menuItemName = menuItemObject.getString("name");
                //String menuItemDescription = menuItemObject.getString("description");
                //String menuItemPrice = menuItemObject.getString("price");
                //String menuItemCategory = menuItemObject.getString("category");



                String menuItemTitle = menuItemObject.getString("title");
                String menuPubDate = menuItemObject.getString("pubDate");
                String menuDescription = menuItemObject.getString("description");
                String menuItemLink = menuItemObject.getString("link");

                MenuItem menuItem = new MenuItem(menuItemTitle,menuPubDate,menuDescription,menuItemLink);
                mRecyclerViewItems.add(menuItem);
                //}
            //} catch(IOException | JSONException exception){
            //    Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", exception);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 編集
    private String readJsonDataFromFile() throws IOException {

        FileInputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try{

            String jsonDataString = null;

            inputStream = openFileInput("sample5.json");
            //InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            //BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //StringBuffer stringBuffer = new StringBuffer();

            //inputStream = getResources().openRawResource(R.raw.ny);
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


            //String lines;
            //while ((lines = bufferedReader.readLine()) != null) {
            //    Log.i(TAG,"line::"+lines);
            //    //stringBuffer.append(lines + "\n");
            //    builder.append(jsonDataString);
            //    Toast.makeText(getApplicationContext(), "OPEN", Toast.LENGTH_SHORT).show();
            //}        } catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        return new String(builder);
        }





    private void xmljson() {
        Ion.with(getApplicationContext()).load("https://api.rss2json.com/v1/api.json?rss_url=http://rss.nytimes.com/services/xml/rss/nyt/Science.xml").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.i(TAG,"JSONNNNNNN:"+obj);


                    //SAVE
                    text = obj.toString();
                    Log.i(TAG,"TEXTTTTTT:"+obj);
                    FileOutputStream fileOutputStream = openFileOutput("sample5.json", MODE_PRIVATE);
                    fileOutputStream.write(text.getBytes());
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Text Saved", Toast.LENGTH_SHORT).show();

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


    //private void xmljson() {
    //    Ion.with(getApplicationContext()).load("http://rss.nytimes.com/services/xml/rss/nyt/Science.xml").asString(Charsets.UTF_8).setCallback(new FutureCallback<String>() {
    //        @Override
    //        public void onCompleted(Exception e, String result) {
    //            // XML → JSON
    //            try {
    //                jsonObject = XML.toJSONObject(result);
    //                Log.i(TAG,"json::"+jsonObject);
    //                text=jsonObject.toString();
    //                FileOutputStream fileOutputStream = openFileOutput("sample2.json", MODE_PRIVATE);
    //                fileOutputStream.write(text.getBytes());
    //                fileOutputStream.close();
    //                Toast.makeText(getApplicationContext(), "Text Saved", Toast.LENGTH_SHORT).show();
    //            } catch (JSONException e1) {
    //                e1.printStackTrace();
    //            } catch (FileNotFoundException e1) {
    //                e1.printStackTrace();
    //            } catch (IOException e1) {
    //                e1.printStackTrace();
    //            }
    //        }
    //    });
    //}





}
