package id.starkey.mitra.TransaksiBahan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.starkey.mitra.R;
import id.starkey.mitra.TransaksiBahan.DetailTransaksiBahan.DetailTransaksiBahanActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 6/10/2018.
 */

public class RVTransaksiBahan extends RecyclerView.Adapter<RVTransaksiBahan.ViewHolder> {

    private List<ListItemTransaksiBahan> listItemTransaksiBahans;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVTransaksiBahan(List<ListItemTransaksiBahan> listItemTransaksiBahans, Context context) {
        this.listItemTransaksiBahans = listItemTransaksiBahans;
        this.context = context;
    }

    @Override
    public RVTransaksiBahan.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_transaksi_bahan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RVTransaksiBahan.ViewHolder holder, int position) {

        ListItemTransaksiBahan listItemTransaksiBahan = listItemTransaksiBahans.get(position);

        holder.tIdTrxBahan.setText(listItemTransaksiBahan.getId());

        String [] formattgl = listItemTransaksiBahan.getTanggal().split("-");
        String fixtanggal = formattgl[2]+"-"+formattgl[1]+"-"+formattgl[0];
        holder.tTglTrxBahan.setText(fixtanggal);

        holder.tStatusTrxBahan.setText(listItemTransaksiBahan.getStatus());

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemTransaksiBahan.getTotal()));
        String result = Rupiah + " " + rupiah;
        holder.tTotalTrxBahan.setText(result);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idtrans = holder.tIdTrxBahan.getText().toString();
                String tanggal = holder.tTglTrxBahan.getText().toString();
                String total = holder.tTotalTrxBahan.getText().toString();
                String status = holder.tStatusTrxBahan.getText().toString();

                Intent toDetail = new Intent(context, DetailTransaksiBahanActivity.class);
                toDetail.putExtra("idtrans", idtrans);
                toDetail.putExtra("tanggal", tanggal);
                toDetail.putExtra("total", total);
                toDetail.putExtra("status", status);
                v.getContext().startActivity(toDetail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listItemTransaksiBahans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tTglTrxBahan, tTotalTrxBahan, tStatusTrxBahan, tIdTrxBahan;

        public ViewHolder(View itemView){
            super(itemView);

            tTglTrxBahan = itemView.findViewById(R.id.tvTglTrxBahan);
            tTotalTrxBahan = itemView.findViewById(R.id.tvTotalTrxBahan);
            tStatusTrxBahan = itemView.findViewById(R.id.tvStatusTrxBahan);
            tIdTrxBahan = itemView.findViewById(R.id.tvTampungIdTrxBahan);
        }


    }
}
