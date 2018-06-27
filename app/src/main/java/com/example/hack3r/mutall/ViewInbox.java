package com.example.hack3r.mutall;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ViewInbox extends Activity {
    ArrayList<Sms> smsMessagesList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    Map<String, String> params = new HashMap<>();

    ListView messages;
    Button post;
    CustomAdapter arrayAdapter;
    public static final int SMS_PERMISSIONS_REQUEST = 1;
    String getIntentString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_inbox);
        messages = (ListView) findViewById(R.id.list_view);
        post = (Button) findViewById(R.id.post);
        getIntentString = getIntent().getStringExtra("type");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestReadRequest();
        } else {
            showInbox(getIntentString);
        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                enter url where you want to post the data
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewInbox.this);
                final EditText edittext = new EditText(getApplicationContext());
                edittext.setHint(R.string.alert_edit);
                edittext.setText("http://mutall.co.ke/mutall_rental/?request=push_msg_to_server");
                alert.setTitle(R.string.alert_edit_title);

                alert.setView(edittext);

                alert.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String Url = edittext.getText().toString();
                        JSONArray jsonArray = new JSONArray();
                        for(Object x: arrayList){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("message", x.toString());
                                jsonArray.put(jsonObject);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        post(jsonArray.toString(), Url);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        dialog.dismiss();
                    }
                });

                alert.show();

            }
        });
        messages.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Use this to remove the error of call requires api level 23
    @TargetApi(Build.VERSION_CODES.M)
    public void requestReadRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(getApplicationContext(), "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                showInbox(getIntentString);
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    public void post(final String smsMessage, String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.i("response", response);
                        showSnack(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                params.put("msg", smsMessage);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void showInbox(String address) {
        ContentResolver contentResolver = getContentResolver();
        String selection = '\'' + address + '\'';
        try {
            Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, "address=" + selection, null, null);
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            if (smsInboxCursor.moveToFirst()) {
                do {
                    String smsNum = smsInboxCursor.getString(indexAddress);
                    String smsBody = smsInboxCursor.getString(indexBody);

                    Sms sms = new Sms();
                    sms.setSmsNumber(smsNum);
                    sms.setSmsBody(smsBody);
                    smsMessagesList.add(sms);
                    arrayAdapter = new CustomAdapter(ViewInbox.this, smsMessagesList);
                    arrayList.add(smsBody);
                } while (smsInboxCursor.moveToNext());
            }
        } catch (SQLiteException e) {
            String message = "No Sms from " + getIntentString;
//            For debbugging
            Log.e("err", e.getMessage());
            showSnack(message);
        }
    }

    public void showSnack(String message){
        View view = findViewById(R.id.inbox);
        final Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("close", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}