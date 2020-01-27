package com.example.consumer_app.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager_Parcel;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.ParcelRepository;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.UserMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private List<Parcel> parcels;
    private RecyclerView parcelRecyclerView;
    private FirebaseAuth mAuth;

    private List<Parcel> roomUpdateParcels; //the parcels we update in the sq

    private List<Parcel> parcelsCopy;

    EditText search;
    static boolean  updateFlag;
    String temp_phone_user;
    ParcelRepository repository;
    User user;
    FirebaseUser userFireBase;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        search=view.findViewById(R.id.search);
        parcelRecyclerView=view.findViewById(R.id.parcelsList);
        parcelRecyclerView.setHasFixedSize(true);

        parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        parcels=new ArrayList<Parcel>();

        mAuth = FirebaseAuth.getInstance();
        userFireBase = mAuth.getCurrentUser();
        temp_phone_user=userFireBase.getPhoneNumber();
        temp_phone_user=temp_phone_user.substring(4);
        temp_phone_user="0"+temp_phone_user;



        homeViewModel.getAllParcel().observe(this, new Observer<List<Parcel>>() {
            @Override
            public void onChanged(List<Parcel> p) {
                // TODO here is the data loading
                //noteAdapter.setNotes(notes);
                roomUpdateParcels=p;
                // load data to adapter.
                if (parcelRecyclerView.getAdapter() == null) {
                    parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());
                }
                else {

                    parcelRecyclerView.getAdapter().notifyDataSetChanged();
                }

            }
        });


       //parcelRecyclerView=view.findViewById(R.id.parcelsList);
        //parcelRecyclerView.setHasFixedSize(true);
        //parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                roomUpdateParcels=new ArrayList<Parcel>();

                if(obj != null )
                {
                    for (Parcel p : obj) {

                        if (p.getRecipientPhoneNumber().equals(temp_phone_user)) {
                            roomUpdateParcels.add(p);
                        }
                    }
                }
                homeViewModel.deleteAllNotes();
                for(Parcel p: roomUpdateParcels)
                    homeViewModel.insert(p);

            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();

            }
        });

        search.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            //SEARCH OPTION
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(""))
                {
                    Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
                    {
                        @Override
                        public void OnDataChanged(List<Parcel> obj)
                        {
                            roomUpdateParcels=new ArrayList<Parcel>();

                            if(obj != null )
                            {
                                for (Parcel p : obj) {

                                    if (p.getRecipientPhoneNumber().equals(temp_phone_user)) {
                                        roomUpdateParcels.add(p);
                                    }
                                }
                                homeViewModel.deleteAllNotes();
                                for(Parcel p: roomUpdateParcels)
                                    homeViewModel.insert(p);
                            }


                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
                else {
                    parcelsCopy = new ArrayList<Parcel>();
                    for(Parcel p:roomUpdateParcels)
                        parcelsCopy.add(p);
                    roomUpdateParcels=new ArrayList<Parcel>();
                    for (Parcel p : parcelsCopy)
                    {
                        if (p.getRecipientAddress().contains(search.getText().toString()) || p.getRecipientName().contains(search.getText().toString()) || p.getRecipientPhoneNumber().contains(search.getText().toString()))
                            roomUpdateParcels.add(p);
                    }
                    Firebase_DBManager_Parcel.stopNotifyToParcelList();
                    parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());

                }
            }
        });
        return view;
    }



    public void getFriendsList(String user,final ArrayList<String> returnedList)
    {
        Query query =  Firebase_DBManager_User.usersRef
                .orderByChild("userName").equalTo("dandan");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User value = new User();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(User.class);
                }
                for (String s: value.getFriendsList())
                {
                    returnedList.add(s);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public class ParcelRecycleViewAdapter extends RecyclerView.Adapter<ParcelViewHolder>
    {
        @NonNull
        @Override
        public ParcelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_parcel, parent,false);
            return new ParcelViewHolder(v); }


        @Override
        public void onBindViewHolder(@NonNull ParcelViewHolder holder, int position) {
            Parcel parcel = roomUpdateParcels.get(position);

            holder.nameTextView.setText(parcel.getRecipientName());


            if(parcel.getType().equals(Parcel.Type.Envelope))
                holder.type_package.setImageResource(R.drawable.envelope);
            else
                holder.type_package.setImageResource(R.drawable.img_package);

            holder.phoneTextView.setText(parcel.getRecipientPhoneNumber());
            holder.address.setText(parcel.getRecipientAddress());
            holder.id.setText(String.valueOf(position+1));

            if(parcel.getFragile())
                holder.other_details.setText("החבילה מכילה תוכן שביר! ");
            else holder.other_details.setText( "החבילה אינה מכילה תוכן שביר ");

            holder.status.setText(parcel.getStatus().toString());

        }

        @Override
        public int getItemCount()
        {
            return roomUpdateParcels.size();
        }
    }

    //represent the information in every card in the recycle view
    class ParcelViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView;
        TextView phoneTextView;
        TextView other_details;
        TextView address;
        TextView id;
        ImageView type_package;
        TextView status;

        ParcelViewHolder(View itemView)
        {
            super(itemView);

            status=itemView.findViewById(R.id.status_parcel);
            type_package=itemView.findViewById(R.id.parcel_type);
            id= itemView.findViewById(R.id.id_parcel);
            address = itemView.findViewById(R.id.address_parcel);
            other_details = itemView.findViewById(R.id.other_details);
            nameTextView = itemView.findViewById(R.id.name_sender);
            phoneTextView = itemView.findViewById(R.id.phone_namber_parcel);


        }
    }
}