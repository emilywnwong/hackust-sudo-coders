package com.example.testmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class mapBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private String taleId;
    private String userId;

    public mapBottomSheet(String taleId) {
        String[] parts = taleId.split("@@@");
        this.taleId = parts[0];
        this.userId = parts[1];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_bottom_sheet, container, false);

        TextView mapBottomText = (TextView) v.findViewById(R.id.mapBottomText);
        mapBottomText.setText(userId+"'s Tale");

        Button button2 = v.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mapBottomSheet.this.getActivity(), arActivity.class);
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