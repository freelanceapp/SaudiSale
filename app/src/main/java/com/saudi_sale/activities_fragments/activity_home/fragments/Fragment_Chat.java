package com.saudi_sale.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.saudi_sale.R;
import com.saudi_sale.activities_fragments.activity_chat.ChatActivity;
import com.saudi_sale.activities_fragments.activity_home.HomeActivity;
import com.saudi_sale.adapters.RoomAdapter;
import com.saudi_sale.databinding.FragmentChatBinding;
import com.saudi_sale.models.ChatUserModel;
import com.saudi_sale.models.DepartmentDataModel;
import com.saudi_sale.models.RoomDataModel;
import com.saudi_sale.models.RoomModel;
import com.saudi_sale.models.UserModel;
import com.saudi_sale.preferences.Preferences;
import com.saudi_sale.remote.Api;
import com.saudi_sale.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.BuildConfig;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Chat extends Fragment {

    private HomeActivity activity;
    private FragmentChatBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private List<RoomModel> roomModelList;
    private RoomAdapter adapter;

    public static Fragment_Chat newInstance() {

        return new Fragment_Chat();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        roomModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new RoomAdapter(roomModelList, activity, this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        getRooms();
        binding.swipeRefresh.setOnRefreshListener(() -> getRooms());

    }

    private void getRooms() {
        try {
            userModel = preferences.getUserData(activity);
            if (userModel == null) {
                binding.progBar.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                binding.tvNoConversation.setVisibility(View.VISIBLE);

                return;
            }
            Api.getService(Tags.base_url)
                    .getRooms("Bearer " + userModel.getData().getToken(), userModel.getData().getId())
                    .enqueue(new Callback<RoomDataModel>() {
                        @Override
                        public void onResponse(Call<RoomDataModel> call, Response<RoomDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        roomModelList.clear();
                                        roomModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoConversation.setVisibility(View.GONE);
                                    } else {
                                        binding.tvNoConversation.setVisibility(View.VISIBLE);

                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RoomDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    public void setRoomDate(RoomModel roomModel) {
        int chat_user_id;
        if (userModel.getData().getId() == roomModel.getFirst_user_id()) {
            chat_user_id = roomModel.getSecond_user_id();
        } else {
            chat_user_id = roomModel.getFirst_user_id();

        }
        ChatUserModel chatUserModel = new ChatUserModel(chat_user_id, roomModel.getOther_user_name(), roomModel.getOther_user_logo(), roomModel.getId());
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivityForResult(intent, 100);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            getRooms();
        }
    }
}