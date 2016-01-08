package com.example.dt.testapp3.VisitDataPackage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import com.example.dt.testapp3.VisitDataPackage.ApprovalData;

/**
 * Created by dt on 2022/8/9.
 */
public class VisitData {
    private int id;
    private String uid;
    private String submitId;
    private String submitName;
    private String date;
    private double dateInt;
    private String place;
    private int companyId;
    private String company;
    private int[] targetId;
    private String[] target;
    private String todo;
    private int mytype;
    private String record;
    private int result;
    private String remark;
    private String tape;
    private int condition; //等待拜访0/正在拜访1/拜访结束2/已提交记录3/已提交报告4/评价：优5良6中7差8,etc
    private int[] partnerId;
    private String[] partnerName;
    private int processIdLei;
    private int processIdCui;
    private boolean isTemp;
    private String censorId;
    private String censorName;

    public VisitData(int _id, String _uid, String _submitId, String _submitName, String _date,
                     double _dateInt, String _place, int _companyId, String _company,
                     int[] _targetId, String[] _target, String _todo, int _mytype,
                     String _record, int _result, String _remark, String _tape,
                     int _condition, int[] _partnerId, String[] _partner,
                     int _pidL, int _pidC, boolean _isTemp, String _censorId, String _censorName) {
        //for json analysis
        id = _id;
        uid = _uid;
        submitId = _submitId;
        submitName = _submitName;
        date = _date;
        dateInt = _dateInt;
        place = _place;
        companyId = _companyId;
        company = _company;
        targetId = _targetId;
        target = _target;
        todo = _todo;
        mytype = _mytype;
        record = _record;
        result = _result;
        remark = _remark;
        tape = _tape;
        condition = _condition;
        partnerId = _partnerId;
        partnerName = _partner;
        processIdLei = _pidL;
        processIdCui = _pidC;
        isTemp = _isTemp;
        censorId = _censorId;
        censorName = _censorName;
    }

    public VisitData(int _id, String _uid, String _submitId, String _submitName, String _date,
                     String _place, int _companyId, String _company, int[] _targetId,
                     String[] _target, String _todo, int _mytype, String _record,
                     int _result, String _remark, String _tape) {
        // temp visit
        id = _id;
        uid = _uid;
        submitId = _submitId;
        submitName = _submitName;
        date = _date;
        place = _place;
        companyId = _companyId;
        company = _company;
        targetId = _targetId;
        target = _target;
        todo = _todo;
        mytype = _mytype;
        record = _record;
        result = _result;
        remark = _remark;
        tape = _tape;

        partnerId = new int[0];
        partnerName = new String[0];
        condition = 0;

        isTemp = true;
    }

