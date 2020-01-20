package com.example.consumer_app.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    List<User> userList;
    private RecyclerView userRecyclerView;
    View view;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        view = inflater.inflate(R.layout.fragment_gallery, container, false);

        userRecyclerView=view.findViewById(R.id.userList);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Firebase_DBManager_User.notifyToUserList(new NotifyDataChange<List<User>>()
        {
            @Override
            public void OnDataChanged(List<User> obj)
            {

                if (userRecyclerView.getAdapter() == null) {
                    userList=obj;
                    userRecyclerView.setAdapter(new UserRecycleViewAdapter());
                }
                else {

                    userRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get user list\n" + exception.toString(), Toast.LENGTH_LONG).show();

            }
        });

        return view;
    }

    class UserRecycleViewAdapter extends RecyclerView.Adapter<GalleryFragment.UserViewHolder>
    {
        @NonNull
        @Override
        public GalleryFragment.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_user, parent,false);
            return new UserViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull GalleryFragment.UserViewHolder holder, int position) {
            User user = userList.get(position);

            Glide.with(getContext())
                    .load(user.getImageFirebaseUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profile_image_recycle);

            holder.name_user.setText(user.getFirstName() + " " + user.getLastName());

            holder.details_user.setText(", 0 חברים משותפים" + user.getAddress());

        }

        @Override
        public int getItemCount()
        {
            return userList.size();
        }
    }

    //represent the information in every card in the recycle view
    class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView name_user;
        TextView details_user;
        CircleImageView profile_image_recycle;

        UserViewHolder(View itemView)
        {
            super(itemView);

            name_user=view.findViewById(R.id.user_name_exmaple_card);
            details_user=view.findViewById(R.id.user_details);
            profile_image_recycle=view.findViewById(R.id.Profile_Image_recycle);


        }
    }
}