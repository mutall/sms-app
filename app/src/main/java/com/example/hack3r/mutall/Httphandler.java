package com.example.hack3r.mutall;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hack3r on 2017-11-27.
 */
//create a http handler class to make a service call to the url
public class Httphandler {
    //define an empty constructor
    public Httphandler() {
    }
    //method to make a service call to the url

    public String makeServiceCall() {
        //define a response to be returned at the end of the method
        String response = null;
        //Define the request url
        String reqUrl="http://mutall.co.ke/mutall_rental/request.php";
        try {
            //create a new url using our requset url
            URL url = new URL(reqUrl);

            //open the connection using HttpURLConnection
            HttpURLConnection myConn = (HttpURLConnection) url.openConnection();

            //Set the request method we use GET.  why?? do research
            myConn.setRequestMethod("GET");

            //read the response
            InputStream in = new BufferedInputStream(myConn.getInputStream());
            //convert our response to string and set it to the variable response
            response = convertStreamToString(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    //method for converting an input stream to a string
    private String convertStreamToString(InputStream in) {
        //
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        //we use the String builder class since it provides methods such as append and insert
        // which accept data of any type
        StringBuilder builder = new StringBuilder();
        //variable line which will represent a line in the buffered output
        String line;
        //we loop over the buffer appending one line to our string builder NB we need to catch the
        // IOexception thrown when using an input stream
        try {
            while ((line = buffer.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //we close the input stream. Again catch the Oexception thrown by the input stream
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Return a string version of out builder
        return builder.toString();
    }

    public void postRequest(String myUrl, String params) {
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
