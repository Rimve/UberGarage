package com.neverim.ubergarage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private List<OfferEntity> offers;
    private Context context;
    private int type = -1;
    private AppDatabase database;
    private int pos = -1;

    public RecyclerAdapter(Context context, List<OfferEntity> offers, int type) {
        this.context = context;
        this.offers = offers;
        this.type = type;
        database = Room.databaseBuilder(context, AppDatabase.class, "offerDao").allowMainThreadQueries().build();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item Layout
        View v;
        switch (type) {
            case Constants.OFFER_RECYCLER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_search, parent, false);
                break;
            case Constants.PROFILE_RECYCLER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_profile, parent, false);
                break;
            default:
                v = null;
                break;
        }
        // set the view's size, margins, paddings and layout parameters
        ItemViewHolder vh = new ItemViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (type == Constants.OFFER_RECYCLER) {
            // set the data in items
            if (offers.size() > 0) {
                pos = position;
                holder.loader.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
                holder.likes.setText(String.valueOf(offers.get(position).likes));
                holder.price.setText(String.valueOf(offers.get(position).price));
                holder.location.setText(offers.get(position).location);
                holder.toolsCheck.setChecked(offers.get(position).attribute1);
                holder.liftCheck.setChecked(offers.get(position).attribute2);
                holder.miscCheck1.setChecked(offers.get(position).attribute3);
                holder.miscCheck2.setChecked(offers.get(position).attribute4);
                holder.miscCheck3.setChecked(offers.get(position).attribute5);
                holder.miscCheck4.setChecked(offers.get(position).attribute6);
                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: increment the like button
                    }
                });
                // implement setOnClickListener event on item view.
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // open another activity on item click
                        Intent intent = new Intent(context, PreviewActivity.class);
                        intent.putExtra("position", offers.get(position).offerId); // put cradView data in Intent
                        context.startActivity(intent); // start Intent
                    }
                });
                if (offers.get(position).imagePath != null) {
                    new AsyncTask<Void, Bitmap, Bitmap>(){
                        @Override
                        protected Bitmap doInBackground(Void... voids) {
                            // Fetch data here and call publishProgress() to invoke
                            // onProgressUpdate() on the UI thread.
                            String[] tempPaths = offers.get(position).imagePath.split(";");
                            File imgFile = new File(tempPaths[0]);
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            return myBitmap;
                        }

                        @Override
                        protected void onProgressUpdate(Bitmap... values) {
                            // This is called on the UI thread when you call
                            // publishProgress() from doInBackground()
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            // This is called on the UI thread when doInBackground() returns, with
                            // the result as the parameter
                            if (bitmap != null) {
                                holder.image.setImageBitmap(bitmap);
                                holder.image.setVisibility(View.VISIBLE);
                                holder.loader.setVisibility(View.INVISIBLE);
                            }
                        }
                    }.execute();
                }
            }
        }
        if (type == Constants.PROFILE_RECYCLER) {
            // set the data in items
            if (offers.size() > 0) {
                holder.image.setVisibility(View.INVISIBLE);
                holder.loader.setVisibility(View.VISIBLE);
                holder.price.setText(String.valueOf(offers.get(position).price));
                holder.location.setText(offers.get(position).location);
                holder.toolsCheck.setChecked(offers.get(position).attribute1);
                holder.liftCheck.setChecked(offers.get(position).attribute2);
                holder.miscCheck1.setChecked(offers.get(position).attribute3);
                holder.miscCheck2.setChecked(offers.get(position).attribute4);
                holder.miscCheck3.setChecked(offers.get(position).attribute5);
                holder.miscCheck4.setChecked(offers.get(position).attribute6);
                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditOfferActivity.class);
                        intent.putExtra("position", offers.get(position).offerId); // put offer id in Intent
                        context.startActivity(intent); // start Intent
                    }
                });
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(true);
                        builder.setTitle("Delete offer");
                        builder.setMessage("Are you sure you want to delete this offer?");
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete
                                database.offerDao().deleteOfferById(offers.get(position).offerId);
                                Toast.makeText(context, "Offer has been deleted.", Toast.LENGTH_SHORT).show();
                                removeAt(position);
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // open another activity on item click
                        Intent intent = new Intent(context, PreviewActivity.class);
                        intent.putExtra("position", offers.get(position).offerId); // put cradView data in Intent
                        context.startActivity(intent); // start Intent
                    }
                });
                if (offers.get(position).imagePath != null) {
                    new AsyncTask<Void, Bitmap, Bitmap>(){
                        @Override
                        protected Bitmap doInBackground(Void... voids) {
                            // Fetch data here and call publishProgress() to invoke
                            // onProgressUpdate() on the UI thread.
                            String[] tempPaths = offers.get(position).imagePath.split(";");
                            File imgFile = new File(tempPaths[0]);
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            return myBitmap;
                        }

                        @Override
                        protected void onProgressUpdate(Bitmap... values) {
                            // This is called on the UI thread when you call
                            // publishProgress() from doInBackground()
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            // This is called on the UI thread when doInBackground() returns, with
                            // the result as the parameter
                            if (bitmap != null) {
                                holder.image.setImageBitmap(bitmap);
                                holder.image.setVisibility(View.VISIBLE);
                                holder.loader.setVisibility(View.INVISIBLE);
                            }
                        }
                    }.execute();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView image;
        TextView price, likes, location;
        CheckBox toolsCheck, liftCheck, miscCheck1, miscCheck2, miscCheck3, miscCheck4;
        ImageButton editBtn, deleteBtn;
        ProgressBar loader;


        public ItemViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            if (type == Constants.OFFER_RECYCLER) {
                image = itemView.findViewById(R.id.card_image);
                image.setClipToOutline(true);
                price = itemView.findViewById(R.id.card_price_value);
                location = itemView.findViewById(R.id.card_location_value);
                toolsCheck = itemView.findViewById(R.id.card_feature_tools);
                liftCheck = itemView.findViewById(R.id.card_feature_lift);
                miscCheck1 = itemView.findViewById(R.id.card_feature_ph1);
                miscCheck2 = itemView.findViewById(R.id.card_feature_ph2);
                miscCheck3 = itemView.findViewById(R.id.card_feature_ph3);
                miscCheck4 = itemView.findViewById(R.id.card_feature_ph4);
                likes = itemView.findViewById(R.id.card_favorite_view);
                loader = itemView.findViewById(R.id.card_loading);
            }
            if (type == Constants.PROFILE_RECYCLER) {
                image = itemView.findViewById(R.id.profile_image);
                image.setClipToOutline(true);
                price = itemView.findViewById(R.id.profile_price_value);
                location = itemView.findViewById(R.id.profile_location_value);
                toolsCheck = itemView.findViewById(R.id.profile_feature_tools);
                liftCheck = itemView.findViewById(R.id.profile_feature_lift);
                miscCheck1 = itemView.findViewById(R.id.profile_feature_ph1);
                miscCheck2 = itemView.findViewById(R.id.profile_feature_ph2);
                miscCheck3 = itemView.findViewById(R.id.profile_feature_ph3);
                miscCheck4 = itemView.findViewById(R.id.profile_feature_ph4);
                loader = itemView.findViewById(R.id.profile_loading);
                editBtn = itemView.findViewById(R.id.profile_edit_btn);
                deleteBtn = itemView.findViewById(R.id.profile_delete_btn);
            }
        }
    }

    private void removeAt(int position) {
        offers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, offers.size());
    }
}