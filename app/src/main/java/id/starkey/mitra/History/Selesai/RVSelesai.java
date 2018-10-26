package id.starkey.mitra.History.Selesai;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.mitra.HistoryDetail.HistoryDetailActivity;
import id.starkey.mitra.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dani on 4/25/2018.
 */

public class RVSelesai extends RecyclerView.Adapter<RVSelesai.ViewHolder> {

    private List<ListItemSelesai> listItemSelesais;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVSelesai(List<ListItemSelesai> listItemSelesais, Context context) {
        this.listItemSelesais = listItemSelesais;
        this.context = context;
    }

    @Override
    public RVSelesai.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history_selesai, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RVSelesai.ViewHolder holder, int position) {
        ListItemSelesai listItemSelesai = listItemSelesais.get(position);

        holder.tTransaksiLayanan.setText("Transaksi "+listItemSelesai.getNama_layanan());

        holder.tIdTrx.setText(listItemSelesai.getId());

        String tgl = listItemSelesai.getTanggal();
        String formatTgl = tgl.substring(0, Math.min(tgl.length(), 10));
        String [] formatbaruTgl = formatTgl.split("-");
        String fixtanggal = formatbaruTgl[2]+"-"+formatbaruTgl[1]+"-"+formatbaruTgl[0];
        holder.tTanggalSelesai.setText(fixtanggal);

        holder.tStatus.setText(listItemSelesai.getStatus());
        final String statusnya = holder.tStatus.getText().toString();
        if (statusnya.equals("selesai")){
            holder.ivSelesai.setBackgroundResource(R.drawable.ic_success);
        } else {
            holder.ivSelesai.setBackgroundResource(R.drawable.ic_cancel);
        }

        holder.tNamaItemSelesai.setText(listItemSelesai.getJenis_item());

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemSelesai.getTotal_biaya()));
        String result = Rupiah + " " + rupiah;
        holder.tBiayaSelesai.setText(result);

        String rupiahSaldo = rupiahFormat.format(Double.parseDouble(listItemSelesai.getSaldo_terakhir()));
        String resultSaldo = Rupiah + " " + rupiahSaldo;
        holder.tSaldo.setText(resultSaldo);

        holder.cHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusnya.equals("selesai")){
                    String idtrx = holder.tIdTrx.getText().toString();
                    Intent todetail = new Intent(context, HistoryDetailActivity.class);
                    todetail.putExtra("idtransaksi", idtrx);
                    context.startActivity(todetail);
                } else {
                    Toast.makeText(context, "Tidak ada detail untuk transaksi ini", Toast.LENGTH_SHORT).show();
                    //API tidak handle object name
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelesais.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tNamaItemSelesai, tBiayaSelesai, tTanggalSelesai, tStatus, tIdTrx, tSaldo, tTransaksiLayanan;
        public ImageView ivSelesai;
        public CardView cHistory;

        public ViewHolder(View itemView){
            super(itemView);

            cHistory = itemView.findViewById(R.id.cvHistorySelesai);
            tIdTrx = itemView.findViewById(R.id.tvTampungIdTrx);
            tTanggalSelesai = itemView.findViewById(R.id.tvTanggalSelesai);
            tNamaItemSelesai = itemView.findViewById(R.id.tvJenisItemSelesai);
            tBiayaSelesai = itemView.findViewById(R.id.tvBiayaSelesai);
            tStatus = itemView.findViewById(R.id.tvTampungStatus);
            ivSelesai = itemView.findViewById(R.id.imageViewSelesai);
            tSaldo = itemView.findViewById(R.id.tvSaldoHistorySelesai);
            tTransaksiLayanan = itemView.findViewById(R.id.tvTransaksiHistorySelesai);
        }
    }
}
