package com.example.sinki.audioplayer;

import android.content.ContentResolver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PlayAudio extends AppCompatActivity {
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    ImageButton btnPlayPause,btnNext,btnBack;
    ListView lstMusic;
    ArrayList<String> dsMusic;
    ArrayAdapter<String>adapterMusic;
    MusicTask task;
    boolean prepared = false;
    int STEP = 2000;

    String RAW_DEFAULT_FILE1 = "$change";
    String RAW_DEFAULT_FILE2 = "serialVersionUID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);
        addControls();
        addEvents();
    }

    private void addEvents() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if(input)
                {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!= null)
                {
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.pause();
                        btnPlayPause.setImageResource(R.drawable.ic_play_circle_outline_black);
                    }
                    else {
                        mediaPlayer.start();
                        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_black);
                        //playCyCle();
                    }
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nowValue = mediaPlayer.getCurrentPosition();
                nowValue += STEP;
                mediaPlayer.seekTo(nowValue);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nowValue = mediaPlayer.getCurrentPosition();
                nowValue -= STEP;
                mediaPlayer.seekTo(nowValue);
            }
        });

        lstMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int resId = getResources().getIdentifier(dsMusic.get(position), "raw", getPackageName());
                if(mediaPlayer!=null&&mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(),resId);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
                updateSeekBar();
            }
        });
    }

    private void addControls() {
        handler = new Handler();
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        //playCyCle();
        btnPlayPause = (ImageButton) findViewById(R.id.btnPlayPause);
        btnPlayPause.setImageResource(R.drawable.ic_pause_circle_outline_black);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnNext = (ImageButton) findViewById(R.id.btnNext);

        lstMusic = (ListView) findViewById(R.id.lstMusic);
        dsMusic = getAllSongs();
        adapterMusic = new ArrayAdapter<String>(PlayAudio.this,android.R.layout.simple_list_item_1,dsMusic);
        lstMusic.setAdapter(adapterMusic);
        mediaPlayer = new MediaPlayer();

    }
    public void updateSeekBar()
    {
        seekBar.setMax(mediaPlayer.getDuration());
        task = new MusicTask();
        task.execute();
    }
    public ArrayList<String> getAllSongs()
    {
        ArrayList<String> songList = new ArrayList<>();
        try
        {
            Field field[] = R.raw.class.getDeclaredFields();
            for (int i=0;i<field.length;i++)
            {
                Field f = field[i];
                if(f.getName()!=RAW_DEFAULT_FILE1&&f.getName()!=RAW_DEFAULT_FILE2)
                    songList.add(f.getName());
            }
            return songList;
        }
        catch (Exception ex)
        {
            Log.e("LOI",ex.toString());
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }
    private class MusicTask extends AsyncTask<Void,Integer,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            seekBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (mediaPlayer!=null)
            {
                int nowValue = mediaPlayer.getCurrentPosition();
                publishProgress(nowValue);
                SystemClock.sleep(1000);
            }

            return null;
        }
    }
}
