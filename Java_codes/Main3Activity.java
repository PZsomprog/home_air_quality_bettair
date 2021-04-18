package com.example.airqualitypro3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;


public class Main3Activity extends AppCompatActivity {

    private LineChart mpChart;
    LineDataSet myLineDataSetsummedval = new LineDataSet(null, "AVG general air quality level");
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
        setContentView(R.layout.activity_main3);

        Button valtogomb = (Button) findViewById(R.id.buttonchange);
        valtogomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button valtogombvissza = (Button) findViewById(R.id.buttonchangeback);
        valtogombvissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Main2Activity.class);
                startActivity(intent);
            }
        });
        mpChart = (LineChart) findViewById(R.id.linechart);
        mpChart.setDragEnabled(true);
        myDb = new DatabaseHelper(this);
        showdata();
        myLineDataSettemp.setLineWidth(4);
        myLineDataSetlight.setLineWidth(4);
        myLineDataSetco2.setLineWidth(4);
        myLineDataSetsummedval.setLineWidth(4);
        showdata();
        XAxis xAxis = mpChart.getXAxis();
        xAxis.setValueFormatter(new MyAxisVAlueFormatter());

    }

    private void showdata()
    {
        myLineDataSetsummedval.setValues(convertvaluessumdatatoarraylastmonth());
        myLineDataSetco2.setValues(convertvaluesco2toarray());
        myLineDataSetlight.setValues(convertvalueslighttoarray());
        myLineDataSettemp.setValues(convertvaluestemptoarray());
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

    private ArrayList<Entry> convertvaluessumdatatoarraylastmonth()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAllVALUESUMMARISEDCALC = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUESUMMARISEDCALCDatafromlastmonth();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAllVALUESUMMARISEDCALC.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAllVALUESUMMARISEDCALC;
    }
    private ArrayList<Entry> convertvaluesco2toarray()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAllco2lastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUECO2Datafromlastmonth();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAllco2lastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAllco2lastmonth;
    }
    private ArrayList<Entry> convertvalueslighttoarray()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataAlllightvallastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUELIGHTDatafromlastmonth();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataAlllightvallastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataAlllightvallastmonth;
    }
    private ArrayList<Entry> convertvaluestemptoarray()
    {
        //az egyes datasetek megalkotasa
        ArrayList<Entry> dataALLtemperaturesfromlastmonth = new ArrayList<>();
        Cursor cursor = myDb.getAllVALUETEMPERATUREDatafromlastmonth();
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount()-1;i++)
        {
            cursor.moveToNext();
            dataALLtemperaturesfromlastmonth.add(new Entry(i, cursor.getFloat(1)));
            //dataVals.add(new Entry(i,i+2));
        }
        return dataALLtemperaturesfromlastmonth;
    }

    //a tengelyek formazasa ... kb 50.nekifutas ustan
    private class MyAxisVAlueFormatter implements IAxisValueFormatter
    {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return "Day " + value;
        }
    }

}
