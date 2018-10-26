package id.starkey.mitra.TransaksiBahan.DetailTransaksiBahan;

/**
 * Created by Dani on 6/12/2018.
 */

public class ListItemDetailTransaksiBahan {

    private String id;
    private String id_barang;
    private String nama_barang;
    private String jumlah;
    private String harga;
    private String subtotal;

    public ListItemDetailTransaksiBahan(String id, String id_barang, String nama_barang, String jumlah, String harga, String subtotal) {
        this.id = id;
        this.id_barang = id_barang;
        this.nama_barang = nama_barang;
        this.jumlah = jumlah;
        this.harga = harga;
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public String getId_barang() {
        return id_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getHarga() {
        return harga;
    }

    public String getSubtotal() {
        return subtotal;
    }
}
