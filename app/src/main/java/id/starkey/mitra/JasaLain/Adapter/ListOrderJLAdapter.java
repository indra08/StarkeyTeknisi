package id.starkey.mitra.JasaLain.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import id.starkey.mitra.R;
import id.starkey.mitra.Utilities.CustomItem;
import id.starkey.mitra.Utilities.FormatItem;
import id.starkey.mitra.Utilities.ItemValidation;

/**
 * Created by Shin on 1/8/2017.
 */

public class ListOrderJLAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListOrderJLAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_order, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_order, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = convertView.findViewById(R.id.tv_item5);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(iv.ChangeFormatDateString(itemSelected.getItem2(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(itemSelected.getItem4());
        holder.tvItem4.setText(iv.ChangeToRupiahFormat(itemSelected.getItem5()));
        holder.tvItem5.setText(itemSelected.getItem6());

        return convertView;

    }
}
