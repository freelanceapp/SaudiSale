package com.saudi_sale.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.saudi_sale.R;
import com.saudi_sale.databinding.ChatImageLeftAdminRowBinding;
import com.saudi_sale.databinding.ChatImageLeftRowBinding;
import com.saudi_sale.databinding.ChatImageRightAdminRowBinding;
import com.saudi_sale.databinding.ChatImageRightRowBinding;
import com.saudi_sale.databinding.ChatMessageLeftAdminRowBinding;
import com.saudi_sale.databinding.ChatMessageLeftRowBinding;
import com.saudi_sale.databinding.ChatMessageRightAdminRowBinding;
import com.saudi_sale.databinding.ChatMessageRightRowBinding;
import com.saudi_sale.models.AdminMessageModel;
import com.saudi_sale.models.MessageModel;
import com.saudi_sale.tags.Tags;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdminAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int msg_left = 1;
    private final int msg_right = 2;
    private final int img_left = 3;
    private final int img_right = 4;
    private final int load = 9;
    private LayoutInflater inflater;
    private List<AdminMessageModel> list;
    private Context context;
    private int current_user_id;


    public ChatAdminAdapter(List<AdminMessageModel> list, Context context, int current_user_id) {
        this.list = list;
        this.context = context;
        this.current_user_id = current_user_id;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msg_left) {
            ChatMessageLeftAdminRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_message_left_admin_row, parent, false);
            return new HolderMsgLeft(binding);
        } else if (viewType == msg_right) {
            ChatMessageRightAdminRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_message_right_admin_row, parent, false);
            return new HolderMsgRight(binding);
        } else if (viewType == img_left) {
            ChatImageLeftAdminRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_image_left_admin_row, parent, false);
            return new HolderImageLeft(binding);
        } else {
            ChatImageRightAdminRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_image_right_admin_row, parent, false);
            return new HolderImageRight(binding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        AdminMessageModel model = list.get(position);


        ///////////////////


        if (holder instanceof HolderMsgLeft) {
            HolderMsgLeft holderMsgLeft = (HolderMsgLeft) holder;
            holderMsgLeft.binding.setModel(model);
            holderMsgLeft.binding.tvTime.setText(getTime(model.getDate()));

        } else if (holder instanceof HolderMsgRight) {
            HolderMsgRight holderMsgRight = (HolderMsgRight) holder;
            holderMsgRight.binding.setModel(model);
            holderMsgRight.binding.tvTime.setText(getTime(model.getDate()));


        } else if (holder instanceof HolderImageLeft) {
            HolderImageLeft holderImageLeft = (HolderImageLeft) holder;
            holderImageLeft.binding.setModel(model);
            holderImageLeft.binding.tvTime.setText(getTime(model.getDate()));
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL + model.getFile_link())).into(holderImageLeft.binding.imageChat);

        } else if (holder instanceof HolderImageRight) {
            HolderImageRight holderImageRight = (HolderImageRight) holder;
            holderImageRight.binding.tvTime.setText(getTime(model.getDate()));
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL + model.getFile_link())).into(holderImageRight.binding.image);


        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getTime(long time) {
        return new SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(new Date(time * 1000));
    }


    public static class HolderMsgLeft extends RecyclerView.ViewHolder {
        private ChatMessageLeftAdminRowBinding binding;

        public HolderMsgLeft(@NonNull ChatMessageLeftAdminRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HolderMsgRight extends RecyclerView.ViewHolder {
        private ChatMessageRightAdminRowBinding binding;

        public HolderMsgRight(@NonNull ChatMessageRightAdminRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HolderImageLeft extends RecyclerView.ViewHolder {
        private ChatImageLeftAdminRowBinding binding;

        public HolderImageLeft(@NonNull ChatImageLeftAdminRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HolderImageRight extends RecyclerView.ViewHolder {
        private ChatImageRightAdminRowBinding binding;

        public HolderImageRight(@NonNull ChatImageRightAdminRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @Override
    public int getItemViewType(int position) {

        AdminMessageModel messageModel = list.get(position);

        if (messageModel == null) {
            return load;
        } else {

            if (messageModel.getMessage_kind().equals("text")) {

                if (messageModel.getFrom_user_id() == current_user_id) {

                    return msg_right;
                } else {
                    return msg_left;
                }
            } else {
                if (messageModel.getFrom_user_id() == current_user_id) {

                    return img_right;
                } else {
                    return img_left;
                }

            }


        }


    }


}
