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

public class RVAdapterMesUser extends RecyclerView.Adapter <RVAdapterMesUser.ViewHolder> {
    private final Messages messages;
    private final List<Map> values;

    FirebaseStorage storage;
    StorageReference ref;

    RVAdapterMesUser(Messages parent, List<Map> items)
    {
        messages = parent;
        values = items;
        storage = FirebaseStorage.getInstance();
        ref = storage.getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_show_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.name.setText(values.get(position).get("name").toString());

        ref.child(values.get(position).get("id").toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(messages).load(uri).into(holder.imageView);
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
        final ImageView imageView;

        ViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.show_name_message);
            imageView = view.findViewById(R.id.image_message_client);
        }
    }

    final private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Map item = (Map) view.getTag();
            Context context = view.getContext();

            Intent intent = new Intent(context, list_messages.class);
            intent.putExtra("id", item.get("id").toString());
            context.startActivity(intent);
        }
    };
}