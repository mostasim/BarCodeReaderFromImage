package tech.mbsoft.barcodereader.adapter;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.InputStream;
import java.util.ArrayList;

import tech.mbsoft.barcodereader.BackgroundExecutor;
import tech.mbsoft.barcodereader.BarcodeListActivity;
import tech.mbsoft.barcodereader.R;

public class BarcodeRecyclerViewAdapter extends RecyclerView.Adapter<BarcodeRecyclerViewAdapter.BarcodeViewHolder> {

    private static final String TAG = "BarcodeRecyclerViewAdap";
    private BarcodeListActivity activity;
    private ArrayList<BarcodeItem> barcodeItems;

    public BarcodeRecyclerViewAdapter(BarcodeListActivity activity, ArrayList<BarcodeItem> barcodeItems) {
        this.activity = activity;
        this.barcodeItems = barcodeItems;
    }

    @NonNull
    @Override
    public BarcodeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.barcode_recycler_view_item, viewGroup, false);
        return new BarcodeViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeViewHolder barcodeViewHolder, int i) {
        BarcodeItem barcodeItem = barcodeItems.get(i);
        barcodeViewHolder.setUiItem(barcodeItem);
    }

    @Override
    public int getItemCount() {
        return barcodeItems.size();
    }

    public class BarcodeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBarcodeImage;
        TextView tvBarcodeInfo;
        ProgressBar pbLoader;

        public BarcodeViewHolder(@NonNull View itemView) {
            super(itemView);
            initUI();
        }

        private void setUiItem(final BarcodeItem barcodeItem) {

            try {
                final InputStream imageStream = activity.getContentResolver().openInputStream(barcodeItem.getBarCodeImageUri());
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivBarcodeImage.setImageBitmap(selectedImage);
                final Bitmap bMap = selectedImage;


                barcodeItem.getIsLoading().observe(activity, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        if (!aBoolean) {
                            pbLoader.setVisibility(View.INVISIBLE);
                            tvBarcodeInfo.setVisibility(View.VISIBLE);
                        }
                    }
                });

                BackgroundExecutor.handler = new Handler();

                BackgroundExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final String contents;
                            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                            Reader reader = new MultiFormatReader();
                            Result result = reader.decode(bitmap);
                            contents = result.getText();
                            BackgroundExecutor.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    barcodeItem.setIsLoading(false);
                                    tvBarcodeInfo.setText(contents);

                                }
                            });
                            Log.e(TAG, contents);
                        } catch (FormatException e) {
                            e.printStackTrace();
                            BackgroundExecutor.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    barcodeItem.setIsLoading(false);
                                    tvBarcodeInfo.setText("No data found");
                                }
                            });
                        } catch (ChecksumException e) {
                            e.printStackTrace();
                            BackgroundExecutor.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    barcodeItem.setIsLoading(false);
                                    tvBarcodeInfo.setText("No data found");
                                }
                            });
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                            BackgroundExecutor.postOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    barcodeItem.setIsLoading(false);
                                    tvBarcodeInfo.setText("No data found");
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        private void initUI() {
            ivBarcodeImage = itemView.findViewById(R.id.iv_barcode_image_item_row);
            tvBarcodeInfo = itemView.findViewById(R.id.tv_barcode_info);
            pbLoader = itemView.findViewById(R.id.pbLoader);
        }
    }
}
