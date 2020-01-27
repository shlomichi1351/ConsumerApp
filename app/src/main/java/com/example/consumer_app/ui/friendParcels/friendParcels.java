package com.example.consumer_app.ui.friendParcels;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager_Parcel;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.UserMenu;
import com.example.consumer_app.ui.home.HomeFragment;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class friendParcels extends Fragment
{
    private List<Parcel> parcels;
    List<Parcel>   friendsParcelsList;
    User user;
    private RecyclerView parcelRecyclerView;
    int mExpandedPosition = -1;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freind_parcels, container, false);

        user=UserMenu.user;
        parcelRecyclerView=view.findViewById(R.id.parcelsFreindList);
        parcelRecyclerView.setHasFixedSize(true);
        parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                parcels=obj;
                friendsParcelsList=new ArrayList<Parcel>();
                if(parcels != null && user.getFriendsList() != null)
                    for(String phone : user.getFriendsList())
                        for (Parcel parcel : parcels)
                            if(phone.equals(parcel.getRecipientPhoneNumber()) && parcel.getStatus().equals(Parcel.Status.Registered))
                                friendsParcelsList.add(parcel);

                if (parcelRecyclerView.getAdapter() == null) {
                    parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());
                }
                else {

                    parcelRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get friend list\n" + exception.toString(), Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }






    public class ParcelRecycleViewAdapter extends RecyclerView.Adapter<ParcelViewHolder>
    {
        @NonNull
        @Override
        public ParcelViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_friend_parcel, parent,false);
            return new ParcelViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull final ParcelViewHolder holder, final int position) {
            final Parcel parcel = friendsParcelsList.get(position);
            final User[] recipientUser = new User[1];

            String[] allName = parcel.getRecipientName().split(" ");
            if(allName.length == 1)
                holder.fname_friend_parcel.setText(allName[0]);
            else
            {
                holder.fname_friend_parcel.setText(allName[0]);
                holder.lname_friend_parcel.setText(allName[1]);
            }

            holder.phone_friend_parcel.setText(parcel.getRecipientPhoneNumber());
            holder.address_friend_parcel.setText(parcel.getDistributionCenterAddress());

            if(parcel.getFragile())
                holder.other_details_friend_parcel.setText(" החבילה היא מסוג " + parcel.getType().toString()+ "," +" ומכילה תוכן שביר! ");
            else holder.other_details_friend_parcel.setText(" החבילה היא מסוג " + parcel.getType().toString()+ "," +" ואינה מכילה תוכן שביר! ");



            Query query =  Firebase_DBManager_User.usersRef
                    .orderByChild("phoneNumber");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {

                        if(child.getValue(User.class).getPhoneNumber().equals(parcel.getRecipientPhoneNumber())) {
                            recipientUser[0] = child.getValue(User.class);
                            break;
                        }

                    }
                    if (recipientUser[0].getImageFirebaseUrl()== null)
                        Glide.with(getContext())
                                .load(R.drawable.user)
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.profile_image_friend_parcel);

                    else
                        Glide.with(getContext())
                                .load(recipientUser[0].getImageFirebaseUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.profile_image_friend_parcel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            // handle expendable
            final boolean isExpanded = position == mExpandedPosition;
            holder.subItem.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            holder.itemView.setActivated(isExpanded);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1:position;
                    TransitionManager.beginDelayedTransition(parcelRecyclerView);
                    notifyDataSetChanged();
                }
            });

            holder.takeParcel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Parcel p;
                    p=friendsParcelsList.get(position);
                    ArrayList<String> temp_phones=new ArrayList<>();
                    for(String parc: p.getSuggesters())
                        temp_phones.add(parc);
                    temp_phones.add(UserMenu.user.getPhoneNumber());
                    p.setSuggesters(temp_phones);
                    p.setStatus(Parcel.Status.CollectionOffered);

                    Firebase_DBManager_Parcel.updateParcel(p, new Action<String>() {
                        @Override
                        public void onSuccess(String obj) {
                            Toast.makeText(getContext(),"התווספת לרשימת המועמדים!", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });
//                    friendsParcelsList.remove(position);
//                    parcelRecyclerView.removeViewAt(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, friendsParcelsList.size());
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return friendsParcelsList.size();
        }
    }

    //represent the information in every card in the recycle view
    class ParcelViewHolder extends RecyclerView.ViewHolder
    {
        TextView lname_friend_parcel;
        TextView fname_friend_parcel;

        TextView phone_friend_parcel;
        TextView other_details_friend_parcel;
        TextView address_friend_parcel;
        CircleImageView profile_image_friend_parcel;
        LinearLayout subItem;
        Button takeParcel;


        ParcelViewHolder(View itemView)
        {
            super(itemView);

            address_friend_parcel = itemView.findViewById(R.id.address_friend_parcel);
            other_details_friend_parcel = itemView.findViewById(R.id.type_friend_parcel);
            lname_friend_parcel = itemView.findViewById(R.id.lname_friend_parcel);
            fname_friend_parcel = itemView.findViewById(R.id.fname_friend_parcel);
            phone_friend_parcel = itemView.findViewById(R.id.phone_namber_friend_parcel);
            takeParcel=itemView.findViewById(R.id.take_parcel_button);
            subItem=itemView.findViewById(R.id.subitem);
            profile_image_friend_parcel=itemView.findViewById(R.id.profil_image_friend_parcel);


            // itemView.setOnClickListener();
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("אפשרויות");
                    MenuItem delete = menu.add(Menu.NONE, 1, 1, "מחיקה");
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int position = getAdapterPosition();
                            String id = parcels.get(position).getParcelId();
                            Firebase_DBManager_Parcel.removeParcel(parcels.get(position).getRecipientPhoneNumber()+"/"+id, new Action<String>() {


                                @Override
                                public void onSuccess(String obj) {

                                }

                                @Override
                                public void onFailure(Exception exception) {                     }
                                @Override
                                public void onProgress(String status, double percent) {                      }                 });
                            return true;

                        }
                    });
                } });

        }

    }
}
