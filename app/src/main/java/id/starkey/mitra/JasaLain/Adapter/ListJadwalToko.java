package id.starkey.mitra.JasaLain.Adapter;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.starkey.mitra.R;
import id.starkey.mitra.Utilities.CustomItem;
import id.starkey.mitra.Utilities.FormatItem;
import id.starkey.mitra.Utilities.ItemValidation;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListJadwalToko extends ArrayAdapter {

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListJadwalToko(Activity context, List<CustomItem> items) {
        super(context, R.layout.cv_jadwal, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private ImageView ivBuka, ivTutup;
        private CheckBox cbItem1;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public List<CustomItem> getItems(){

        return this.items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(R.layout.cv_jadwal, null);

        holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
        holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
        holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);

        holder.ivBuka= (ImageView) convertView.findViewById(R.id.iv_buka);
        holder.ivTutup= (ImageView) convertView.findViewById(R.id.iv_tutup);

        holder.cbItem1= (CheckBox) convertView.findViewById(R.id.cb_item1);

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.cbItem1.setChecked(itemSelected.isStatus());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4());
        /*if(itemSelected.isStatus()){
            holder.cbItem1.setChecked(true);
        }else{
            holder.cbItem1.setChecked(false);
        }*/

        final ViewHolder finalHolder = holder;

        holder.ivBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);

                        if(jam.length() == 1) jam = "0" + jam;
                        if(menit.length() == 1) menit = "0" + menit;

                        finalHolder.tvItem2.setText(jam + ":" + menit);
                        items.get(position).setItem3(jam+ ":" + menit);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        holder.ivTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);

                        if(jam.length() == 1) jam = "0" + jam;
                        if(menit.length() == 1) menit = "0" + menit;

                        finalHolder.tvItem3.setText(jam + ":" + menit);
                        items.get(position).setItem4(jam+ ":" + menit);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        holder.cbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                itemSelected.setStatus(b);
                items.get(position).setStatus(b);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
