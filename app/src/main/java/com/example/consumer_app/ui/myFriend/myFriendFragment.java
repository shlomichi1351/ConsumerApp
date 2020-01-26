package com.example.consumer_app.ui.myFriend;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.consumer_app.ui.UserMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class myFriendFragment extends Fragment {

    private myFriendViewModel galleryViewModel;
    List<User> userList;
    private RecyclerView userRecyclerView;
    View view;


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
        public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position)
        {
            final User user = userList.get(position);

            if (user.getImageFirebaseUrl() == null)
                Glide.with(getContext())
                        .load(R.drawable.user)
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.profile_image_recycle);
            else
                Glide.with(getContext())
                        .load(user.getImageFirebaseUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.profile_image_recycle);


            holder.name_user.setText(user.getFirstName() + " " + user.getLastName());
            user.setAddress(user.getAddress());
            holder.details_user.setText(user.getAddress());

            if(UserMenu.user.getFriendsList().contains(user.getPhoneNumber()))
                holder.addOrDelete.setText("הסר");
            else
                holder.addOrDelete.setText("הוסף");

            holder.addOrDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    boolean flag=false;
                    ArrayList<String> updateFriends = new ArrayList<String>();

                    for (String s : UserMenu.user.getFriendsList())
                        updateFriends.add(s);


                    for (String s : UserMenu.user.getFriendsList()) {
                        if (userList.get(position).getPhoneNumber().equals(s))
                        {
                            flag = true;

                            updateFriends.remove(s);
                            UserMenu.user.setFriendsList(updateFriends);
                            Firebase_DBManager_User.updateUser(UserMenu.user, new Action<String>() {


                                @Override
                                public void onSuccess(String obj) {
                                    Toast.makeText(getContext(),"החבר התווסף",Toast.LENGTH_LONG).show();
                                    notifyDataSetChanged();
                                    return;
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    Toast.makeText(getContext(),"החבר לא התווסף",Toast.LENGTH_LONG);

                                }

                                @Override
                                public void onProgress(String status, double percent) {
                                }
                            });
                        }
                    }

                    if(!flag)
                    {
                        updateFriends.add(userList.get(position).getPhoneNumber());
                        UserMenu.user.setFriendsList(updateFriends);
                        Firebase_DBManager_User.updateUser(UserMenu.user, new Action<String>() {


                            @Override
                            public void onSuccess(String obj) {
                                Toast.makeText(getContext(),"החבר התווסף",Toast.LENGTH_LONG).show();
                                notifyDataSetChanged();
                                return;
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(getContext(),"החבר לא התווסף",Toast.LENGTH_LONG);

                            }

                            @Override
                            public void onProgress(String status, double percent) {
                            }
                        });
                    }

                }
            });

            holder.address_friend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        List<Double>points=getLocationFromAddress(user.getAddress());

                        String uri = "http://maps.google.com/maps?daddr=" + points.get(0) + "," + points.get(1);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getContext(),"אין אפשרות להמיר את הכתובת לקווי אורך ורוחב", Toast.LENGTH_LONG).show();
                    }

                }
            });

            holder.phone_friend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(UserMenu.newPhoneCallIntent(user.getPhoneNumber()));
                }
            });


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
        Button addOrDelete;
        Button phone_friend_button , address_friend_button;



        UserViewHolder(View itemView) {
            super(itemView);
            phone_friend_button=itemView.findViewById(R.id.phone_friend_button);
            address_friend_button=itemView.findViewById(R.id.address_friend_button);
            name_user = itemView.findViewById(R.id.user_name_exmaple_card);
            details_user = itemView.findViewById(R.id.user_details);
            profile_image_recycle = itemView.findViewById(R.id.Profile_Image_recycle);
            addOrDelete = itemView.findViewById(R.id.add_or_delete_friend);

        }
    }

    public List<Double> getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        List<Double> list_points = Arrays.asList();


        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            list_points.add((double) (location.getLatitude() * 1E6));
            list_points.add((double) (location.getLongitude() * 1E6));

            return list_points;
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"אין אפשרות להמיר את הכתובת לקווי אורך ורוחב", Toast.LENGTH_LONG);
        }
        return null;
    }
}



/*
addOrDelete.setText("ff");
 */