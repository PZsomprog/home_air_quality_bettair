package com.example.airqualitypro3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.sql.SQLOutput;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import java.time.Instant;
import java.util.List;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class MainActivity extends AppCompatActivity {

    private int kritikusertek = 5;
    private String mycolor = "RED";
    TextView liveosszesitett, liveco2, livelight, livetemp;
    Button valtogomb;
    DatabaseHelper myDb;
    InternetIO myInetIO;
    // private static String influxdburl  = "https://us-central1-1.gcp.cloud2.influxdata.com";
    private char[] token = "your token".toCharArray();
    private String bucket = "your bucket";
    private String org = "passw";

    //RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        valtogomb = (Button) findViewById(R.id.buttonchange);
        valtogomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Please wait, the system is loading data!!", Toast.LENGTH_LONG).show();
                //toast.getView().setBackgroundColor(Color.parseColor("#F6AE2D"));
                //toast.show();
                queryfrominfluxdbandfetchtomytable();
                Intent intent = new Intent(v.getContext(), Main2Activity.class);
                startActivity(intent);
            }
        });


        liveosszesitett = (TextView) findViewById(R.id.textView1);
        liveco2 = (TextView) findViewById(R.id.textView2);
        livelight = (TextView) findViewById(R.id.textView3);
        livetemp = (TextView) findViewById(R.id.textView4);
        myDb = new DatabaseHelper(this);
        myInetIO = new InternetIO();
        directlivequeryandshowfrominfluxdb(); // minden 5 masodpercben lekerem az online erteket es kiiratom

        ScheduledExecutorService scheduledES = Executors.newScheduledThreadPool(1);
        scheduledES.scheduleWithFixedDelay(new Runnable(){
            public void run(){
                directlivequeryandshowfrominfluxdb();
            }
        }, 0, 5, TimeUnit.SECONDS);

        /*final int delay = 5000;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                directlivequeryandshowfrominfluxdb();
                handler.postDelayed(this, delay);
            }
        }, delay);*/

        //getandshowlivedata();
        //ertekbeadas test
       /* Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
        float mytimestamp = myDate.getTime();
        myDb.insertallData(mytimestamp, 20200509f,6f,6f,6f,30f);*/


        /*// minden 5 másodpercben az online adatbazisbol az adatok lekerese, ezt beletenni az offline adatbazisba majd ennek erteket lekerni er kiiratni
        nResume();
        final int delay = 1000;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                AddData();
                getandshowlivedata();
                handler.postDelayed(this, delay);
            }
        }, delay);*/
