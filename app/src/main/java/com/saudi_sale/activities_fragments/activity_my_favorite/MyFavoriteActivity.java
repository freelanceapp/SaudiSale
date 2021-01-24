package com.saudi_sale.activities_fragments.activity_my_favorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_my_ads.MyAdsActivity;
import com.saudi_sale.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.saudi_sale.adapters.MyFavoriteAdapter;
import com.saudi_sale.databinding.ActivityMyFavoriteBinding;
import com.saudi_sale.language.Language;
import com.saudi_sale.models.ProductModel;
import com.saudi_sale.models.ProductsDataModel;
import com.saudi_sale.models.StatusResponse;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;
import com.saudi_sale.remote.Api;
import com.saudi_sale.share.Common;
import com.saudi_sale.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteActivity extends AppCompatActivity {
    private ActivityMyFavoriteBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private MyFavoriteAdapter adapter;
    private List<ProductModel> productModelList;
    private String lang;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_favorite);
        initView();
    }

    private void initView() {
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MyFavoriteAdapter(productModelList, this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getMyFavorite);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getMyFavorite();
    }

    private void getMyFavorite() {

        try {

            Api.getService(Tags.base_url)
                    .getMyFavorite("Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        productModelList.clear();
                                        productModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }

    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivity(intent);
    }

    public void disLike(ProductModel productModel, int adapterPosition) {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();

            Api.getService(Tags.base_url)
                    .like_disliked("Bearer " + userModel.getData().getToken(), productModel.getId())
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                dialog.dismiss();
                                if (response.body().getStatus() == 200) {
                                    productModelList.remove(adapterPosition);
                                    adapter.notifyItemRemoved(adapterPosition);
                                    if (productModelList.size() > 0) {
                                        binding.tvNoData.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                } else {


                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}