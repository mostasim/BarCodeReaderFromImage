package tech.mbsoft.barcodereader.adapter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import tech.mbsoft.barcodereader.BarcodeListActivity;
import tech.mbsoft.barcodereader.R;
import tech.mbsoft.barcodereader.thread.Worker;

public class BarcodeRecyclerViewAdapter extends RecyclerView.Adapter<BarcodeRecyclerViewAdapter.BarcodeViewHolder> {

    private static final String TAG = "BarcodeRecyclerViewAdap";
    private BarcodeListActivity activity;
    private ArrayList<BarcodeItem> barcodeItems;
    Worker worker;
//    public BarcodeDecoderThread barcodeDecoderThread;
//    public Handler handler ;

    public BarcodeRecyclerViewAdapter(BarcodeListActivity activity, ArrayList<BarcodeItem> barcodeItems, Worker worker) {
        this.activity = activity;
        this.barcodeItems = barcodeItems;
//        barcodeDecoderThread = new BarcodeDecoderThread();
        this.worker = worker;
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
        barcodeViewHolder.setUiItem(i);


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
            /*handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    tvBarcodeInfo.setText((String)msg.obj);
                }
            };*/
        }

        private void setUiItem(final int barcodeItem) {


            try {
                Log.e(TAG,Thread.currentThread().getName());
                Glide.with(activity).load(barcodeItems.get(barcodeItem).getBarCodeImageUri()).into(ivBarcodeImage);
//                Glide.with(activity).load(selectedImage).into(ivBarcodeImage);

//                ivBarcodeImage.setImageBitmap(selectedImage);
//                final Bitmap bMap = selectedImage;



                LiveData<String> barcodeContents = barcodeItems.get(barcodeItem).getBarcodeContents();
                tvBarcodeInfo.setText(barcodeContents.getValue());
                if (!barcodeContents.hasActiveObservers()){
                    barcodeContents.observe(activity, new Observer<String>() {
                        @Override
                        public void onChanged(@Nullable String s) {
                            tvBarcodeInfo.setText(s);
                        }
                    });
                }
                LiveData<Boolean> isLoading = barcodeItems.get(barcodeItem).getIsLoading();
                if (!isLoading.hasActiveObservers()){
                    barcodeItems.get(barcodeItem).getIsLoading().observe(activity, new Observer<Boolean>() {
                        @Override
                        public void onChanged(@Nullable Boolean aBoolean) {
                            if (!aBoolean) {
                                pbLoader.setVisibility(View.INVISIBLE);
                                tvBarcodeInfo.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }




               /* barcodeDecoderThread.execute(new Runnable() {
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

                            Message message = Message.obtain();
                            message.obj = contents;
                            handler.sendMessage(message);
                            barcodeItem.setIsLoading(false);

                            Log.e(TAG, contents);
                        } catch (FormatException e) {
                            e.printStackTrace();
                            barcodeItem.setIsLoading(false);
                            Message message = Message.obtain();
                            message.obj = "No data found";
                            handler.sendMessage(message);

                        } catch (ChecksumException e) {
                            e.printStackTrace();
                            barcodeItem.setIsLoading(false);
                            Message message = Message.obtain();
                            message.obj = "No data found";
                            handler.sendMessage(message);
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                            barcodeItem.setIsLoading(false);
                            Message message = Message.obtain();
                            message.obj = "No data found";
                            handler.sendMessage(message);
                        }
                    }
                });
*/


//                Handler handler = new Handler();
                Runnable runnable= new Runnable(){
                    @Override
                    public void run() {
                      Log.e(TAG,""+Thread.currentThread().getName());

                        try {
                            InputStream imageStream = activity.getContentResolver().openInputStream(barcodeItems.get(barcodeItem).getBarCodeImageUri());
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            Bitmap bMap = selectedImage;
                            final String contents;
                            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                            Reader reader = new MultiFormatReader();
                            Result result = reader.decode(bitmap);
                            contents = result.getText();

//
//                            tvBarcodeInfo.setText(contents);
                            barcodeItems.get(barcodeItem).setIsLoading(false);
                            barcodeItems.get(barcodeItem).setBarcodeContents(contents);

                            Log.e(TAG, contents);
                        } catch (FormatException e) {
                            e.printStackTrace();
                            barcodeItems.get(barcodeItem).setIsLoading(false);
                            barcodeItems.get(barcodeItem).setBarcodeContents("No data found");
//                            tvBarcodeInfo.setText("No data found");

                        } catch (ChecksumException e) {
                            e.printStackTrace();
                            barcodeItems.get(barcodeItem).setIsLoading(false);
                            barcodeItems.get(barcodeItem).setBarcodeContents("No data found");
//                            tvBarcodeInfo.setText("No data found");
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                            barcodeItems.get(barcodeItem).setIsLoading(false);
                            barcodeItems.get(barcodeItem).setBarcodeContents("No data found");
//                            tvBarcodeInfo.setText("No data found");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            barcodeItems.get(barcodeItem).setIsLoading(false);
                            barcodeItems.get(barcodeItem).setBarcodeContents("No data found");
                        }
                    }
                };
//                Thread thread = new Thread(runnable);
//                thread.start();

               if (barcodeItems.get(barcodeItem).getBarcodeContents().getValue().equals(""))
                    worker.execute(runnable);
//                handler.postDelayed(runnable, 1500);

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
