package com.example.junheejang.recommandmusicbyvoicerange;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RecordResultActivity extends AppCompatActivity {

    String resultString ;
    ListView list ;
    MusicAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_result);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        list = (ListView) findViewById(R.id.list);
        adapter = new MusicAdapter();
        resultString = "2옥 솔#";

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://icd0422.cafe24.com/music.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURlConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int c = 0;
                while (c < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(c);

                    if(resultString.equals(object.getString("high_octave"))) {
                        adapter.addItem(new MusicItem(object.getString("title"), object.getString("singer"), object.getString("high_octave")));
                    }
                    c++;
                }

                list.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MusicItemView extends LinearLayout
    {
        TextView title ;
        TextView singer ;
        TextView high_octave ;

        public MusicItemView(Context context) {
            super(context);
            init(context) ;
        }

        public MusicItemView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context) ;
        }

        public void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            inflater.inflate(R.layout.music_item, this, true) ;

            title = (TextView) findViewById(R.id.title) ;
            singer= (TextView) findViewById(R.id.singer) ;
            high_octave = (TextView) findViewById(R.id.high_octave) ;
            high_octave.setTextColor(Color.RED);
        }

        public void setTitle(String s)
        {
            title.setText(s);
        }

        public void setSinger(String s)
        {
            singer.setText(s);
        }

        public void setHigh_octave(String s)
        {
            high_octave.setText(s);
        }
    }

    public class MusicItem
    {
        String title ;
        String  singer ;
        String  high_octave ;

        public MusicItem(String a, String b, String c) {
            title = a;
            singer = b;
            high_octave = c;
        }

        public String getTitle()
        {
            return title;
        }

        public String getSinger()
        {
            return singer;
        }

        public String getHigh_octave()
        {
            return high_octave;
        }
    }

    class MusicAdapter extends BaseAdapter
    {
        ArrayList<MusicItem> items = new ArrayList<MusicItem>() ;

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MusicItem item)
        {
            items.add(item) ;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        void clear()
        {
            items.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MusicItemView view = new MusicItemView(getApplicationContext()) ;
            MusicItem item = items.get(position) ;
            view.setTitle(item.getTitle());
            view.setSinger(item.getSinger());
            view.setHigh_octave(item.getHigh_octave());

            return view;
        }
    }
}
