package id.gaia.treemonitoring;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import id.gaia.treemonitoring.model.Pohon;
import id.gaia.treemonitoring.model.Survey;
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
    private TextView tvkodepohon, tvnamepohon, tvpersil, tvgapoktan, tvdesa, tvtglnextupdate, tveditpohon, tvurlfoto, titletglnextupdate;
    private EditText etlatitude, etlongitude, etdiameter, etketerangan, etkeliling;
    private RadioGroup rgStatuspohon, rgKeadaanpohon, rgTipesurvey;
    private RadioButton rbonExist, rbonBaru, rbonSulam, rbonHidup, rbonMati, rbonTanam, rbonPantau;
    private ImageView imgstatusupdate, imgfotopohon;
    private Session session;
    private String petUname, petaniId, surveydbh, surveyket, surveylatitude, surveylongitude;
    private int aktifitasId, aktifitasDo, keliling, statusphnId, statusPohon, keadaanId, keadaanDo;

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
            keliling = bundle.getInt("kll");
            surveydbh = bundle.getString("dbh");
            statusPohon = bundle.getInt("statphn");
            keadaanDo = bundle.getInt("katphn");
            aktifitasDo = bundle.getInt("tipsurvey");
            surveyket = bundle.getString("ket");
            surveylatitude = bundle.getString("lat");
            surveylongitude = bundle.getString("lon");
        } else {
            persilId = null;
            gapoktanId = 0;
            pohonId = 0;
            keliling = 0;
            surveydbh = "0";
            statusPohon = 0;
            keadaanDo = 0;
            aktifitasDo = 0;
            surveyket = null;
            surveylatitude = null;
            surveylongitude = null;
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
                   String dbh = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_dbh();
                   String latitude = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_latitude();
                   String longitude = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_longitude();
                   int phnstatus = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_pohonstatus();
                   int phnkategori = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_pohonkategori();
                   int tipesurvey = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_tipe();

                   etdiameter.setText(String.valueOf(dbh));
                   etketerangan.setText(ket);
                   etlatitude.setText(latitude);
                   etlongitude.setText(longitude);
                   switch (phnstatus){
                       case 0:
                           rbonExist.setChecked(true);
                           rbonBaru.setChecked(false);
                           rbonSulam.setChecked(false);
                           break;
                       case 1:
                           rbonExist.setChecked(false);
                           rbonBaru.setChecked(true);
                           rbonSulam.setChecked(false);
                           break;
                       case 2:
                           rbonExist.setChecked(false);
                           rbonBaru.setChecked(false);
                           rbonSulam.setChecked(true);
                           break;
                       default:
                   }

                   if(phnkategori == 1){
                       rbonHidup.setChecked(true);
                       rbonMati.setChecked(false);
                   } else {
                       rbonHidup.setChecked(false);
                       rbonMati.setChecked(true);
                   }

                   if(tipesurvey == 0) {
                       rbonTanam.setChecked(true);
                       rbonPantau.setChecked(false);
                   } else if(tipesurvey == 1) {
                       rbonTanam.setChecked(false);
                       rbonPantau.setChecked(true);
                   }
                   tveditpohon.setText(String.valueOf(tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_id()));
                   //Toast.makeText(this, "Aktifitas "+tipesurvey+", Status "+phnstatus+", Keadaan "+phnkategori, Toast.LENGTH_SHORT).show();

                   // JIKA BELUM DIUPDATE MAKA TANGGAL TAMPIL TANGGAL NEXT UPDATE, JIKA SUDAH DONE MAKA TAMPILKAN TANGGAL DONE
                   if(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_statusupdate() == 1) {
                       tvtglnextupdate.setText(tbPohon.ambilDataPohonWherePohonid(String.valueOf(pohonId)).getPohon_nextupdate());
                       titletglnextupdate.setText("Survey Selanjutnya, ");
                   } else {
                       tvtglnextupdate.setText(tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_tanggal());
                       titletglnextupdate.setText("Selesai, ");
                   }
               } else {
                   etkeliling.setText(String.valueOf(keliling));
                   etdiameter.setText(String.valueOf(surveydbh));
                   etketerangan.setText(surveyket);
                   etlatitude.setText(surveylatitude);
                   etlongitude.setText(surveylongitude);
                   switch (statusPohon){
                       case 0:
                           rbonExist.setChecked(true);
                           rbonBaru.setChecked(false);
                           rbonSulam.setChecked(false);
                           break;
                       case 1:
                           rbonExist.setChecked(false);
                           rbonBaru.setChecked(true);
                           rbonSulam.setChecked(false);
                           break;
                       case 2:
                           rbonExist.setChecked(false);
                           rbonBaru.setChecked(false);
                           rbonSulam.setChecked(true);
                           break;
                       default:
                   }

                   if(keadaanDo == 1){
                       rbonHidup.setChecked(true);
                       rbonMati.setChecked(false);
                   } else {
                       rbonHidup.setChecked(false);
                       rbonMati.setChecked(true);
                   }

                   if(aktifitasDo == 0) {
                       rbonTanam.setChecked(true);
                       rbonPantau.setChecked(false);
                   } else if(aktifitasDo == 1) {
                       rbonTanam.setChecked(false);
                       rbonPantau.setChecked(true);
                   }
                   tveditpohon.setText("0");
               }

               // CEK SUDAH ADA DATA POHON ATAU BELUM
               if(tbPohonfoto.cekFoto(String.valueOf(pohonId))){
                   String namefile = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_name();
                   String dir = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_dir();

                   String myJpgPath = dir+"/"+namefile;

                   BitmapFactory.Options options = new BitmapFactory.Options();
                   options.inSampleSize = 4;
                   Bitmap bm = BitmapFactory.decodeFile(myJpgPath, options);
                   imgfotopohon.setImageBitmap(bm);

                   //tvurlfoto.setText(dir+" | "+namefile);
                   //tvurlfoto.setVisibility(View.VISIBLE);
               } else {
                   //tvurlfoto.setText("belum ada data foto");
                   //tvurlfoto.setVisibility(View.VISIBLE);
                   imgfotopohon.setImageResource(R.drawable.ic_a_pohon_100dp);
               }

               etkeliling.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void afterTextChanged(Editable editable) {
                       if(!editable.toString().equals("")) {
                           hitungDBh(editable.toString());
                       } else {
                           hitungDBh("0");
                       }

                   }
               });

           }
        }
    }

    private void hitungDBh(String kll){
        DecimalFormat precision = new DecimalFormat("0.00");
        int keliling;
        if(kll != null) {
            keliling = Integer.parseInt(kll);
        } else {
            keliling = 0;
        }
        /*if(kll == null) {
            Toast.makeText(this, "kosong Kll " + kll, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ada Kll " + kll, Toast.LENGTH_SHORT).show();
        }*/

        double nilaidbh;
        if(keliling != 0) {
            double phi = 3.14;
            nilaidbh = keliling / phi;
        } else {
            nilaidbh = 0;
        }
        etdiameter.setText(precision.format(nilaidbh));

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
        etkeliling = (EditText) findViewById(R.id.etkeliling);
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
        tvtglnextupdate = (TextView) findViewById(R.id.tvtglnextupdate);
        titletglnextupdate = (TextView) findViewById(R.id.titletglnextupdate);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSimpan:
                simpanData();
                break;
            case R.id.goTopohon:
                this.finish();
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

            case R.id.onTanam:
                rbonExist.setChecked(false);
                rbonBaru.setChecked(true);
                rbonSulam.setChecked(false);

                rbonHidup.setChecked(true);
                rbonMati.setChecked(false);

                //Toast.makeText(this, "Penanaman onClick", Toast.LENGTH_SHORT).show();;
                break;

            case R.id.onPantau:
                rbonHidup.setChecked(true);
                rbonMati.setChecked(false);

                //Toast.makeText(this, "Pemantauan onClick", Toast.LENGTH_SHORT).show();;
                break;
            default:
        }
    }

    private void ambilFoto() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);

        keliling = Integer.parseInt(etkeliling.getText().toString().trim());
        surveydbh = etdiameter.getText().toString().trim();
        surveyket = etketerangan.getText().toString().trim();
        surveylatitude = etlatitude.getText().toString().trim();
        surveylongitude = etlongitude.getText().toString().trim();

        aktifitasId = rgTipesurvey.getCheckedRadioButtonId();
        if(aktifitasId == rbonTanam.getId()){
            aktifitasDo = 0; // TANAM
        } else if(aktifitasId == rbonPantau.getId()) {
            aktifitasDo = 1; // PANTAU
        } else {
            aktifitasDo = 2; // TIDAK DIPILIH
        }

        statusphnId = rgStatuspohon.getCheckedRadioButtonId();
        if(statusphnId == rbonExist.getId()){
            statusPohon = 0; // EXISTING
        } else if(statusphnId == rbonBaru.getId()) {
            statusPohon = 1; // POHON BARU
        } else if(statusphnId == rbonSulam.getId()){
            statusPohon = 2; // POHON DISULAM
        } else {
            statusPohon = 3; // TIDAK ADA PILIHAN
        }

        keadaanId = rgKeadaanpohon.getCheckedRadioButtonId();
        if(keadaanId == rbonHidup.getId()){
            keadaanDo = 1; // HIDUP
        } else if(aktifitasId == rbonMati.getId()) {
            keadaanDo = 0; // MATI
        } else {
            keadaanDo = 2; // TIDAK DIPILIH
        }

        intent.putExtra("kll", keliling);
        intent.putExtra("dbh", surveydbh);
        intent.putExtra("ket", surveyket);
        intent.putExtra("lat", surveylatitude);
        intent.putExtra("lon", surveylongitude);
        intent.putExtra("statphn", statusPohon);
        intent.putExtra("katphn", keadaanDo);
        intent.putExtra("tipsurvey", aktifitasDo);
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

            etlatitude.setText(String.valueOf(lat));
            etlongitude.setText(String.valueOf(lon));

            //Toast.makeText(this, "Lat "+lat+", Log "+lon, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanData() {
        // CEK STATUS EDIT SURVEY (TEXTVIEW.tvEditpohon). JIKA NILAI != 0 MAKA BERARTI UPDATE DATA, JIKA == 0 BERARTI TAMBAH DATA SURVEY BARU
        surveydbh = etdiameter.getText().toString().trim();
        surveyket = etketerangan.getText().toString().trim();
        surveylatitude = etlatitude.getText().toString().trim();
        surveylongitude = etlongitude.getText().toString().trim();

        aktifitasId = rgTipesurvey.getCheckedRadioButtonId();
        if(aktifitasId == rbonTanam.getId()){
            aktifitasDo = 0; // TANAM
        } else if(aktifitasId == rbonPantau.getId()) {
            aktifitasDo = 1; // PANTAU
        } else {
            aktifitasDo = 2; // TIDAK DIPILIH
        }

        statusphnId = rgStatuspohon.getCheckedRadioButtonId();
        if(statusphnId == rbonExist.getId()){
            statusPohon = 0; // EXISTING
        } else if(statusphnId == rbonBaru.getId()) {
            statusPohon = 1; // POHON BARU
        } else if(statusphnId == rbonSulam.getId()){
            statusPohon = 2; // POHON DISULAM
        } else {
            statusPohon = 3; // TIDAK ADA PILIHAN
        }

        keadaanId = rgKeadaanpohon.getCheckedRadioButtonId();
        if(keadaanId == rbonHidup.getId()){
            keadaanDo = 1; // HIDUP
        } else if(keadaanId == rbonMati.getId()) {
            keadaanDo = 0; // MATI
        } else {
            keadaanDo = 2; // TIDAK DIPILIH
        }

        final String surveyTanggal = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // CEK MANDATORY FORM


            if(aktifitasDo == 2){
                Toast.makeText(this, "Pilih aktifitas yang dilakukan", Toast.LENGTH_SHORT).show();
                rgTipesurvey.setFocusable(true);
            } else {
                if (statusPohon == 3) {
                    Toast.makeText(this, "Pilih status pohon", Toast.LENGTH_SHORT).show();
                    rgStatuspohon.setFocusable(true);
                } else {
                    if (keadaanDo == 2) {
                        Toast.makeText(this, "Pilih keadaan pohon", Toast.LENGTH_SHORT).show();
                        rgKeadaanpohon.setFocusable(true);
                    } else {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setMessage("Simpan data aktifitas pada pohon ini ?");
                        alertDialogBuilder.setPositiveButton("Ya",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        if (!tbSurvey.cekSurvey(String.valueOf(pohonId))) {
                                            tbSurvey.tambahSurvey(new Survey(pohonId, Integer.parseInt(petaniId), surveydbh, surveyket, surveylatitude, surveylongitude, statusPohon, keadaanDo, surveyTanggal, aktifitasDo));

                                            // SETELAH DI INPUT SURVEY BARU, MAKA STATUS UPDATE DI TABLE POHON DIRUBAH MENJADI 2 BERARTI DONE, 1 BERARTI NEEDUPDATE
                                            if (tbPohon.cekPohonid(String.valueOf(pohonId))) {
                                                tbPohon.updateStatusUpdate(new Pohon(2), String.valueOf(pohonId));
                                            }

                                            Toast.makeText(FormPohon.this, "Insert data survey", Toast.LENGTH_SHORT).show();

                                        } else {
                                            int surveyId = tbSurvey.ambilSemuaSurveyWherePohon(String.valueOf(pohonId)).get(0).getSurvey_id();
                                            tbSurvey.updateSurvey(new Survey(pohonId, Integer.parseInt(petaniId), surveydbh, surveyket, surveylatitude, surveylongitude, statusPohon, keadaanDo, surveyTanggal, aktifitasDo), String.valueOf(surveyId));
                                            Toast.makeText(FormPohon.this, "Update data survey", Toast.LENGTH_SHORT).show();
                                        }

                                        onBack();

                                    }
                                });

                        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialogBuilder.setCancelable(true);

                                onBack();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }
            }

    }

    public void onBack(){
        Intent intent = new Intent(getApplicationContext(), ListPohon.class);
        intent.putExtra("gapoktanId", gapoktanId);
        intent.putExtra("persilId", persilId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();

    }

    public void onBackPressed() {
        simpanData();
    }

}
