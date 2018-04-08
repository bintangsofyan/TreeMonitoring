package id.gaia.treemonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBProvinsi;
import id.gaia.treemonitoring.database.TBSinkronisasi;
import id.gaia.treemonitoring.helper.Connection_Detector;
import id.gaia.treemonitoring.helper.Session;
import id.gaia.treemonitoring.model.GetProvinsi;
import id.gaia.treemonitoring.model.Petani;
import id.gaia.treemonitoring.model.Provinsi;
import id.gaia.treemonitoring.model.Sinkronisasi;
import id.gaia.treemonitoring.rest.ApiClient;
import id.gaia.treemonitoring.rest.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout koordinatorHomeLay;
    private TextView tvNama, tvDesa, tvHariini, tvSinkrontgl;
    private Session session;
    private Button btLogout;
    private TBPetani tbPetani;
    private TBProvinsi tbProvinsi;
    private TBSinkronisasi tbSinkronisasi;
    private List<Petani> petaniList = new ArrayList<>();

    private String petaniId;
    private String petaniName;
    private String petUname;
    private String tanggalHariini;
    private String tglsaja;
    private String tgldatabase;

    // STRING STATUTS SETIAP TABEL
    private String statusInsertProvinsi;

    private ApiInterface pApiInterface;
    Connection_Detector connection_detector;

    private List<Provinsi> provinsiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // CETAK BLUPRINT DBHANDLER
        tbPetani = new TBPetani(this);
        tbProvinsi = new TBProvinsi(this);
        tbSinkronisasi = new TBSinkronisasi(this);
        session = new Session(this);
        ApiInterface pApiInterface;

        // MENAMPILKAN STATUS UPDATE PETANI BERDASARKAN STATUS INTERNET
        connection_detector = new Connection_Detector(this);
        if(!connection_detector.isConnected()){
            Snackbar.make(koordinatorHomeLay, "Perangkat tidak memiliki koneksi internet. Silahkan nyalakan koneksi internet pada perangkat anda", Snackbar.LENGTH_LONG).show();
        }

        // INITIALISASI
        koordinatorHomeLay = (CoordinatorLayout) findViewById(R.id.koordinatorHomeLay);
        btLogout = (Button) findViewById(R.id.btnLogout);
        tvNama = (TextView) findViewById(R.id.tthome_nama);
        tvDesa = (TextView) findViewById(R.id.tthome_desa);
        tvHariini = (TextView) findViewById(R.id.tthome_tanggal);
        tvSinkrontgl = (TextView) findViewById(R.id.tthome_sinkronstatus);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        btLogout.setOnClickListener(this);

        if(!session.loggedin()) {
            logout();
        }

        // AMBIL DATA PETANI YANG LOGIN, UNAME DIPANGGIL INTENT DARI HALAMAN SEBELUMNYA
        /*String petUname = getIntent().getStringExtra("UNAME");
        String petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());
        */

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());
        petaniName = tbPetani.ambilDataPetani(petUname).getPetani_name();

        if(!tbSinkronisasi.cekSinkronisasi(petaniId)){
            alertLoginPertama();
            //Snackbar.make(koordinatorHomeLay, "Belum Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_LONG).show();
        } else {
            tgldatabase = "Database tanggal : "+tbSinkronisasi.ambilDataSinkronisasi(petaniId).getSinkrontanggal();
            //Snackbar.make(koordinatorHomeLay, "Sudah Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_LONG).show();
        }

        setPetaniStatus();
        //AmbilSemuaPetani();

    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogout:
                logout();
                break;
            case R.id.tthome_sinkronbutton:
                alertSinkronisasi();
                break;
            default:

        }
    }

    // ALERT DIALOG LOGIN PERTAMA
    public void alertLoginPertama(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Selamat datang, sinkronisasi data sekarang. Pastikan koneksi internet dalam keadaan menyala");
            alertDialogBuilder.setPositiveButton("Oke",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        insertSinkronisasi(); // PROSES SINKROISASI PERTAMA SEKALI

                    }
                });

            alertDialogBuilder.setNegativeButton(null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // ALERT DIALOG SINKRONISASI
    public void alertSinkronisasi(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Sinkronisasi Data. Pastikan Jaringan Internet pada smartphone anda dalam keadaan menyala.");
        alertDialogBuilder.setPositiveButton("Oke, sinkronisasi",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        prosesSinkronisasi(); // PROSES SINRKONISASI DATA
                    }
                });

        alertDialogBuilder.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // SINKRONISASI DATA PERTAMA LOGIN
    //Pindahkan semua data dari mysql ke sqlite
    // 1. Table Provinsi
    public void prosesProvinsi(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET
            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetProvinsi> provinsiCall = pApiInterface.getProvinsi();
            provinsiCall.enqueue(new Callback<GetProvinsi>() {
                @Override
                public void onResponse(Call<GetProvinsi> call, Response<GetProvinsi> response) {
                    List<Provinsi> ProvinsiList = response.body().getListDataProvinsi();

                    int PR = ProvinsiList.size();
                    for(int po = 0; po < PR; po++){
                        if(!tbProvinsi.cekProvinsiid(String.valueOf(ProvinsiList.get(po).getProvinsi_kode()))) { // PERIKSA PROVINSIID
                            int provinsiId = ProvinsiList.get(po).getProvinsi_id();
                            String provinsiKode = ProvinsiList.get(po).getProvinsi_kode();
                            String provinsiName = ProvinsiList.get(po).getProvinsi_name();

                            tbProvinsi.tambahProvinsi(new Provinsi(provinsiId, provinsiName, provinsiKode));
                        }
                    }
                    statusInsertProvinsi = "Data Provinsi Berhasil disinkronisasi";
                }

                @Override
                public void onFailure(Call<GetProvinsi> call, Throwable t) {
                    statusInsertProvinsi = "Data Provinsi Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan database. ", Snackbar.LENGTH_LONG).show();
            statusInsertProvinsi = "Tabel Provinsi Gagal disinkronisasi";
        }
    }

    // 2. Tabel Kabkota

    // 3. Table Kecamatan

    // 4. Tabel Desa

    // 5. Tabel Gapoktan

    // 6. Tabel KTH

    // 7. Tabel Persil

    // 8. Tabel Pemilik Persil

    // 9. Tabel Buku Pohon

    // 10. Tabel Pohon Dari Persil Yang Ada

    // SINKRONISASI DATA UNTUK KEDUA KALINYA
    /* Langkah 1 : Periksa Tabel Survey
    Jika ada isinya maka migrasikan ke MYSql, setelah selesai migrasi kosongkan table survey.
    Upload semua gambar di folder foto ke server. Setelah itu kosongkan folder foto
    */
    public void prosesSinkronisasi(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET
            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);

            // PROSES UPLOAD GAMBAR DAN DATA KE SERVER MYSQL,

            // MENGOSONGKAN DATA SURVEY

            insertSinkronisasi();
        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan database. ", Snackbar.LENGTH_LONG).show();
        }
        Toast.makeText(HomeActivity.this,"Kamu pilih sinkronisasi data",Toast.LENGTH_LONG).show();
        checkTanggaldatabase();
    }


    // INPUT DATA SINKRONISASI DAN INSERT DATA LAIN DARI MYSQL KE SQLITE
    public void insertSinkronisasi(){
        // INSERT DATA DATA KE MYSQL
        prosesProvinsi();

        // INSERT DATA SINKRONISASI
        String datetime = tglsaja;
        String statussinkron = "| " + statusInsertProvinsi + " | "; // STATUS STATUS DARI SETIAP TABEL YANG DISINKRONISASI
        tbSinkronisasi.tambahSinkronisasi(new Sinkronisasi(Integer.parseInt(petaniId), datetime, statussinkron));

        Toast.makeText(HomeActivity.this,"Sinkronisasi data selesai",Toast.LENGTH_LONG).show();
        checkTanggaldatabase();
    }

    public void checkTanggaldatabase() {
        if(tbSinkronisasi.cekSinkronisasi(petaniId)) {
            tgldatabase = "Database tanggal : " + tbSinkronisasi.ambilDataSinkronisasi(petaniId).getSinkrontanggal();
        } else {
            tgldatabase = "Belum Ada Data ";
        }
        tvSinkrontgl.setText(tgldatabase);
    }

    private void setPetaniStatus() {
        tvNama.setText(petaniName);
        tvDesa.setText("Aik Bual");


        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd-MM-yyyy HH:mm:ss");
                                tanggalHariini = simpleDateFormat.format(date);
                                tvHariini.setText(tanggalHariini);
                                SimpleDateFormat simpleDF= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                tglsaja = simpleDF.format(date);

                                checkTanggaldatabase();

                            }
                        });
                    }
                } catch (InterruptedException e){}
            }
        };
        thread.start();
    }

    // LOGOUT
    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    public void AmbilSemuaPetani(){
        List<Petani> petaniList = tbPetani.ambilSemuaPetani();
        Log.d("SQLite Get", "Jumlah Data Petani :" + String.valueOf(petaniList.size()));
        for(int sqp = 0; sqp < petaniList.size(); sqp++){
            Log.d("SQLite ", "Nama Petani = " + petaniList.get(sqp).getPetani_name());
        }
    }
}
