package com.dimo.PayByQR.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRException;
import com.dimo.PayByQR.PayByQRProperties;
import com.dimo.PayByQR.PayByQRSDK;
import com.dimo.PayByQR.PayByQRSDKListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.model.InvoiceDetailResponse;
import com.dimo.PayByQR.model.LoyaltyListResponse;
import com.dimo.PayByQR.model.LoyaltyProgramModel;
import com.dimo.PayByQR.utils.DIMOService;
import com.dimo.PayByQR.utils.DIMOUtils;
import com.dimo.PayByQR.utils.DividerItemDecoration;
import com.dimo.PayByQR.utils.LoyaltyListAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoyaltyActivity extends DIMOBaseActivity {
    private PayByQRSDKListener listener;
    private TextView txtTitle;
    private ImageView btnBack;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar loader;
    private ArrayList<LoyaltyListResponse> mLoyaltyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty);

        // set an exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
            getWindow().setExitTransition(new Slide(Gravity.LEFT));
        }

        listener = PayByQRSDK.getListener();

        txtTitle = (TextView) findViewById(R.id.header_bar_title);
        btnBack = (ImageView) findViewById(R.id.header_bar_action_back);
        searchView = (SearchView) findViewById(R.id.activity_loyalty_search_view);
        recyclerView = (RecyclerView) findViewById(R.id.activity_loyalty_recycler_view);
        loader = (ProgressBar) findViewById(R.id.activity_loyalty_loader);

        recyclerView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        txtTitle.setText(getString(R.string.text_header_title_loyalty));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new GetLoyaltyListTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(closeSDKBroadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION_CLOSE_SDK));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeSDKBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        PayByQRProperties.setSDKContext(this);
        super.onResume();
    }

    BroadcastReceiver closeSDKBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSDK();
        }
    };

    @Override
    public void onBackPressed() {
        closeSDK();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constant.ACTIVITY_RESULT_CLOSE_SDK == resultCode){
            if(data.getBooleanExtra(Constant.INTENT_EXTRA_IS_CLOSE_SDK, true)) {
                closeSDK();
            }else{
                if (data.getBooleanExtra(Constant.INTENT_EXTRA_IS_SHOW_CUSTOM_DIALOG, false)){
                    listener.callbackShowDialog(LoyaltyActivity.this, data.getIntExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_CODE, 0),
                            data.getStringExtra(Constant.INTENT_EXTRA_CUSTOM_DIALOG_DESC), null);
                }
            }
        }else if(Constant.ACTIVITY_RESULT_NO_CONNECTION == resultCode){
            new GetLoyaltyListTask().execute();
        }
    }

    private void closeSDK(){
        listener.callbackSDKClosed();
        finish();
    }

    private class GetLoyaltyListTask extends AsyncTask<Void, Void, String> {

        public GetLoyaltyListTask(){}

        @Override
        protected void onPreExecute() {
            recyclerView.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return DIMOService.getLoyaltyList(LoyaltyActivity.this);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                mLoyaltyList = DIMOService.parseJSONLoyaltyList(LoyaltyActivity.this, s);

                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                recyclerView.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(LoyaltyActivity.this);
                recyclerView.setLayoutManager(mLayoutManager);

                //add divider
                recyclerView.addItemDecoration(new DividerItemDecoration(LoyaltyActivity.this));

                // specify an adapter (see also next example)
                final LoyaltyListAdapter mAdapter = new LoyaltyListAdapter(LoyaltyActivity.this, mLoyaltyList);
                recyclerView.setAdapter(mAdapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (PayByQRProperties.isDebugMode()) Log.d("RHIO", "onQueryTextChange: " + newText);
                        mAdapter.getFilter().filter(newText);
                        return true;
                    }
                });

                loader.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }catch (PayByQRException e){
                if(PayByQRProperties.isUsingCustomDialog()){
                    listener.callbackShowDialog(LoyaltyActivity.this, e.getErrorCode(), e.getErrorMessage() + " " + e.getErrorDetail(), null);
                }else {
                    if (e.getErrorCode() == Constant.ERROR_CODE_CONNECTION) {
                        goToNoConnectionScreen();
                    } else {
                        goToFailedScreen(getString(R.string.error_connection_header), e.getErrorMessage() + " " + e.getErrorDetail(), Constant.REQUEST_CODE_ERROR_UNKNOWN);
                    }
                }
            }
        }
    }

    private void goToNoConnectionScreen(){
        Intent intentFailed = new Intent(LoyaltyActivity.this, NoConnectionActivity.class);
        startActivityForResult(intentFailed, Constant.REQUEST_CODE_ERROR_CONNECTION);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }

    private void goToFailedScreen(String title, String errorDetail, int requestCode){
        Intent intentFailed = new Intent(LoyaltyActivity.this, FailedActivity.class);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_HEADER, title);
        intentFailed.putExtra(Constant.INTENT_EXTRA_ERROR_DETAIL, errorDetail);
        startActivityForResult(intentFailed, requestCode);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.fade_out);
    }
}
