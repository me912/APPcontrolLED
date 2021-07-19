package com.example.bluetoothscan;

import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedModelMode1 extends ViewModel {
    private MutableLiveData<List<Byte>> Shared = new MutableLiveData<List<Byte>>();

    public void setShared(List<Byte> item){
        Shared.setValue(item);
    }

    public LiveData<List<Byte>> getShared(){
        return  Shared;
    }
}
