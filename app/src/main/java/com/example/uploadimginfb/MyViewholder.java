package com.example.uploadimginfb;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uploadimginfb.R;

public class MyViewholder extends RecyclerView.ViewHolder {

    TextView tv_name;
    ImageView img;
    CardView parentLayout;

    public MyViewholder(@NonNull View itemView) {
        super(itemView);

        tv_name=itemView.findViewById(R.id.image_name);
        img=itemView.findViewById(R.id.card_imageView);
        parentLayout=itemView.findViewById(R.id.cardView);
    }
}
