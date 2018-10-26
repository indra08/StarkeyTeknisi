package id.starkey.mitra.TransaksiBahan.DetailTransaksiBahan;

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
 * Created by Dani on 6/12/2018.
 */

public class RVDetailTransaksiBahan extends RecyclerView.Adapter<RVDetailTransaksiBahan.ViewHolder> {

    private List<ListItemDetailTransaksiBahan> listItemDetailTransaksiBahans;
    private Context context;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    public RVDetailTransaksiBahan(List<ListItemDetailTransaksiBahan> listItemDetailTransaksiBahans, Context context) {
        this.listItemDetailTransaksiBahans = listItemDetailTransaksiBahans;
        this.context = context;
    }

    @Override
    public RVDetailTransaksiBahan.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_detail_transaksi_bahan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVDetailTransaksiBahan.ViewHolder holder, int position) {
        ListItemDetailTransaksiBahan listItemDetailTransaksiBahan = listItemDetailTransaksiBahans.get(position);

        holder.tvNamabarangdetbahan.setText(listItemDetailTransaksiBahan.getNama_barang());
        holder.tvQtydetbahan.setText(listItemDetailTransaksiBahan.getJumlah() + " Pcs");

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiah = rupiahFormat.format(Double.parseDouble(listItemDetailTransaksiBahan.getHarga()));
        String result = Rupiah + " " + rupiah;
        holder.tvHargadetbahan.setText(result);

        String rupiahSub = rupiahFormat.format(Double.parseDouble(listItemDetailTransaksiBahan.getSubtotal()));
        String resultSub = Rupiah + " " + rupiahSub;
        holder.tvSubtotaldetbahan.setText(resultSub);
    }

    @Override
    public int getItemCount() {
        return listItemDetailTransaksiBahans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvNamabarangdetbahan, tvQtydetbahan, tvHargadetbahan, tvSubtotaldetbahan;

        public ViewHolder(View itemView){
            super(itemView);

            tvNamabarangdetbahan = itemView.findViewById(R.id.lblnamabarangtrxbahan);
            tvQtydetbahan = itemView.findViewById(R.id.lblqtytrxbahan);
            tvHargadetbahan = itemView.findViewById(R.id.lblhargatrxbahan);
            tvSubtotaldetbahan = itemView.findViewById(R.id.lblsubtotaltrxbahan);

        }
    }
}
