package id.starkey.mitra.Stempel.PilihProdusenStemp;

/**
 * Created by Dani on 4/18/2018.
 */

public class ListItemPilihProdusenStempel {

    private String id;
    private String nama;
    private String alamat;
    private String lat;
    private String lng;
    private String email;
    private String keterangan;

    public ListItemPilihProdusenStempel(String id, String nama, String alamat, String lat, String lng, String email,
                                        String keterangan) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.lat = lat;
        this.lng = lng;
        this.email = email;
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getEmail() {
        return email;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
