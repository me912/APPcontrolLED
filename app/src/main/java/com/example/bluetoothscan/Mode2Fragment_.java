package com.example.bluetoothscan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

public class Mode2Fragment_ extends Fragment implements View.OnTouchListener
{
    private String TAG = "Mode2_";
    private Button mainbtn;
    private static int KEYN = 3;
    private ImageButton ckbtn[] = new ImageButton[32];
    private TreeMap<Integer, TreeMap<Integer, ImageButton>> buttonTreeMap = new TreeMap<>();
    private List<Byte> Result = new ArrayList<>();
    private ConstraintLayout mylayout;
    private HashMap<Integer, HashMap<Integer, ImageButton>> buttonHashMap= new HashMap<Integer, HashMap<Integer, ImageButton>>();
    private SharedModelMode1 MyModel;
    private List<ImageButton> listbtn = new ArrayList<>();
    private List<Byte> list0 = new ArrayList<>();

    private View view;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mode2_, container, false);
        mylayout = (ConstraintLayout)view.findViewById(R.id.mylayout);
        MyModel = new ViewModelProvider(getActivity()).get(SharedModelMode1.class);
        mainbtn = (Button)view.findViewById(R.id.center);
        for(int i=1; i<=32; i++){
            String btnid = "button" + i;
            int resid = getResources().getIdentifier(btnid, "id", getActivity().getPackageName());
            ckbtn[i-1] = (ImageButton) view.findViewById(resid);
        }
        listbtn.addAll(Arrays.asList(ckbtn));


        mylayout.post(new Runnable() {
            @Override
            public void run() {

                List<TreeMap<Integer, ImageButton>> ListTreeX = new ArrayList<>();
                for(int i=1; i<=32; i++){
                    TreeMap<Integer, ImageButton> buttonTreeX = new TreeMap<>();
                    buttonTreeX.put((int) ckbtn[i-1].getY(), ckbtn[i-1]);
                    ListTreeX.add(i-1, buttonTreeX);
                    ckbtn[i-1].setClickable(false);
                }
                for(int i=1; i<=32; i++){
                    buttonTreeMap.put((int)ckbtn[i-1].getX(), ListTreeX.get(i-1));
                }

            }
        });
        view.setOnTouchListener(this);


        return view;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                try {
                    byte a;
                   TreeMap<Integer, ImageButton> treeMap = getkey1(buttonTreeMap, (int) event.getX());
                   ImageButton b = getkey2(treeMap, (int)event.getY());
                   b.setBackgroundResource(R.drawable.ic_fi_rr_add);
                   int position = listbtn.indexOf(b);
                    a = (byte) ((1<<position%8)|(Result.get(position/8+2)));
                    Result.set(position/8+2, new Byte(a));
                    Result.set(0, new Byte((byte)0)); // byte0: 0
                    Result.set(1, new Byte((byte)1));// Byte1: mode byte
                    MyModel.setShared(Result);
                    Log.d(TAG, "onTouch: " + Result);
                }catch (Exception e){

                }
            break;
            case MotionEvent.ACTION_UP:
                for(int i=0; i<32; i++){
                    ckbtn[i].setBackgroundResource(R.drawable.ic_fi_rr_circle);
                }
                Result.clear();
                Result.addAll(list0);
                MyModel.setShared(Result);
        }
        return true;
    }

    private TreeMap<Integer, ImageButton> getkey1(TreeMap<Integer, TreeMap<Integer, ImageButton>> treeMap, Integer Key){
        Integer after = treeMap.ceilingKey(Key);
        Integer before = treeMap.floorKey(Key);
        if (before == null) return treeMap.get(after);
        if (after == null) return treeMap.get(before);
        return (Key - before < after - Key) ? treeMap.get(before) : treeMap.get(after);

    }
    private  ImageButton getkey2(TreeMap<Integer, ImageButton> treeMap, Integer Key){

        Integer after = treeMap.ceilingKey(Key);
        Integer before = treeMap.floorKey(Key);
        if (before == null && after!=null && after - Key < 80) return treeMap.get(after);
        if (after == null && before != null && Key - before < 80) return treeMap.get(before);
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
            for(int i=0; i< 6; i++){
            list0.add(new Byte((byte)0));
        }
            Result.clear();
            Result.addAll(list0);
            super.onCreate(savedInstanceState);
    }
}
