package com.example.consumer_app.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class myFriendFragment extends Fragment {

    private myFriendViewModel galleryViewModel;
    List<User> userList;
    private RecyclerView userRecyclerView;
    View view;
    Button addFriend;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(myFriendViewModel.class);
        view = inflater.inflate(R.layout.fragment_gallery, container, false);

        final ArrayList<String> phoneList=new ArrayList<String>();
        Query query =  Firebase_DBManager_User.usersRef
                .orderByChild("phoneNumber");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = new User();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(User.class);
                    phoneList.add(value.getPhoneNumber());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userList = new ArrayList<User>();
        userRecyclerView = view.findViewById(R.id.userList);
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //getFriendsList();
        Firebase_DBManager_User.notifyToUserList(new NotifyDataChange<List<User>>()
        {
            @Override
            public void OnDataChanged(List<User> obj)
            {
                userList=obj;
                if (userRecyclerView.getAdapter() == null) {
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

    private void filteringFriendsParcels(String phone) {

    }

    public void getFriendsList() {
        Query query = Firebase_DBManager_User.usersRef
                .orderByChild("userName");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue() != null)
                        userList.add(child.getValue(User.class));

                }
                if (userRecyclerView.getAdapter() == null) {
                    userRecyclerView.setAdapter(new UserRecycleViewAdapter());
                } else {

                    userRecyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class UserRecycleViewAdapter extends RecyclerView.Adapter<UserViewHolder> {
        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_user, parent, false);
            return new UserViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);

            Glide.with(getContext())
                    .load(user.getImageFirebaseUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.profile_image_recycle);

            holder.name_user.setText(user.getFirstName() + " " + user.getLastName());
            user.setAddress(user.getAddress());
            holder.details_user.setText(user.getAddress());

        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

    //represent the information in every card in the recycle view
    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name_user;
        TextView details_user;
        CircleImageView profile_image_recycle;

        UserViewHolder(View itemView) {
            super(itemView);

            name_user = itemView.findViewById(R.id.user_name_exmaple_card);
            details_user = itemView.findViewById(R.id.user_details);
            profile_image_recycle = itemView.findViewById(R.id.Profile_Image_recycle);
            addFriend = itemView.findViewById(R.id.not_friend);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User a = new User(); //
                    int position = getAdapterPosition();
                    ArrayList<String> updateFriends = new ArrayList<String>();
                    for (String s : a.getFriendsList())
                        updateFriends.add(s);
                    updateFriends.add(userList.get(position).getPhoneNumber());
                    a.setFriendsList(updateFriends);

                    Firebase_DBManager_User.updateUser(a, new Action<String>() {


                        @Override
                        public void onSuccess(String obj) {

                        }

                        @Override
                        public void onFailure(Exception exception) {
                        }

                        @Override
                        public void onProgress(String status, double percent) {
                        }
                    });
                }
            });

        }
    }
}