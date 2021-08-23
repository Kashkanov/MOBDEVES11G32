package com.mobdeve.s11.g32.tindergree.Adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s11.g32.tindergree.Activities.MatchRequestsActivity;
import com.mobdeve.s11.g32.tindergree.Activities.OtherPicsActivity;
import com.mobdeve.s11.g32.tindergree.Activities.ProfilePage2Activity;
import com.mobdeve.s11.g32.tindergree.Models.MatchRequest;
import com.mobdeve.s11.g32.tindergree.Models.OtherPic;
import com.mobdeve.s11.g32.tindergree.R;
import com.mobdeve.s11.g32.tindergree.ViewHolders.MatchRequestViewHolder;
import com.mobdeve.s11.g32.tindergree.ViewHolders.MatchViewHolder;

import java.util.ArrayList;

public class MatchRequestAdapter extends RecyclerView.Adapter<MatchRequestViewHolder> {
    private ArrayList<MatchRequest> mrs;
    private MatchRequestsActivity matchRequestsActivity;

    public static String KEY_MATCHREQPIC = "KEY_MATCHREQPIC";
    public static String KEY_MATCHREQNAME = "KEY_MATCHREQNAME";
    public static String KEY_MATCHREQDESC = "KEY_MATCHREQDESC";
    public static String KEY_MATCHUID = "KEY_MATCHUID";

    public MatchRequestAdapter(ArrayList<MatchRequest> mrs, MatchRequestsActivity matchRequestsActivity){
        this.mrs = mrs;
        this.matchRequestsActivity = matchRequestsActivity;
    }

    public MatchRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.match_request_layout, parent, false);

        MatchRequestViewHolder matchRequestViewHolder = new MatchRequestViewHolder(view);

        return matchRequestViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchRequestViewHolder holder, int position) {
        holder.setIb_mrPic(mrs.get(position).getProfile().getuserProfilePicture());
        holder.setTv_mrName(mrs.get(position).getProfile().getPetname());

        String matchreqname = mrs.get(position).getProfile().getPetname();
        String matchreqdesc = mrs.get(position).getProfile().getPetdesc();
        String matchUid = mrs.get(position).getProfile().getUid();

        // Go to profile
        holder.getIb_mrPic().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ProfilePage2Activity.class); // TODO: Redirect to OtherPicsActivity
                i.putExtra(KEY_MATCHREQNAME, matchreqname);
                i.putExtra(KEY_MATCHREQDESC, matchreqdesc);
                i.putExtra(KEY_MATCHUID, matchUid);
                v.getContext().startActivity(i);
            }
        });

        // Accept Request
        holder.getIb_accept().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchRequestsActivity.acceptRequest(matchUid);
            }
        });

        // Reject Request
        holder.getIb_reject().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchRequestsActivity.rejectRequest(matchUid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mrs.size();
    }
}
