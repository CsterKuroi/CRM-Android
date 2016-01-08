package com.example.dt.testapp3.VisitNetworks;

import android.os.Message;

import com.example.dt.testapp3.VisitDataPackage.VisitData;
import com.example.dt.testapp3.Graphics.VisitMainActivity;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dt on 2015/8/28.
 */
public class VisitSyncNewApproval implements Runnable{

    private CountDownLatch latch;
    private ArrayList<VisitData> app2dataList;
    private VisitMainActivity act;

    public VisitSyncNewApproval setAct(VisitMainActivity act){
        this.act = act;
        return this;
    }

    public VisitSyncNewApproval setLatch(CountDownLatch latch){
        this.latch = latch;
        return this;
    }

    public VisitSyncNewApproval setList(ArrayList<VisitData> app2dataList){
        this.app2dataList = app2dataList;
        return this;
    }

    @Override
    public void run() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        act.syncHandler.sendMessage(new Message());
    }
}
