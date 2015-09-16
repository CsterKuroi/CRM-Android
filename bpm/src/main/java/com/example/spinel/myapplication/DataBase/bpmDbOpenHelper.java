package com.example.spinel.myapplication.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Spinel on 2015/7/25.
 */
public class bpmDbOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "placedb.db";
    private static final int DATABASE_VERSION = 1;
    public bpmDbOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        //时间戳数据库
        db.execSQL("CREATE TABLE timestamp(userId TEXT NOT NULL, type TEXT NOT NULL, time TEXT NOT NULL)");


        //bpm list
        db.execSQL("CREATE TABLE bpmlist(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT NOT NULL, " +
                "activityId TEXT NOT NULL, " +
                "activityName TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "datas TEXT NOT NULL, " +
                "info TEXT NOT NULL)" );

        //草稿
        db.execSQL("CREATE TABLE draft(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "activityId TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "datas TEXT NOT NULL, " +
                "blankdatas TEXT NOT NULL, " +
                "title TEXT)" );

        //地址
        db.execSQL("CREATE TABLE swf_area\n" +
                "(\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "    parent_id INT NOT NULL DEFAULT 0,\n" +
                "    name TEXT NOT NULL,\n" +
                "    sort INT NOT NULL DEFAULT 0\n" +
                ")");

        //task
        db.execSQL("CREATE TABLE task(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT," +

                "activityId TEXT, " +
                "activityName TEXT, " +
                "taskId TEXT," +
                "status TEXT," +
                "submitUser TEXT," +
                "processIds TEXT, " +
                "startTime TEXT, " +
                "endTime TEXT)" );

        //process
        db.execSQL("CREATE TABLE process(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT," +
                "type TEXT," +

                "activityId TEXT, " +
                "activityName TEXT, " +
                "processId TEXT," +
                "status TEXT," +
                "stepId TEXT," +
                "stepName TEXT," +
                "submitUserId TEXT, " +
                "submitUserName TEXT, " +
                "startTime TEXT, " +
                "endTime TEXT)" );


        db.execSQL("INSERT INTO swf_area VALUES(0101, 0100, '合肥', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0102, 0100, '安庆', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0103, 0100, '毫州', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0104, 0100, '蚌埠', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0105, 0100, '滁州', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0106, 0100, '巢湖', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0107, 0100, '池州', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0108, 0100, '阜阳', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0109, 0100, '淮北', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0110, 0100, '淮南', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0111, 0100, '黄山站', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0112, 0100, '六安', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0113, 0100, '马鞍山', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0114, 0100, '宿州', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0115, 0100, '铜陵', 1);" );
        db.execSQL("INSERT INTO swf_area VALUES(0116, 0100, '芜湖', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0117, 0100, '宣城', 1);");
        db.execSQL("INSERT INTO swf_area VALUES(0200, 0, '澳门', 2);");
        db.execSQL("INSERT INTO swf_area VALUES(0300, 0, '北京', 3);");
        db.execSQL("INSERT INTO swf_area VALUES(0400, 0, '重庆', 4);");
        db.execSQL("INSERT INTO swf_area VALUES(0501, 0500, '福州', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0502, 0500, '龙岩', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0503, 0500, '南平', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0504, 0500, '宁德', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0505, 0500, '莆田', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0506, 0500, '泉州', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0507, 0500, '三明', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0508, 0500, '厦门', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0509, 0500, '漳州', 5);");
        db.execSQL("INSERT INTO swf_area VALUES(0601, 0600, '兰州', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0602, 0600, '白银', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0603, 0600, '定西', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0604, 0600, '合作', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0605, 0600, '金昌', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0606, 0600, '酒泉', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0607, 0600, '嘉峪关', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0608, 0600, '临夏', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0609, 0600, '平凉', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0610, 0600, '庆阳', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0611, 0600, '天水', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0612, 0600, '武威', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0613, 0600, '武都', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0614, 0600, '张掖', 6);");
        db.execSQL("INSERT INTO swf_area VALUES(0701, 0700, '广州', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0702, 0700, '潮州', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0703, 0700, '东莞', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0704, 0700, '佛山', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0705, 0700, '河源', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0706, 0700, '惠州', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0707, 0700, '江门', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0708, 0700, '揭阳', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0709, 0700, '梅州', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0710, 0700, '茂名', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0711, 0700, '清远', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0712, 0700, '深圳', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0713, 0700, '汕头', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0714, 0700, '韶关', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0715, 0700, '汕尾', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0716, 0700, '阳江', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0717, 0700, '云浮', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0718, 0700, '珠海', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0719, 0700, '中山', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0720, 0700, '湛江', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0721, 0700, '肇庆', 7);");
        db.execSQL("INSERT INTO swf_area VALUES(0801, 0800, '南宁', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0802, 0800, '北海', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0803, 0800, '白色', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0804, 0800, '崇左', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0805, 0800, '防城港', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0806, 0800, '桂林', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0807, 0800, '贵港', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0808, 0800, '贺州', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0809, 0800, '河池', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0810, 0800, '柳州', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0811, 0800, '来宾', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0812, 0800, '钦州', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0813, 0800, '梧州', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0814, 0800, '玉林', 8);");
        db.execSQL("INSERT INTO swf_area VALUES(0901, 0900, '贵阳', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0902, 0900, '安顺', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0903, 0900, '毕节', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0904, 0900, '都匀', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0905, 0900, '凯里', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0906, 0900, '六盘水', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0907, 0900, '晴隆', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0908, 0900, '铜仁', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0909, 0900, '兴义', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(0910, 0900, '遵义', 9);");
        db.execSQL("INSERT INTO swf_area VALUES(1001, 1000, '海口', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1002, 1000, '白沙', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1003, 1000, '保亭', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1004, 1000, '澄迈', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1005, 1000, '昌江', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1006, 1000, '儋州', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1007, 1000, '定安', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1008, 1000, '东方', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1009, 1000, '临高', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1010, 1000, '陵水', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1011, 1000, '乐东', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1012, 1000, '南沙岛', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1013, 1000, '琼海', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1014, 1000, '琼中', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1015, 1000, '三亚', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1016, 1000, '屯昌', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1017, 1000, '五指山', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1018, 1000, '文昌', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1019, 1000, '万宁', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1020, 1000, '西沙', 10);");
        db.execSQL("INSERT INTO swf_area VALUES(1101, 1100, '石家庄', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1102, 1100, '保定', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1103, 1100, '承德', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1104, 1100, '沧州', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1105, 1100, '衡水', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1106, 1100, '邯郸', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1107, 1100, '廊坊', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1108, 1100, '秦皇岛', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1109, 1100, '唐山', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1110, 1100, '邢台', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1111, 1100, '张家口', 11);");
        db.execSQL("INSERT INTO swf_area VALUES(1201, 1200, '郑州', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1202, 1200, '安阳', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1203, 1200, '鹤壁', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1204, 1200, '焦作', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1205, 1200, '济源', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1206, 1200, '开封', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1207, 1200, '洛阳', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1208, 1200, '漯河', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1209, 1200, '南阳', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1210, 1200, '濮阳', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1211, 1200, '平顶山', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1212, 1200, '三门峡', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1213, 1200, '商丘', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1214, 1200, '新乡', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1215, 1200, '许昌', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1216, 1200, '信阳', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1217, 1200, '周口', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1218, 1200, '驻马店', 12);");
        db.execSQL("INSERT INTO swf_area VALUES(1301, 1300, '哈尔滨', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1302, 1300, '大庆', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1303, 1300, '大兴安岭', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1304, 1300, '鹤岗', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1305, 1300, '黑河', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1306, 1300, '佳木斯', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1307, 1300, '鸡西', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1308, 1300, '牡丹江', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1309, 1300, '齐齐哈尔', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1310, 1300, '七台河', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1311, 1300, '双鸭山', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1312, 1300, '绥化', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1313, 1300, '伊春', 13);");
        db.execSQL("INSERT INTO swf_area VALUES(1401, 1400, '武汉', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1402, 1400, '鄂州', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1403, 1400, '恩施', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1404, 1400, '黄石', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1405, 1400, '黄冈', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1406, 1400, '荆州', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1407, 1400, '荆门', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1408, 1400, '潜江', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1409, 1400, '十堰', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1410, 1400, '随州', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1411, 1400, '神农架', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1412, 1400, '天门', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1413, 1400, '襄阳', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1414, 1400, '孝感', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1415, 1400, '咸宁', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1416, 1400, '仙桃', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1417, 1400, '宜昌', 14);");
        db.execSQL("INSERT INTO swf_area VALUES(1501, 1500, '长沙', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1502, 1500, '常德', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1503, 1500, '郴州', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1504, 1500, '衡阳', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1505, 1500, '怀化', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1506, 1500, '吉首', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1507, 1500, '娄底', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1508, 1500, '黔阳', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1509, 1500, '邵阳', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1510, 1500, '湘潭', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1511, 1500, '岳阳', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1512, 1500, '益阳', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1513, 1500, '永州', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1514, 1500, '株洲', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1515, 1500, '张家界', 15);");
        db.execSQL("INSERT INTO swf_area VALUES(1601, 1600, '长春', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1602, 1600, '白山', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1603, 1600, '白城', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1604, 1600, '吉林', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1605, 1600, '辽源', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1606, 1600, '四平', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1607, 1600, '松原', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1608, 1600, '通化', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1609, 1600, '延吉', 16);");
        db.execSQL("INSERT INTO swf_area VALUES(1701, 1700, '南京', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1702, 1700, '常州', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1703, 1700, '淮安', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1704, 1700, '连云港', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1705, 1700, '南通', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1706, 1700, '苏州', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1707, 1700, '宿迁', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1708, 1700, '秦州', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1709, 1700, '无锡', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1710, 1700, '徐州', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1711, 1700, '盐城', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1712, 1700, '扬州', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1713, 1700, '镇江', 17);");
        db.execSQL("INSERT INTO swf_area VALUES(1801, 1800, '南昌', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1802, 1800, '抚州', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1803, 1800, '赣州', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1804, 1800, '九江', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1805, 1800, '景德镇', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1806, 1800, '吉安', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1807, 1800, '萍乡', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1808, 1800, '上饶', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1809, 1800, '新余', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1810, 1800, '鹰潭', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1811, 1800, '宜春', 18);");
        db.execSQL("INSERT INTO swf_area VALUES(1901, 1900, '沈阳', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1902, 1900, '鞍山', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1903, 1900, '本溪', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1904, 1900, '朝阳', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1905, 1900, '大连', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1906, 1900, '丹东', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1907, 1900, '抚顺', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1908, 1900, '阜新', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1909, 1900, '葫芦岛', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1910, 1900, '锦州', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1911, 1900, '辽阳', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1912, 1900, '盘锦', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1913, 1900, '铁岭', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(1914, 1900, '营口', 19);");
        db.execSQL("INSERT INTO swf_area VALUES(2001, 2000, '呼和浩特', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2002, 2000, '阿拉善左旗', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2003, 2000, '包头', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2004, 2000, '赤峰', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2005, 2000, '鄂尔多斯', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2006, 2000, '呼伦贝尔', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2007, 2000, '集宁', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2008, 2000, '临河', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2009, 2000, '通辽', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2010, 2000, '乌兰浩特', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2011, 2000, '乌海', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2012, 2000, '锡林浩特', 20);");
        db.execSQL("INSERT INTO swf_area VALUES(2101, 2100, '银川', 21);");
        db.execSQL("INSERT INTO swf_area VALUES(2102, 2100, '固原', 21);");
        db.execSQL("INSERT INTO swf_area VALUES(2103, 2100, '石嘴山', 21);");
        db.execSQL("INSERT INTO swf_area VALUES(2104, 2100, '吴忠', 21);");
        db.execSQL("INSERT INTO swf_area VALUES(2105, 2100, '中卫', 21);");
        db.execSQL("INSERT INTO swf_area VALUES(2201, 2200, '西宁', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2202, 2200, '果洛', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2203, 2200, '海东', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2204, 2200, '海南', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2205, 2200, '海北', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2206, 2200, '海西', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2207, 2200, '黄南', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2208, 2200, '玉树', 22);");
        db.execSQL("INSERT INTO swf_area VALUES(2301, 2300, '济南', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2302, 2300, '滨州', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2303, 2300, '东营', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2304, 2300, '德州', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2305, 2300, '菏泽', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2306, 2300, '济宁', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2307, 2300, '莱芜', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2308, 2300, '临沂', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2309, 2300, '聊城', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2310, 2300, '青岛', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2311, 2300, '日照', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2312, 2300, '泰安', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2313, 2300, '潍坊', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2314, 2300, '威海', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2315, 2300, '烟台', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2316, 2300, '淄博', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2317, 2300, '枣庄', 23);");
        db.execSQL("INSERT INTO swf_area VALUES(2401, 2400, '太原', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2402, 2400, '长治', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2403, 2400, '大同', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2404, 2400, '晋城', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2405, 2400, '晋中', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2406, 2400, '临汾', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2407, 2400, '吕梁', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2408, 2400, '朔州', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2409, 2400, '忻州', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2410, 2400, '阳泉', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2411, 2400, '运城', 24);");
        db.execSQL("INSERT INTO swf_area VALUES(2501, 2500, '西安', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2502, 2500, '宝康', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2503, 2500, '宝鸡', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2504, 2500, '陈仓', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2505, 2500, '汉中', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2506, 2500, '商洛', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2507, 2500, '铜川', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2508, 2500, '渭南', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2509, 2500, '咸阳', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2510, 2500, '延安', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2511, 2500, '榆林', 25);");
        db.execSQL("INSERT INTO swf_area VALUES(2600, 0, '上海', 26);");
        db.execSQL("INSERT INTO swf_area VALUES(2701, 2700, '成都', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2702, 2700, '阿贝', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2703, 2700, '巴中', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2704, 2700, '德阳', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2705, 2700, '达州', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2706, 2700, '广元', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2707, 2700, '广安', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2708, 2700, '甘孜', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2709, 2700, '泸州', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2710, 2700, '乐山', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2711, 2700, '凉山', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2712, 2700, '绵阳', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2713, 2700, '眉山', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2714, 2700, '内江', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2715, 2700, '南充', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2716, 2700, '攀枝花', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2717, 2700, '遂宁', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2718, 2700, '宜宾', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2719, 2700, '雅安', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2720, 2700, '自贡', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2721, 2700, '资阳', 27);");
        db.execSQL("INSERT INTO swf_area VALUES(2800, 0, '天津', 28);");
        db.execSQL("INSERT INTO swf_area VALUES(2900, 0, '台湾', 29);");
        db.execSQL("INSERT INTO swf_area VALUES(3001, 3000, '拉萨', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3002, 3000, '阿里', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3003, 3000, '昌都', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3004, 3000, '林芝', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3005, 3000, '那曲', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3006, 3000, '日喀则', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3007, 3000, '山南', 30);");
        db.execSQL("INSERT INTO swf_area VALUES(3100, 0, '香港', 31);");
        db.execSQL("INSERT INTO swf_area VALUES(3201, 3200, '乌鲁木齐', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3202, 3200, '阿克苏', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3203, 3200, '阿图什', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3204, 3200, '阿勒泰', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3205, 3200, '阿拉尔', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3206, 3200, '博乐', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3207, 3200, '昌吉', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3208, 3200, '哈密', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3209, 3200, '和田', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3210, 3200, '克拉玛依', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3211, 3200, '喀什', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3212, 3200, '库尔勒', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3213, 3200, '石河子', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3214, 3200, '吐鲁番', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3215, 3200, '塔城', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3216, 3200, '伊宁', 32);");
        db.execSQL("INSERT INTO swf_area VALUES(3301, 3300, '昆明', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3302, 3300, '保山', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3303, 3300, '楚雄', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3304, 3300, '大理', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3305, 3300, '德宏', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3306, 3300, '红河', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3307, 3300, '景洪', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3308, 3300, '丽江', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3309, 3300, '临沧', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3310, 3300, '怒江', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3311, 3300, '曲靖', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3312, 3300, '思茅', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3313, 3300, '文山', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3314, 3300, '香格里拉', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3315, 3300, '玉溪', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3316, 3300, '昭通', 33);");
        db.execSQL("INSERT INTO swf_area VALUES(3401, 3400, '杭州', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3402, 3400, '湖州', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3403, 3400, '嘉兴', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3404, 3400, '金华', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3405, 3400, '丽水', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3406, 3400, '宁波', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3407, 3400, '衢州', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3408, 3400, '绍兴', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3409, 3400, '台州', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3410, 3400, '温州', 34);");
        db.execSQL("INSERT INTO swf_area VALUES(3411, 3400, '舟山', 34);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}