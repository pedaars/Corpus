package com.example.firebaseauth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SlideshowAdapter extends RecyclerView.Adapter<SlideshowAdapter.SlideshowViewHolder> {

    private Context context;
    private List<Room> roomList;

    SlideshowAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public SlideshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SlideshowViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_slideshow, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SlideshowViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.textViewRoom.setText(room.getRoomName());
        holder.textViewLength.setText(String.format("Length %s", room.getLength()));
        holder.textViewWidth.setText(String.format("Width %s", room.getWidth()));
        holder.textViewRoomDesc.setText(room.getRoomDesc());

        Log.d("NAME", String.valueOf(room.getRoomName()));

        String imageUri = String.valueOf(room.getImageUrl());

        if(!imageUri.isEmpty()) {
            Glide.with(context)
                    .load(imageUri)
                    .into(holder.imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.image_placeholder)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    class SlideshowViewHolder extends RecyclerView.ViewHolder {

        TextView textViewRoom, textViewLength, textViewWidth, textViewRoomDesc;
        ImageView imageView;

        SlideshowViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_room);
            textViewRoom = itemView.findViewById(R.id.textview_room_name);
            textViewLength = itemView.findViewById(R.id.textview_length);
            textViewWidth = itemView.findViewById(R.id.textview_width);
            textViewRoomDesc = itemView.findViewById(R.id.textview_desc);
        }
    }
}
