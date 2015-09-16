package care;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.ricky.database.CenterDatabase;

import remind_yanhao.yh729_DBUtil;
import remind_yanhao.yh729_LocalDataBase;

/**
 * Created by renxin on 2015/8/27.
 */
    public class boot_care extends BroadcastReceiver {

     String  userid;
    Context context;
    public crmMyDatabaseHelper dbHelper;
    static String remindid[]=new String[100];
    String data[][]=new String[100][9];//uid type time remind note fid fname fsex fphone sate
        @Override
        public void onReceive(Context context,Intent intent){
          /* // Toast.makeText(context, "�?机启动了", Toast.LENGTH_LONG).show();
            Intent intent1=new Intent();
            intent1.setClass(context, myservice.class);
            context.startService(intent1);*/
            this.context=context;
            dbHelper = new crmMyDatabaseHelper(context, "customer.db3", 1);
            CenterDatabase centerDatabase = new CenterDatabase(context, null);
            userid = centerDatabase.getUID();
            centerDatabase.close();
            Cursor cursor =dbHelper.getReadableDatabase().rawQuery("select * from care_table where uid=?",new String[]{userid});
            int i=0;
            while (cursor.moveToNext()){
                int j=0;
                data[i][j++]=cursor.getString(2);//type
                data[i][j++]=cursor.getString(3);//time
                data[i][j++]=cursor.getString(4);//remind
                data[i][j++]=cursor.getString(5);//note
                data[i][j++]=cursor.getString(6);//fid
                data[i][j++]=cursor.getString(7);//fname
                data[i][j++]=cursor.getString(8);//fsex
                data[i][j++]=cursor.getString(9);//fphone
                remindid[i]=setRemind(data[i][1],data[i][5]+","+data[i][7]+data[5],"care_remind",0,2);
                i++;
            }
        }

    public String setRemind(String time,String content,String type,int way,int repeat){
        SQLiteDatabase db=(new yh729_LocalDataBase(context,null)).getDataBase();
        yh729_DBUtil.insert(db,"alarm_record",new String[]{"date_time","content","type","way","repeat"}
                ,new String[]{yh729_DBUtil.String2Date(time).getTime()+"",content,type,way+"",repeat+""});
        Cursor c=db.rawQuery("select * from alarm_record where date_time='"+ yh729_DBUtil.String2Date(time).getTime()+"' and type='"+type+"'",null);
        c.moveToLast();
        String id=c.getString(c.getColumnIndex("_id"));
        c.close();
        return id;
    }

    public void cancelRemind(String id){
        SQLiteDatabase db=(new yh729_LocalDataBase(context,null)).getDataBase();
        db.execSQL("delete from alarm_record where _id='" + id + "'");
    }
    }

