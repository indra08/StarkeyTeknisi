package id.starkey.mitra.Stempel.PilihProdusenStemp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.starkey.mitra.R;

import java.util.List;

/**
 * Created by Dani on 4/18/2018.
 */

public class RVProdusenStempel extends RecyclerView.Adapter<RVProdusenStempel.ViewHolder> {

    /*
    private List<ListItemKunciLain> listItemKunciLains;
    private Context context;
     */
    private List<ListItemPilihProdusenStempel> listItemPilihProdusenStempels;
    private Context context;

    public RVProdusenStempel(List<ListItemPilihProdusenStempel> listItemPilihProdusenStempels, Context context) {
        this.listItemPilihProdusenStempels = listItemPilihProdusenStempels;
        this.context = context;
    }

    @Override
    public RVProdusenStempel.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_produsen_stempel, parent, false);
        return new ViewHolder(v);
    }

    int posisi = -1;

    @Override
    public void onBindViewHolder(RVProdusenStempel.ViewHolder holder, final int position) {
        ListItemPilihProdusenStempel listItemPilihProdusenStempel = listItemPilihProdusenStempels.get(position);

        holder.tvNamaProdusenStemp.setText(listItemPilihProdusenStempel.getNama());
        holder.tvAlamatProdusenStemp.setText(listItemPilihProdusenStempel.getAlamat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posisi = position;
                notifyDataSetChanged();
            }
        });

        if (position == posisi){
            holder.cProdusenStempel.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.cProdusenStempel.setBackgroundColor(context.getResources().getColor(R.color.colorPutih));
        }
    }

    public int getPosisi(){
        return posisi;
    }

    @Override
    public int getItemCount() {
        return listItemPilihProdusenStempels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivProdusenStemp;
        public TextView tvNamaProdusenStemp, tvAlamatProdusenStemp;
        public CardView cProdusenStempel;

        public ViewHolder(View itemView){
            super(itemView);

            ivProdusenStemp = itemView.findViewById(R.id.ivProdusenStemp);
            tvNamaProdusenStemp = itemView.findViewById(R.id.tvNamaProdusenStemp);
            tvAlamatProdusenStemp = itemView.findViewById(R.id.tvAlamatProdusenStemp);
            cProdusenStempel = itemView.findViewById(R.id.cvProdusenStempel);
        }
    }
}
