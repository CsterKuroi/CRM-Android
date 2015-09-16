package com.example.spinel.myapplication.Form;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spinel.myapplication.bpmMainActivity;
import com.example.spinel.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Spinel on 2015/8/20.
 */

public class bpmRecorder {
    private bpmFormActivity activity;
    public String filename;

    //---------------------界面组件---------------------
    public boolean hasVoice = false;
    public boolean isUpload = false;
    public Button recordBtn, playBtn;
    public bpmForm_Group form_group;

    //---------------------声音属性---------------------
    private static int frequency = 11025;
    private static int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static int bitrate = AudioFormat.ENCODING_PCM_16BIT;

    //---------------------录音---------------------
    private AudioRecord audioRecord = null;
    private int recBufSize = 0;
    public boolean isRecording = false;
    private Thread recordingThread = null;

    //---------------------播放---------------------
    private int playBufSize = 0;
    private AudioTrack audioTrack = null;
    public boolean isPlaying = false;
    private PCMAudioTrack m_player;

    //------------------------------------------
    public bpmRecorder(bpmFormActivity activity, bpmForm_Group form_group, String itemId, String value){
        this.activity = activity;
        this.form_group = form_group;


        //
        if(value.isEmpty()) {
            filename = bpmMainActivity.userId + "_" + bpmMainActivity.currentStepId + "_" + bpmMainActivity.currentProcessId + "_" + itemId;
            deleteFile(filename);
        }
        else {
            filename = value;
            if(existsFile(filename))
                hasVoice = true;
            else {
                if(activity.dFiles==null)
                    activity.dFiles = new ArrayList<>();
                activity.dFiles.add(filename);
            }
        }

    }

    public void refreshFile(){
        if(existsFile(filename))
            hasVoice = true;
        Log.e("recorder refresh", hasVoice?"true":"false");
    }

    public boolean existsFile(String name){

        String path = activity.getFilesDir().getAbsolutePath()+"/"+name;
        File file = new File(path);
        return file.exists();
    }

    public boolean deleteFile(String name){

        String path = activity.getFilesDir().getAbsolutePath()+"/"+name;
        File file = new File(path);
        if(file.exists())
            return file.delete();
        return false;
    }

    private void makeToast(String str){
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }

    //---------------------界面组件---------------------
    public void setButton(Button record, Button play){
        recordBtn = record;
        playBtn = play;


        if(hasVoice && !isRecording) {
            playBtn.setEnabled(true);
            if(!isPlaying)
                playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_start_enable));
            else
                playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_stop_enable));
        }
        else {
            playBtn.setEnabled(false);
            playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_start_unenable));
        }

        if(isPlaying)
            recordBtn.setEnabled(false);
        else
            recordBtn.setEnabled(true);

        if(isRecording)
            recordBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_record_enable));
        else
            recordBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_record_unenable));

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording)
                    stopRecording();
                else
                    startRecording();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                    stopPlay();
                else
                    startPlay();
            }
        });

    }

    public void setButton(Button play){
        playBtn = play;
        recordBtn = null;

        if(hasVoice) {
            playBtn.setEnabled(true);
            if(!isPlaying)
                playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_start_enable));
            else
                playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_stop_enable));
        }
        else {
            playBtn.setEnabled(false);
            playBtn.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.bpm_start_unenable));
        }


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                    stopPlay();
                else
                    startPlay();
            }
        });
    }

    //---------------------开始录音---------------------
    public void startRecording(){
        recBufSize = AudioRecord.getMinBufferSize(frequency, channel, bitrate);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channel, bitrate, recBufSize);

        audioRecord.startRecording();
        makeToast("开始录音...");
        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "Recording Thread");

        recordingThread.start();

        if(form_group.adapter!=null)
            form_group.adapter.notifyDataSetChanged();
        else if(form_group.adapter_read!=null)
            form_group.adapter_read.notifyDataSetChanged();
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[recBufSize];

        FileOutputStream os = null;
        try {
            os = activity.openFileOutput(filename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;

        if(os!=null){
            while(isRecording){
                read = audioRecord.read(data, 0, recBufSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //---------------------停止录音---------------------
    public void stopRecording(){
        if(audioRecord!=null){
            isRecording = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;

            makeToast("录音结束");
            hasVoice = true;

            if(form_group.adapter!=null)
                form_group.adapter.notifyDataSetChanged();
            else if(form_group.adapter_read!=null)
                form_group.adapter_read.notifyDataSetChanged();

        }
    }

    //---------------------播放---------------------
    public void startPlay(){
        m_player = new PCMAudioTrack();
        m_player.init();
        m_player.start();

        if(form_group.adapter!=null)
            form_group.adapter.notifyDataSetChanged();
        else if(form_group.adapter_read!=null)
            form_group.adapter_read.notifyDataSetChanged();
    }

    public void stopPlay(){
        m_player.free();
        m_player = null;

        if(form_group.adapter!=null)
            form_group.adapter.notifyDataSetChanged();
        else if(form_group.adapter_read!=null)
            form_group.adapter_read.notifyDataSetChanged();
    }

    public void createAudioTrack(){
        playBufSize= AudioTrack.getMinBufferSize(frequency,
                channel, bitrate);


        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                channel, bitrate,
                playBufSize, AudioTrack.MODE_STREAM);
    }

    class PCMAudioTrack extends Thread {

        protected byte[] m_out_bytes;

        FileInputStream in;

        public void init() {
            try {
                in = activity.openFileInput(filename);


                isPlaying = true;

                createAudioTrack();

                m_out_bytes = new byte[playBufSize];

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void free() {
            isPlaying = false;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.d("sleep exceptions...\n", "");
            }

        }

        public void run() {
            byte[] bytes_pkg = null;
            audioTrack.play();
            int length = 0;
            while (isPlaying) {
                try {
                    int i = in.read(m_out_bytes);
                    length+=i;
                    if(i<0){
                        isPlaying = false;

                        Message message = Message.obtain();
                        message.obj = bpmRecorder.this;

                        activity.handler.sendMessage(message);
                    }
                    bytes_pkg = m_out_bytes.clone();
                    audioTrack.write(bytes_pkg, 0, bytes_pkg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("total length: "+length);

            audioTrack.stop();
            audioTrack = null;
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
