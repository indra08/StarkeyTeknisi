package id.starkey.mitra.TransaksiBahan;

/**
 * Created by Dani on 6/10/2018.
 */

public class ListItemTransaksiBahan {

    private String id;
    private String tanggal;
    private String total;
    private String status;

    public ListItemTransaksiBahan(String id, String tanggal, String total, String status) {
        this.id = id;
        this.tanggal = tanggal;
        this.total = total;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
