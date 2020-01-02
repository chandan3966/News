package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MessageViewHolder> {
    private List<NewsItems> list;
    private Context context;

    public NewsAdapter(List<NewsItems> list,Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_items,parent,false);
        MessageViewHolder viewHolder = new MessageViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        final NewsItems newsItem = list.get(position);
        holder.title.setText(newsItem.getTitle());
        holder.desc.setText(newsItem.getDesc());
//        holder.time.setText(newsItem.getTimestamp());
        String[] times = newsItem.getTimestamp().split("T");
        holder.time.setText(times[0]);
        holder.content.setText("Description : "+newsItem.getContent());
        if (newsItem.getAuth().equals(null))
        holder.author.setText(newsItem.getAuth());
        else
            holder.author.setVisibility(View.GONE);
        Glide.with(context).load(newsItem.getImg()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getUrl()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView title,desc,author,time,content;
        ImageView imageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc  =itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
