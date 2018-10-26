package id.starkey.mitra.History.Selesai;

/**
 * Created by Dani on 4/25/2018.
 */

public class ListItemSelesai {

    private String id;
    private String id_layanan;
    private String nama_layanan;
    private String id_biaya_layanan;
    private String jenis_item;
    private String total_biaya;
    private String saldo_terakhir;
    private String tanggal;
    private String status_code;
    private String status;
    private String on_process;
    private String is_rated;

    public ListItemSelesai(String id, String id_layanan, String nama_layanan, String id_biaya_layanan, String jenis_item,
                           String total_biaya, String saldo_terakhir, String tanggal, String status_code, String status,
                           String on_process, String is_rated) {
        this.id = id;
        this.id_layanan = id_layanan;
        this.nama_layanan = nama_layanan;
        this.id_biaya_layanan = id_biaya_layanan;
        this.jenis_item = jenis_item;
        this.total_biaya = total_biaya;
        this.saldo_terakhir = saldo_terakhir;
        this.tanggal = tanggal;
        this.status_code = status_code;
        this.status = status;
        this.on_process = on_process;
        this.is_rated = is_rated;
    }

    public String getId() {
        return id;
    }

    public String getId_layanan() {
        return id_layanan;
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

    public String getTotal_biaya() {
        return total_biaya;
    }

    public String getSaldo_terakhir() {
        return saldo_terakhir;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getStatus_code() {
        return status_code;
    }

    public String getStatus() {
        return status;
    }

    public String getOn_process() {
        return on_process;
    }

    public String getIs_rated() {
        return is_rated;
    }
}
