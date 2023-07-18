package com.example.minimusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;

import io.alterac.blurkit.BlurLayout;

public class PlaySong extends AppCompatActivity {


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView, textStart, textStop;
    ImageView previous, play, next, playlist, albumArt;
    ImageButton loop;
    Boolean repeatFlag = false;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    BlurLayout blur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        playlist = findViewById(R.id.playlist);
        loop = findViewById(R.id.loop);
        textStart = findViewById(R.id.textStart);
        textStop= findViewById(R.id.textStop);
        blur = findViewById(R.id.blur);
        albumArt = findViewById(R.id.albumArt);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });




        updateSeek = new Thread() {
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        try {
                            String startTime = createTime(mediaPlayer.getCurrentPosition());
                            textStart.setText(startTime);
                        } catch (Exception e1) {

                        }
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        String endTime = createTime(mediaPlayer.getDuration());
        textStop.setText(endTime);
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.playbtn);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pausebtn);
                    mediaPlayer.start();
                }
            }


        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if ( repeatFlag)
                {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    seekBar.setProgress(0);
                }else {
                    play.setImageResource(R.drawable.playbtn);
                    next.performClick();
                    seekBar.setProgress(0);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pausebtn);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                String endTime = createTime(mediaPlayer.getDuration());
                textStop.setText(endTime);
                updateAlbumArt();


            }
        });

        //sample change
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pausebtn);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                String endTime = createTime(mediaPlayer.getDuration());
                textStop.setText(endTime);
                updateAlbumArt();

            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaySong.this, MainActivity.class);
                startActivity(intent);
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });

        updateAlbumArt();

}

    private void updateAlbumArt(){

        Bitmap bitmap = createAlbumArt(songs.get(position).toString());
        albumArt.setImageBitmap(bitmap);

    }

    public Bitmap createAlbumArt(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            byte[] embedPic = retriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length);
            Log.d("Debug", "Found album art");
        } catch (Exception e) {
            Log.d("Debug", "Cannot find album art");
            e.printStackTrace();
            return drawableToBitmap(getResources().getDrawable(R.drawable.bg));
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private String createTime(int duration) {

        int  min = (duration/ 1000) / 60;
        int sec = (duration / 1000);
        return min+":"+sec;
    }

    public void repeatSong(View view) {
        if ( repeatFlag){
            loop.setBackgroundResource(R.drawable.repeat_off);
        }
        else{
            loop.setBackgroundResource(R.drawable.repeat_on);
        }
        repeatFlag = !repeatFlag;
    }

    @Override
    protected void onStart() {
        super.onStart();
        blur.startBlur();
    }

    @Override
    protected void onStop() {
        blur.pauseBlur();
        super.onStop();

    }
}

