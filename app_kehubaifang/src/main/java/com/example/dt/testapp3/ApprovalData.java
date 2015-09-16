package com.example.dt.testapp3;

import java.util.ArrayList;

/**
 * Created by dt on 2015/8/20.
 */
public class ApprovalData {
    private String uid;
    private String uname;
    private int processIdLei;
    private String client;
    private double departTime;
    private int[] partnerId;
    private String[] partnerName;
    private String planTarget;
    //    private int processIdCui;
    private int companyId;
    private String companyName;
    private String address;
    private int[] contactId;
    private String[] contactName;

    public ApprovalData(String _uid, String _uname, int[] _partnerId, String[] _partenrName,
                        String _client, double _date, String _planTarget, int _processIdLei) {
        uid = _uid;
        uname = _uname;
        partnerId = _partnerId;
        partnerName = _partenrName;
        client = _client;
        departTime = _date;
        planTarget = _planTarget;
        processIdLei = _processIdLei;

        ArrayList<String> contactList = new ArrayList<>();
        ArrayList<Integer> contactIdList = new ArrayList<>();
        if (_client != null && !_client.equals("")) {
            String[] tmpData = _client.split(" ");
            String[] coms = tmpData[0].split("_");
            companyId = coms[0].equals("")?0:Integer.parseInt(coms[0]);
            companyName = coms.length<2?"":coms[1];
            for (int i = 1; i < tmpData.length; i += 2) {
                switch (tmpData[i]) {
                    case "address":
                        address = tmpData[i + 1];
                        break;
                    case "contact":
                        String[] IdName = tmpData[i + 1].split("_");
                        contactIdList.add(Integer.parseInt(IdName[0]));
                        contactList.add(IdName[1]);
                        break;
                }
            }
            int n = contactIdList.size();
            contactId = new int[n];
            for (int i = 0; i < n; i++)
                contactId[i] = contactIdList.get(i);
            contactName = contactList.toArray(new String[]{});
        }
    }

//    public void setProcessIdCui(int _processIdCui){processIdCui = _processIdCui;}

    public String getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public int getProcessIdLei() {
        return processIdLei;
    }

    public String getClient() {
        return client;
    }

    public double getDepartTime() {
        return departTime;
    }

    public int[] getPartnerId() {
        return partnerId;
    }

    public String[] getPartnerName() {
        return partnerName;
    }

    public String getPlanTarget() {
        return planTarget;
    }

    //    public int getProcessIdCui(){return processIdCui;}
    public int getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public int[] getContactId() {
        return contactId;
    }

    public String[] getContactName() {
        return contactName;
    }

}
