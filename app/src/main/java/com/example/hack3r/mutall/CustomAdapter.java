package com.example.hack3r.mutall;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Sms>{
    private Context context;
    private List<Sms> objectList = new ArrayList<>();

    public  CustomAdapter(Context c, ArrayList<Sms> arrayList){
        super(c, 0, arrayList);
        this.context=c;
        this.objectList=arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.sms_inflater, parent, false);
            Sms currentObject = objectList.get(position);

            TextView smsMobile = (TextView)convertView.findViewById(R.id.mobile);
            smsMobile.setText(currentObject.getSmsNumber());

            TextView smsBody = (TextView)convertView.findViewById(R.id.sms);
            smsBody.setText(currentObject.getSmsBody());



        }
        return convertView;
    }
}
