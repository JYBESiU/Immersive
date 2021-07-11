package com.example.thekaist.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.thekaist.R;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private OnItemClickListener mListener;
    private static Context context;

    ArrayList<Integer> cards_order;
    ArrayList<Integer> nums_order;
    ArrayList<Drawable> cards_list = new ArrayList<Drawable>();
    ArrayList<Drawable> nums_list = new ArrayList<Drawable>();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView card_img;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            card_img = itemView.findViewById(R.id.card_img);
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

    public CardsAdapter(Context context, ArrayList<Integer> cards_order, ArrayList<Integer> nums_order) {
        this.context = context;
        this.cards_order = cards_order;
        this.nums_order = nums_order;

        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card1));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card2));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card3));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card4));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card5));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card6));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card7));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card8));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card9));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card10));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card11));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card12));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card13));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card14));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card15));
        this.cards_list.add(this.context.getResources().getDrawable(R.drawable.card16));

        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num1));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num2));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num3));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num4));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num5));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num6));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num7));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num8));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num9));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num10));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num11));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num12));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num13));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num14));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num15));
        this.nums_list.add(this.context.getResources().getDrawable(R.drawable.num16));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cards_item, parent, false);
        ViewHolder vh = new ViewHolder(view, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.card_img.setImageDrawable(cards_list.get(cards_order.get(position)));
    }

    @Override
    public int getItemCount() {
        return 16;
    }

}
