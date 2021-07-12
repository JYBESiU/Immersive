package com.example.thekaist.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thekaist.R;
import com.example.thekaist.UserInfo;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    static Context context;
    private ArrayList<UserInfo> mList=null;



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView game, scr1, scr2, opponent;

        public ViewHolder(View itemView) {
            super(itemView);

            game = itemView.findViewById(R.id.Result);
            scr1 = itemView.findViewById(R.id.my_scr);
            scr2 = itemView.findViewById(R.id.oppo_scr);
            opponent = itemView.findViewById(R.id.oppo);

        }
    }

    public HistoryAdapter(Context context, ArrayList<UserInfo> mlist) {

        this.context = context;
        this.mList = mlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.history_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //holder.user_name.setText(mList.get(position).getName());
        //holder.user_record.setText(mList.get(position).getWin().toString());

    }

    @Override
    public int getItemCount() {

        return mList.size();    }
}
