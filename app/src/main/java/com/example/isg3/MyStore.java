package com.example.isg3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyStore extends RecyclerView.Adapter<MyStoreHolder> {
    private Context context;
    private List<StoreClass> storeList;

    public MyStore(Context context, List<StoreClass> dataList) {
        this.context = context;
        this.storeList = dataList;
    }


    @NonNull
    @Override
    public MyStoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);

        return new MyStoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyStoreHolder holder, int position) {
        Glide.with(context).load(storeList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(storeList.get(position).getDataTitle());
        holder.recDesc.setText(storeList.get(position).getDataDesc());
        holder.recLang.setText(storeList.get(position).getDataLang());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DeaitlsNewsActivity.class);
                intent.putExtra("Image",storeList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description",storeList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title",storeList.get(holder.getAdapterPosition()).getDataTitle());
               intent.putExtra("Key",storeList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public void searchDataList(List<StoreClass> searchList){
        storeList = searchList;
        notifyDataSetChanged();
    }

}

class MyStoreHolder extends  RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recTitle, recDesc , recLang;
    CardView recCard ;

    public MyStoreHolder(@NonNull View itemView){
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recLang = itemView.findViewById(R.id.recLang);
        recTitle = itemView.findViewById(R.id.recTitle);


    }

}

