package com.goldcode.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldcode.ecommerce.Interfaces.ItemClickListener;
import com.goldcode.ecommerce.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView product_name,productDescription,productPrice;
    public ImageView productImage;
    public ItemClickListener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.recycler_product_image);
        product_name = itemView.findViewById(R.id.recycler_product_name);
        productDescription = itemView.findViewById(R.id.recycler_product_description);
        productPrice = itemView.findViewById(R.id.recycler_product_price);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
