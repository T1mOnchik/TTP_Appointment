package com.example.ttp_appointment;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHandler {

    public void startProcess(SlotRequestCallback callBack, String locationID, String date, int typeOfRequest) {
        new RunService(callBack, locationID, date, typeOfRequest).execute();
    }

    public static class RunService extends AsyncTask<Void, Void, String> {

        SlotRequestCallback callback;
        String locationId;
        String date;
        int typeOfRequest;

        public RunService(SlotRequestCallback callback, String locationId, String date, int typeOfRequest) {
            this.callback = callback;
            this.locationId = locationId;
            this.date = date;
            this.typeOfRequest = typeOfRequest;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = null;
            if (typeOfRequest == 0)
//                 request = new Request.Builder().url("http://localhost:8080/slots/beforeDate?inputDate="+ date +"&locationID="+locationId).build();
                request = new Request.Builder().url("https://ttp.cbp.dhs.gov/schedulerapi/slots?orderBy=soonest&limit=1&locationId="+locationId+"&minimum=1").build();
            else if(typeOfRequest == 1)
//                request = new Request.Builder().url("http://localhost:8080/slots/?count=1&locationID="+locationId).build();
                request = new Request.Builder().url("https://ttp.cbp.dhs.gov/schedulerapi/slots?orderBy=soonest&li50mit=1&locationId="+locationId+"&minimum=1").build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(response == null){
                try {
                    throw new IOException("no response from server");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null && !response.isSuccessful()){
                try {
                    throw new IOException(String.valueOf(response));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null && response.isSuccessful()) {
                try {
                    return Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    JSONArray data = new JSONArray(s);
                    String result = data.getJSONObject(0).optString("startTimestamp");
                    callback.processSuccess(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                try {
                    throw new IOException(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
