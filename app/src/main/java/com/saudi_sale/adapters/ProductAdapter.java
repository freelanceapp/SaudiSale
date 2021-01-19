package com.saudi_sale.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.saudi_sale.activities_fragments.activity_home.fragments.Fragment_Home;
import com.saudi_sale.databinding.ProductRowBinding;
import com.saudi_sale.models.ProductModel;
import com.saudi_sale.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private AppCompatActivity activity;

    public ProductAdapter(List<ProductModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));

            myHolder.itemView.setOnClickListener(view -> {
                if (fragment!=null){
                    if (fragment instanceof Fragment_Home) {

                        Fragment_Home fragment_main = (Fragment_Home) fragment;
                        fragment_main.setProductItemData(list.get(myHolder.getAdapterPosition()));
                    }
                }else {
                    if (activity instanceof DepartmentDetailsActivity){
                        DepartmentDetailsActivity departmentDetailsActivity = (DepartmentDetailsActivity) activity;
                        departmentDetailsActivity.setProductItemData(list.get(myHolder.getAdapterPosition()));

                    }
                }

            });

            /*if (list.get(myHolder.getAdapterPosition()).getProduct_images().size() > 0) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + list.get(myHolder.getAdapterPosition()).getProduct_images().get(0).getImage())).fit().into(myHolder.binding.imageAds);

            }
*/
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductRowBinding binding;

        public MyHolder(@NonNull ProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
