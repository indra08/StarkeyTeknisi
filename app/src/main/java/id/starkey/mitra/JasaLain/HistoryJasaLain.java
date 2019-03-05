package id.starkey.mitra.JasaLain;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.starkey.mitra.JasaLain.Adapter.ListHistoryJLAdapter;
import id.starkey.mitra.R;
import id.starkey.mitra.Utilities.CustomItem;
import id.starkey.mitra.Utilities.FormatItem;
import id.starkey.mitra.Utilities.ItemValidation;

public class HistoryJasaLain extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private EditText edtTglDari, edtTglSampai;
    private ImageView ivTglDari, ivTglSampai;
    private ImageView ivNext;
    private ListView lvHistory;
    private ProgressBar pbLoading;
    private String dateFrom = "", dateTo = "";
    private List<CustomItem> masterList = new ArrayList<>();
    private ListHistoryJLAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_jasa_lain);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("History Transaksi");

        context = this;

        initUI();
        initEvent();
        getData();
    }

    private void initUI() {

        edtTglDari = (EditText) findViewById(R.id.edt_tgl_dari);
        ivTglDari = (ImageView) findViewById(R.id.iv_tgl_dari);
        edtTglSampai = (EditText) findViewById(R.id.edt_tgl_sampai);
        ivTglSampai = (ImageView) findViewById(R.id.iv_tgl_sampai);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        dateFrom = iv.getCurrentDate(FormatItem.formatDateDisplay);
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        edtTglDari.setText(dateFrom);
        edtTglSampai.setText(dateTo);

        masterList = new ArrayList<>();
        adapter = new ListHistoryJLAdapter((Activity) context, masterList);
        lvHistory.setAdapter(adapter);

    }

    private void initEvent() {

        ivTglDari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateFrom = sdFormat.format(customDate.getTime());
                        edtTglDari.setText(dateFrom);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivTglSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateTo = sdFormat.format(customDate.getTime());
                        edtTglSampai.setText(dateTo);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dateFrom = edtTglDari.getText().toString();
                dateTo = edtTglSampai.getText().toString();
                getData();
            }
        });
    }

    private void getData(){

        masterList.clear();
        masterList.add(new CustomItem("1", "2019-02-28 20:10:11", "Indra", "Potong Rambut, Smoothing", "50000", "12 KM, Jl. Majapahit No. 53", "Berhasil"));
        masterList.add(new CustomItem("2", "2019-03-01 20:10:11", "Maul", "Potong Rambut", "50000", "11 KM, Jl. Majapahit No. 53", "Dibatalkan user"));
        masterList.add(new CustomItem("3", "2019-03-02 20:10:11", "Lana", "Smoothing", "50000", "13 KM, Jl. Majapahit No. 53", "Berhasil"));
        masterList.add(new CustomItem("4", "2019-03-03 20:10:11", "Husni", "Creambath", "50000", "10 KM, Jl. Majapahit No. 53", "Dibatalkan mitra"));
        masterList.add(new CustomItem("5", "2019-03-04 20:10:11", "Muba", "Potong Rambut", "50000", "12 KM, Jl. Majapahit No. 53", "Berhasil"));

        adapter.notifyDataSetChanged();
    }
}
