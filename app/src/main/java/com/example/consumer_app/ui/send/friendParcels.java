package com.example.consumer_app.ui.send;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumer_app.Model.Action;
import com.example.consumer_app.Model.Firebase_DBManager_Parcel;
import com.example.consumer_app.Model.NotifyDataChange;
import com.example.consumer_app.Model.Parcel;
import com.example.consumer_app.Model.User;
import com.example.consumer_app.R;
import com.example.consumer_app.ui.UserMenu;

import java.util.ArrayList;
import java.util.List;

public class friendParcels extends Fragment
{
    private List<Parcel> parcels;
    List<Parcel>   friendsParcelsList;
    User user;
    private RecyclerView parcelRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        user=((UserMenu)getActivity()).getUser();
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
                            if(phone.equals(parcel.getRecipientPhoneNumber()))
                                friendsParcelsList.add(parcel);
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
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_parcel, parent,false);
            return new ParcelViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull ParcelViewHolder holder, int position) {
            Parcel parcel = friendsParcelsList.get(position);

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
            return friendsParcelsList.size();
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
