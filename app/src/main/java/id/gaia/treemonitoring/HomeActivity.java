package id.gaia.treemonitoring;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBSinkronisasi;
import id.gaia.treemonitoring.helper.Session;
import id.gaia.treemonitoring.model.Petani;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout koordinatorHomeLay;
    private TextView tvNama, tvDesa, tvHariini, tvSinkrontgl;
    private Session session;
    private Button btLogout;
    private TBPetani tbPetani;
    private TBSinkronisasi tbSinkronisasi;
    private List<Petani> petaniList = new ArrayList<>();

    private String petaniId;
    private String petaniName;
    private String petUname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // CETAK BLUPRINT DBHANDLER
        tbPetani = new TBPetani(this);
        tbSinkronisasi = new TBSinkronisasi(this);
        session = new Session(this);

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
            Snackbar.make(koordinatorHomeLay, "Belum Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(koordinatorHomeLay, "Sudah Ada Data Sinkronisasi, Petani Nama = " + petaniName, Snackbar.LENGTH_LONG).show();
        }

        setPetaniStatus();
        //AmbilSemuaPetani();

    }

    private void setPetaniStatus() {
        tvNama.setText(petaniName);
        tvDesa.setText("Aik Bual");
        tvHariini.setText("Selasa, 23 Juni 2017");
        tvSinkrontgl.setText("Database Tanggal : 20 Juni 2017");
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
            default:

        }
    }

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
