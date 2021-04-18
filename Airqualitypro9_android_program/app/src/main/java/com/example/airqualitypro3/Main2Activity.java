package com.example.airqualitypro3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.charts.BarLineChartBase;
//import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;


public class Main2Activity extends AppCompatActivity {

    private LineChart mpChart;
    LineDataSet myLineDataSetsummedval = new LineDataSet(null, "AVG general air pollution level");
    LineDataSet myLineDataSetco2 = new LineDataSet(null, "AVG CO2 level");
    LineDataSet myLineDataSetlight = new LineDataSet(null, "AVG Light");
    LineDataSet myLineDataSettemp = new LineDataSet(null, "AVG Temperature");
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineData mylinedata;
    Button valtogomb, valtogomb2;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        valtogomb = (Button) findViewById(R.id.buttonchange);
        valtogomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        valtogomb2 = (Button) findViewById(R.id.buttonchange2);
        valtogomb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Main3Activity.class);
                startActivity(intent);
            }
        });


        mpChart = (LineChart) findViewById(R.id.linechart);
        mpChart.setDragEnabled(true);
        myDb = new DatabaseHelper(this);
        myLineDataSettemp.setLineWidth(4);
        myLineDataSettemp.setColors(Color.GREEN);
        myLineDataSetlight.setLineWidth(4);
        myLineDataSetlight.setColors(Color.BLUE);
        myLineDataSetco2.setLineWidth(4);
        myLineDataSetco2.setColors(Color.RED);
        myLineDataSetsummedval.setLineWidth(4);
        myLineDataSetsummedval.setColors(Color.BLACK);
        showdata();
      //  myLineDataSet.setLineWidth(4);
        //DayAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mpChart);
        //XAxis xAxis = mpChart.getXAxis();
        //xAxis.setValueFormatter(xAxisFormatter);
        /*HourAxisValueFormatter xAxisformatter = new HourAxisValueFormatter(50000);
        XAxis xAxis = mpChart.getXAxis();
        xAxis.setValueFormatter(xAxisformatter);*/
        //XAxis xAxis = mpChart.getXAxis();
        //xAxis.setValueFormatter(new MyAxisVAlueFormatter());
        //YAxis yAxisLeft = mpChart.getAxisLeft();
        //YAxis yAxisRight = mpChart.getAxisRight();

        //xAxis.setValueFormatter(new MyAxisVAlueFormatter());
    }


    private void showdata() {
        myLineDataSetsummedval.setValues(convertvaluessumdatatoarraytoday());
        myLineDataSetco2.setValues(convertvaluesco2toarraytoday());
        myLineDataSetlight.setValues(convertvalueslighttoarraytoday());
        myLineDataSettemp.setValues(convertvaluestemptoarraytoday());
        dataSets.clear();
        dataSets.add(myLineDataSetsummedval);
        dataSets.add(myLineDataSetco2);
        dataSets.add(myLineDataSetlight);
        dataSets.add(myLineDataSettemp);
        mylinedata = new LineData(dataSets);
        mpChart.clear();
        mpChart.setData(mylinedata);
        mpChart.invalidate();
    }

    private ArrayList<Entry> convertvaluessumdatatoarraytoday()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAllVALUESUMMARISEDCALC = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUESUMMARISEDCALCDatafromtoday();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAllVALUESUMMARISEDCALC.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAllVALUESUMMARISEDCALC;
    }
    private ArrayList<Entry> convertvaluesco2toarraytoday()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAllco2lastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUECO2Datafromtoday();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAllco2lastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAllco2lastmonth;
    }
    private ArrayList<Entry> convertvalueslighttoarraytoday()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAlllightvallastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUELIGHTDatafromtoday();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAlllightvallastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAlllightvallastmonth;
    }
    private ArrayList<Entry> convertvaluestemptoarraytoday()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataALLtemperaturesfromlastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUETEMPERATUREDatafromtoday();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataALLtemperaturesfromlastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataALLtemperaturesfromlastmonth;
    }


    /*private ArrayList<Entry> getDatavalues() {
        ArrayList<Entry> dataVals = new ArrayList<>();
        String[] columns = {"DATE", "VALUESUMMARISEDCALC"}; //ezeket az ertekeket kerem ki
        Cursor cursor = myDb.getWritableDatabase().query("Measuredvalues_table", columns, null, null, null, null, null);

        cursor.moveToFirst();
        *//*for (int i=0; i<16; i++)
        {
            cursor.moveToNext();
            dataVals.add(new Entry(cursor.getInt(0), cursor.getInt(1)));
        }*//*

        for (int i = 0; i < cursor.getCount() - 1; i++) {
            cursor.moveToNext();
            dataVals.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }

        *//*for(int i=0;i<cursor.getCount();i++)
        {
            cursor.moveToNext();
            dataVals.add(new Entry(i,i+1));
           i++;
        }*//*
     *//*   dataVals.add(new Entry(1,2));
        dataVals.add(new Entry(2,200));
        dataVals.add(new Entry(3,2551));*//*

        return dataVals;
    }*/

    //a tengelyek formazasa ... kb 50.nekifutas ustan
    private class MyAxisVAlueFormatter implements IAxisValueFormatter
    {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return "Day " + value;
        }
    }

}