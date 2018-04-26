package id.gaia.treemonitoring;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import id.gaia.treemonitoring.database.DBHandler;
import id.gaia.treemonitoring.database.TBDesa;
import id.gaia.treemonitoring.database.TBGapoktan;
import id.gaia.treemonitoring.database.TBPersil;
import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBPohon;
import id.gaia.treemonitoring.database.TBPohonfoto;
import id.gaia.treemonitoring.database.TBSurvey;
import id.gaia.treemonitoring.helper.GPStracker;
import id.gaia.treemonitoring.helper.Session;
import me.anwarshahriar.calligrapher.Calligrapher;

public class FormPohon extends AppCompatActivity implements View.OnClickListener {
    private String persilId;
    private int pohonId;
    private int gapoktanId;
    private DBHandler dbHandler;
    private TBPohon tbPohon;
    private TBPetani tbPetani;
    private TBPersil tbPersil;
    private TBDesa tbDesa;
    private TBGapoktan tbGapoktan;
    private TBPohonfoto tbPohonfoto;
    private TBSurvey tbSurvey;
    private TextView tvkodepohon, tvnamepohon, tvpersil, tvgapoktan, tvdesa, tvtglnextupdate, tveditpohon, tvurlfoto;
    private EditText etlatitude, etlongitude, etdiameter, etketerangan;
    private RadioGroup rgStatuspohon, rgKeadaanpohon, rgTipesurvey;
    private RadioButton rbonExist, rbonBaru, rbonSulam, rbonHidup, rbonMati, rbonTanam, rbonPantau;
    private ImageView imgstatusupdate, imgfotopohon;
    private Session session;
    private String petUname, petaniId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pohon);

        // SET FONT
        //Calligrapher calligrapher = new Calligrapher(this);
        //calligrapher.setFont(FormPohon.this, "VarelaRound-Regular.ttf", true);

        dbHandler = new DBHandler(this);
        tbPetani = new TBPetani(this);
        tbPohon = new TBPohon(this);
        tbPersil = new TBPersil(this);
        tbDesa = new TBDesa(this);
        tbGapoktan = new TBGapoktan(this);
        tbPohonfoto = new TBPohonfoto(this);
        tbSurvey = new TBSurvey(this);
        session = new Session(this);

        iniComponent();
        ambilGPS();

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            pohonId = bundle.getInt("pohonId");
            persilId = bundle.getString("persilId");
            gapoktanId = bundle.getInt("gapoktanId");
        } else {
            persilId = null;
            gapoktanId = 0;
            pohonId = 0;
        }

        if(pohonId == 0){
            Intent intent = new Intent(getApplicationContext(), ListPohon.class);
            intent.putExtra("gapoktanId", gapoktanId);
            intent.putExtra("persilId", persilId);
            Toast.makeText(this, "Tidak ada Pohon Id", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else {
           if(!tbPohon.cekPohonid(String.valueOf(pohonId))){
               Intent intent = new Intent(getApplicationContext(), ListPohon.class);
               intent.putExtra("gapoktanId", gapoktanId);
               intent.putExtra("persilId", persilId);
               Toast.makeText(this, "Data Pohon tidak ditemukan", Toast.LENGTH_SHORT).show();
               startActivity(intent);
           } else {

               tvkodepohon.setText(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_kode());
               tvnamepohon.setText(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_namalokal());
               tvtglnextupdate.setText(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_nextupdate());
               tvpersil.setText(persilId);
               String gpname;
               String dsname;
               if(tbGapoktan.cekGapoktanid(String.valueOf(gapoktanId))){
                  gpname = tbGapoktan.ambilDataGapoktan(String.valueOf(gapoktanId)).getGapoktan_name();
                  int dsid = tbGapoktan.ambilDataGapoktan(String.valueOf(gapoktanId)).getDesa_id();
                  dsname = tbDesa.ambilDataDesa(String.valueOf(dsid)).getDesa_name();
               } else {
                   gpname = "Gapoktan";
                   dsname = "Desa";
               }
               tvdesa.setText(dsname);
               tvgapoktan.setText(gpname);

               if(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_statusupdate() == 1){
                   imgstatusupdate.setImageResource(R.drawable.ic_status_need_24dp);
               } else {
                   imgstatusupdate.setImageResource(R.drawable.ic_status_done_24dp);
               }

               // CEK SUDAH ADA DATA SURVEY ATAU BELUM
               if(tbSurvey.cekSurvey(String.valueOf(pohonId))) {
                   String ket = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_keterangan();
                   int dbh = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_dbh();
                   String latitude = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_latitude();
                   String longitude = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_longitude();
                   int phnstatus = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_pohonstatus();
                   int phnkategori = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_pohonkategori();
                   int tipesurvey = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_tipe();

                   etdiameter.setText(dbh);
                   etketerangan.setText(ket);
                   etlatitude.setText(latitude);
                   etlongitude.setText(longitude);
                   switch (phnstatus){
                       case 0:
                           rbonExist.setSelected(true);
                           rbonBaru.setSelected(false);
                           rbonSulam.setSelected(false);
                           break;
                       case 1:
                           rbonExist.setSelected(false);
                           rbonBaru.setSelected(true);
                           rbonSulam.setSelected(false);
                           break;
                       case 2:
                           rbonExist.setSelected(false);
                           rbonBaru.setSelected(false);
                           rbonSulam.setSelected(true);
                           break;
                       default:
                   }

                   if(phnkategori == 1){
                       rbonHidup.setSelected(true);
                       rbonMati.setSelected(false);
                   } else {
                       rbonHidup.setSelected(false);
                       rbonMati.setSelected(true);
                   }

                   if(tipesurvey == 0) {
                       rbonTanam.setSelected(true);
                       rbonPantau.setSelected(false);
                   } else if(tipesurvey == 1) {
                       rbonTanam.setSelected(false);
                       rbonPantau.setSelected(true);
                   }
                   tveditpohon.setText(tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_id());
               } else {
                   tveditpohon.setText("0");
               }

               // CEK SUDAH ADA DATA POHON ATAU BELUM
               if(tbPohonfoto.cekFoto(String.valueOf(pohonId))){
                   String namefile = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_name();
                   String dir = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_dir();

                   tvurlfoto.setText(dir+" | "+namefile);
                   tvurlfoto.setVisibility(View.VISIBLE);
               } else {
                   tvurlfoto.setText("belum ada data foto");
                   tvurlfoto.setVisibility(View.VISIBLE);
                   imgfotopohon.setImageResource(R.drawable.ic_a_pohon_100dp);
               }

           }
        }
    }

    public void iniComponent(){
        tvkodepohon = (TextView) findViewById(R.id.tvkodepohon);
        tvnamepohon = (TextView) findViewById(R.id.tvnamepohon);
        tvpersil = (TextView) findViewById(R.id.tvpersil);
        tvgapoktan = (TextView)findViewById(R.id.tvgapoktan);
        tvdesa = (TextView)findViewById(R.id.tvdesa);
        imgstatusupdate = (ImageView) findViewById(R.id.imgstatusupdate);
        imgfotopohon = (ImageView) findViewById(R.id.imgfotopohon);
        tvtglnextupdate = (TextView) findViewById(R.id.tvtglnextupdate);
        etlatitude = (EditText) findViewById(R.id.etlatitude);
        etlongitude = (EditText) findViewById(R.id.etlongitude);
        etketerangan = (EditText) findViewById(R.id.etketerangan);
        etdiameter = (EditText) findViewById(R.id.etdiameter);
        rgKeadaanpohon = (RadioGroup) findViewById(R.id.rgKeadaanpohon);
        rgStatuspohon = (RadioGroup) findViewById(R.id.rgStatuspohon);
        rgTipesurvey = (RadioGroup) findViewById(R.id.rgTipesurvey);
        rbonBaru = (RadioButton) findViewById(R.id.onBaru);
        rbonExist = (RadioButton) findViewById(R.id.onExist);
        rbonSulam = (RadioButton) findViewById(R.id.onSulam);
        rbonHidup = (RadioButton) findViewById(R.id.onHidup);
        rbonMati = (RadioButton) findViewById(R.id.onMati);
        rbonTanam = (RadioButton) findViewById(R.id.onTanam);
        rbonPantau = (RadioButton) findViewById(R.id.onPantau);
        tveditpohon = (TextView) findViewById(R.id.tveditpohon);
        tvurlfoto = (TextView) findViewById(R.id.tvurlfoto);
    }

    /* //////////////////////////////////////// MENGAMBIL NILAI RADIO BUTTON
        int selectedId = rgJawaban1.getCheckedRadioButtonId(); // RADIOGROUP

                if (selectedId == rbJawaTimur.getId()){ // RADIO BUTTON
        showToast("Jawaban Kamu : " + rbJawaTimur.getText().toString());
            } else if (selectedId == rbJawaBarat.getId()){ // RADIO BUTTO
        showToast("Jawaban Kamu : " + rbJawaBarat.getText().toString());
    } else if (selectedId == rbJawaTengah.getId()){
        showToast("Jawaban Kamu : " + rbJawaTengah.getText().toString());
    } else {
        showToast("Kamu Belum Memilih Jawaban");
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSimpan:
                Intent in = new Intent(getApplicationContext(), FormPohon.class);
                startActivity(in);
                simpanData();

                break;
            case R.id.goTopohon:
                Intent intent = new Intent(getApplicationContext(), ListPohon.class);
                intent.putExtra("gapoktanId", gapoktanId);
                intent.putExtra("persilId", persilId);
                startActivity(intent);
                break;
            case R.id.btnGetgps:
                ambilGPS();
                break;

            case R.id.btnGetfoto:
                ambilFoto();
                break;
            default:
        }
    }

    private void ambilFoto() {
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra("gapoktanId", gapoktanId);
        intent.putExtra("persilId", persilId);
        intent.putExtra("pohonId", pohonId);
        startActivity(intent);
    }

    private void ambilGPS() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        
        GPStracker gpStracker = new GPStracker(getApplicationContext());
        Location l = gpStracker.getLocation();
        if(l != null){
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            if(!tbSurvey.cekSurvey(String.valueOf(pohonId))) {
                etlatitude.setText(String.valueOf(lat));
                etlongitude.setText(String.valueOf(lon));

            } else {
                etlatitude.setText(String.valueOf(tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_latitude()));
                etlongitude.setText(String.valueOf(tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_latitude()));
                //Toast.makeText(this, "Lat "+lat+", Log "+lon, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanData() {
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ListPohon.class);
        intent.putExtra("gapoktanId", gapoktanId);
        intent.putExtra("persilId", persilId);
        startActivity(intent);
    }

}
