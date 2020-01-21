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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager_Parcel;
import com.example.consumer_app.Model.Firebase_DBManager_User;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
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


    private List<Parcel> parcelsCopy;

    EditText search;
    static boolean  updateFlag;
    String temp_phone_user;
    Button btn_test;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        search=view.findViewById(R.id.search);
        btn_test=view.findViewById(R.id.button_test);

        parcels=new ArrayList<Parcel>();

        temp_phone_user="0522222222";

        parcelRecyclerView=view.findViewById(R.id.parcelsList);
        parcelRecyclerView.setHasFixedSize(true);
        parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                parcels=obj;

                if(parcels != null)
                {
                    for (Parcel p : parcels) {

                        if (!p.getRecipientName().contains("ד")) {
                            Firebase_DBManager_Parcel.r(p);
                        }
                    }
                }

                if (parcelRecyclerView.getAdapter() == null) {
                    parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());
                }
                else {

                    parcelRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();

            }
        });
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                ArrayList<String> kuku=new ArrayList<String>();
                getFriendsList("dandan",kuku);
                */


                ArrayList<String> array_test=new ArrayList<String>();
                array_test.add("yossi");
                array_test.add("dandan");
                User a=new User("dana","bid","dahnad", "1234", "0522222222", array_test);
                addUser(a);
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
                    Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>() {
                        @Override
                        public void OnDataChanged(List<Parcel> obj) {
                            parcels = obj;
                            parcelsCopy = obj;

                            if (parcelRecyclerView.getAdapter() == null) {
                                parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());
                            }
                            else {
                                parcelRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
                else {
                    parcels = new ArrayList<>();

                    for (Parcel p : parcelsCopy)
                    {
                        if (p.getRecipientAddress().contains(search.getText().toString()) || p.getRecipientName().contains(search.getText().toString()) || p.getRecipientPhoneNumber().contains(search.getText().toString()))
                            parcels.add(p);
                    }
                    Firebase_DBManager_Parcel.stopNotifyToParcelList();
                    parcelRecyclerView.setAdapter(new ParcelRecycleViewAdapter());

                }
            }
        });
        return view;
    }

    public void  addUser(User a)
    {
        try
        {
            Firebase_DBManager_User.addUserToFirebase(a, new Action<String>()
            {
                @Override
                public void onSuccess(String obj) {



                    Toast.makeText(getContext(),obj, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(), "Error \n" + exception.getMessage(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onProgress(String status, double percent) {


                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            Parcel parcel = parcels.get(position);

            holder.nameTextView.setText(parcel.getRecipientName());


            holder.phoneTextView.setText(parcel.getRecipientPhoneNumber());
            holder.address.setText(parcel.getRecipientAddress());
            holder.id.setText(String.valueOf(position+1));

            if(parcel.getFragile())
                holder.other_details.setText(" החבילה היא מסוג " + parcel.getType().toString()+ "," +" והיא מכילה תוכן שביר! ");
            else holder.other_details.setText(" החבילה היא מסוג " + parcel.getType().toString()+ "," +" ואינה מכילה תוכן שביר! ");


        }

        @Override
        public int getItemCount()
        {
            return parcels.size();
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

        ParcelViewHolder(View itemView)
        {
            super(itemView);

            id= itemView.findViewById(R.id.id_parcel);
            address = itemView.findViewById(R.id.address_parcel);
            other_details = itemView.findViewById(R.id.other_details);
            nameTextView = itemView.findViewById(R.id.name_sender);
            phoneTextView = itemView.findViewById(R.id.phone_);

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