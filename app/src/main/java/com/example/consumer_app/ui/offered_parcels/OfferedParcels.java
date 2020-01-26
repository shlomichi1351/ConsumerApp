package com.example.consumer_app.ui.offered_parcels;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferedParcels extends Fragment {

    String explain="חבילות שלי שאני צריך לבחור איזה חבר יקח אותם";

    private RecyclerView parcelRecyclerView;
    int mExpandedPosition=-1;
    User user;
    private List<Parcel> parcels;
    List<Parcel>  offeredParcelsList;
    private OfferedParcelsViewModel offeredParcelsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        offeredParcelsViewModel = ViewModelProviders.of(this).get(OfferedParcelsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        user=((UserMenu)getActivity()).getUser();
        parcelRecyclerView=view.findViewById(R.id.offered_parcels_List);
        parcelRecyclerView.setHasFixedSize(true);
        parcelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Firebase_DBManager_Parcel.notifyToParcelList(new NotifyDataChange<List<Parcel>>()
        {
            @Override
            public void OnDataChanged(List<Parcel> obj)
            {
                parcels=obj;
                offeredParcelsList=new ArrayList<Parcel>();
                if(parcels != null)
                    for (Parcel parcel : parcels)
                        if(parcel.getRecipientPhoneNumber().equals(user.getPhoneNumber()) && parcel.getSuggesters() != null && parcel.getSuggesters().size()>0)
                            offeredParcelsList.add(parcel);

            }
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get the parcels list\n" + exception.toString(), Toast.LENGTH_LONG).show();

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
            View v = LayoutInflater.from(getContext()).inflate(R.layout.example_offered_parcels, parent,false);
            return new ParcelViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull final ParcelViewHolder holder, final int position) {
            final Parcel parcel = offeredParcelsList.get(position);
            final List<User> offers=new ArrayList<>();
            List<String>images=new ArrayList<>();
            List<String> texts=new ArrayList<>();

            if (parcel.getType() == Parcel.Type.Envelope)
                holder.details_package.setText("החבילה היא " + parcel.getType().toString() + "ונמצאת כרגע ב" + parcel.getDistributionCenterAddress());
            else
                holder.details_package.setText("החבילה היא " + parcel.getType().toString() + "עם משקל של " + parcel.getWeight()+ " ונמצאת כרגע ב" + parcel.getDistributionCenterAddress());


            Query query =  Firebase_DBManager_User.usersRef
                    .orderByChild("phoneNumber");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren())
                    {
                        for(String phone:parcel.getSuggesters())
                        {
                            if(child.getValue(User.class).getPhoneNumber().equals(phone)) {
                                user = child.getValue(User.class);
                                break;
                            }

                        }
                        offers.add(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            for(User user : offers)
            {
                if(user.getImageFirebaseUrl() != null)
                    images.add(user.getImageFirebaseUrl());
                else
                    images.add(String.valueOf(R.drawable.user));
                texts.add(user.getFirstName() + " " + user.getLastName() + " ," + user.getAddress());
            }


            String[] array_texts = new String[texts.size()];
            for(int i = 0; i < texts.size(); i++) array_texts[i] = texts.get(i);

            String[] array_images = new String[images.size()];
            for(int i = 0; i < images.size(); i++) array_images[i] = images.get(i);


            holder.spinner.setAdapter(new SpinnerAdapter(getContext(), 0, array_texts, array_images));

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        @Override
        public int getItemCount()
        {
            return offeredParcelsList.size();
        }
    }

    //represent the information in every card in the recycle view
    class ParcelViewHolder extends RecyclerView.ViewHolder
    {
        TextView details_package;
        Spinner spinner;


        ParcelViewHolder(View itemView)
        {
            super(itemView);

           details_package=itemView.findViewById(R.id.parcel_details_offer);
           spinner=itemView.findViewById(R.id.offered_parcels_List);

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



    public class SpinnerAdapter extends ArrayAdapter<String> {

        private Context ctx;
        private String[] contentArray;
        private String[] imageArray;

        public SpinnerAdapter(Context context, int resource, String[] objects,
                              String[] imageArray) {
            super(context,  R.layout.row, R.id.spinnerTextView, objects);
            this.ctx = context;
            this.contentArray = objects;
            this.imageArray = imageArray;
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row, parent, false);

            TextView textView =row.findViewById(R.id.spinnerTextView);
            textView.setText(contentArray[position]);

            CircleImageView imageView = row.findViewById(R.id.spinnerImages);

            Integer dra=new Integer(R.drawable.user);
            Integer pos=new Integer(Integer.parseInt(imageArray[position]));

            if(dra.equals(pos))
                Glide.with(getContext())
                        .load(R.drawable.user)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            else
                Glide.with(getContext())
                        .load(imageArray[position])
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageView);
            return row;
        }
    }
}