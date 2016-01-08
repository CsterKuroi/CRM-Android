package com.example.dt.testapp3.VisitNetworks;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.example.dt.testapp3.Graphics.VisitEvaluationFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Spinel on 2015/8/20.
 *
 *
 * Created by dt on 2015/8/27.
 */
public class GodLiuEvaluationPlayer {
    private String filename;
    private String dir = Environment.getExternalStorageDirectory().getPath() +
                    File.separator + "SaleCircle" + File.separator + "VisitCensorTape";

    private VisitEvaluationFragment fragment;
    public GodLiuEvaluationPlayer(String _filename){
        filename = _filename;
    }

    public GodLiuEvaluationPlayer setFragment(VisitEvaluationFragment fragment){
        this.fragment = fragment;
        return this;
    }

    //---------------------声音属性---------------------
    private static int frequency = 11025;
    private static int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static int bitrate = AudioFormat.ENCODING_PCM_16BIT;

    //---------------------播放---------------------
    private int playBufSize = 0;
    private AudioTrack audioTrack = null;
    public boolean isPlaying = false;
    private PCMAudioTrack m_player;

    //---------------------播放---------------------
    public void startPlay(){
        m_player = new PCMAudioTrack();
        m_player.init();
        m_player.start();
    }

    public void stopPlay(){
        m_player.free();
        m_player = null;
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
                in = new FileInputStream(dir + File.separator + filename);


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


            File file = new File(dir + File.separator + filename);
            if(file.exists())
                System.out.println("file exists");
            else
                System.out.println("file not exists");
        }

        public void run() {
            byte[] bytes_pkg = null;
            if (audioTrack == null){
                fragment.setButtonText.sendMessage(new Message());
                return;
            }
            audioTrack.play();
            int length = 0;
            while (isPlaying) {
                try {
                    int i = in.read(m_out_bytes);
                    length+=i;
                    if(i<0){
                        isPlaying = false;
                        fragment.setButtonText.sendMessage(new Message());
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
