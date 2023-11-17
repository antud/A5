package com.example.a5;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//lets just try to do the first checkbox to include sketches first
public class StoryTellerActivity extends AppCompatActivity {

    SQLiteDatabase bigDb;

    private ArrayList<ListItem> listData;

    private CheckBox includeSketchesToggle;

    boolean isSketchIncluded;
    TextView tagField;
    EditText searchField;
    ListView lv;
    ListItemAdapter adapter;
    MyDrawingArea mda;
    String url = "https://api.textcortex.com/v1/texts/social-media-posts";
    String contextString;
    String keywordsString;
    EditText context;
    EditText keywords;
    TextView story;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_teller);

        listData = new ArrayList<>();
        adapter = new ListItemAdapter(this, R.layout.list_item, listData);

        lv = findViewById(R.id.story_image_list);
        lv.setAdapter(adapter);

        bigDb = this.openOrCreateDatabase("both", Context.MODE_PRIVATE, null);
        bigDb.execSQL("CREATE TABLE IF NOT EXISTS BOTH (PHOTO BLOB, DATE DATETIME, TAGS TEXT, TYPE TEXT)");

        searchField = findViewById(R.id.story_tag_search_text);

        includeSketchesToggle = findViewById(R.id.include_sketches_toggle);
        includeSketchesToggle.setChecked(true);

        includeSketchesToggle.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            updateImageList();
        }));

        ArrayList<ListItem> latestImages = showLatestImages();
        adapter.updateData(latestImages);

        Button backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(StoryTellerActivity.this, MainActivity.class);
            startActivity(intent);
        });



    }

    private void updateImageList() {
        ArrayList<ListItem> latestImages = showLatestImages();
        adapter.updateData(latestImages);
        adapter.notifyDataSetChanged();
    }

    public void onFind(View view) {
        showLatestImages();
    }


    public ArrayList<ListItem> showLatestImages() {
        isSketchIncluded = includeSketchesToggle.isChecked();

        String query;

        if (isSketchIncluded) {
            query = "SELECT * FROM BOTH";
        } else {
            query = "SELECT * FROM BOTH WHERE TYPE = 'photo'";
        }

        //this needs to have a check on the toggle, append WHERE TYPE = "type"
        Cursor c = bigDb.rawQuery(query, null);
        ArrayList<ListItem> latestImages = new ArrayList<>();

        //not sure why but for some reason move to last is pointing to the 2nd to last image??
        //so we need to go to the actual last one with next
        c.moveToLast();
        c.moveToNext();
        while (c.moveToPrevious()) {
            byte[] ba = c.getBlob(0);
            String date = c.getString(1);
            String tags = c.getString(2);

            latestImages.add(new ListItem(BitmapFactory.decodeByteArray(ba, 0, ba.length), tags + "\n" + date));
        }
        c.close();
        return latestImages;
    }

    void makeHttpRequest(String c, String[] k) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("context", c);
        data.put("max_tokens", 250);
        data.put("mode", "twitter");
        data.put("model", "chat-sophos-1");

        String[] keywords = k;
        data.put("keywords", new JSONArray(keywords));

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray outArr = data.getJSONArray("outputs");
                    JSONObject newRes = outArr.getJSONObject(0);
                    story.setText("Response: " + newRes.getString("text"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", new String(error.networkResponse.data));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Key.CORETEXT_API_KEY);
                return headers;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(req);
    }

    public void onSubmit(View view) throws JSONException {
        contextString = context.getText().toString();
        keywordsString = keywords.getText().toString();

        String[] k = keywordsString.split(",");

        for (int i = 0; i < k.length; i++) {
            k[i] = k[i].trim();
        }
        makeHttpRequest(contextString, k);
    }
}
