package tech.mbsoft.barcodereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

import tech.mbsoft.barcodereader.adapter.BarcodeItem;
import tech.mbsoft.barcodereader.adapter.BarcodeRecyclerViewAdapter;

public class BarcodeListActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_MULTIPLE = 1;
    private RecyclerView rvBarcodeList;
    private ArrayList<BarcodeItem> barcodeItems;
    private BarcodeRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_list);

        initUI();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void initUI() {
        rvBarcodeList = findViewById(R.id.rv_barcode_list);
        barcodeItems = new ArrayList<>();
        adapter = new BarcodeRecyclerViewAdapter(BarcodeListActivity.this, barcodeItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        rvBarcodeList.setLayoutManager(layoutManager);
        rvBarcodeList.setAdapter(adapter);
        rvBarcodeList.addItemDecoration(new DividerItemDecoration(BarcodeListActivity.this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_MULTIPLE) {
                if (data.getClipData() == null)
                    return;

                Log.e("++data", "" + data.getClipData().getItemCount());// Get count of image here.

                Log.e("++count", "" + data.getClipData().getItemCount());

                barcodeItems.clear();

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    BarcodeItem barcodeItem = new BarcodeItem();
                    barcodeItem.setBarCodeImageUri(data.getClipData().getItemAt(i).getUri());
                    barcodeItems.add(barcodeItem);
                }
                Log.e("SIZE", barcodeItems.size() + "");
                adapter = new BarcodeRecyclerViewAdapter(BarcodeListActivity.this, barcodeItems);
                rvBarcodeList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

    }
}
