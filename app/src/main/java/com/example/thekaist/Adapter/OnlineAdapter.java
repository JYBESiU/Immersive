package com.example.thekaist.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thekaist.R;
import com.example.thekaist.online_player;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OnlineAdapter extends RecyclerView.Adapter<OnlineAdapter.ViewHolder> {

    private ArrayList<online_player> mList=null;
    private Context mcontext;
    private LayoutInflater inflater;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public OnlineAdapter(Context context, ArrayList<online_player> mList) {
        this.mcontext = context;
        this.mList = mList;
    }



    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NotNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.online_player,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, mListener);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OnlineAdapter.ViewHolder holder, int position) {

        holder.view_name.setText(mList.get(position).getName());
        if(mList.get(position).getOnline().equals("true")){
            holder.view_id.setText("Online");
            holder.status.setBackgroundColor(mcontext.getResources().getColor(R.color.online));
        }
        if(mList.get(position).getOnline().equals("playing")){
            holder.view_id.setText("Playing");
            holder.status.setBackgroundColor(mcontext.getResources().getColor(R.color.playing));

        }





    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView view_name, view_id;
        ImageView status;


        public ViewHolder(@NonNull View itemView,  OnItemClickListener listener) {
            super(itemView);

            view_name = itemView.findViewById(R.id.item_name);
            view_id = itemView.findViewById(R.id.item_id);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });



        }
    }
}
