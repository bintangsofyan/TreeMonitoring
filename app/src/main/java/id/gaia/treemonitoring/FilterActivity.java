package id.gaia.treemonitoring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.gaia.treemonitoring.adapater.SpinnerDesa;
import id.gaia.treemonitoring.adapater.SpinnerGapoktan;
import id.gaia.treemonitoring.adapater.SpinnerKabkota;
import id.gaia.treemonitoring.adapater.SpinnerKecamatan;
import id.gaia.treemonitoring.adapater.SpinnerProvinsi;
import id.gaia.treemonitoring.database.TBDesa;
import id.gaia.treemonitoring.database.TBGapoktan;
import id.gaia.treemonitoring.database.TBKabkota;
import id.gaia.treemonitoring.database.TBKecamatan;
import id.gaia.treemonitoring.database.TBProvinsi;
import id.gaia.treemonitoring.model.Desa;
import id.gaia.treemonitoring.model.Gapoktan;
import id.gaia.treemonitoring.model.Kabkota;
import id.gaia.treemonitoring.model.Kecamatan;
import id.gaia.treemonitoring.model.Provinsi;
import me.anwarshahriar.calligrapher.Calligrapher;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner spinnerProvinsi, spinnerKabkota, spinnerKecamatan, spinnerDesa, spinnerGapoktan;
    private Button btTampil;
    private int gapoktanSelectedId;

    private List<Provinsi> provinsiList = new ArrayList<>();

    // Variable Database
    public TBProvinsi tbProvinsi;
    public TBKabkota tbKabkota;
    public TBKecamatan tbKecamatan;
    public TBDesa tbDesa;
    public TBGapoktan tbGapoktan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // SET FONT
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(FilterActivity.this, "VarelaRound-Regular.ttf", true);

        // BLUEPRINT
        tbProvinsi = new TBProvinsi(this);
        tbKabkota = new TBKabkota(this);
        tbKecamatan = new TBKecamatan(this);
        tbDesa = new TBDesa(this);
        tbGapoktan = new TBGapoktan(this);

        // INITIALISASI
        spinnerProvinsi = (Spinner) findViewById(R.id.spinprovinsi);
        spinnerKabkota = (Spinner) findViewById(R.id.spinkabkota);
        spinnerKecamatan = (Spinner) findViewById(R.id.spinkecamatan);
        spinnerDesa = (Spinner) findViewById(R.id.spindesa);
        spinnerGapoktan = (Spinner) findViewById(R.id.spingapoktan);
        btTampil = (Button) findViewById(R.id.btnTampil);

        final List<Provinsi> listProvinsi = tbProvinsi.ambilSemuaProvinsi();

        // CREATE SPINNER PROVINSI
        SpinnerProvinsi spinnerProAdapter = new SpinnerProvinsi(FilterActivity.this, R.layout.spinner_filter, listProvinsi);
        spinnerProvinsi.setAdapter(spinnerProAdapter);

        spinnerProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(FilterActivity.this, listProvinsi.get(i).getProvinsi_name() + " - " + listProvinsi.get(i).getProvinsi_id(), Toast.LENGTH_SHORT).show();
                showSpinKabKota(String.valueOf(listProvinsi.get(i).getProvinsi_id()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void showSpinKabKota(String provinsiId){
        final List<Kabkota> listKabkota = tbKabkota.ambilSemuaKabkotaWhereProvinsi(provinsiId);
        SpinnerKabkota spinnerKkAdapter = new SpinnerKabkota(FilterActivity.this, R.layout.spinner_filter, listKabkota);
        spinnerKabkota.setAdapter(spinnerKkAdapter);
        if(listKabkota.size() == 0){
            showSpinKecamatan("0");
        }
        spinnerKabkota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showSpinKecamatan(String.valueOf(listKabkota.get(i).getKabkota_id()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showSpinKecamatan(String kabkotaId){
        final List<Kecamatan> listKecamatan = tbKecamatan.ambilSemuaKecamatanWhereKabkota(kabkotaId);
        SpinnerKecamatan spinnerKcAdapter = new SpinnerKecamatan(FilterActivity.this, R.layout.spinner_filter, listKecamatan);
        spinnerKecamatan.setAdapter(spinnerKcAdapter);
        //Log.d("SPINNER ", "Adapater : " + spinnerKcAdapter + ", List : " + listKecamatan.size());
        if(listKecamatan.size() == 0){
            showSpinDesa("0");
        }

        spinnerKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showSpinDesa(String.valueOf(listKecamatan.get(i).getKecamatan_id()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showSpinDesa(String kecamatanId){
        final List<Desa> listDesa = tbDesa.ambilSemuaDesaWhereKecamatan(kecamatanId);

        SpinnerDesa spinnerDsAdapter = new SpinnerDesa(FilterActivity.this, R.layout.spinner_filter, listDesa);
        spinnerDesa.setAdapter(spinnerDsAdapter);

        if(listDesa.size() == 0){
            showSpinGapoktan("0");
        }

        spinnerDesa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showSpinGapoktan(String.valueOf(listDesa.get(i).getDesa_id()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void showSpinGapoktan(String desaId){
        final List<Gapoktan> listGapoktan = tbGapoktan.ambilSemuaGapoktanWhereDesa(desaId);

        if(listGapoktan.size() == 0) {
            listGapoktan.add(new Gapoktan(0, 0, null));
        }
        SpinnerGapoktan spinnerGpAdapter = new SpinnerGapoktan(FilterActivity.this, R.layout.spinner_filter, listGapoktan);
        spinnerGapoktan.setAdapter(spinnerGpAdapter);

        spinnerGapoktan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gapoktanSelectedId = listGapoktan.get(i).getGapoktan_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.goTohome:
                Intent in = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(in);
                break;
            case R.id.btnTampil:
                Intent intent = new Intent(getApplicationContext(), ListPersil.class);
                intent.putExtra("gapoktanId", gapoktanSelectedId);
                startActivity(intent);
                break;
            default:
        }
    }

    public void onBackPressed() {

    }

}
