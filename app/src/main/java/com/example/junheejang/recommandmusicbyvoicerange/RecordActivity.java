package com.example.junheejang.recommandmusicbyvoicerange;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    final private static String RECORDED_FILE = "/sdcard/recordfile/example.mp4";

    Button startBtn, stopBtn, listenBtn;
    Button resultBtn;
    ImageView micImage;
    TextView timeText;
    boolean timebool,playtimebool;
    MediaPlayer player;
    MediaRecorder recorder;
    int m_time ;
    int s_time  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        listenBtn = (Button) findViewById(R.id.listenBtn);
        resultBtn = (Button) findViewById(R.id.resultBtn);
        micImage = (ImageView) findViewById(R.id.imageView2);
        timeText = (TextView) findViewById(R.id.timeText);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micImage.setImageResource(R.drawable.amic);
                timebool = true;
                playtimebool = false ;
                new timeBackground().execute();

                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }
                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(RECORDED_FILE);

                try {
                    recorder.prepare();
                    recorder.start();
                } catch (Exception ex) {
                    Log.e("recommandMusic", "Exception : ", ex);
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micImage.setImageResource(R.drawable.bmic);
                timebool = false;

                if (recorder == null) {
                    return;
                }

                recorder.stop();
                recorder.release();
                recorder = null;
            }
        });

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playtimebool = true ;
                    new playtimeBackground().execute() ;
                    playAudio(RECORDED_FILE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecordResultActivity.class);
                startActivity(intent);
            }
        });
    }

    class timeBackground extends AsyncTask {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            m_time = 0;
            s_time = 0;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                while (timebool) {
                    Thread.sleep(1000);
                    publishProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            s_time += 1;
            if (s_time == 60) {
                s_time = 0;
                m_time += 1;
            }

            timeText.setText(String.format("%02d", m_time) + ":" + String.format("%02d", s_time));
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            timeText.setText("00:00");

            micImage.setImageResource(R.drawable.bmic);
            timebool = false;

            if (recorder == null) {
                return;
            }

            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    class playtimeBackground extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timebool = false ;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                while (playtimebool) {
                    Thread.sleep(1000);
                    publishProgress();

                    if(s_time==0&&m_time==0) break ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            s_time -= 1;
            if (s_time == -1) {
                s_time = 59;
                m_time -= 1;
            }

            timeText.setText(String.format("%02d", m_time) + ":" + String.format("%02d", s_time));
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            timeText.setText("00:00");
        }
    }

    protected void onPause() {
        super.onPause();

        timebool = false;
        playtimebool = false ;
    }

    private void playAudio(String r) throws Exception {
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(r) ;
        player.prepare();
        player.start();
    }

    private void killMediaPlayer() {
        if(player != null)
        {
            try
            {
                player.release();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
