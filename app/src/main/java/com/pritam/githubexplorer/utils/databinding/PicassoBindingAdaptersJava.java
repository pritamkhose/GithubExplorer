package com.pritam.githubexplorer.utils.databinding;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

import com.pritam.githubexplorer.R;
import com.squareup.picasso.Picasso;

public class PicassoBindingAdaptersJava {

    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView view, String imageUrl){

        if (imageUrl != null && imageUrl.length() > 5){
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.mipmap.no_image_placeholder)
                    .into(view);
        } else {
            Picasso.get()
                    .load(R.mipmap.no_image_placeholder)
                    .placeholder(R.mipmap.no_image_placeholder)
                    .into(view);
        }

    }

}
