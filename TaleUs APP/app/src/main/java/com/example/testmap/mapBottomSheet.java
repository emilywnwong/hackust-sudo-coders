package com.example.testmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class mapBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private String taleId, userId, title, lat, lon, datetime, ori_taleId;


    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        String jsonString = null;

        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        for (int i = 0;  i < 10 ; i++){
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                jsonString = sb.toString();
                break;
            }
            catch (ProtocolException e){ }
        }

        return new JSONObject(jsonString);
    }

    public mapBottomSheet(String taleId) {
        this.ori_taleId = taleId;
        String[] parts = taleId.split("@@@");
        this.taleId = parts[0];
        this.userId = parts[1];
        this.title = parts[2];
        this.lat = parts[3];
        this.lon = parts[4];
        this.datetime = parts[5];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_bottom_sheet, container, false);
        ImageView icon_ImageView = (ImageView) v.findViewById(R.id.bot_icon);
        String url = "http://35.194.218.135:5000/getAccountSummary?userId="+ userId;

        new Thread(new Runnable(){
            public void run(){
                try {
                    JSONObject jsonObject = getJSONObjectFromURL(url);
                    String iconString = jsonObject.get("icon").toString();
                    if (iconString!= "null") {
                        System.out.println(iconString);
                        byte[] iconBytes = Base64.decode(iconString, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
                        for (int i=0;i<10;i++) {
                            try{
                                icon_ImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, icon_ImageView.getWidth(), icon_ImageView.getHeight(), false));
                                break;
                            } catch (Exception e) {}
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        TextView mapBottomTitleText = (TextView) v.findViewById(R.id.bot_title);
        mapBottomTitleText.setTypeface(null, Typeface.BOLD);
        if (title.equals("")){
            mapBottomTitleText.setText("Untitled");
        } else {
            mapBottomTitleText.setText(title);
        }

        TextView mapBottomLocText = (TextView) v.findViewById(R.id.loc);
        mapBottomLocText.setText(lat+", "+lon);

        TextView mapBottomDateText = (TextView) v.findViewById(R.id.tale_date);
        mapBottomDateText.setText(datetime);

        ImageView button2 = v.findViewById(R.id.bot_detail);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mapBottomSheet.this.getActivity(), arActivity.class);
                intent.putExtra("taleId", ori_taleId);
                startActivity(intent);
                dismiss();
            }
        });
        return v;
    }
    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

}