    public VisitData(String _uid, ApprovalData ad) {
        //for Approval info
        uid = _uid;
        submitId = ad.getUid();
        submitName = ad.getUname();
        processIdLei = ad.getProcessIdLei();
        dateInt = ad.getDepartTime();
        partnerId = ad.getPartnerId();
        partnerName = ad.getPartnerName();
        todo = ad.getPlanTarget();
        companyId = ad.getCompanyId();
        company = ad.getCompanyName();
        place = ad.getAddress();
        targetId = ad.getContactId();
        target = ad.getContactName();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        date = format.format(new Date((long) (dateInt * 1000)));
        result = 0;
        condition = 0;

        isTemp = false;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public VisitData setId(int _id) {
        id = _id;
        return this;
    }

    public VisitData setProcessIdLei(int _processIdLei) {
        processIdLei = _processIdLei;
        return this;
    }

    public VisitData setProcessIdCui(int _processIdCui) {
        processIdCui = _processIdCui;
        return this;
    }

    public VisitData setCensor(String _censorId, String _censorName) {
        censorId = _censorId;
        censorName = _censorName;
        return this;
    }

    public VisitData setCondition(int _condition) {
        condition = _condition;
        return this;
    }


    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getSubmitId() {
        return submitId;
    }

    public String getSubmitName() {
        return submitName;
    }

    public String getDate() {
        return date;
    }

    public double getDateInt() {
        return dateInt;
    }

    public String getPlace() {
        return (place == null ? "" : place);
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getCompany() {
        return (company == null ? "" : company);
    }

    public int[] getTargetId() {
        return targetId;
    }

    public String[] getTarget() {
        return (target == null ? new String[0] : target);
    }

    public String getTodo() {
        return todo;
    }

    public int getMytype() {
        return mytype;
    }

    public String getRecord() {
        return (record == null ? "" : record);
    }

    public int getResult() {
        return result;
    }

    public String getRemark() {
        return (remark == null ? "" : remark);
    }

    public String getTape() {
        return (tape == null ? "" : tape);
    }

    public int getCondition() {
        return condition;
    }

    public int[] getPartnerId() {
        return partnerId;
    }

    public String[] getPartnerName() {
        return partnerName;
    }

    public int processIdLei() {
        return processIdLei;
    }

    public int processIdCui() {
        return processIdCui;
    }

    public String getCensorId() {
        return censorId == null ? "" : censorId;
    }

    public String getCensorName() {
        return censorName == null ? "" : censorName;
    }

    public String getAddSQLString() {
        return id + "," +
                "'" + (uid == null ? "" : uid) + "'," +
                "'" + (submitId == null ? "" : submitId) + "'," +
                "'" + (submitName == null ? "" : submitName) + "'," +
                "'" + (date == null ? "" : date) + "'," +
                dateInt + "," +
                "'" + (place == null ? "" : place) + "'," +
                companyId + "," +
                "'" + (company == null ? "" : company) + "'," +
                "'" + (targetId == null ? "[]" : Arrays.toString(targetId)) + "'," +
                "'" + (target == null ? "[]" : Arrays.toString(target)) + "'," +
                "'" + (todo == null ? "" : todo) + "'," +
                mytype + "," +
                "'" + (record == null ? "" : record) + "'," +
                result + "," +
                "'" + (remark == null ? "" : remark) + "'," +
                "'" + (tape == null ? "" : tape) + "'," +
                condition + "," +
                "'" + (partnerId == null ? "[]" : Arrays.toString(partnerId)) + "'," +
                "'" + (partnerName == null ? "[]" : Arrays.toString(partnerName)) + "'," +
                processIdLei + "," +
                processIdCui + "," +
                (isTemp ? 1 : 0) + "," +
                "'" + (censorId == null ? "" : censorId) + "'," +
                "'" + (censorName == null ? "" : censorName) + "'";
    }

    public String getUpdateSQLString() {
        return "id = " + id + "," +
                "uid = '" + (uid == null ? "" : uid) + "'," +
                "submitId = '" + (submitId == null ? "" : submitId) + "'," +
                "submitName = '" + (submitName == null ? "" : submitName) + "'," +
                "date = '" + (date == null ? "" : date) + "'," +
                "dateInt = " + dateInt + "," +
                "place = '" + (place == null ? "" : place) + "'," +
                "companyId = " + companyId + "," +
                "company = '" + (company == null ? "" : company) + "'," +
                "targetId = '" + (targetId == null ? "[]" : Arrays.toString(targetId)) + "'," +
                "target = '" + (target == null ? "[]" : Arrays.toString(target)) + "'," +
                "todo = '" + (todo == null ? "" : todo) + "'," +
                "type = " + mytype + "," +
                "record = '" + (record == null ? "" : record) + "'," +
                "result = " + result + "," +
                "remark = '" + (remark == null ? "" : remark) + "'," +
                "tape = '" + (tape == null ? "" : tape) + "'," +
                "condition = " + condition + "," +
                "partnerId = '" + (partnerId == null ? "[]" : Arrays.toString(partnerId)) + "'," +
                "partnerName = '" + (partnerName == null ? "[]" : Arrays.toString(partnerName)) + "'," +
                "pidL = " + processIdLei + "," +
                "pidC = " + processIdCui + "," +
                "isTemp = " + (isTemp ? 1 : 0) + "," +
                "censorId = '" + (censorId == null ? "" : censorId) + "'," +
                "censorName = '" + (censorName == null ? "" : censorName) + "'";
    }
}
