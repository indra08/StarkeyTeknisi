package id.starkey.mitra.History.Share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.starkey.mitra.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 5/13/2018.
 */

public class RVShare extends RecyclerView.Adapter<RVShare.ViewHolder> {

    private List<ListItemShare> listItemShares;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVShare(List<ListItemShare> listItemShares, Context context) {
        this.listItemShares = listItemShares;
        this.context = context;
    }

    @Override
    public RVShare.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history_share, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVShare.ViewHolder holder, int position) {

        ListItemShare listItemShare = listItemShares.get(position);

        holder.tOrderIdShare.setText(listItemShare.getId());
        holder.tNamaLayananShare.setText(listItemShare.getNama_layanan());
        holder.tJenisItemShare.setText(listItemShare.getJenis_item());

        String tgl = listItemShare.getTanggal();
        String formatTgl = tgl.substring(0, Math.min(tgl.length(), 10));
        String [] formatbaruTgl = formatTgl.split("-");
        String fixtanggal = formatbaruTgl[2]+"-"+formatbaruTgl[1]+"-"+formatbaruTgl[0];
        holder.tTglShare.setText(fixtanggal);

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiahShareProfit = rupiahFormat.format(Double.parseDouble(listItemShare.getShare_profit()));
        String resultShareProfit = Rupiah + " " + rupiahShareProfit;
        holder.tShareProfit.setText(resultShareProfit);

    }

    @Override
    public int getItemCount() {
        return listItemShares.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tOrderIdShare, tTglShare, tNamaLayananShare, tJenisItemShare, tShareProfit;

        public ViewHolder(View itemView){
            super(itemView);

            tOrderIdShare = itemView.findViewById(R.id.tvOrderId);
            tTglShare = itemView.findViewById(R.id.tvTglShare);
            tNamaLayananShare = itemView.findViewById(R.id.tvNamaLayananShare);
            tJenisItemShare = itemView.findViewById(R.id.tvJenisItemShare);
            tShareProfit = itemView.findViewById(R.id.tvShareProfit);
        }

    }
}
