package id.starkey.mitra.History.Share;

/**
 * Created by Dani on 5/13/2018.
 */

public class ListItemShare {

    private String id;
    private String nama_layanan;
    private String id_biaya_layanan;
    private String jenis_item;
    private String share_profit;
    private String tanggal;
    private String status_code;

    public ListItemShare(String id, String nama_layanan, String id_biaya_layanan, String jenis_item, String share_profit,
                         String tanggal, String status_code) {
        this.id = id;
        this.nama_layanan = nama_layanan;
        this.id_biaya_layanan = id_biaya_layanan;
        this.jenis_item = jenis_item;
        this.share_profit = share_profit;
        this.tanggal = tanggal;
        this.status_code = status_code;
    }

    public String getId() {
        return id;
    }

    public String getNama_layanan() {
        return nama_layanan;
    }

    public String getId_biaya_layanan() {
        return id_biaya_layanan;
    }

    public String getJenis_item() {
        return jenis_item;
    }

    public String getShare_profit() {
        return share_profit;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStatus_code() {
        return status_code;
    }
}