/*

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

// This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                AddData();
                getandshowlivedata();
            }
        }, 0, 5, TimeUnit.SECONDS);

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                AddData();
                                getandshowlivedata();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
*/


        // elso megnyitás-e

        SharedPreferences settings = getSharedPreferences("isfirstrunpreference", 0);
        boolean silent = settings.getBoolean("isfirstrun", false);

        //es utana beallitom true ra, ezentul true
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isfirstrun", true);
        editor.commit();

    }

   /* public float[] querydata()
    {

        float[] resultarray = {0,0,0,0};

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {


           InfluxDBClient client = InfluxDBClientFactory.create("https://eu-central-1-1.aws.cloud2.influxdata.com", token, org, bucket);

            String flux = "from(bucket: \"ldmsa's Bucket\")\n" +
                    "  |> range(start: -1d ) ";

            QueryApi queryApi = client.getQueryApi();

            List<FluxTable> tables = queryApi.query(flux);
            System.out.println(tables.toString());
            for (FluxTable fluxTable : tables) {
                List<FluxRecord> records = fluxTable.getRecords();

                for (FluxRecord fluxRecord : records) {
                    //value = Float.parseFloat(fluxRecord.getField());
                }
            }

            client.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        return resultarray;
    }*/

    // kerjuk le es irassuk ki a live adatokat
    public void directlivequeryandshowfrominfluxdb() {

        String[] queryresultsstring = new String[7];


        //az osszes adat lekerese az internetrol
        Thread influxlivefetch = new Thread(new Runnable() {
            @Override
            public void run() {
                InfluxDBClient client = InfluxDBClientFactory.create("https://eu-central-1-1.aws.cloud2.influxdata.com", token, org, bucket);

                String flux = "from(bucket: \"ldmsa's Bucket\")\n" +
                        "  |> range(start: -5m ) ";

                QueryApi queryApi = client.getQueryApi();

                List<FluxTable> tables = queryApi.query(flux);
                System.out.println(tables.toString());
                String field = "a";
                String value = "a";


                int i = 4;
                for (FluxTable fluxTable : tables) {
                    List<FluxRecord> records = fluxTable.getRecords();

                    for (FluxRecord fluxRecord : records) {
                        //value = Float.parseFloat(fluxRecord.getField());
                        System.out.println(fluxRecord.getField() + "    " + fluxRecord.getValue() + "  " + fluxRecord.getTime());
                        field = fluxRecord.getField();
                        if (field.equals("total_value")) {
                            i = 0;
                        } else if (field.equals("co2")) {
                            i = 1;
                        } else if (field.equals("light")) {
                            i = 2;
                        } else if (field.equals("temperature")) {
                            i = 3;
                        } else {
                            i = 4;
                        }
                        value = fluxRecord.getValue().toString();
                        //value = fluxRecord.getValueByKey("co2").toString();
                        queryresultsstring[i] = value;
                    }
                    System.out.println(field + " " + value);
                }

                client.close();
            }
            //return queryresults;
        }, "influxlivefetch");
        influxlivefetch.setDaemon(true);
        influxlivefetch.start();

        try {
            influxlivefetch.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



                float livevaluesumvalue = 0, liveco2value = 0, livelightvalue = 0, livetempvalue = 0;


                if(queryresultsstring[0] != null) {
                    livevaluesumvalue = Float.parseFloat(queryresultsstring[0]); //total value
                    liveco2value = ((Float.parseFloat(queryresultsstring[1])) / 1000f); //co2
                    livelightvalue = Float.parseFloat(queryresultsstring[2]); //light
                    livetempvalue = Float.parseFloat(queryresultsstring[3]); //temp
                }
                else
                {
                    livevaluesumvalue = -1;
                    liveco2value = -1;
                    livelightvalue = -1;
                    livetempvalue = -1;
                }

             /*try {
                 livevaluesumvalue = Float.parseFloat(queryresultsstring[0]); //total value
                 liveco2value = ((Float.parseFloat(queryresultsstring[1])) / 1000f); //co2
                 livelightvalue = Float.parseFloat(queryresultsstring[2]); //light
                 livetempvalue = Float.parseFloat(queryresultsstring[3]); //temp
             }
             catch (NullPointerException e)
             {
                 livevaluesumvalue = 0f;
                 liveco2value = 0f;
                 livelightvalue = 0f;
                 livetempvalue = 0f;
             }*/
                //livesumvalue kiirasa
                if(livevaluesumvalue ==-1)
                {
                    liveosszesitett.setText("aktualis összesített legszennyezettseg (1-5) = " + "nincs friss adat" + "\n" + "                                              (1 jó 5 szennyezett)");
                    liveosszesitett.setTextColor(Color.MAGENTA);
                    liveco2.setText("aktualis CO2 érték (PPM/1000) = " + "nincs friss adat" + "\n" + "                                             (<800 jó)");
                    liveco2.setTextColor(Color.MAGENTA);
                    livelight.setText("aktualis fényerő (1-10)= " + "nincs friss adat");
                    livelight.setTextColor(Color.MAGENTA);
                    livetemp.setText("aktualis hőmérséklet (°C)= " + "nincs friss adat");
                    livetemp.setTextColor(Color.MAGENTA);
                   setWallpaperfresh();
                }
                else {
                    liveosszesitett.setText("aktualis összesített legszennyezettseg (1-5) = " + livevaluesumvalue + "\n" + "   (1 jó 5 szennyezett)");
                    if (livevaluesumvalue > 3) {
                        liveosszesitett.setTextColor(Color.RED);
                       setWallpapersmog();
                    }
                    if (livevaluesumvalue >= 3) {
                        Toast toast = Toast.makeText(MainActivity.this, "szellőztess!!", Toast.LENGTH_LONG);
                        toast.getView().setBackgroundColor(Color.parseColor("#F6AE2D"));
                        toast.show();
                    } else if (livevaluesumvalue < 3) {
                        liveosszesitett.setTextColor(Color.GREEN);
                       setWallpaperfresh();
                    } else {
                        liveosszesitett.setTextColor(Color.GRAY);
                    }


                    //co2 lekereseeskiiratasa
                    liveco2.setText("aktualis CO2 érték (PPM/1000) = " + liveco2value + "\n" + "   (<800/1000 jó)");
                    if (liveco2value > (1000 / 1000)) {
                        liveco2.setTextColor(Color.RED);
                    } else if (liveco2value < (800 / 1000)) {
                        liveco2.setTextColor(Color.GREEN);
                    } else {
                        liveco2.setTextColor(Color.GRAY);
                    }

                    //light lekereseeskiiratasa
                    livelight.setText("aktualis fényerő (1-10)= " + livelightvalue);

                    //temp adatlekereseeskiiratasa
                    livetemp.setText("aktualis hőmérséklet (°C)= " + livetempvalue);
                }


    }

    public void setWallpaperfresh()
    {
        ImageView myimageview = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fresh);
        myimageview.setImageBitmap(bitmap);
        //setContentView(relativeLayout);
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fresh);
        //WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());


    }


    public void setWallpapersmog()
    {
        ImageView myimageview = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wp2802824);
        myimageview.setImageBitmap(bitmap);

    }

    public void queryfrominfluxdbandfetchtomytabletest() {
        myDb.deletetable();
        myDb.insertdailyData(20201118f,20201118f,3f,3f,3f,3f);
        myDb.insertdailyData(20201118f,20201118f,5f,6f,8f,4f);
        myDb.insertdailyData(20201118f,20201118f,3f,3f,3f,4f);

    }

