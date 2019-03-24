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

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder> {

    private Context mCtx;
    private List<Customer> customerList;

    CustomersAdapter(Context mCtx, List<Customer> customerList) {
        this.mCtx = mCtx;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_customer, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);

        holder.textViewName.setText(customer.getName());
        holder.textViewEmail.setText(customer.getEmail());
        holder.textViewNumber.setText(customer.getPhoneNo());
        holder.textViewHouseNo.setText(MessageFormat.format("{0} {1}", customer.getAddress().getHouseNo(), customer.getAddress().getStreet()));
        holder.textViewTown.setText(customer.getAddress().getTown());
        holder.textViewPostCode.setText(customer.getAddress().getPostCode());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewName, textViewEmail, textViewNumber, textViewHouseNo, textViewTown, textViewPostCode;

        CustomerViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textview_name);
            textViewEmail = itemView.findViewById(R.id.textview_email);
            textViewNumber = itemView.findViewById(R.id.textview_phone_no);
            textViewHouseNo = itemView.findViewById(R.id.textview_width);
            textViewTown = itemView.findViewById(R.id.textview_town);
            textViewPostCode = itemView.findViewById(R.id.textview_postcode);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Customer customer = customerList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdateCustomerActivity.class);
            intent.putExtra("customer", customer);
            mCtx.startActivity(intent);
        }
    }
}
