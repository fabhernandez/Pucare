package com.example.a01363207.pucare.UserPlantPackage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a01363207.pucare.GetImageFromURL;
import com.example.a01363207.pucare.R;

import java.util.List;

/** This class handles the users catalogue **/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public static String EXTRA_PLANT_PK = "PLANT_SELECTED";
    private static String TAG           = "RecyclerViewAdapter";

    // global variables
    private Context context;
    List<UserPlantDP> plantsList;
    PlantsEdit plantsEdit;

    // Constructor
    public RecyclerViewAdapter(Context c, List<UserPlantDP> l) {
        context = c;
        plantsList = l;
        plantsEdit = new PlantsEdit();
            //Log.d(TAG,"*------- Contents of plantsList -------*");
            /*
            for(int i = 0; i < plantsList.size(); i++){
                Log.d(TAG,"Nick: "+plantsList.get(i).getNickname());
                Log.d(TAG,"Type: "+plantsList.get(i).getPlantName());
                Log.d(TAG,"Water: "+plantsList.get(i).getNextWater());
            }
            */
    }

    // Gets the items to set up the info
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.plants_card_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    /* gives a better format to the next water date */
    private String getTimeRemaining(String date){
        OperationsHandler oh = new OperationsHandler();
        return oh.formatDate(date);
    }

    /* Sets the values to elements in layout, also the onClick listener is here,
        loads the next layout and sends the primary key of this selected item
    */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        new GetImageFromURL(myViewHolder.ivImage).execute(plantsList.get(i).getImage());
        myViewHolder.tvName.setText(plantsList.get(i).getNickname());
        // loads only time remaining to water the plant
        //myViewHolder.tvWater.setText(getTimeRemaining(plantsList.get(i).getNextWater()));
        myViewHolder.tvWater.setText(plantsList.get(i).getNextWater());
            //Log.d(TAG, "*** nick: " + plantsList.get(i).getNickname());
            //Log.d(TAG, "*** water: " + plantsList.get(i).getNextWater());

        // add listener to each card, displays its details
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pk = plantsList.get(i).getUserEmail() + "*" + plantsList.get(i).getDateRegistered();
                    //Log.d(TAG, ">>>>>>> pk: " + pk);
                Intent intent = new Intent(context, PlantsEdit.class);
                intent.putExtra(EXTRA_PLANT_PK, pk);
                context.startActivity(intent);
            }
        });
    }

    // Size of the list
    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    // This class creates the elements that will be set with its corresponding information
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvWater;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            ivImage = (ImageView) view.findViewById(R.id.idPlantImage);
            tvName = (TextView) view.findViewById(R.id.idPlantName);
            tvWater = (TextView) view.findViewById(R.id.idNextWater);
            cardView = (CardView) view.findViewById(R.id.idCardView);
        }
    }
}
