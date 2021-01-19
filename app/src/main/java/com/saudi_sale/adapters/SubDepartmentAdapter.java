package com.saudi_sale.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.saudi_sale.databinding.SubCategoryRowBinding;
import com.saudi_sale.models.DepartmentModel;

import java.util.List;

public class SubDepartmentAdapter extends RecyclerView.Adapter<SubDepartmentAdapter.MyHolder> {

    private List<DepartmentModel.SubCategory> list;
    private Context context;
    private int selectedPos ;
    private int oldPos = selectedPos;
    private DepartmentDetailsActivity  activity;

    public SubDepartmentAdapter(List<DepartmentModel.SubCategory> list, Context context,int selectedPos) {
        this.list = list;
        this.context = context;
        activity = (DepartmentDetailsActivity) context;
        this.selectedPos = selectedPos;
        oldPos = this.selectedPos;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SubCategoryRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.sub_category_row, parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.binding.setModel(list.get(position));
        DepartmentModel.SubCategory mainDepartmentModel = list.get(position);

        if (mainDepartmentModel.isSelected()){
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.white));
        }else {
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.gray5));
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context,R.color.gray9));

        }

        holder.itemView.setOnClickListener(view -> {
            selectedPos = holder.getAdapterPosition();
            updateSelectedPos(selectedPos);
            activity.setItemData(list.get(selectedPos));




        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private SubCategoryRowBinding binding;

        public MyHolder(SubCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }


    }

    public void updateSelectedPos(int selectedPos){
        this.selectedPos = selectedPos;

        if (oldPos!=this.selectedPos){
            DepartmentModel.SubCategory model2 = list.get(oldPos);
            model2.setSelected(false);
            list.set(oldPos,model2);
            notifyItemChanged(oldPos);
        }


        DepartmentModel.SubCategory model1 = list.get(this.selectedPos);
        model1.setSelected(true);
        list.set(selectedPos,model1);
        notifyItemChanged(this.selectedPos);

        oldPos = this.selectedPos;
    }
}
