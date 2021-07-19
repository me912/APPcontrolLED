package com.example.bluetoothscan;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.sax.EndElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Mode4Fragment extends Fragment {

    private Context mContext;
    private SharedModelMode1 Mymodel;
    private List<ListViewHolder> mListViewHolder = new ArrayList<>();
    private List<List<Byte>> Result = new ArrayList<>();
    private List<Byte> Result2 = new ArrayList<>();

    private int number = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mode4, container, false);
        LinearLayout mLinear = view.findViewById(R.id.mode4layout);
        EditText numberanimate = view.findViewById(R.id.numberanimate);
        ImageButton mbtn = (ImageButton) view.findViewById(R.id.buttonadd);
        ImageButton send = (ImageButton) view.findViewById(R.id.send);
        Mymodel = new ViewModelProvider(getActivity()).get(SharedModelMode1.class);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Byte> Re = new ArrayList<>();
                Re.add(new Byte((byte)0)); //byte0: 0
                Re.add(new Byte((byte)4));//byte1: mode byte
                Re.add(new Byte((byte)number));
                for(int i=0; i<Result.size(); i++){
                    Re.addAll(Result.get(i));
                    for(int j=Result.get(i).size()+1; j<=4; j++){
                        Re.add(new Byte((byte)0));
                    }
                    Re.add(Result2.get(i));
                }
                Mymodel.setShared(Re);
                Log.d(TAG, "onClick: " + Re);
            }
        });

        numberanimate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    number = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(container.getContext(), "Vui lòng nhập giá trị hợp lệ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number == 0) {
                    Toast.makeText(container.getContext(), "Nhập lớn hơn 0 đi", Toast.LENGTH_SHORT).show();
                    return;
                } else if (number > 20) {
                    Toast.makeText(container.getContext(), "Nhập nhỏ 10 đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (number == mListViewHolder.size()) return;
                if (number > mListViewHolder.size()) {
                    try {
                        Result.clear();
                    } catch (Exception e) {
                    }
                    for (int i = mListViewHolder.size(); i < number; i++) {
                        mListViewHolder.add(i, new ListViewHolder());
                        mListViewHolder.get(i).mView = inflater.inflate(R.layout.mode4_adapter, container, false);
                        mListViewHolder.get(i).Anitmate = (EditText) mListViewHolder.get(i).mView.findViewById(R.id.animate);
                        mListViewHolder.get(i).Time = (EditText) mListViewHolder.get(i).mView.findViewById(R.id.Time);
                        addTextChangedListener(mListViewHolder.get(i).Anitmate, i);
                        addTextChangedListener2(mListViewHolder.get(i).Time, i);
                        mListViewHolder.get(i).mView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        mLinear.addView(mListViewHolder.get(i).mView);
                    }
                    for (int i = 0; i < number; i++) {
                        Result.add(Arrays.asList(new Byte[]{0}));
                        Result2.add(new Byte((byte)0));
                    }
                }
                if (number < mListViewHolder.size()) {
                    try {
                        Result.clear();
                    } catch (Exception e) {
                    }
                    for (int i = number; i < mListViewHolder.size(); i++) {
                        mLinear.removeView(mListViewHolder.get(i).mView);
                    }
                    while (number != mListViewHolder.size()) {
                        mListViewHolder.remove(number);
                    }
                    for (int i = 0; i < number; i++) {
                        Result.add(Arrays.asList(new Byte[]{0}));
                        Result2.add(new Byte((byte)0));
                    }
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    public static Byte[] hexStringToByteArray(String s) {
        int len = s.length();
        Integer a;
        Byte[] data = new Byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            a = ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
            data[i / 2] = new Byte(a.byteValue());
        }
        return data;
    }

    private void addTextChangedListener(final EditText editText, final int i) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Result.set(i, Arrays.asList(hexStringToByteArray(s.toString())));
                    Log.d(TAG, "afterTextChanged: success" + Result);
                } catch (Exception e) {

                }
            }
        });
    }

    private void addTextChangedListener2(final EditText editText, final int i) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Integer a = Integer.parseInt(s.toString());
                    Result2.set(i, new Byte(a.byteValue()));
                    Log.d(TAG, "afterTextChanged: success" + Result2);
                } catch (Exception e) {
                }
            }
        });
    }
}
