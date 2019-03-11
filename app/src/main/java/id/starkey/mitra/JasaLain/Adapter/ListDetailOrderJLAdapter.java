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
import id.starkey.mitra.Utilities.ItemValidation;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListDetailOrderJLAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListDetailOrderJLAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.cv_order, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2;
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
            convertView = inflater.inflate(R.layout.cv_order, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem3()));

        return convertView;
    }
}
