package com.example.consumer_app.ui.takenParcels;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProviders;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TakenParcels extends Fragment {

    String explain="חבילות שאני לקחתי ועדיין לא מסרתי";


    User user;
    private List<Parcel> parcels;
    List<Parcel>   takenParcelsList;
    private RecyclerView parcelRecyclerView;
    int mExpandedPosition=-1;
    private TakenParcelsViewModel takenParcelsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takenParcelsViewModel = ViewModelProviders.of(this).get(TakenParcelsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_taken_parcels, container, false);

        user=UserMenu.user;
        parcelRecyclerView=view.findViewById(R.id.taken_parcels_List);
        parcelRecyclerView.setHasFixedSize(true);
        parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                parcels=obj;
                takenParcelsList=new ArrayList<Parcel>();
                if(parcels != null)
                    for (Parcel parcel : parcels)
                        if(parcel.getPhoneDeliver().equals(user.getPhoneNumber()) && parcel.getStatus().equals(Parcel.Status.OnTheWay))
                            takenParcelsList.add(parcel);
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
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_taken_parcel, parent,false);
            return new ParcelViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull final ParcelViewHolder holder, final int position) {
            final Parcel parcel = takenParcelsList.get(position);
            final User[] recipientUser = new User[1];

            String[] allName = parcel.getRecipientName().split(" ");
            if(allName.length == 1)
                holder.fname_taken_parcel.setText(allName[0]);
            else
            {
                holder.fname_taken_parcel.setText(allName[0]);
                holder.fname_taken_parcel.setText(allName[1]);
            }

            holder.phone_taken_parcel.setText(parcel.getRecipientPhoneNumber());
            holder.address_taken_parcel.setText(parcel.getRecipientAddress());


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
                                .into(holder.profile_image_taken_parcel);

                    else
                        Glide.with(getContext())
                                .load(recipientUser[0].getImageFirebaseUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.profile_image_taken_parcel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            // handle expendable

            final boolean isExpanded = position == mExpandedPosition;
            holder.subItem_taken_parcel.setVisibility(isExpanded?View.VISIBLE:View.GONE);
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
                    parcel.setStatus(Parcel.Status.Delivered);
                    takenParcelsList.remove(position);
                    parcelRecyclerView.removeViewAt(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, takenParcelsList.size());
                    Firebase_DBManager_Parcel.updateParcel(parcel, new Action<String>() {
                        @Override
                        public void onSuccess(String obj) {
                            Toast.makeText(getContext(),"החבילה הגיעה ליעדה", Toast.LENGTH_LONG).show();

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



            holder.phone_recipient_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(UserMenu.newPhoneCallIntent(parcel.getRecipientPhoneNumber()));
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return takenParcelsList.size();
        }
    }

    //represent the information in every card in the recycle view
    class ParcelViewHolder extends RecyclerView.ViewHolder
    {
        TextView lname_taken_parcel;
        TextView fname_taken_parcel;
        TextView phone_taken_parcel;
        TextView address_taken_parcel;
        CircleImageView profile_image_taken_parcel;
        LinearLayout subItem_taken_parcel;
        Button takeParcel;
        Button phone_recipient_button ;


        ParcelViewHolder(View itemView)
        {
            super(itemView);

            address_taken_parcel = itemView.findViewById(R.id.address_taken_parcel);
            lname_taken_parcel = itemView.findViewById(R.id.lname_taken_parcel);
            fname_taken_parcel = itemView.findViewById(R.id.fname_taken_parcel);
            phone_taken_parcel = itemView.findViewById(R.id.phone_namber_taken_parcel);
            takeParcel=itemView.findViewById(R.id.taken_parcel_button);
            phone_recipient_button=itemView.findViewById(R.id.phone_recipient_taken_parcel);
            subItem_taken_parcel=itemView.findViewById(R.id.subitem_taken_parcel);
            profile_image_taken_parcel=itemView.findViewById(R.id.Profile_image_taken_parcel);


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
