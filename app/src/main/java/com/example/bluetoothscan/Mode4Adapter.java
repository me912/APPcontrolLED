package com.example.bluetoothscan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import java.util.List;

public class Mode4Adapter extends BaseAdapter {
    private String TAG = "Mode4Adapter ";
    private Context mContext;
    private int mLayoutid;
    private int pos = 0;
    private SharedModelMode1 mShared;
    private List<holder> mDataList;
    public Mode4Adapter(Context context, int idLayout, List<holder> mylist) {
        this.mContext = context;
        this.mLayoutid = idLayout;
        this.mDataList = mylist;
    }
    @Override
    public int getCount() {
        if(mDataList.size()!= 0 && !mDataList.isEmpty()) return mDataList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListViewHolder listViewHolder;
        View row;

        if(convertView == null) {
            row = LayoutInflater.from(parent.getContext()).inflate(mLayoutid, parent, false);
            listViewHolder = new ListViewHolder();
            listViewHolder.Anitmate = (EditText) row.findViewById(R.id.animate);
            listViewHolder.Time = (EditText) row.findViewById(R.id.Time);
            listViewHolder.mlayout  = (LinearLayout) row.findViewById(R.id.abc);
            row.setTag(listViewHolder);
        }
        else {
            row=convertView;
           listViewHolder = (ListViewHolder) row.getTag();
        }
        listViewHolder.Anitmate.setText(mDataList.get(position).getAnimate());
        listViewHolder.Time.setText(mDataList.get(position).getTime());

        listViewHolder.mlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: 1");
            }
        });
        listViewHolder.Anitmate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDataList.get(position).setAnimate(listViewHolder.Anitmate.getText().toString());
                notifyDataSetChanged();
            }
        });
        return row;
    }
}
