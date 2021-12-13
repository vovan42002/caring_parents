package com.company.caringparents.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> ar;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");


        String array = "child1"+"\n"+"child2";

        System.out.println(array);
        ar = new MutableLiveData<>();
        ar.setValue(array);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getTextChild() {
        return ar;
    }

}