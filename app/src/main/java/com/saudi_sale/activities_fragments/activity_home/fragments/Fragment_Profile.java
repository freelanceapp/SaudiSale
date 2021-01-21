package com.saudi_sale.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_add_ads.AddAdsActivity;
import com.saudi_sale.activities_fragments.activity_home.HomeActivity;
import com.saudi_sale.activities_fragments.activity_my_coupon.MyCouponsActivity;
import com.saudi_sale.databinding.FragmentProfileBinding;
import com.saudi_sale.interfaces.Listeners;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment implements Listeners.ProfileActions {

    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {

        return new Fragment_Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setModel(userModel);
        binding.setActions(this);


    }












    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK){
            userModel = preferences.getUserData(activity);
            binding.setModel(userModel);

        }
    }


    @Override
    public void onAddAd() {
        if (userModel!=null){
            Intent intent = new Intent(activity, AddAdsActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onContactUs() {

    }

    @Override
    public void onMyFavorite() {
        if (userModel!=null){

        }else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMyAds() {

    }

    @Override
    public void onCommission() {

    }

    @Override
    public void onLogout() {
        activity.deleteFirebaseToken();
    }

    @Override
    public void onSetting() {

    }
}
