package com.example.bluetoothscan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import me.sujanpoudel.wheelview.WheelView;

public class Mode2Fragment extends Fragment {
    private WheelView myWheelview;
    private SharedModelMode1 mShared;
    private List<Byte> Result = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode2, container, false);
        mShared = new ViewModelProvider(getActivity()).get(SharedModelMode1.class);
        String TAG = "MODE2";

        myWheelview = (WheelView) view.findViewById(R.id.wheel_view);
        String[] titles = new String[32];
        for(int i=0; i<32; i++){
            titles[i] = "";
        }
        for(int i=0; i< 6; i++){
            Result.add(new Byte((byte)0));
        }
        myWheelview.setTitles(Arrays.asList(titles));
        myWheelview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    int position = myWheelview.getFocusedIndex();
                    byte a = (byte) (1<<position%8);
                    for(int i=0; i< 6; i++){
                        Result.set(i, new Byte((byte)0));
                    }
                    Result.set(position/8+2, a);
                    Result.set(0,  new Byte((byte)0));  //byte0: 0
                    Result.set(1, new Byte((byte)1));   //byte1:mode
                    Log.d(TAG, "onTouch: " + Result);
                    mShared.setShared(Result);
                    }
                return false;
            }
        });

        return view;
    }
}
