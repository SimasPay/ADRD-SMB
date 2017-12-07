package com.mfino.bsim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.mfino.bsim.R;

/**
 * Created by widy on 10/30/17.
 * 30
 */

public class UpgradeToSimobiPlus extends AppCompatActivity {
    boolean isUpgradeEnable=false;
    int upgradeValue = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgradenow);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUpgradeEnable = bundle.getBoolean("upgradeEnable", false);
            upgradeValue = bundle.getInt("getUpgradeValue",0);
        }
        ImageButton closeBtn=findViewById(R.id.close_btn);
        Button upgradeNow=findViewById(R.id.upgrade_now);
        if(upgradeValue==2){
            closeBtn.setVisibility(View.GONE);
        }else{
            closeBtn.setVisibility(View.VISIBLE);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpgradeToSimobiPlus.this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("upgradeNow", "no");
                intent.putExtra("getUpgradeValue", upgradeValue);
                if(isUpgradeEnable){
                    intent.putExtra("upgradeEnable", true);
                }else{
                    intent.putExtra("upgradeEnable", false);
                }
                startActivity(intent);
            }
        });

        upgradeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpgradeToSimobiPlus.this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("upgradeNow", "yes");
                intent.putExtra("getUpgradeValue", upgradeValue);
                if(isUpgradeEnable){
                    intent.putExtra("upgradeEnable", true);
                }else{
                    intent.putExtra("upgradeEnable", false);
                }
                startActivity(intent);
            }
        });


    }
}
