package com.kirsten.testapps.meteorapp.adatpers;

import android.content.Context;
import android.graphics.Color;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kirsten.testapps.meteorapp.R;

import java.io.File;
import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private ArrayList<String> imageNameList;
    private Context context;
    private final String PATH;
    private ImageView scaledImg;
    private static boolean[] isSelected;
    private boolean isScaled = false;


    public RVAdapter(Context context, ArrayList<String> imageNameList, String path, ImageView scaledImg) {
        this.imageNameList = imageNameList;
        this.context = context;
        this.PATH = path;
        this.scaledImg = scaledImg;
        isSelected = new boolean[imageNameList.size()];
    }

    private void deleteImageFromListWithRefresh(final RVAdapter.ViewHolder viewHolder, int i) {
        imageNameList.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, imageNameList.size());
        viewHolder.cellLayout.removeAllViews();
        viewHolder.cellLayout.refreshDrawableState();
    }

    public boolean[] getIsSelected() {
        return isSelected;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RVAdapter.ViewHolder viewHolder, final int i) {
        final File image = new File(new StringBuilder().
                append(PATH).
                append("/").
                append(imageNameList.get(i)).
                toString());
        Glide.with(context).load(image).into(viewHolder.img);

        viewHolder.filename.setText(imageNameList.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected[i]) {
                    isSelected[i] = !isSelected[i];
                    viewHolder.cellLayout.setBackgroundColor(context.getResources().getColor(R.color.colorCellActive));
                    viewHolder.filename.setTextColor(Color.BLACK);
                } else {
                    isSelected[i] = !isSelected[i];
                    viewHolder.cellLayout.setBackgroundColor(Color.TRANSPARENT);
                    viewHolder.filename.setTextColor(Color.WHITE);
                }
            }
        });

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScaled) {
                    scaledImg.setImageURI(Uri.fromFile(image));
                    scaledImg.setVisibility(View.VISIBLE);
                    isScaled = !isScaled;
                }
            }
        });


        scaledImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaledImg.setVisibility(View.INVISIBLE);
                isScaled = !isScaled;
            }
        });

        viewHolder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleted = image.delete();
                if (isDeleted) {
                    Toast.makeText(context, "Done.", Toast.LENGTH_LONG).show();
                    deleteImageFromListWithRefresh(viewHolder, i);
                } else if (image.exists()) {
                    Toast.makeText(context, "Unknown error. File was not deleted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Unknown error. Gallery refresh started...", Toast.LENGTH_LONG).show();
                    deleteImageFromListWithRefresh(viewHolder, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageNameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView filename;
        private ImageView img;
        private Button delButton;
        private LinearLayout cellLayout;

        ViewHolder(View view) {
            super(view);
            filename = view.findViewById(R.id.filename);
            img = view.findViewById(R.id.img);
            delButton = view.findViewById(R.id.delete_button);
            cellLayout = view.findViewById(R.id.cell);
        }
    }
}