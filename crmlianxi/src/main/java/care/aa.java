package care;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.melnykov.fab.sample.tools.crmMyDatabaseHelper;
import com.ricky.database.CenterDatabase;

import remind_yanhao.yh729_DBUtil;
import remind_yanhao.yh729_LocalDataBase;

/**
 * Created by renxin on 2015/8/27.
 */
public class aa {
    String  userid;
    Context context;
    public crmMyDatabaseHelper dbHelper;
    public static String remindid[][]=new String[100][2];
    String data[][]=new String[100][9];//uid type time remind note fid fname fsex fphone sate
    public  aa(Context context){
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
            data[i][j++]=cursor.getString(4);//TIME2
            data[i][j++]=cursor.getString(5);//note
            data[i][j++]=cursor.getString(6);//fid
            data[i][j++]=cursor.getString(7);//fname
            data[i][j++]=cursor.getString(8);//fsex
            data[i][j++]=cursor.getString(9);//fphone
            remindid[i][0]=cursor.getString(0);
            remindid[i][1]=setRemind(data[i][2],data[i][5]+","+data[i][7]+data[5],"care_remind",0,2,context);
            i++;
            //"create table care_table(_id integer primary " +
            //"key autoincrement, uid text,type text,time text,time2/4 text,note text,fid text,fname text,fsex text,fphone text,state text)";
        }
       // Toast.makeText(context, userid+" "+data[i-1][2]+" "+remindid[i-1][1], Toast.LENGTH_LONG).show();
        //Toast.makeText(context, "闹钟启动成功！", Toast.LENGTH_LONG).show();

    }
    public static String setRemind(String time,String content,String type,int way,int repeat,Context context){
        SQLiteDatabase db=(new yh729_LocalDataBase(context,null)).getDataBase();
        yh729_DBUtil.insert(db, "alarm_record", new String[]{"date_time", "content", "type", "way", "repeat"}
                , new String[]{yh729_DBUtil.String2Date(time).getTime() + "", content, type, way + "", repeat + ""});
        Cursor c=db.rawQuery("select * from alarm_record where date_time='"+ yh729_DBUtil.String2Date(time).getTime()+"' and type='"+type+"'",null);
        c.moveToLast();
        String id=c.getString(c.getColumnIndex("_id"));
        c.close();
        return id;
    }

    public static void cancelRemind(String id,Context context){
        SQLiteDatabase db=(new yh729_LocalDataBase(context,null)).getDataBase();
        db.execSQL("delete from alarm_record where _id='" + id + "'");
    }
}
