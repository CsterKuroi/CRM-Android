package com.example.dt.testapp3.Graphics;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dt.testapp3.R;
import com.example.dt.testapp3.VisitNetworks.GodLiuEvaluationPlayer;


/**
 * A simple {@link Fragment} subclass.
 */
public class VisitEvaluationFragment extends Fragment {

    private GodLiuEvaluationPlayer player;
    private boolean isPlaying = false;

    private Button tapeButton;

    public Handler setButtonText;

    private MediaPlayer mplayer;
    public VisitEvaluationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_visit_evaluation, container, false);

        TextView Level = (TextView) layout.findViewById(R.id.EvaluationLevel);
//        Spinner Tape = (Spinner) layout.findViewById(R.id.EvaluationTape);
        TextView Detail = (TextView) layout.findViewById(R.id.Evaluation);
        tapeButton = (Button) layout.findViewById(R.id.playButton);

        Bundle bundle = getArguments();
        final String tapeStr = bundle.getStringArray("tapes")[0];
        final int lv = bundle.getInt("level");
        switch (lv){
            case 5:
                Level.setText("评价：优秀");
                break;
            case 6:
                Level.setText("评价：良好");
                break;
            case 7:
                Level.setText("评价：合格");
                break;
            case 8:
                Level.setText("评价：较差");
                break;
        }
        Detail.setText(bundle.getString("detail"));

        tapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null){
                    if (!player.isPlaying){
                        if (!tapeStr.equals("")) {
                            player = new GodLiuEvaluationPlayer(tapeStr).setFragment(VisitEvaluationFragment.this);
                            player.startPlay();
                            tapeButton.setText("停止播放");
                            isPlaying = true;
                        }
                    }
                    else {
                        player.stopPlay();
                        tapeButton.setText("播放录音");
                    }

                }
                else {
                    player = new GodLiuEvaluationPlayer(tapeStr).setFragment(VisitEvaluationFragment.this);
                    player.startPlay();
                    tapeButton.setText("停止播放");
                    isPlaying = true;
                }

            }
        });

        setButtonText = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                setText("播放录音");
            }
        };

        return layout;
    }

    private void setText(String str){
        tapeButton.setText(str);
    }
}
