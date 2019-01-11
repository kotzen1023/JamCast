package com.seventhmoon.jamcast.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.seventhmoon.jamcast.R;

import java.io.File;
import java.util.ArrayList;

import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.FileChooseLongClick;


public class FileChooseArrayAdapter extends ArrayAdapter<FileChooseItem> {
    private static final String TAG = FileChooseArrayAdapter.class.getName();

    private LayoutInflater inflater = null;
    public SparseBooleanArray mSparseBooleanArray;
    private int layoutResourceId;
    private ArrayList<FileChooseItem> items = new ArrayList<>();
    private int count = 0;
    private Context context;

    public FileChooseArrayAdapter(Context context, int textViewResourceId,
                                  ArrayList<FileChooseItem> objects) {
        super(context, textViewResourceId, objects);
        this.layoutResourceId = textViewResourceId;
        this.items = objects;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSparseBooleanArray = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return items.size();

    }

    public FileChooseItem getItem(int position)
    {
        return items.get(position);
    }
    @Override

    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //Log.e(TAG, "getView = "+ position);
        View view;
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            //Log.e(TAG, "convertView = null");
            /*view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            view.setTag(holder);*/

            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, null);
            holder = new ViewHolder(view);
            //holder.checkbox.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        else {
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }

        //holder.fileicon = (ImageView) view.findViewById(R.id.fd_Icon1);
        //holder.filename = (TextView) view.findViewById(R.id.fileChooseFileName);
        //holder.checkbox = (CheckBox) view.findViewById(R.id.checkBoxInRow);

        FileChooseItem fileChooseItem = items.get(position);
        if (fileChooseItem != null) {

            holder.filename.setText(fileChooseItem.getName());
            holder.checkbox.setTag(position);

            TextView t1 = view.findViewById(R.id.fileChooseFileName);
            CheckBox ck = view.findViewById(R.id.checkBoxInRow);
            //TextView t2 = (TextView) v.findViewById(R.id.TextView02);
            //TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
            /* Take the ImageView from layout and set the city's image */

            fileChooseItem.setCheckBox(ck);
            ck.setVisibility(View.INVISIBLE);

            if (FileChooseLongClick) {
                ck.setVisibility(View.VISIBLE);
            }

            if (mSparseBooleanArray.get(position))
            {
                ck.setChecked(true);
            } else {
                ck.setChecked(false);
            }
            /*if (holder.checkbox != null) {
                holder.checkbox.setVisibility(View.INVISIBLE);
                holder.checkbox.setChecked(false);
                if (Data.FileChooseLongClick == true) {
                    holder.checkbox.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e(TAG, "checkbox = null");
            }*/
            if(t1!=null)
                t1.setText(fileChooseItem.getName());
            //if(t2!=null)
            //    t2.setText(o.getData());
            //if(t3!=null)
            //    t3.setText(o.getDate());


            Bitmap bitmap, bm;
            if (fileChooseItem.getPath() != null) {

                File file = new File(fileChooseItem.getPath());

                //Log.i(TAG, "file: abs path"+file.getAbsolutePath()+ "name = "+fileChooseItem.getName()+" data = "+fileChooseItem.getData());

                if (file.isDirectory()) {
                    if (fileChooseItem.getName().equals("..")) {
                        bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.up);
                        ck.setVisibility(View.INVISIBLE);
                    } else {
                        bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.folder);
                    }
                } else if (file.isFile()) {
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.file);
                } else {
                    bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.folder);
                }
                bm = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                ImageView imageCity =  view.findViewById(R.id.fd_Icon1);
                //String uri = "drawable/" + o.getImage();
                //int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
                //Drawable image = c.getResources().getDrawable(imageResource);
                imageCity.setImageBitmap(bm);
                //imageCity.setImageDrawable(image);

                holder.checkbox.setOnCheckedChangeListener(mCheckedChangeListener);
            }

        }
        return view;
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i(TAG, "switch " + buttonView.getTag() + " checked = " + isChecked);
            //int idx = (Integer) buttonView.getTag();

            //if(isChecked == true) {
            FileChooseItem fileChooseItem = items.get((Integer) buttonView.getTag());

            if (fileChooseItem.getCheckBox() != null) {

                if (!fileChooseItem.getName().equals("..")) {
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                    if (isChecked)
                        count++;
                    else
                        count--;
                }
                else {
                    fileChooseItem.getCheckBox().setChecked(false);
                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), false);
                    count--;
                }
            }
            //}
            /*int count = 0;

            for (int i=0; i<fileChooseArrayAdapter.getCount(); i++) {
                if (mSparseBooleanArray.get(i)) {
                    count++;
                }
            }*/

            //Log.e(TAG, "Count = "+count);

            Intent newNotifyIntent = new Intent();

            if (count > 0) {
                newNotifyIntent.setAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_SHOW);

                //confirm.setVisibility(View.VISIBLE);
            } else  {

                newNotifyIntent.setAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_HIDE);

                //confirm.setVisibility(View.GONE);
            }
            context.sendBroadcast(newNotifyIntent);
        }
    };

    private class ViewHolder {
        ImageView fileicon;
        TextView filename;
        CheckBox checkbox;


        private ViewHolder(View view) {
            this.fileicon = view.findViewById(R.id.fd_Icon1);
            this.filename = view.findViewById(R.id.fileChooseFileName);
            this.checkbox = view.findViewById(R.id.checkBoxInRow);
        }
    }
}
