package com.example.airqualitypro3;

import android.os.AsyncTask;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;

public class InternetIO extends AsyncTask<Void,Void,float[]> {

    // private static String influxdburl  = "https://us-central1-1.gcp.cloud2.influxdata.com";
    private char[] token = "BOCIbRNVEKusiHoeUyT21OnX3KaWeOkpEM6k315sar63CTQEoV6JF_603FDzKRZ8sgo7PotIuGM8SVzm1lxH5g==".toCharArray();
    private String bucket = "ldmsa's Bucket";
    private String org = "f2a6962fe1df51db";


    @Override
    protected float[] doInBackground(Void... voids) {

            float[] resultarray = {0,0,0,0};

            try {
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
        return resultarray;

        }


}
