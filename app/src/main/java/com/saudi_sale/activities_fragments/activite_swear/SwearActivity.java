package com.saudi_sale.activities_fragments.activite_swear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_home.HomeActivity;
import com.saudi_sale.activities_fragments.activity_login.LoginActivity;
import com.saudi_sale.databinding.ActivitySplashBinding;
import com.saudi_sale.databinding.ActivitySwearBinding;
import com.saudi_sale.generated.callback.OnClickListener;
import com.saudi_sale.language.Language;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;

import io.paperdb.Paper;

public class SwearActivity extends AppCompatActivity {

    private ActivitySwearBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private String lang;
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_swear);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);

        binding.btnDone.setOnClickListener(view -> {
            finish();
        });
        binding.llBack.setOnClickListener(view -> finish());
    }
}
