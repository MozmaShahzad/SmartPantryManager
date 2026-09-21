package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Adapter connects the scrollable list to our data
//it is responsible for creating each row and filling
//it with the right data
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    //interface for handling taps
    //so it knows what to do when a pantry row or its delete button is tapped
    public interface OnItemActionListener{
        void onItemClick(PantryItem item); //called when a row is tapped
        void onDeleteClick(PantryItem item); //called when the delete icon is tapped
    }

    private List<PantryItem> items;
    private final OnItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    //call this when the pantry data changed, to refresh what is on the screen
    public void updateData(List<PantryItem> newItems){
        this.items = newItems;
        notifyDataSetChanged();
    }

    //creates a new row view
    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry,parent,false);
        return new PantryViewHolder(view);
    }

    //fills one rw with data from the list, at the given position
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder,int position) {
        PantryItem item = items.get(position);
        holder.itemName.setText(item.name);
        holder.itemQuantity.setText(item.quantity + " " + item.unit);
        holder.itemEmoji.setText(FoodEmojiHelper.getEmoji(item.name));

        // Tapping the row opens it for editing
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        // Tapping the trash icon deletes it
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    //tells RecyclerView how many rows to draw
    @Override
    public int getItemCount(){
        return items.size();
    }

    //holds references to the views inside one row, so we don't have to
    //call findViewById() repeatedly - this improves scrolling performance
    static class PantryViewHolder extends RecyclerView.ViewHolder{
        TextView itemName,itemQuantity,itemEmoji;
        ImageButton deleteButton;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.itemName);
            itemQuantity=itemView.findViewById(R.id.itemQuantity);
            itemEmoji = itemView.findViewById(R.id.itemEmoji);
            deleteButton=itemView.findViewById(R.id.deleteButton);
        }
    }
}
