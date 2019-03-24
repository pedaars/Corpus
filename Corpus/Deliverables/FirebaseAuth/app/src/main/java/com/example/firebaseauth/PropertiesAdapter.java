package com.example.firebaseauth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.text.MessageFormat;
import java.util.List;

public class PropertiesAdapter extends RecyclerView.Adapter<PropertiesAdapter.PropertyViewHolder> {

    private Context mCtx;
    private List<Property> propertyList;

    PropertiesAdapter(Context mCtx, List<Property> propertyList) {
        this.mCtx = mCtx;
        this.propertyList = propertyList;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PropertyViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_property, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property  = propertyList.get(position);

        holder.textViewCust.setText(property.getCustomer());
        holder.textViewPrice.setText(property.getPrice());
        holder.textViewDesc.setText(String.format("%s Bed %s", property.getBedrooms(), property.getPropertyType()));
        holder.textViewHouseNo.setText(MessageFormat.format("{0} {1}", property.getAddress().getHouseNo(), property.getAddress().getStreet()));
        holder.textViewTown.setText(property.getAddress().getTown());
        holder.textViewPostCode.setText(property.getAddress().getPostCode());

        String imageUri = String.valueOf(property.getMainPhotoUrl());


            Glide.with(mCtx)
                    .load(imageUri)
                    .placeholder(R.drawable.image_placeholder)
                    .fitCenter()
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCust, textViewDesc, textViewHouseNo, textViewTown, textViewPostCode, textViewPrice;
        ImageView imageView;

        PropertyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewProp);
            textViewCust = itemView.findViewById(R.id.textview_cust_name);
            textViewPrice = itemView.findViewById(R.id.textview_price);
            textViewDesc = itemView.findViewById(R.id.textview_desc);
            textViewHouseNo = itemView.findViewById(R.id.textview_house_no);
            textViewTown = itemView.findViewById(R.id.textview_town);
            textViewPostCode = itemView.findViewById(R.id.textview_postcode);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Property property = propertyList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdatePropertyActivity.class);
            intent.putExtra("property", property);
            mCtx.startActivity(intent);
        }
    }
}
