package tech.mbsoft.barcodereader.adapter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

public class BarcodeItem {

    private int id;
    private Uri barCodeImageUri;
    private String barcodeContents ="";
    private LiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> _isLoading;

    public BarcodeItem() {
        _isLoading=new MutableLiveData<>();
        _isLoading.setValue(true);
       isLoading= _isLoading;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getBarCodeImageUri() {
        return barCodeImageUri;
    }

    public void setBarCodeImageUri(Uri barCodeImageUri) {
        this.barCodeImageUri = barCodeImageUri;
    }

    public String getBarcodeContents() {
        return barcodeContents;
    }

    public void setBarcodeContents(String barcodeContents) {
        this.barcodeContents = barcodeContents;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(Boolean loading) {
        _isLoading.setValue(loading);
    }
}
