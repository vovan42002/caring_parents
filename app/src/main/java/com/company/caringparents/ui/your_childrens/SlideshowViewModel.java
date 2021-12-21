package com.company.caringparents.ui.your_childrens;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> ar;
    private List<String> list;
    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("List of child's is empty");
    }

    public LiveData<String> getText() {
        return mText;
    }
}