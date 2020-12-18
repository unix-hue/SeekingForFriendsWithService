package com.example.seekingforfriends;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RVAdapter extends RecyclerView.Adapter <RVAdapter.ViewHolder> {
    private final ScrollingActivity scrollingActivity;
    private final List<Map> values;

    FirebaseStorage storage;
    StorageReference ref;

    RVAdapter(ScrollingActivity parent, List<Map> items)
    {
        scrollingActivity = parent;
        values = items;
        storage = FirebaseStorage.getInstance();
        ref = storage.getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.name.setText(values.get(position).get("name").toString());
        holder.city.setText("г." + values.get(position).get("city").toString());

        ref.child(values.get(position).get("id").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(scrollingActivity).load(uri).into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //
            }
        });

        holder.itemView.setTag(values.get(position));
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount()
    {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView name;
        final TextView city;
        final ImageView imageView;

        ViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.show_name);
            city = view.findViewById(R.id.show_city);
            imageView = view.findViewById(R.id.image_element);
        }
    }

    final private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Map item = (Map) view.getTag();
            Context context = view.getContext();

            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name", item.get("name").toString());
            intent.putExtra("year", item.get("birth day").toString());
            intent.putExtra("city", item.get("city").toString());
            intent.putExtra("id", item.get("id").toString());
            intent.putExtra("doing", item.get("doing").toString());
            intent.putExtra("interes", item.get("interes").toString());
            intent.putExtra("music", item.get("music").toString());
            intent.putExtra("film", item.get("film").toString());
            intent.putExtra("book", item.get("book").toString());
            intent.putExtra("game", item.get("game").toString());
            intent.putExtra("me", item.get("me").toString());
            intent.putExtra("politic", item.get("politic").toString());
            intent.putExtra("think", item.get("think").toString());
            context.startActivity(intent);
        }
    };
}