//az adatok lekerese az internetrol es a sajat adatbazisba betoltese
    public void queryfrominfluxdbandfetchtomytable() {




        List<String> Timestamp = new ArrayList<String>();
        List<String> Date = new ArrayList<String>();
        List<String> ValueSummarisedcalc = new ArrayList<String>();
        List<String> ValueCO2 = new ArrayList<String>();
        List<String> ValueLight = new ArrayList<String>();
        List<String> ValueTemperature = new ArrayList<String>();

        String[] queryresultsstring = new String[7];

        myDb.deletetable();

        //az osszes adat lekerese az internetrol
        Thread influxlivefetch = new Thread(new Runnable() {
            @Override
            public void run() {
                InfluxDBClient client = InfluxDBClientFactory.create("https://eu-central-1-1.aws.cloud2.influxdata.com", token, org, bucket);

                String flux = "from(bucket: \"ldmsa's Bucket\")\n" +
                        "  |> range(start: -1d ) ";

                QueryApi queryApi = client.getQueryApi();

                List<FluxTable> tables = queryApi.query(flux);
                System.out.println(tables.toString());
                String field = "a";
                String value = "a";

                for (FluxTable fluxTable : tables) {
                    List<FluxRecord> records = fluxTable.getRecords();

                    for (FluxRecord fluxRecord : records) {
                        //value = Float.parseFloat(fluxRecord.getField());
                        System.out.println(fluxRecord.getField() + "    " + fluxRecord.getValue() + "  " + fluxRecord.getTime());
                        field = fluxRecord.getField();
                        if (field.equals("total_value")) {
                            ValueSummarisedcalc.add(fluxRecord.getValue().toString());

                            String isotime = fluxRecord.getTime().toString();
                          //  System.out.println(isotime);
                            String mytimeyear = isotime.substring(0,4);
                            String mytimemont =  isotime.substring(5,7);
                            String mytimeday = isotime.substring(8,10);
                            String mytimehour = isotime.substring(11,13);
                            String normaltime = mytimeyear+mytimemont+mytimeday+mytimehour;
                            Date.add(normaltime);
                            //String mytimestamp = toStringUnixTime(fluxRecord.getTime());
                            //System.out.println(toStringUnixTime(fluxRecord.getTime()));
                            /*Date myDate = new Date(); // a lekerdezeshez kell neznem az idot
                            float mytimestamp2 = myDate.getTime();
                            System.out.println(mytimestamp2);*/
                          //  Timestamp.add(mytimestamp);
                            //Date.add(fluxRecord.getTime().toString());
                        } else if (field.equals("co2")) {
                            ValueCO2.add(fluxRecord.getValue().toString());
                        } else if (field.equals("light")) {
                            ValueLight.add(fluxRecord.getValue().toString());
                        } else if (field.equals("temperature")) {
                            ValueTemperature.add(fluxRecord.getValue().toString());
                        }
                    }
                }

                client.close();

            }
            //return queryresults;
        }, "influxlivefetch");
        influxlivefetch.setDaemon(true);
        influxlivefetch.start();

        try {
            influxlivefetch.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        Thread influxlivefetch2 = new Thread(new Runnable() {
            @Override
            public void run() {


                //az osszes adat siman hozzaadasa a tablazatokhoz

                if (ValueSummarisedcalc.size() == 0) {
                    myDb.insertallData(0f, 1f, 0f, 0f, 0f, 0f);
                    myDb.insertallData(0f, 1f, 0f, 0f, 0f, 0f);
                    myDb.insertallData(0f, 1f, 0f, 0f, 0f, 0f);
                } else {
                    for (int i = 0; i < ValueSummarisedcalc.size(); i++) {
                        myDb.insertallData(0f, Float.parseFloat(Date.get(i)), Float.parseFloat(ValueSummarisedcalc.get(i)), (Float.parseFloat(ValueCO2.get(i))) / 1000, Float.parseFloat(ValueLight.get(i)), Float.parseFloat(ValueTemperature.get(i)));
                    }

                }
            }
        }, "influxlivefetch2");
        influxlivefetch2.setDaemon(true);
        influxlivefetch2.start();


         Thread influxlivefetch3 = new Thread(new Runnable() {
             @Override
              public void run() {

       //egy 6*24 es matrix az atlagok tarolasara
        int rowLen = 24, colLen = 6;
        Float[][] hourlyaveragematrix = new Float[rowLen][colLen];
        for(int i = 0; i < rowLen; i++)
            for(int j = 0; j < colLen; j++)
                hourlyaveragematrix[i][j]=0f;
        /*float hourdate01 = 0f;
        float hourlyaveragesumval01 = 0f;
        float hourlyaverageco201 = 0f;
        float hourlyaveragelight01 = 0f;
        float hourlyaveragetemperature01 = 0f;
        int j01 = 0;
*/


        for (int i=0; i<ValueSummarisedcalc.size(); i++) {
            System.out.println(Date.get(i).substring(8,10));
            if((Date.get(i).substring(8,10)).equals("00")) {
                hourlyaveragematrix[0][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[0][1] = hourlyaveragematrix[0][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[0][2] = hourlyaveragematrix[0][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[0][3] = hourlyaveragematrix[0][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[0][4] = hourlyaveragematrix[0][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[0][5] =  hourlyaveragematrix[0][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("01")) {

                hourlyaveragematrix[1][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[1][1] = hourlyaveragematrix[1][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[1][2] = hourlyaveragematrix[1][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[1][3] = hourlyaveragematrix[1][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[1][4] = hourlyaveragematrix[1][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[1][5] =  hourlyaveragematrix[1][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("02")) {
                hourlyaveragematrix[2][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[2][1] = hourlyaveragematrix[2][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[2][2] = hourlyaveragematrix[2][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[2][3] = hourlyaveragematrix[2][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[2][4] = hourlyaveragematrix[2][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[2][5] =  hourlyaveragematrix[2][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("03")) {
                hourlyaveragematrix[3][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[3][1] = hourlyaveragematrix[3][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[3][2] = hourlyaveragematrix[3][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[3][3] = hourlyaveragematrix[3][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[3][4] = hourlyaveragematrix[3][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[3][5] =  hourlyaveragematrix[3][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("04")) {
                hourlyaveragematrix[4][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[4][1] = hourlyaveragematrix[4][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[4][2] = hourlyaveragematrix[4][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[4][3] = hourlyaveragematrix[4][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[4][4] = hourlyaveragematrix[4][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[4][5] =  hourlyaveragematrix[4][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("05")) {
                hourlyaveragematrix[5][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[5][1] = hourlyaveragematrix[5][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[5][2] = hourlyaveragematrix[5][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[5][3] = hourlyaveragematrix[5][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[5][4] = hourlyaveragematrix[5][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[5][5] =  hourlyaveragematrix[5][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("06")) {
                hourlyaveragematrix[6][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[6][1] = hourlyaveragematrix[6][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[6][2] = hourlyaveragematrix[6][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[6][3] = hourlyaveragematrix[6][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[6][4] = hourlyaveragematrix[6][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[6][5] =  hourlyaveragematrix[6][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("07")) {
                hourlyaveragematrix[7][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[7][1] = hourlyaveragematrix[7][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[7][2] = hourlyaveragematrix[7][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[7][3] = hourlyaveragematrix[7][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[7][4] = hourlyaveragematrix[7][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[7][5] =  hourlyaveragematrix[7][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("08")) {
                hourlyaveragematrix[8][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[8][1] = hourlyaveragematrix[8][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[8][2] = hourlyaveragematrix[8][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[8][3] = hourlyaveragematrix[8][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[8][4] = hourlyaveragematrix[8][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[8][5] =  hourlyaveragematrix[8][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("09")) {
                hourlyaveragematrix[10][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[9][1] = hourlyaveragematrix[9][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[9][2] = hourlyaveragematrix[9][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[9][3] = hourlyaveragematrix[9][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[9][4] = hourlyaveragematrix[9][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[9][5] =  hourlyaveragematrix[9][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("10")) {
                hourlyaveragematrix[10][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[10][1] = hourlyaveragematrix[10][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[10][2] = hourlyaveragematrix[10][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[10][3] = hourlyaveragematrix[10][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[10][4] = hourlyaveragematrix[10][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[10][5] =  hourlyaveragematrix[10][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("11")) {
                hourlyaveragematrix[11][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[11][1] = hourlyaveragematrix[11][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[11][2] = hourlyaveragematrix[11][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[11][3] = hourlyaveragematrix[11][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[11][4] = hourlyaveragematrix[11][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[11][5] =  hourlyaveragematrix[11][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("12")) {
                hourlyaveragematrix[12][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[12][1] = hourlyaveragematrix[12][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[12][2] = hourlyaveragematrix[12][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[12][3] = hourlyaveragematrix[12][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[12][4] = hourlyaveragematrix[12][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[12][5] =  hourlyaveragematrix[12][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("13")) {
                hourlyaveragematrix[13][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[13][1] = hourlyaveragematrix[13][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[13][2] = hourlyaveragematrix[13][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[13][3] = hourlyaveragematrix[13][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[13][4] = hourlyaveragematrix[13][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[13][5] =  hourlyaveragematrix[13][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("14")) {
                hourlyaveragematrix[14][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[14][1] = hourlyaveragematrix[14][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[14][2] = hourlyaveragematrix[14][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[14][3] = hourlyaveragematrix[14][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[14][4] = hourlyaveragematrix[14][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[14][5] =  hourlyaveragematrix[14][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("15")) {
                hourlyaveragematrix[15][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[15][1] = hourlyaveragematrix[15][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[15][2] = hourlyaveragematrix[15][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[15][3] = hourlyaveragematrix[15][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[15][4] = hourlyaveragematrix[15][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[15][5] =  hourlyaveragematrix[15][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("16")) {
                hourlyaveragematrix[16][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[16][1] = hourlyaveragematrix[16][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[16][2] = hourlyaveragematrix[16][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[16][3] = hourlyaveragematrix[16][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[16][4] = hourlyaveragematrix[16][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[16][5] =  hourlyaveragematrix[16][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("17")) {
                hourlyaveragematrix[17][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[17][1] = hourlyaveragematrix[17][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[17][2] = hourlyaveragematrix[17][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[17][3] = hourlyaveragematrix[17][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[17][4] = hourlyaveragematrix[17][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[17][5] =  hourlyaveragematrix[17][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("18")) {
                hourlyaveragematrix[18][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[18][1] = hourlyaveragematrix[18][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[18][2] = hourlyaveragematrix[18][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[18][3] = hourlyaveragematrix[18][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[18][4] = hourlyaveragematrix[18][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[18][5] =  hourlyaveragematrix[18][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("19")) {
                hourlyaveragematrix[19][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[19][1] = hourlyaveragematrix[19][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[19][2] = hourlyaveragematrix[19][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[19][3] = hourlyaveragematrix[19][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[19][4] = hourlyaveragematrix[19][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[19][5] =  hourlyaveragematrix[19][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("20")) {
                hourlyaveragematrix[20][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[20][1] = hourlyaveragematrix[20][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[20][2] = hourlyaveragematrix[20][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[20][3] = hourlyaveragematrix[20][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[20][4] = hourlyaveragematrix[20][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[20][5] =  hourlyaveragematrix[20][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("21")) {
                hourlyaveragematrix[21][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[21][1] = hourlyaveragematrix[21][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[21][2] = hourlyaveragematrix[21][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[21][3] = hourlyaveragematrix[21][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[21][4] = hourlyaveragematrix[21][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[21][5] =  hourlyaveragematrix[21][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("22")) {
                hourlyaveragematrix[22][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[22][1] = hourlyaveragematrix[22][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[22][2] = hourlyaveragematrix[22][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[22][3] = hourlyaveragematrix[22][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[22][4] = hourlyaveragematrix[22][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[22][5] =  hourlyaveragematrix[22][5]+1; //az elemek szama
            }
            if((Date.get(i).substring(8,10)).equals("23")) {
                hourlyaveragematrix[23][0] = Float.parseFloat(Date.get(i).substring(8,10));  //az ido
                hourlyaveragematrix[23][1] = hourlyaveragematrix[23][1] + Float.parseFloat(ValueSummarisedcalc.get(i));  //a sumkalkulalt ertekek
                hourlyaveragematrix[23][2] = hourlyaveragematrix[23][2] + (Float.parseFloat(ValueCO2.get(i))/1000);  //hourlyaverageco2
                hourlyaveragematrix[23][3] = hourlyaveragematrix[23][3] + (Float.parseFloat(ValueLight.get(i)));  //valuelight
                hourlyaveragematrix[23][4] = hourlyaveragematrix[23][4] + (Float.parseFloat(ValueTemperature.get(i)));  //valueTemp
                hourlyaveragematrix[23][5] =  hourlyaveragematrix[23][5]+1; //az elemek szama
            }
            i++;
        }

        if (ValueSummarisedcalc.size() == 0) {
             myDb.insertdailyData(0f, 1f, 0f, 0f, 0f, 0f);
             myDb.insertdailyData(0f, 1f, 0f, 0f, 0f, 0f);
             myDb.insertdailyData(0f, 1f, 0f, 0f, 0f, 0f);
         }
        else {
            for (int i = 0; i < 24; i++) {
                myDb.insertdailyData(0f, hourlyaveragematrix[i][0] / (hourlyaveragematrix[i][5] + 1), hourlyaveragematrix[i][1] / (hourlyaveragematrix[i][5] + 1), hourlyaveragematrix[i][2] / (hourlyaveragematrix[i][5] + 1), hourlyaveragematrix[i][3] / (hourlyaveragematrix[i][5] + 1), hourlyaveragematrix[i][4] / (hourlyaveragematrix[i][5] + 1));
            }
        }
               /* myDb.insertdailyData(20201118f,2020111801f,3f,3f,3f,4f);
        myDb.insertdailyData(20201118f,2020111801f,3f,3f,3f,4f);*/

            }
        }, "influxlivefetch3");
        influxlivefetch3.setDaemon(true);
        influxlivefetch3.start();

        try {
            influxlivefetch2.join();
            influxlivefetch3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //timestamp váltó
    /*public static String toStringUnixTime(Instant i){
        BigDecimal nanos = BigDecimal.valueOf(i.getNano(), 9);
        BigDecimal seconds = BigDecimal.valueOf(i.getEpochSecond());
        BigDecimal total = seconds.add(nanos);
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(total);
    }*/

/*    //frissitsuk az összes adatbazist
    public void FetchAndAddData()
    {

        try {
            InetAddress influxdburl = InetAddress.getByName("https://eu-central-1-1.aws.cloud2.influxdata.com");
            final String serverURL = influxdburl.toString();
            final String username = "ldmsa@yandex.com", password = "wsnhazifeladat";
            final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);
            String getdata = "SELECT * FROM ";
            Query query1 = new Query(getdata,"dbname");
            QueryResult queryResult = influxDB.query(query1);

            influxDB.close();
        }
        catch (IOException e)
        {e.printStackTrace();}

   }*/



  /*  //az adatok lekerese majd kiiratasa a sajat adatbazisombol (regi megoldas) ((itt minden 3 masodpercben frissitettem az original adatbazisomat majd onnan lekerve irattam ki az adatokat))
    public void getandshowlivedata()
    {


        //osszesitettadatlekereseeskiiratasa

       *//* String[] columns = {"VALUESUMMARISEDCALC"}; //ezeket az ertekeket kerem ki
        Cursor cursor =  myDb.getWritableDatabase().query("Measuredvalues_table", columns,null,null, null,null, null);
*//*
        Cursor cursor = myDb.getlivedatavaluesum();
        cursor.moveToFirst(); // a lekeres mindig csak egy elemet ad vissza, a legutolsot
        float value = cursor.getFloat(0);
        liveosszesitett.setText("aktualis összesített legszennyezettseg = " + value);
        if(value>20)
        {liveosszesitett.setTextColor(Color.RED);}
        else if(value<10)
        {liveosszesitett.setTextColor(Color.GREEN);}
        else{liveosszesitett.setTextColor(Color.GRAY);}


        //co2 lekereseeskiiratasa
        cursor = myDb.getlivedataco2();
        cursor.moveToFirst();
        value = cursor.getFloat(0);
        liveco2.setText("aktualis CO2 érték = " + value);
        if(value>20)
        {liveco2.setTextColor(Color.RED);}
        else if(value<10)
        {liveco2.setTextColor(Color.GREEN);}
        else{liveco2.setTextColor(Color.GRAY);}

        //light lekereseeskiiratasa
        cursor = myDb.getlivedatalight();
        cursor.moveToFirst();
        value = cursor.getFloat(0);
        livelight.setText("aktualis fényerő = " + value);

        //temp adatlekereseeskiiratasa
        cursor = myDb.getlivedatavaluesum();
        cursor.moveToFirst();
        value = cursor.getFloat(0);
        livetemp.setText("aktualis hőmérséklet = " + value);
    }
}
*/
}