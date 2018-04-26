package id.gaia.treemonitoring;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.gaia.treemonitoring.database.TBDesa;
import id.gaia.treemonitoring.database.TBGapoktan;
import id.gaia.treemonitoring.database.TBKabkota;
import id.gaia.treemonitoring.database.TBKecamatan;
import id.gaia.treemonitoring.database.TBPersil;
import id.gaia.treemonitoring.database.TBPersilpemilik;
import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBPetanipersil;
import id.gaia.treemonitoring.database.TBPohon;
import id.gaia.treemonitoring.database.TBProvinsi;
import id.gaia.treemonitoring.database.TBSinkronisasi;
import id.gaia.treemonitoring.helper.Connection_Detector;
import id.gaia.treemonitoring.helper.Session;
import id.gaia.treemonitoring.model.Desa;
import id.gaia.treemonitoring.model.Gapoktan;
import id.gaia.treemonitoring.model.GetDesa;
import id.gaia.treemonitoring.model.GetGapoktan;
import id.gaia.treemonitoring.model.GetKabkota;
import id.gaia.treemonitoring.model.GetKecamatan;
import id.gaia.treemonitoring.model.GetLastsurvey;
import id.gaia.treemonitoring.model.GetLastsurveycalculate;
import id.gaia.treemonitoring.model.GetPersil;
import id.gaia.treemonitoring.model.GetPersilpemilik;
import id.gaia.treemonitoring.model.GetPetanipersil;
import id.gaia.treemonitoring.model.GetPohon;
import id.gaia.treemonitoring.model.GetPohontotal;
import id.gaia.treemonitoring.model.GetProvinsi;
import id.gaia.treemonitoring.model.Kabkota;
import id.gaia.treemonitoring.model.Kecamatan;
import id.gaia.treemonitoring.model.Persil;
import id.gaia.treemonitoring.model.Persilpemilik;
import id.gaia.treemonitoring.model.Petani;
import id.gaia.treemonitoring.model.Petanipersil;
import id.gaia.treemonitoring.model.Pohon;
import id.gaia.treemonitoring.model.Provinsi;
import id.gaia.treemonitoring.model.Sinkronisasi;
import id.gaia.treemonitoring.rest.ApiClient;
import id.gaia.treemonitoring.rest.ApiInterface;
import id.gaia.treemonitoring.rest.BackgroundService;
import me.anwarshahriar.calligrapher.Calligrapher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout koordinatorHomeLay;
    private TextView tvNama, tvTotalpersil, tvHariini, tvSinkrontgl;
    private Session session;
    private Button btLogout;
    private ProgressDialog progressDoalog, progressPersil, progressPemilik, progressPohonview, progressPohon;

    private TBPetani tbPetani;
    private TBProvinsi tbProvinsi;
    private TBKabkota tbKabkota;
    private TBKecamatan tbKecamatan;
    private TBDesa tbDesa;
    private TBGapoktan tbGapoktan;
    private TBPersil tbPersil;
    private TBPersilpemilik tbPersilpemilik;
    private TBPetanipersil tbPetanipersil;
    private TBPohon tbPohon;
    private TBSinkronisasi tbSinkronisasi;
    private List<Petani> petaniList = new ArrayList<>();
    private List<Kabkota> kabkotaList = new ArrayList<>();
    private List<Kecamatan> kecamatanList = new ArrayList<>();
    private List<Desa> desaList = new ArrayList<>();
    private List<Gapoktan> gapoktanList = new ArrayList<>();
    private List<Provinsi> provinsiList = new ArrayList<>();
    private List<Persil> persilList = new ArrayList<>();
    private List<Petanipersil> petanipersilList = new ArrayList<>();
    private List<Persilpemilik> pemilikList = new ArrayList<>();
    private List<Pohon> pohonList = new ArrayList<>();

    private String petaniId;
    private String petaniName;
    private String petUname;
    private String tanggalHariini;
    private String tglsaja;
    private String tgltimesaja;
    private String tgldatabase;

    // STRING STATUTS SETIAP TABEL
    private String statusInsertProvinsi;
    private String statusInsertKabkota;
    private String statusInsertKecamatan;
    private String statusInsertDesa;
    private String statusInsertGapoktan;

    private ApiInterface pApiInterface;
    Connection_Detector connection_detector;

    public static final int progrees_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // SET FONT
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(HomeActivity.this, "VarelaRound-Regular.ttf", true);

        // TANGGAL HARI INI
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDF= new SimpleDateFormat("yyyy-MM-dd");
        tglsaja = simpleDF.format(date);

        // CETAK BLUPRINT DBHANDLER
        tbPetani = new TBPetani(this);
        tbProvinsi = new TBProvinsi(this);
        tbKabkota = new TBKabkota(this);
        tbKecamatan = new TBKecamatan(this);
        tbDesa = new TBDesa(this);
        tbGapoktan = new TBGapoktan(this);
        tbPersil = new TBPersil(this);
        tbPersilpemilik = new TBPersilpemilik(this);
        tbPetanipersil = new TBPetanipersil(this);
        tbPohon = new TBPohon(this);
        tbSinkronisasi = new TBSinkronisasi(this);
        session = new Session(this);
        ApiInterface pApiInterface;

        // INITIALISASI
        koordinatorHomeLay = (CoordinatorLayout) findViewById(R.id.koordinatorHomeLay);
        btLogout = (Button) findViewById(R.id.btnLogout);
        tvNama = (TextView) findViewById(R.id.tthome_nama);
        tvTotalpersil = (TextView) findViewById(R.id.tthome_totalpersil);
        tvHariini = (TextView) findViewById(R.id.tthome_tanggal);
        tvSinkrontgl = (TextView) findViewById(R.id.tthome_sinkronstatus);
        //android.support.v7.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);
        btLogout.setOnClickListener(this);

        // MENAMPILKAN STATUS UPDATE PETANI BERDASARKAN STATUS INTERNET
        connection_detector = new Connection_Detector(this);
        if(!connection_detector.isConnected()){
            Snackbar.make(koordinatorHomeLay, "Perangkat tidak memiliki koneksi internet. Silahkan nyalakan koneksi internet pada perangkat anda", Snackbar.LENGTH_SHORT).show();
        }

        if(!session.loggedin()) {
            logout();
        }

        // AMBIL DATA PETANI YANG LOGIN, UNAME DIPANGGIL INTENT DARI HALAMAN SEBELUMNYA
        /*String petUname = getIntent().getStringExtra("UNAME");
        String petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());
        */

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        if(petUname != null) {
            if (tbPetani.ambilDataPetani(petUname) != null){
                petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());
                petaniName = tbPetani.ambilDataPetani(petUname).getPetani_name();
            } else {
                logout();
            }
        } else {
            logout();
        }

        if(!tbSinkronisasi.cekSinkronisasi(petaniId)){
            alertLoginPertama();
            //Snackbar.make(koordinatorHomeLay, "Belum Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_SHORT).show();
        } else {
            tgldatabase = "Database tanggal : "+tbSinkronisasi.ambilDataSinkronisasi(petaniId).getSinkrontanggal();
            //Snackbar.make(koordinatorHomeLay, "Sudah Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_SHORT).show();
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
            case R.id.goTomytree:

                break;
            case R.id.goTopantau:
                Intent in = new Intent(getApplicationContext(), FilterActivity.class);
                startActivity(in);
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
                        //new Syncronizing.execute(insertSinkronisasi());
                        insertSinkronisasi();

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
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan database. ", Snackbar.LENGTH_SHORT).show();
        }
        //Toast.makeText(HomeActivity.this,"Kamu pilih sinkronisasi data",Toast.LENGTH_SHORT).show();
        checkTanggaldatabase();
    }

    public void kosongkanDB(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET
            // KOSONGKAN TABLE
            tbProvinsi.kosongkanProvinsi();
            tbKabkota.kosongkanKabkota();
            tbKecamatan.kosongkanKecamatan();
            tbDesa.kosongkanDesa();
            tbGapoktan.kosongkanGapoktan();
            tbPersil.kosongkanPersil();
            tbPersilpemilik.kosongkanPersilpemilik();
            tbPetanipersil.kosongkanPetanipersil();
            tbPohon.kosongkanPohon();

        }
    }

    // SINKRONISASI DATA PERTAMA LOGIN
    //Pindahkan semua data dari mysql ke sqlite
    // 1. Table Provinsi
    public void prosesProvinsi(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetProvinsi> provinsiCall = pApiInterface.getProvinsi();

            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Database Provinsi....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setCancelable(false);

            provinsiCall.enqueue(new Callback<GetProvinsi>() {
                @Override
                public void onResponse(Call<GetProvinsi> call, Response<GetProvinsi> response) {
                    if (response.isSuccessful()) {
                        List<Provinsi> ProvinsiList = response.body().getListDataProvinsi();

                        int PR = ProvinsiList.size();
                        if(PR != 0) {
                            // show it
                            progressDoalog.setMax(ProvinsiList.size());
                            progressDoalog.show();

                            int dataKe = 0;
                            for (int po = 0; po < PR; po++) {
                                dataKe = po + 1;
                                if (!tbProvinsi.cekProvinsiid(String.valueOf(ProvinsiList.get(po).getProvinsi_kode()))) { // PERIKSA PROVINSIID
                                    int provinsiId = ProvinsiList.get(po).getProvinsi_id();
                                    String provinsiKode = ProvinsiList.get(po).getProvinsi_kode();
                                    String provinsiName = ProvinsiList.get(po).getProvinsi_name();

                                    tbProvinsi.tambahProvinsi(new Provinsi(provinsiId, provinsiKode, provinsiName));
                                    Log.d("Insert SQLITE ", "Provinsi " + ProvinsiList.get(po).getProvinsi_kode() + " - " + ProvinsiList.get(po).getProvinsi_name());
                                } else {
                                    Log.d("Sudah ada ", "Provinsi " + ProvinsiList.get(po).getProvinsi_kode() + " - " + ProvinsiList.get(po).getProvinsi_name());
                                }
                                progressDoalog.setProgress(dataKe * 1);

                                if (progressDoalog.getProgress() == progressDoalog.getMax()) {
                                    progressDoalog.dismiss();
                                }
                            }
                        }
                        statusInsertProvinsi = "Data Provinsi Berhasil disinkronisasi";

                    } else {
                        // ERROR RESPONSE, NO ACCESS TO RESOURCE
                    }
                }

                @Override
                public void onFailure(Call<GetProvinsi> call, Throwable t) {
                    statusInsertProvinsi = "Data Provinsi Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data provinsi. ", Snackbar.LENGTH_SHORT).show();
            statusInsertProvinsi = "Tabel Provinsi Gagal disinkronisasi";
        }
    }

    // 2. Tabel Kabkota
    public void prosesKabkota(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetKabkota> kabkotaCall = pApiInterface.getKabkota();

            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Database Kabupaten / Kota....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setCancelable(false);

            kabkotaCall.enqueue(new Callback<GetKabkota>() {
                @Override
                public void onResponse(Call<GetKabkota> call, Response<GetKabkota> response) {
                    List<Kabkota> KabkotaList = response.body().getListDataKabkota();

                    int PR = KabkotaList.size();
                    if(PR != 0) {
                        // show it
                        progressDoalog.setMax(KabkotaList.size());
                        progressDoalog.show();
                        int dataKe = 0;
                        for (int po = 0; po < PR; po++) {
                            dataKe = po + 1;
                            if(!tbKabkota.cekKabkotaid(String.valueOf(KabkotaList.get(po).getKabkota_id()))) { // PERIKSA KABKOTAID
                            int kabkotaId = KabkotaList.get(po).getKabkota_id();
                            int provinsiId = KabkotaList.get(po).getProvinsi_id();
                            String kabkotaKode = KabkotaList.get(po).getKabkota_kode();
                            String kabkotaName = KabkotaList.get(po).getKabkota_name();

                            tbKabkota.tambahKabkota(new Kabkota(kabkotaId, provinsiId, kabkotaKode, kabkotaName));
                            Log.d("Insert SQLITE ", "Kabkota " + KabkotaList.get(po).getKabkota_kode() + " - " + KabkotaList.get(po).getKabkota_name());
                            } else {
                                Log.d("Sudah ada ", "Kabkota " + KabkotaList.get(po).getKabkota_kode() + " - " + KabkotaList.get(po).getKabkota_name());
                            }
                            progressDoalog.setProgress(dataKe * 1);

                            if (progressDoalog.getProgress() == progressDoalog.getMax()) {
                                progressDoalog.dismiss();
                            }

                        }
                        statusInsertKabkota = "Data Kabupaten/kota Berhasil disinkronisasi";
                    }
                }

                @Override
                public void onFailure(Call<GetKabkota> call, Throwable t) {
                    statusInsertKabkota = "Data Provinsi Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data kabupaten/kota.", Snackbar.LENGTH_SHORT).show();
            statusInsertKabkota = "Tabel Kabupaten/kota Gagal disinkronisasi";
        }
    }

    // 3. Table Kecamatan
    public void prosesKecamatan(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetKecamatan> kecamatanCall = pApiInterface.getKecamatan();
            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Database Kecamatan....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setCancelable(false);

            kecamatanCall.enqueue(new Callback<GetKecamatan>() {
                @Override
                public void onResponse(Call<GetKecamatan> call, Response<GetKecamatan> response) {
                    List<Kecamatan> KecamatanList = response.body().getListDataKecamatan();

                    int PR = KecamatanList.size();
                    if(PR != 0) {
                        // show it
                        progressDoalog.setMax(KecamatanList.size());
                        progressDoalog.show();
                        int dataKe = 0;
                        for (int po = 0; po < PR; po++) {
                            dataKe = po + 1;
                            if(!tbKecamatan.cekKecamatanid(String.valueOf(KecamatanList.get(po).getKecamatan_id()))) { // PERIKSA KECAMATANID
                                int kecId = KecamatanList.get(po).getKecamatan_id();
                                int kabkotaId = KecamatanList.get(po).getKabkota_id();
                                String kecKode = KecamatanList.get(po).getKecamatan_kode();
                                String kecName = KecamatanList.get(po).getKecamatan_name();

                                tbKecamatan.tambahKecamatan(new Kecamatan(kecId, kabkotaId, kecKode, kecName));
                                Log.d("Insert SQLITE ", "Kecamatan " + kecKode + " - " + kecName);
                            } else {
                               Log.d("Sudah ada ", "Kecamatan " + KecamatanList.get(po).getKecamatan_kode() + " - " + KecamatanList.get(po).getKecamatan_name());
                            }
                            progressDoalog.setProgress(dataKe * 1);

                            if (progressDoalog.getProgress() == progressDoalog.getMax()) {
                                progressDoalog.dismiss();
                            }
                        }
                        statusInsertKecamatan = "Data Kecamatan Berhasil disinkronisasi";
                    }
                }

                @Override
                public void onFailure(Call<GetKecamatan> call, Throwable t) {
                    statusInsertKecamatan = "Data Provinsi Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data kecamatan.", Snackbar.LENGTH_SHORT).show();
            statusInsertKecamatan = "Tabel Kecamatan Gagal disinkronisasi";
        }
    }

    // 4. Tabel Desa
    public void prosesDesa(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetDesa> desaCall = pApiInterface.getDesa();

            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Database Desa....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setCancelable(false);

            desaCall.enqueue(new Callback<GetDesa>() {
                @Override
                public void onResponse(Call<GetDesa> call, Response<GetDesa> response) {
                    List<Desa> DesaList = response.body().getListDataDesa();

                    int PR = DesaList.size();
                    if(PR != 0) {
                        // show it
                        progressDoalog.setMax(DesaList.size());
                        progressDoalog.show();
                        int dataKe = 0;
                        for (int po = 0; po < PR; po++) {
                            dataKe = po + 1;
                            if (!tbDesa.cekDesaid(String.valueOf(DesaList.get(po).getDesa_id()))) { // PERIKSA DESAID
                                int desaId = DesaList.get(po).getDesa_id();
                                int kecId = DesaList.get(po).getKecamatan_id();
                                String desaName = DesaList.get(po).getDesa_name();

                                tbDesa.tambahDesa(new Desa(desaId, kecId, desaName));
                                Log.d("Insert SQLITE ", "Desa " + desaName);
                            } else {
                                Log.d("Sudah ada ", "Desa " + DesaList.get(po).getDesa_name());
                            }
                            progressDoalog.setProgress(dataKe * 1);

                            if (progressDoalog.getProgress() == progressDoalog.getMax()) {
                                progressDoalog.dismiss();
                            }
                        }
                        statusInsertDesa = "Data Desa Berhasil disinkronisasi";
                    }
                }

                @Override
                public void onFailure(Call<GetDesa> call, Throwable t) {
                    statusInsertDesa= "Data Desa Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data desa.", Snackbar.LENGTH_SHORT).show();
            statusInsertDesa = "Tabel Desa Gagal disinkronisasi";
        }
    }

    // 5. Tabel Gapoktan
    public void prosesGapoktan(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetGapoktan> gapoktanCall = pApiInterface.getGapoktan();

            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Database Gapoktan....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setCancelable(false);

            gapoktanCall.enqueue(new Callback<GetGapoktan>() {
                @Override
                public void onResponse(Call<GetGapoktan> call, Response<GetGapoktan> response) {
                    List<Gapoktan> GapoktanList = response.body().getListDataGapoktan();
                    int PR = GapoktanList.size();
                    if(PR != 0) {
                        // show it
                        progressDoalog.setMax(GapoktanList.size());
                        progressDoalog.show();
                        int dataKe = 0;
                        for (int po = 0; po < PR; po++) {
                            dataKe = po + 1;
                            if (!tbGapoktan.cekGapoktanid(String.valueOf(GapoktanList.get(po).getGapoktan_id()))) { // PERIKSA GAPOKTANID
                                int gapoktanId = GapoktanList.get(po).getGapoktan_id();
                                int desaId = GapoktanList.get(po).getDesa_id();
                                String gapoktanName = GapoktanList.get(po).getGapoktan_name();

                                tbGapoktan.tambahGapoktan(new Gapoktan(gapoktanId, desaId, gapoktanName));
                                Log.d("Insert SQLITE ", "Gapoktan " + gapoktanName);
                            } else {
                                Log.d("Sudah ada ", "Gapoktan " + GapoktanList.get(po).getGapoktan_name());
                            }
                            progressDoalog.setProgress(dataKe * 1);

                            if (progressDoalog.getProgress() == progressDoalog.getMax()) {
                                progressDoalog.dismiss();
                            }
                        }
                        statusInsertDesa = "Data Gapoktan Berhasil disinkronisasi";
                    }
                }

                @Override
                public void onFailure(Call<GetGapoktan> call, Throwable t) {
                    statusInsertGapoktan = "Data Gapoktan Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data gapoktan.", Snackbar.LENGTH_SHORT).show();
            statusInsertGapoktan = "Tabel Gapoktan Gagal disinkronisasi";
        }

        // SINKRONISASI PERSIL DAN RELASI RELASI DAN JUGA ISINYA
        prosesPetanipersil();
    }

    // 8. Tabel Petani Persil, Persil, Persil Pemilik, Pohon
    public void prosesPetanipersil(){
        if(connection_detector.isConnected()) { // JIKA ADA KONEKSI INTERNET

            // PANGGIL API INTERFACE,
            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetPetanipersil> petanipersilCall = pApiInterface.getPetanipersil(petaniId);

            // Set up progress before call
            final ProgressDialog progressDoalog;
            progressDoalog = new ProgressDialog(HomeActivity.this);
            progressDoalog.setMessage("Data Persil, Pemilik dan Pohon....");
            progressDoalog.setTitle("Sinkronisasi");
            progressDoalog.incrementProgressBy(1);
            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // show it
            progressDoalog.show();
            petanipersilCall.enqueue(new Callback<GetPetanipersil>() {
                @Override
                public void onResponse(Call<GetPetanipersil> call, Response<GetPetanipersil> response) {
                    final List<Petanipersil> PersilpetaniList = response.body().getListDataPetanipersil();

                    int PR = PersilpetaniList.size();
                    if(PR != 0) {

                        progressDoalog.setMax(PR);
                        int dataPersilpetanike = 0;

                        for (int po = 0; po < PR; po++) {

                            final int finalPo = po;
                            dataPersilpetanike = po + 1;

                            // INSERT PETANI PERSIL
                            if (!tbPetanipersil.cekPtpsid(PersilpetaniList.get(po).getPersil_id(), petaniId)) {
                                String persilId = PersilpetaniList.get(po).getPersil_id();
                                int petaniId = PersilpetaniList.get(po).getPetani_id();
                                tbPetanipersil.tambahPetanipersil(new Petanipersil(persilId, petaniId));
                                Log.d("Insert SQLITE ", "PetaniPersil " + persilId);
                            } else {
                                Log.d("Sudah ada ", "PetaniPersil " + PersilpetaniList.get(po).getPersil_id());
                            }

                            // AMBIL DATA PERSIL
                            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                            Call<GetPersil> persilCall = pApiInterface.getPersil(PersilpetaniList.get(po).getPersil_id());
                            progressPersil = new ProgressDialog(HomeActivity.this);
                            progressPersil.setMessage("Sinkronisasi data Persil " + PersilpetaniList.get(po).getPersil_id() + " ....");
                            progressPersil.setTitle("Sinkronisasi");
                            progressPersil.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressPersil.incrementProgressBy(1);
                            progressPersil.setCancelable(false);

                            persilCall.enqueue(new Callback<GetPersil>() {
                                @Override
                                public void onResponse(Call<GetPersil> call, Response<GetPersil> response) {
                                    final List<Persil> PersilList = response.body().getListDataPersil();

                                    if (PersilList.size() != 0) {

                                        progressPersil.setMax(PersilList.size());
                                        progressPersil.show();

                                        int dataPersilke = 0;
                                        int PER = PersilList.size();
                                        for (int por = 0; por < PER; por++) {
                                            dataPersilke = por + 1;
                                            final int dataPersilkefinal = dataPersilke;

                                            // AMBIL TOTAL POHON
                                            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                                            Call<GetPohontotal> pohontotalCall = pApiInterface.getPohontotal(PersilpetaniList.get(finalPo).getPersil_id());

                                            pohontotalCall.enqueue(new Callback<GetPohontotal>() {
                                                @Override
                                                public void onResponse(Call<GetPohontotal> call, Response<GetPohontotal> response) {
                                                    final List<Persil> pohonTotal = response.body().getListDataTotalPohon();
                                                    int totalPohon = pohonTotal.get(0).getTotal_pohon();

                                                    if (!tbPersil.cekPersilid(PersilList.get(0).getPersil_id())) {
                                                        String persilId = PersilList.get(0).getPersil_id();
                                                        int desaId = PersilList.get(0).getDesa_id();
                                                        int gapoktanId = PersilList.get(0).getGapoktan_id();

                                                        tbPersil.tambahPersil(new Persil(persilId, desaId, gapoktanId, totalPohon));
                                                        Log.d("SQLITE insert ", "Persil, Desa, GP, Total " + persilId + ", " + desaId + ", " + gapoktanId + ", " + totalPohon);
                                                    } else {
                                                        Log.d("Sudah ada data ", "Persil " + PersilList.get(0).getPersil_id());
                                                    }
                                                    progressPersil.setProgress(dataPersilkefinal * 1);
                                                    if (progressPersil.getProgress() == progressPersil.getMax()) {
                                                        progressPersil.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<GetPohontotal> call, Throwable t) {
                                                    Log.e("Respon Error", "Persil " + PersilpetaniList.get(finalPo).getPersil_id());
                                                }
                                            });


                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetPersil> call, Throwable t) {

                                }
                            });
                            // END PROSES DATA PESIL

                            // AMBIL DATA PEMILIK PERSIL
                            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                            Call<GetPersilpemilik> persilpemilikCall = pApiInterface.getPersilpemilik(PersilpetaniList.get(po).getPersil_id(), 1);
                            progressPemilik = new ProgressDialog(HomeActivity.this);
                            progressPemilik.incrementProgressBy(1);
                            progressPemilik.setMessage("Sinkronisasi data Pemilik Persil " + PersilpetaniList.get(po).getPersil_id() + " ....");
                            progressPemilik.setTitle("Sinkronisasi");
                            progressPemilik.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressPemilik.setCancelable(false);
                            persilpemilikCall.enqueue(new Callback<GetPersilpemilik>() {
                                @Override
                                public void onResponse(Call<GetPersilpemilik> call, Response<GetPersilpemilik> response) {
                                    List<Persilpemilik> PersilpemilikList = response.body().getListDataPersilpemilik();

                                    int bpp = PersilpemilikList.size();
                                    if (bpp != 0) {

                                        progressPemilik.setMax(bpp);
                                        progressPemilik.show();

                                        int dataPemilikke = 0;
                                        for (int bb = 0; bb < bpp; bb++) {
                                            dataPemilikke = bb + 1;

                                            if (!tbPersilpemilik.cekPersilpemilikid(String.valueOf(PersilpemilikList.get(bb).getPemilik_id()))) {
                                                String persilId = PersilpemilikList.get(bb).getPersil_id();
                                                int pemilikId = PersilpemilikList.get(bb).getPemilik_id();
                                                String pemilikName = PersilpemilikList.get(bb).getPemilik_name();
                                                tbPersilpemilik.tambahPemilik(new Persilpemilik(pemilikId, persilId, pemilikName));
                                                Log.d("Insert SQLITE ", "PemilikPersil " + PersilpemilikList.get(bb).getPersil_id() + " - " + PersilpemilikList.get(bb).getPemilik_name());
                                            } else {
                                                Log.d("Sudah ada data", "PemilikPersil " + PersilpemilikList.get(bb).getPersil_id() + " - " + PersilpemilikList.get(bb).getPemilik_name());
                                            }

                                            progressPemilik.setProgress(dataPemilikke * 1);
                                            if (progressPemilik.getProgress() == progressPemilik.getMax()) {
                                                progressPemilik.dismiss();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetPersilpemilik> call, Throwable t) {

                                }
                            });
                            // END PROSES PEMILIK PERSIL

                            // AMBIL DATA POHON
                            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                            Call<GetPohon> pohonCall = pApiInterface.getPohon(PersilpetaniList.get(po).getPersil_id());

                            pohonCall.enqueue(new Callback<GetPohon>() {
                                @Override
                                public void onResponse(Call<GetPohon> call, Response<GetPohon> response) {
                                    final List<Pohon> PohonList = response.body().getListDataPohon();

                                    final int ph = PohonList.size();

                                    if (ph != 0) {
                                        progressPohonview = new ProgressDialog(HomeActivity.this);
                                        progressPohonview.incrementProgressBy(1);
                                        progressPohonview.setMessage("Data Semua Pohon ...." );
                                        progressPohonview.setTitle("Sinkronisasi");
                                        progressPohonview.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressPohonview.setCancelable(false);

                                        progressPohon = new ProgressDialog(HomeActivity.this);
                                        progressPohon.incrementProgressBy(1);
                                        progressPohon.setTitle("Sinkronisasi");
                                        progressPohon.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        progressPohon.setCancelable(false);

                                        int dataPohonke = 0;
                                        for (int pho = 0; pho < ph; pho++) {
                                            progressPohonview.show();
                                            dataPohonke++;

                                            final int dataPohonKe = dataPohonke;
                                            final int finalPho = pho;

                                            // AMBIL TANGGAL LAST UPDATE DENGAN MENGIRIMKAN DATA POHON ID KE SERVER UNTUK DICEK DI TABEL SURVEY
                                            String skala = null;
                                            if (PohonList.get(pho).getPohon_skalasurvey() == 1) {
                                                skala = PohonList.get(pho).getPohon_nilaiskala() + "-MONTH";
                                            } else if (PohonList.get(pho).getPohon_skalasurvey() == 0) {
                                                skala = PohonList.get(pho).getPohon_nilaiskala() + "-DAY";
                                            } else if (PohonList.get(pho).getPohon_skalasurvey() == 2) {
                                                skala = PohonList.get(pho).getPohon_nilaiskala() + "-YEAR";
                                            }

                                            // AMBIL DATA LAST UPDATE DI TABEL SURVEY
                                            pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                                            Call<GetLastsurvey> lastsurveyCall = pApiInterface.getLastsurvey(PohonList.get(finalPho).getPohon_id(), skala);
                                            lastsurveyCall.enqueue(new Callback<GetLastsurvey>() {
                                                @Override
                                                public void onResponse(Call<GetLastsurvey> call, Response<GetLastsurvey> response) {
                                                    final List<Pohon> LastUpdate = response.body().getListLastUpdate();

                                                    progressPohonview.dismiss();

                                                    String tgllastupdate = null;
                                                    String tglnextupdate = null;
                                                    if (LastUpdate.size() != 0) {
                                                        tgllastupdate = LastUpdate.get(0).getPohon_lastupdate();
                                                        tglnextupdate = LastUpdate.get(0).getPohon_nextupdate();
                                                    } else {
                                                        tgllastupdate = PohonList.get(finalPho).getPohon_tgltanamsulam();
                                                        tglnextupdate = tglsaja;
                                                    }

                                                    // HITUNG SELISIH HARI INI KE NEXT UPDATE
                                                    final String tglLastUpdate = tgllastupdate;
                                                    final String tglNextUpdate = tglnextupdate;

                                                    pApiInterface = ApiClient.getClient().create(ApiInterface.class);
                                                    Call<GetLastsurveycalculate> lastcalculateCall = pApiInterface.getSelisihhari(tglnextupdate, tglsaja);
                                                    lastcalculateCall.enqueue(new Callback<GetLastsurveycalculate>() {
                                                        @Override
                                                        public void onResponse(Call<GetLastsurveycalculate> call, Response<GetLastsurveycalculate> response) {
                                                            List<Pohon> selisihHari = response.body().getListSelisihhari();
                                                            final int selisihNya = selisihHari.get(0).getSelisih_hari();


                                                            progressPohon.setMax(ph);
                                                            progressPohon.setProgress(dataPohonKe);
                                                            progressPohon.setMessage("Data Semua Pohon "+ PersilpetaniList.get(finalPo).getPersil_id() +"....");

                                                            progressPohonview.dismiss();
                                                            progressPohon.show();



                                                            if (selisihNya >= -30) { // JIKA SELISIH HARI SUDAH SEBULAN SEBELUM TANGGAL SURVEY ATAU LEBIH DARI HARI HARUS SURVEY MAKA
                                                                // INSERT DATA POHON
                                                                int pohonId = PohonList.get(finalPho).getPohon_id();

                                                                if(!tbPohon.cekPohonid(String.valueOf(pohonId))){
                                                                    String persilId = PohonList.get(finalPho).getPersil_id();
                                                                    String pohonKode = PohonList.get(finalPho).getPohon_kode();
                                                                    String namaLatin = PohonList.get(finalPho).getPohon_namalatin();
                                                                    String namaLokal = PohonList.get(finalPho).getPohon_namalokal();
                                                                    int pohonStatus = PohonList.get(finalPho).getPohon_status();
                                                                    int pohonJenis = PohonList.get(finalPho).getPohon_jenis();
                                                                    String tglTanamslm = PohonList.get(finalPho).getPohon_tgltanamsulam();
                                                                    int skalaSurvey = PohonList.get(finalPho).getPohon_skalasurvey();
                                                                    int nilaiSkala = PohonList.get(finalPho).getPohon_nilaiskala();
                                                                    int statusUpdate = 1;
                                                                    tbPohon.tambahPohon(new Pohon(pohonId, persilId, pohonKode, namaLatin, namaLokal, pohonStatus, pohonJenis, tglTanamslm, skalaSurvey, nilaiSkala, tglLastUpdate, tglNextUpdate, statusUpdate));

                                                                    Log.d("Insert SQLite ", "Pohon " + PohonList.get(finalPho).getPohon_kode() + ", " + PohonList.get(finalPho).getPohon_namalokal() + ", " + tglLastUpdate + ", " + tglNextUpdate + ", " + tglsaja + ", " + selisihNya);
                                                                } else {
                                                                    Log.d("Data Sudah Ada ", "Pohon " + PohonList.get(finalPho).getPohon_kode() + ", "+ PohonList.get(finalPho).getPohon_namalokal() + ", " + tglLastUpdate + ", " + tglNextUpdate + ", " + tglsaja + ", "+ selisihNya);
                                                                }
                                                                Log.e("PROGRESS POON ", "Pohon " + progressPohon.getProgress() + " = " + progressPohon.getMax() + ", " + finalPho + " - " + dataPohonKe + ", Max " + ph);

                                                                if (progressPohon.getProgress() >= progressPohon.getMax()) {
                                                                    progressPohon.dismiss();
                                                                }
                                                            } else {
                                                                Log.e("PROGRESS POON ", "Pohon " + progressPohon.getProgress() + " = " + progressPohon.getMax()+ ", " + finalPho + " - " + dataPohonKe + ", Max " + ph);

                                                                if (progressPohon.getProgress() >= progressPohon.getMax()) {
                                                                    progressPohon.dismiss();
                                                                }
                                                            }

                                                            //Log.e("DATA POHON ", "Persil = "+ PersilpetaniList.get(finalPo).getPersil_id() + ", ke " +dataPohonKe+" dari " + ph );
                                                            if (progressPohon.getProgress() >= progressPohon.getMax()) {
                                                                progressPohon.dismiss();
                                                            }

                                                        }

                                                        @Override
                                                        public void onFailure(Call<GetLastsurveycalculate> call, Throwable t) {
                                                            progressPohonview.dismiss();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<GetLastsurvey> call, Throwable t) {
                                                    progressPohonview.dismiss();
                                                }
                                            });

                                            //Log.e("PROGRESS POON ", "Pohon " + progressPohon.getProgress() + " = " + progressPohon.getMax());
                                            //progressPohon.dismiss(); // NOT THIS

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetPohon> call, Throwable t) {

                                }
                            });
                            // END PROSES DATA POHON

                            progressDoalog.setProgress(dataPersilpetanike * 1);

                            if (progressDoalog.getProgress() >= progressDoalog.getMax()) {
                                progressDoalog.dismiss();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetPetanipersil> call, Throwable t) {
                    // close it after response
                    progressDoalog.dismiss();
                    //statusInsertProvinsi = "Data Provinsi Gagal disinkronisasi. Koneksi server tidak ditemukan";
                }
            });

        } else {
            Snackbar.make(koordinatorHomeLay, "Koneksi internet tidak tersedia. Gagal menyelaraskan data provinsi. ", Snackbar.LENGTH_SHORT).show();
            //statusInsertProvinsi = "Tabel Provinsi Gagal disinkronisasi";
        }
    }

    // INPUT DATA SINKRONISASI DAN INSERT DATA LAIN DARI MYSQL KE SQLITE
    public void insertSinkronisasi(){
        kosongkanDB();// KOSONGKAN DB

        // INSERT DATA DATA KE SQLITE
        prosesProvinsi();
        prosesKabkota();
        prosesKecamatan();
        prosesDesa();
        prosesGapoktan();

        // INSERT DATA SINKRONISASI
        String datetime = tgltimesaja;
        String statussinkron = "| " + statusInsertProvinsi + " | " + statusInsertKabkota + " | " + statusInsertKecamatan + " | " + statusInsertDesa; // STATUS STATUS DARI SETIAP TABEL YANG DISINKRONISASI
        tbSinkronisasi.tambahSinkronisasi(new Sinkronisasi(Integer.parseInt(petaniId), datetime, statussinkron));

        //Toast.makeText(HomeActivity.this,"Sinkronisasi data selesai",Toast.LENGTH_SHORT).show();
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
                                tgltimesaja = simpleDF.format(date);

                                checkTanggaldatabase();

                                int totalPersil = tbPetanipersil.cekTotalpersil(petaniId);
                                tvTotalpersil.setText(totalPersil + " persil");
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

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin ingin keluar ?");
        alertDialogBuilder.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        session.setLoggedin(false);
                        finish();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    }
                });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    public void AmbilSemuaPetani(){
        List<Petani> petaniList = tbPetani.ambilSemuaPetani();
        Log.d("SQLite Get", "Jumlah Data Petani :" + String.valueOf(petaniList.size()));
        for(int sqp = 0; sqp < petaniList.size(); sqp++){
            Log.d("SQLite ", "Nama Petani = " + petaniList.get(sqp).getPetani_name());
        }
    }
}
