package id.gaia.treemonitoring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.gaia.treemonitoring.adapater.PersilRVAdapter;
import id.gaia.treemonitoring.database.TBDesa;
import id.gaia.treemonitoring.database.TBGapoktan;
import id.gaia.treemonitoring.database.TBKabkota;
import id.gaia.treemonitoring.database.TBKecamatan;
import id.gaia.treemonitoring.database.TBPersil;
import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBProvinsi;
import id.gaia.treemonitoring.helper.RecyclerItemClickListener;
import id.gaia.treemonitoring.helper.Session;
import id.gaia.treemonitoring.model.Persil;
import me.anwarshahriar.calligrapher.Calligrapher;

public class ListPersil extends AppCompatActivity implements View.OnClickListener {
    List<Persil> listPersil = new ArrayList<>();
    private TextView tvfilter, tvlist;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private PersilRVAdapter adapter;
    private String petUname, petaniId;
    private Session session;

    private TBPersil tbPersil;
    private TBPetani tbPetani;
    private TBGapoktan tbGapoktan;
    private TBProvinsi tbProvinsi;
    private TBKabkota tbKabkota;
    private TBKecamatan tbKecamatan;
    private TBDesa tbDesa;
    private List<Persil> persilList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_persil);

        // CETAK BLUEPRITN
        tbPersil = new TBPersil(this);
        tbGapoktan = new TBGapoktan(this);
        tbPetani = new TBPetani(this);
        tbProvinsi = new TBProvinsi(this);
        tbKabkota = new TBKabkota(this);
        tbKecamatan = new TBKecamatan(this);
        tbDesa = new TBDesa(this);
        session = new Session(this);

        // SET FONT
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(ListPersil.this, "VarelaRound-Regular.ttf", true);

        // INIT
        tvfilter = (TextView) findViewById(R.id.tvfilter);
        tvlist = (TextView) findViewById(R.id.tvlist);

        // INIT RECYCLE
        recyclerView = (RecyclerView) findViewById(R.id.rvlistpersil);
        recyclerView.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());

        // GET DATA GAPOKTANID
        final int gapoktanId = getIntent().getIntExtra("gapoktanId", 0);
        persilList = tbPersil.ambilSemuaPersilWherePetGap(String.valueOf(gapoktanId), petaniId);
        if(gapoktanId != 0) {
            String gapoktanName = tbGapoktan.ambilDataGapoktan(String.valueOf(gapoktanId)).getGapoktan_name();
            int desaId = tbGapoktan.ambilDataGapoktan(String.valueOf(gapoktanId)).getDesa_id();
            String desaName = tbDesa.ambilDataDesa(String.valueOf(desaId)).getDesa_name();
            int kecId = tbDesa.ambilDataDesa(String.valueOf(desaId)).getKecamatan_id();
            String kecName = tbKecamatan.ambilDataKecamatan(String.valueOf(kecId)).getKecamatan_name();
            int kabkotId = tbKecamatan.ambilDataKecamatan(String.valueOf(kecId)).getKabkota_id();
            String kabkotName = tbKabkota.ambilDataKabkota(String.valueOf(kabkotId)).getKabkota_name();
            int provId = tbKabkota.ambilDataKabkota(String.valueOf(kabkotId)).getProvinsi_id();
            String provName = tbProvinsi.ambilDataProvinsi(String.valueOf(provId)).getProvinsi_name();

            tvfilter.setText("Filter : " + provName + " | " + kabkotName + " | " + kecName + " | " + desaName + " | " + gapoktanName + " Total Persil : "  + persilList.size());
        } else {
            tvfilter.setText("Filter Error : Data GAPOKTAN tidak ditemukan");
        }

        // SET RECYCLE
        adapter = new PersilRVAdapter(persilList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(persilList.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            tvlist.setText("Tidak ada persil");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvlist.setText("");
            tvlist.setVisibility(View.INVISIBLE);

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            // TODO Handle item click
                            Persil persil = persilList.get(position);
                            String persilId = persil.getPersil_id();

                            Intent intent = new Intent(getApplicationContext(), ListPohon.class);
                            intent.putExtra("gapoktanId", gapoktanId);
                            intent.putExtra("persilId", persilId);
                            startActivity(intent);

                            //Toast.makeText(ListPersil.this, "Klik di " + persilId, Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.goTofilter:
                Intent in = new Intent(getApplicationContext(), FilterActivity.class);
                startActivity(in);
                break;
            default:
        }
    }

    public void onBackPressed() {

    }
}
