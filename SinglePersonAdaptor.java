package com.jeet.profilescreen;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeet on 12/4/2016.
 */
public class SinglePersonAdaptor extends RecyclerView.Adapter<SinglePersonAdaptor.ViewHolder> {

    private ArrayList<SinglePersonModelClass> singlePersonData;
    private Context context;
    private OnImagePick onImagePickListner;
    private SharedPreferences sharedPreferences;

    public SinglePersonAdaptor(Context context, ArrayList<SinglePersonModelClass> singlePersonData) {
        this.singlePersonData = singlePersonData;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("mSharedPref", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.single_person_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.personName.setText(singlePersonData.get(position).getName());
        holder.personAge.setText(singlePersonData.get(position).getAge() + "");
        holder.personCity.setText(singlePersonData.get(position).getCity());
        try {
            if (sharedPreferences.contains("img" + position)) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(sharedPreferences.getString("img" + position, null)));
//                        BitmapFactory.decodeFile(sharedPreferences.getString("img" + position, null));

//                Bitmap bitmap = getThumbnail(context.getContentResolver(), sharedPreferences.getString("img" + position, null));
                if (bitmap != null) {

                    Bitmap bitmap1 = getCroppedBitmap(bitmap);
                    holder.profilePic.setImageBitmap(bitmap1);

                    bitmap.recycle();
                }
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dummy_profile);
                Bitmap bitmap1 = getCroppedBitmap(bitmap);
                holder.profilePic.setImageBitmap(bitmap1);
                bitmap.recycle();
            }
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dummy_profile);
            Bitmap bitmap1 = getCroppedBitmap(bitmap);
            holder.profilePic.setImageBitmap(bitmap1);
            bitmap.recycle();
        }
        holder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImagePickListner != null)
                    onImagePickListner.onPick(position, holder.profilePic);
            }
        });

    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(Math.min(bitmap.getWidth(), bitmap.getHeight()),
                Math.min(bitmap.getWidth(), bitmap.getHeight()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, Math.min(bitmap.getWidth(), bitmap.getHeight()), Math.min(bitmap.getWidth(), bitmap.getHeight()));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2, Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2,
                Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    public int getItemCount() {
        return singlePersonData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        private de.hdodenhof.circleimageview.CircleImageView profilePic;
        private ImageView profilePic;
        private TextView personName, personAge, personCity;

        public ViewHolder(View itemView) {
            super(itemView);
//            profilePic = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.profilePic);
            profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
            personAge = (TextView) itemView.findViewById(R.id.age);
            personName = (TextView) itemView.findViewById(R.id.name);
            personCity = (TextView) itemView.findViewById(R.id.city);
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        ca.close();
        return null;

    }

    public interface OnImagePick {
        void onPick(int position, View view);
    }

    public void setOnPickImageListner(OnImagePick onImagePick) {
        onImagePickListner = onImagePick;
    }
}
