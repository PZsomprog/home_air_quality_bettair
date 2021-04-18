package com.example.airqualitypro3;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Measuredvalues.db";
    public static final String TABLE_NAME = "Measuredvalues_table";
    public static final String TABLE2_NAME = "Measuredvaluesdaily_table";
    public static final String TABLE3_NAME = "Measuredvaluesmonthly_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "TIMESTAMP";
    public static final String COL3 = "DATE";
    public static final String COL4 = "VALUESUMMARISEDCALC";
    public static final String COL5 = "VALUECO2";
    public static final String COL6 = "VALUELIGHT";
    public static final String COL7 = "VALUETEMPERATURE";

    //maga az adatbazis megalokotasa
    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    //a table megalkotasa
    @Override
    public void onCreate(SQLiteDatabase db) {
        //queryvel hozzuk letre
        // String createTablequer = "create table Measuredvalues_table(xvalue REAL, YValues REAL)";
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP FLOAT, DATE FLOAT, VALUESUMMARISEDCALC FLOAT, VALUECO2 FLOAT, VALUELIGHT FLOAT, VALUETEMPERATURE FLOAT  ) ");
        db.execSQL("create table " + TABLE2_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP FLOAT, DATE FLOAT, VALUESUMMARISEDCALC FLOAT, VALUECO2 FLOAT, VALUELIGHT FLOAT, VALUETEMPERATURE FLOAT  ) ");
        db.execSQL("create table " + TABLE3_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP FLOAT, DATE FLOAT, VALUESUMMARISEDCALC FLOAT, VALUECO2 FLOAT, VALUELIGHT FLOAT, VALUETEMPERATURE FLOAT  ) ");
    }


    //az upgrade erdekeben ha mar letezik dropoljuk
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS "+TABLE2_NAME);
        db.execSQL(" DROP TABLE IF EXISTS "+TABLE3_NAME);
        onCreate(db);
    }

    //beszurofuggvenyekelottrorles
    public void deletetable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Measuredvalues_table", "id>0", null);
        db.delete("Measuredvaluesdaily_table", "id>0", null);
    }

    //beszurofuggvenyek
    public boolean insertallData(Float Timestamp, Float Date, Float ValueSummarisedcalc, Float ValueCO2, Float ValueLight, Float ValueTemperature)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, Timestamp);
        contentValues.put(COL3, Date);
        contentValues.put(COL4, ValueSummarisedcalc);
        contentValues.put(COL5, ValueCO2);
        contentValues.put(COL6, ValueLight);
        contentValues.put(COL7, ValueTemperature);
        long result = db.insert(TABLE_NAME,null, contentValues);
        if(result ==-1)
            return false;
        else
            return true;
    }

    public boolean insertdailyData(Float Timestamp, Float Date, Float ValueSummarisedcalc, Float ValueCO2, Float ValueLight, Float ValueTemperature)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, Timestamp);
        contentValues.put(COL3, Date);
        contentValues.put(COL4, ValueSummarisedcalc);
        contentValues.put(COL5, ValueCO2);
        contentValues.put(COL6, ValueLight);
        contentValues.put(COL7, ValueTemperature);
        long result = db.insert(TABLE2_NAME,null, contentValues);
        if(result ==-1)
            return false;
        else
            return true;
    }

    public boolean insertmonthlyData(Float Timestamp, Float Date, Float ValueSummarisedcalc, Float ValueCO2, Float ValueLight, Float ValueTemperature)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, Timestamp);
        contentValues.put(COL3, Date);
        contentValues.put(COL4, ValueSummarisedcalc);
        contentValues.put(COL5, ValueCO2);
        contentValues.put(COL6, ValueLight);
        contentValues.put(COL7, ValueTemperature);
        long result = db.insert(TABLE3_NAME,null, contentValues);
        if(result ==-1)
            return false;
        else
            return true;
    }



    //livedatakhoz lekeresek
    public Cursor getlivedatavaluesum()
    {
       SQLiteDatabase db = this.getWritableDatabase();
       Cursor res = db.rawQuery("select VALUESUMMARISEDCALC from Measuredvalues_table ORDER BY ID DESC LIMIT 1 ",null);
       return res;
    }
    public Cursor getlivedataco2()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select VALUECO2 from Measuredvalues_table ORDER BY ID DESC LIMIT 1 ",null);
        return res;
    }
    public Cursor getlivedatalight()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select VALUELIGHT from Measuredvalues_table ORDER BY ID DESC LIMIT 1 ",null);
        return res;
    }
    public Cursor getlivedatatemp()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select VALUETEMPERATURE from Measuredvalues_table ORDER BY ID DESC LIMIT 1 ",null);
        return res;
    }


    //napigrafikonhoz lekerofuggvenyek x y ertekek
     public Cursor getAllVALUESUMMARISEDCALCDatafromtoday()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60;
        String mytime2 = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        mytime2 = mytime2+"00";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUESUMMARISEDCALC from Measuredvaluesdaily_table",null);
        return res;
        //Float.parseFloat(mytime2)
    }

    public Cursor getAllVALUECO2Datafromtoday()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60;
        String mytime2 = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        mytime2 = mytime2+"00";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUECO2 from Measuredvaluesdaily_table where  DATE>" + 0,null);
        return res;
    }

    public Cursor getAllVALUELIGHTDatafromtoday()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60;
        String mytime2 = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        mytime2 = mytime2+"00";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUELIGHT from Measuredvaluesdaily_table where  DATE>" + 0,null);
        return res;
    }

    public Cursor getAllVALUETEMPERATUREDatafromtoday()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60;
        String mytime2 = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        mytime2 = mytime2+"00";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUETEMPERATURE from Measuredvaluesdaily_table where  DATE>" + 0,null);
        return res;
    }






    //havigrafikonhozlekeroertekek x y ertekek
    public Cursor getAllVALUESUMMARISEDCALCDatafromlastmonth()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60*30;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUESUMMARISEDCALC from Measuredvaluesmonthly_table where TIMESTAMP>" + beginningtimestamp + "TIMESTAMP<" + mytimestamp,null);
        return res;
    }

    public Cursor getAllVALUECO2Datafromlastmonth()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60*30;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUECO2 from Measuredvaluesmonthly_table where TIMESTAMP>" + beginningtimestamp + "TIMESTAMP<" + mytimestamp,null);
        return res;
    }

    public Cursor getAllVALUELIGHTDatafromlastmonth()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60*30;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUELIGHT from Measuredvaluesmonthly_table where TIMESTAMP>" + beginningtimestamp + "TIMESTAMP<" + mytimestamp,null);
        return res;
    }

    public Cursor getAllVALUETEMPERATUREDatafromlastmonth()
    {
        Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        float beginningtimestamp = mytimestamp-24*60*60*30;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DATE,VALUETEMPERATURE from Measuredvaluesmonthly_table where TIMESTAMP>" + beginningtimestamp + "TIMESTAMP<" + mytimestamp,null);
        return res;
    }


}
