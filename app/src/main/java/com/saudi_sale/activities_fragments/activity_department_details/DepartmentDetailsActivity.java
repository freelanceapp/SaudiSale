package com.saudi_sale.activities_fragments.activity_department_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.saudi_sale.R;
import com.saudi_sale.adapters.ProductAdapter;
import com.saudi_sale.adapters.SubDepartmentAdapter;
import com.saudi_sale.databinding.ActivityDepartmentDetailsBinding;
import com.saudi_sale.language.Language;
import com.saudi_sale.models.DepartmentModel;
import com.saudi_sale.models.ProductModel;
import com.saudi_sale.models.ProductsDataModel;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;
import com.saudi_sale.remote.Api;
import com.saudi_sale.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentDetailsActivity extends AppCompatActivity {
    private ActivityDepartmentDetailsBinding binding;
    private String lang;
    private List<ProductModel> productModelList;
    private ProductAdapter adapter;
    private DepartmentModel departmentModel;
    private List<DepartmentModel.SubCategory> subCategoryList;
    private SubDepartmentAdapter subDepartmentAdapter;
    private Preferences preferences;
    private UserModel userModel;

    private int child_pos = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_department_details);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        departmentModel = (DepartmentModel) intent.getSerializableExtra("data");
        child_pos = intent.getIntExtra("child_pos", 0);
        DepartmentModel.SubCategory subCategory = departmentModel.getSub_categories().get(child_pos);
        subCategory.setSelected(true);


    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        productModelList = new ArrayList<>();
        binding.setLang(lang);
        binding.setModel(departmentModel);
        subCategoryList = new ArrayList<>(departmentModel.getSub_categories());
        binding.recViewSubDepartment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        subDepartmentAdapter = new SubDepartmentAdapter(subCategoryList, this, child_pos);
        binding.recViewSubDepartment.setAdapter(subDepartmentAdapter);

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(productModelList,this,null);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(view -> onBackPressed());
        int sub_category_id = departmentModel.getSub_categories().get(child_pos).getId();
        getProducts(sub_category_id);

    }


    private void getProducts(int sub_category_id) {
        productModelList.clear();
        adapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);
        try {
            Api.getService(Tags.base_url)
                    .getFilteredProducts(departmentModel.getId(),sub_category_id, "no")
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
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
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(DepartmentDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(DepartmentDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(DepartmentDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DepartmentDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    public void setItemData(DepartmentModel.SubCategory subCategory) {
        getProducts(subCategory.getId());
    }


    public void setProductItemData(ProductModel productModel) {

    }
}