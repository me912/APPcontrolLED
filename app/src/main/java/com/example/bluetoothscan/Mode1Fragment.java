    package com.example.bluetoothscan;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;

public class Mode1Fragment extends Fragment {
    private String  TAG = "MODE1";
    private ListView listview;
    private List<String> Button_list = new ArrayList<String>();
    private ArrayAdapter<String> ListViewAdapter;
    private List<Byte> Result = new ArrayList<>();
    private SharedModelMode1 MyModel;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.mode1, container, false);
        listview = (ListView) view.findViewById(R.id.ListViewID);

        MyModel = new ViewModelProvider(getActivity()).get(SharedModelMode1.class);
        for(int i=1; i<=32; i++){
            this.Button_list.add("LED" + i);
        }
        for(Integer i = 0; i<6; i++) {
            Result.add(new Byte((byte)0));
        }
        ListViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, Button_list);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(ListViewAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                byte a;
                CheckedTextView b = (CheckedTextView) view;
                if(b.isChecked()) {
                    a = (byte) ((1<<position%8)|(Result.get(position/8+2)));
                    Result.set(position/8+2, new Byte(a));
                    Result.set(0,  new Byte((byte)0));  //byte0: 0
                    Result.set(1, new Byte((byte)1)); //byte1:mode
                    MyModel.setShared(Result);
                    Log.d(TAG, "onItemClick: " + Result);
                }
                else {
                    a = (byte) (~(1<<(position%8))&(Result.get(position/8+2)));
                    Result.set(position / 8 + 2, new Byte(a));
                    MyModel.setShared(Result);
                    Log.d(TAG, "onItemClick: " + Result);
                }
            }
        });
        return view;
    }



    public List<Byte> getResult(){
        return Result;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
