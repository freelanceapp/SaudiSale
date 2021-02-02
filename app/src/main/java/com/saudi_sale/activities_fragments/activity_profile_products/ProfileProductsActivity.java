package com.saudi_sale.activities_fragments.activity_profile_products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_chat.ChatActivity;
import com.saudi_sale.activities_fragments.activity_chat_us.ChatUsActivity;
import com.saudi_sale.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.saudi_sale.adapters.ProductAdapter2;
import com.saudi_sale.databinding.ActivityProfileProductsBinding;
import com.saudi_sale.language.Language;
import com.saudi_sale.models.ChatUserModel;
import com.saudi_sale.models.MessageModel;
import com.saudi_sale.models.OtherProfileDataModel;
import com.saudi_sale.models.ProductModel;
import com.saudi_sale.models.RoomDataModel2;
import com.saudi_sale.models.SettingDataModel;
import com.saudi_sale.models.StatusResponse;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;
import com.saudi_sale.remote.Api;
import com.saudi_sale.share.Common;
import com.saudi_sale.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileProductsActivity extends AppCompatActivity {
    private ActivityProfileProductsBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private int other_user_id;
    private UserModel.Data otherUserData;
    private String lang;
    private List<ProductModel> productModelList;
    private ProductAdapter2 adapter;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_products);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        other_user_id = intent.getIntExtra("user_id", 0);


    }

    private void initView() {
        productModelList = new ArrayList<>();
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter2(productModelList, this);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(v -> finish());
        binding.llChat.setOnClickListener(v -> createChat());
        binding.llFollow.setOnClickListener(v -> {
            if (otherUserData.getFollow_status().equals("no")) {
                follow_un_follow(true);
                otherUserData.setFollow_status("yes");
            } else {
                follow_un_follow(false);
                otherUserData.setFollow_status("no");


            }
            binding.setModel(otherUserData);
        });
        getData();
    }

    private void getData() {
        try {
            Api.getService(Tags.base_url)
                    .getOtherProfile("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), other_user_id)
                    .enqueue(new Callback<OtherProfileDataModel>() {
                        @Override
                        public void onResponse(Call<OtherProfileDataModel> call, Response<OtherProfileDataModel> response) {
                            binding.progBarData.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    otherUserData = response.body().getData();
                                    binding.setModel(otherUserData);
                                    binding.scrollView.setVisibility(View.VISIBLE);
                                    if (otherUserData.getProducts() != null && otherUserData.getProducts().size() > 0) {
                                        productModelList.addAll(otherUserData.getProducts());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                binding.progBarData.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(ProfileProductsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OtherProfileDataModel> call, Throwable t) {
                            try {
                                binding.progBarData.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProfileProductsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileProductsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void createChat() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .createRoom("Bearer " + userModel.getData().getToken(), userModel.getData().getId(), other_user_id)
                    .enqueue(new Callback<RoomDataModel2>() {
                        @Override
                        public void onResponse(Call<RoomDataModel2> call, Response<RoomDataModel2> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    navigateToChatActivity(response.body().getData());
                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(ProfileProductsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RoomDataModel2> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProfileProductsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileProductsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void navigateToChatActivity(MessageModel.RoomModel data) {
        ChatUserModel chatUserModel = new ChatUserModel(otherUserData.getId(), otherUserData.getName(), otherUserData.getLogo(), data.getId());
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }

    private void follow_un_follow(boolean status) {
        if (userModel == null) {
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            return;

        }
        try {

            Api.getService(Tags.base_url)
                    .follow_un_follow("Bearer " + userModel.getData().getToken(), other_user_id)
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {

                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                if (status) {
                                    otherUserData.setFollow_status("no");

                                } else {
                                    otherUserData.setFollow_status("yes");

                                }
                                binding.setModel(otherUserData);

                                if (response.code() == 500) {
                                    Toast.makeText(ProfileProductsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {

                                if (status) {
                                    otherUserData.setFollow_status("no");

                                } else {
                                    otherUserData.setFollow_status("yes");

                                }
                                binding.setModel(otherUserData);
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProfileProductsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileProductsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }
}