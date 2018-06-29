package com.example.hack3r.mutall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Accounts extends Activity {
    ArrayList arrayList = new ArrayList();
    ArrayAdapter adapter;
    Button button;
    ListView lview;
    ProgressDialog progressDialog;
//    final String url = "http://mutall.co.ke/receive.php";
    final String url = "http://mutall.co.ke/mutall_rental/?request=send_sms_2kplc&job=temp";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_accounts);
        button = (Button) findViewById(R.id.send);
        lview = (ListView) findViewById(R.id.lview);

        getVolley(url);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int no_accounts = arrayList.size();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Accounts.this);
                mBuilder.setTitle("SEND SMS")
                        .setMessage(String.format("You are sending  %1$d accounts to KPLC?", no_accounts))
                        .setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for(Object x :arrayList){
                                    sendSms("95551", x.toString());
                                }

                            }
                        })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });
    }

    public void getVolley(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Accounts.this);

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response", response);
                        try {
                            JSONArray bills = new JSONArray(response);

                            //looping through all clients
                            for (int i = 0; i < bills.length(); i++) {
                                JSONArray x = bills.getJSONArray(i);

                                String account = x.getString(1);
                                arrayList.add(account);
                            }
                            adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
                            lview.setAdapter(adapter);
                        } catch (final JSONException e) {
                            Log.e("Parse error", e.getMessage());
                            showToast(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERR", error.getMessage());
//                showToast(error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    private void sendSms(String mobile, String msg) {
        //use smsmanger to send sms to clients
        SmsManager mySms = SmsManager.getDefault();
        mySms.sendTextMessage(mobile, null, msg, null, null);
    }
    public void showSnack(String message) {
        final Snackbar snackbar;
        View view = findViewById(R.id.show_accounts);

        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("clear", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void showToast(String message) {
        Toast toast;
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
//    class RequestData extends AsyncTask<Void, Void, Void>{
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(Accounts.this);
//            progressDialog.setMessage("Fetching data");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            //        String url = "http://mutall.co.ke/mutall_rental/?request=send_sms_2kplc&job=temp";
//            String url = "http://mutall.co.ke/receive.php";
//            getVolley(url);
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if(progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
//        }
//    }
}


