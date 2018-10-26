package id.starkey.mitra;

/**
 * Created by Dani on 3/20/2018.
 */

public class ConfigLink {

    private static String BASE_URL = "https://api.starkey.id";

    //LOGIN
    public static final String login = "https://api.starkey.id/api/mitra/login";

    //for sharepreferences
    public static final String loginPref = "login";

    //for pref firebasetoken in here
    public static final String firebasePref = "firebase";

    //for pref changelog
    public static final String countPrefChangeLog = "changelog";

    //location update user
    public static final String update_location_user = "https://api.starkey.id/api/mitra/status/update";

    public static final String mitra_accepted = "https://api.starkey.id/api/mitra_accepted";

    public static final String mitra_accepted_stempel = "https://api.starkey.id/api/stempel/mitra_accepted";

    //telp confirmation
    public static final String telp_confirmation = "https://api.starkey.id/api/telp_confirmation";

    //update trx mitra
    public static final String update_status_transaksi_kunci = "https://api.starkey.id/api/update_status_transaksi_kunci";

    //update distance
    public static final String update_distance = "https://api.starkey.id/api/update_distance";

    //finish trx
    //public static final String finish_transaction = "https://api.starkey.id/api/finish_transaction";
    //finis trx url new
    public static final String finish_transaction_new = "https://api.starkey.id/api/v1.1/finish_transaction";

    //update biaya lain dan tips mitra
    public static final String update_biayalain_tips = "https://api.starkey.id/api/update_biaya_lain_tips";

    //mitra cancel order
    public static final String mitra_cancel = "https://api.starkey.id/api/mitra_cancel";

    //mitra cancel order stempel
    public static final String mitra_cancel_stempel = "https://api.starkey.id/api/stempel/mitra_cancel";

    //mitra declined when countdown has finished (kunci)
    public static final String mitra_declined = "https://api.starkey.id/api/mitra_declined";

    //mitra declined when countdown has finished (stempel)
    public static final String mitra_declined_stempel = "https://api.starkey.id/api/stempel/mitra_declined";

    //pref location gps
    public static final String locationbyGps = "GPS";
    //pref location network
    public static final String locationbyNetwork = "Network";

    //telp confirm stempel
    public static final String telp_confirm_stempel = "https://api.starkey.id/api/stempel/telp_confirmation";

    //update status code
    public static final String update_status_trx_stempel = "https://api.starkey.id/api/stempel/update_status_transaksi";

    //produsen stempel
    public static final String produsen_stempel = "https://api.starkey.id/api/produsen_stempel";

    //update selected produsen stempel
    public static final String update_selected_produsen_stempel = "https://api.starkey.id/api/stempel/update_selected_produsen";

    //finish transaction stempel
    public static final String finish_transaction_stempel = "https://api.starkey.id/api/stempel/finish_transaction";

    //produsen stempel closed
    public static final String produsen_stempel_closed = "https://api.starkey.id/api/stempel/produsen_closed";

    //request item on produsen not available
    public static final String request_stempel_not_available = "https://api.starkey.id/api/stempel/request_not_available";

    //update biaya dp stempel
    public static final String update_dp = "https://api.starkey.id/api/stempel/update_dp";

    //detail trx stempel
    public static final String detail_trx_stempel = "https://api.starkey.id/api/stempel/transaction/";

    //permorma mitra
    public static final String performa_mitra = "https://api.starkey.id/api/mitra/dashboard";

    //trx outstanding mitra
    public static final String trx_outstanding_mitra = "https://api.starkey.id/api/mitra/transaction_checker";

    //update tips biaya lain stempel
    public static final String update_biayalain_tips_stempel = "https://api.starkey.id/api/stempel/update_biaya_lain_tips";

    //history mitra selesai
    public static final String history_selesai = "https://api.starkey.id/api/mitra/history";

    //history detail
    public static final String detail_history = "https://api.starkey.id/api/transaction/mitra/";

    //unpaid share
    public static final String unpaid_share = "https://api.starkey.id/api/mitra/unpaid_share";

    //transaksi bahan
    public static final String transaksi_bahan = BASE_URL + "/api/mitra/history/transaksi_komersial";

    //detail transaksi bahan
    public static final String detail_transaksi_bahan = BASE_URL + "/api/mitra/history/transaksi_komersial/";

    //ubah password
    public static final String ubah_password = BASE_URL + "/api/mitra/password/update";

    //maintenance status
    public static final String maintenance_status = BASE_URL + "/api/v1/maintenance_status";

    //changelog info
    public static final String changeLogInfo = BASE_URL + "/api/v1/app_changelog?";
}
