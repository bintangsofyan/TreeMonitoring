package id.gaia.treemonitoring;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.gaia.treemonitoring.adapater.PersilRVAdapter;
import id.gaia.treemonitoring.database.TBDesa;
import id.gaia.treemonitoring.database.TBGapoktan;
import id.gaia.treemonitoring.database.TBKabkota;
import id.gaia.treemonitoring.database.TBKecamatan;
import id.gaia.treemonitoring.database.TBPersil;
import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.database.TBPohon;
import id.gaia.treemonitoring.database.TBPohonfoto;
import id.gaia.treemonitoring.database.TBProvinsi;
import id.gaia.treemonitoring.database.TBSurvey;
import id.gaia.treemonitoring.helper.Connection_Detector;
import id.gaia.treemonitoring.helper.RecyclerItemClickListener;
import id.gaia.treemonitoring.helper.Session;
import id.gaia.treemonitoring.model.Persil;
import id.gaia.treemonitoring.model.PostImage;
import id.gaia.treemonitoring.model.PostPutDelSurvey;
import id.gaia.treemonitoring.model.Survey;
import id.gaia.treemonitoring.rest.ApiClient;
import id.gaia.treemonitoring.rest.ApiClientUpload;
import id.gaia.treemonitoring.rest.ApiInterface;
import me.anwarshahriar.calligrapher.Calligrapher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPersil extends AppCompatActivity implements View.OnClickListener {
    List<Persil> listPersil = new ArrayList<>();
    private TextView tvfilter, tvlist, btnupload;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearLayout layoutbtnupload, liLayListPersil;
    private PersilRVAdapter adapter;
    private String petUname, petaniId, pohonId;
    private Session session;

    private TBPersil tbPersil;
    private TBPetani tbPetani;
    private TBGapoktan tbGapoktan;
    private TBProvinsi tbProvinsi;
    private TBKabkota tbKabkota;
    private TBKecamatan tbKecamatan;
    private TBDesa tbDesa;
    private TBPohon tbPohon;
    private TBPohonfoto tbPohonfoto;
    private TBSurvey tbSurvey;
    private List<Persil> persilList = new ArrayList<>();

    private String part_image;
    private ProgressDialog pdUpload;
    private Connection_Detector connection_detector;
    private ApiInterface pApiInterface, sApiInterface;
    public static ListPersil lp;

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
        tbSurvey = new TBSurvey(this);
        tbPohon = new TBPohon(this);
        tbPohonfoto = new TBPohonfoto(this);
        session = new Session(this);

        // SET FONT
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(ListPersil.this, "VarelaRound-Regular.ttf", true);

        // INIT
        tvfilter = (TextView) findViewById(R.id.tvfilter);
        tvlist = (TextView) findViewById(R.id.tvlist);
        layoutbtnupload = (LinearLayout) findViewById(R.id.layoutbtnupload);
        liLayListPersil = (LinearLayout) findViewById(R.id.liLayListPersil);
        btnupload = (TextView) findViewById(R.id.btnUpload);

        // INIT RECYCLE
        recyclerView = (RecyclerView) findViewById(R.id.rvlistpersil);
        recyclerView.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());

        lp = this;
        refresh();

    }

    public void refresh(){
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

        if(!tbSurvey.cekSurveywherePetani(petaniId)){
            layoutbtnupload.setVisibility(View.GONE);
        } else {
            layoutbtnupload.setVisibility(View.VISIBLE);
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
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
                this.finish();
                Intent in = new Intent(getApplicationContext(), FilterActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.btnUpload:
                goUpload();
                break;
            default:
        }
    }


    private void goUpload() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Pastikan jaringan internet pada perangkat anda dalam keadaan stabil ?");
        alertDialogBuilder.setPositiveButton("Upload Sekarang",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        prosesUpload();
                    }
                });

        alertDialogBuilder.setNegativeButton("Nanti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBuilder.setCancelable(true);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //private static final String UPLOAD_URL = "http://gaia.id/ams/uploadfromandroid/uploadother.php";
    private static final String UPLOAD_URL = "http://35.240.234.150/uploadfromandroid/uploadother.php";

    private void prosesUpload() {

        // MENAMPILKAN STATUS UPDATE PETANI BERDASARKAN STATUS INTERNET
        connection_detector = new Connection_Detector(this);
        if(!connection_detector.isConnected()){
            Snackbar.make(liLayListPersil, "Perangkat tidak memiliki koneksi internet. Silahkan nyalakan koneksi internet pada perangkat anda", Snackbar.LENGTH_SHORT).show();
        } else {
            pdUpload = new ProgressDialog(this);
            pdUpload.setCancelable(false);
            pdUpload.setMessage("Sedang proses upload data ke server...");
            pdUpload.show();

            // AMBIL SEMUA DATA SURVEY DARI PETANI ID
            final List<Survey> surveyList = tbSurvey.ambilSemuaSurveyWherePetani(String.valueOf(petaniId));
            //Log.d("BANYAK SURVEY", " = "+surveyList.size());
            if(surveyList.size() != 0) {


                int banyakdata = surveyList.size();
                for(int i=0; i<banyakdata; i++) {

                    final int dataKe = i;
                    pohonId = String.valueOf(surveyList.get(dataKe).getPohon_id());
                    String persilId;
                    if(tbPohon.cekPohonid(pohonId)) {
                        persilId = tbPohon.ambilDataPohonWherePohonid(pohonId).getPersil_id();
                    } else {
                        persilId = null;
                    }

                    // CEK ADA DATA FOTO POHON ATAU TIDAK
                    final String namefile;
                    String dir;
                    if(tbPohonfoto.cekFoto(String.valueOf(pohonId))) {
                        namefile = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_name();
                        dir = tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_dir();

                    } else {
                        namefile = null;
                        dir = null;
                        Log.d("UPLOAD IMAGE", "Tidak ada data foto dari survey ini");
                    } // END UPLOAD IMAGE

                    // INSERT DATA SURVEY TO DATABASE SERVER
                    String dbh = String.valueOf(surveyList.get(dataKe).getSurvey_dbh());
                    String lat = String.valueOf(surveyList.get(dataKe).getSurvey_latitude());
                    String lot = String.valueOf(surveyList.get(dataKe).getSurvey_longitude());
                    String ket = String.valueOf(surveyList.get(dataKe).getSurvey_keterangan());
                    String svyphnstatus = String.valueOf(surveyList.get(dataKe).getSurvey_pohonstatus());
                    String svyphnkategori = String.valueOf(surveyList.get(dataKe).getSurvey_pohonkategori());
                    String svyimg = namefile;
                    String tgl = String.valueOf(surveyList.get(dataKe).getSurvey_tanggal());
                    String tipe = String.valueOf(surveyList.get(dataKe).getSurvey_tipe());

                    sApiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<PostPutDelSurvey> postSurveyCall = sApiInterface.postSurvey(pohonId, petaniId, dbh, lat, lot, ket, svyphnstatus, svyphnkategori, svyimg, tgl, tipe);
                    postSurveyCall.enqueue(new Callback<PostPutDelSurvey>() {
                        @Override
                        public void onResponse(Call<PostPutDelSurvey> call, Response<PostPutDelSurvey> response) {
                            Log.d("RESPONSE SURVEY", " : "+response.body().toString());

                            // HAPUS DATA POHON DI SQILITE KARENA TELAH SELESAI DISURVEY
                            tbPohon.deletePohon(String.valueOf(surveyList.get(dataKe).getPohon_id()));

                            // DELETE SURVEY
                            tbSurvey.deleteSurvey(String.valueOf(surveyList.get(dataKe).getSurvey_id()));
                            pdUpload.dismiss();
                            ListPersil.lp.refresh();
                        }

                        @Override
                        public void onFailure(Call<PostPutDelSurvey> call, Throwable t) {
                            Log.d("FAILURE SURVEY", " : "+t.getMessage());
                            pdUpload.dismiss();
                        }
                    });

                    part_image = dir + "/" + namefile;
                    if (part_image != null) {
                        uploadImage(part_image, namefile, persilId, pohonId);
                        /*pdUpload.show();
                        File imageFile = new File(part_image);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), imageFile);
                        MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageupload", imageFile.getName(), requestBody);
                        MultipartBody.Part partDir = MultipartBody.Part.createFormData("dir", persilId);

                        pApiInterface = ApiClientUpload.getClient().create(ApiInterface.class);
                        Call<PostImage> postImageCall = pApiInterface.uploadImage(partImage, partDir);

                        postImageCall.enqueue(new Callback<PostImage>() {
                            @Override
                            public void onResponse(Call<PostImage> call, Response<PostImage> response) {
                                pdUpload.dismiss();

                                if(tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).size() != 0) {
                                    tbPohonfoto.deleteFoto(String.valueOf(tbPohonfoto.ambilSemuaFotoWherePohon(String.valueOf(pohonId)).get(0).getFoto_id()));
                                }
                                ListPersil.lp.refresh();
                                Log.d("UPLOAD IMAGE", "ON RESPONS " + response.body().toString());

                            }

                            @Override
                            public void onFailure(Call<PostImage> call, Throwable t) {
                                Log.d("UPLOAD IMAGE", "ON FAILURE " + t.getMessage());
                                ListPersil.lp.refresh();
                                pdUpload.dismiss();
                            }
                        });*/
                    }
                }
                pdUpload.dismiss();

            }
        }
    }

    private void uploadImage(String part, String name, String persilId, String pohonId){
        name = name;
        String path = part;
        String makedir = persilId;
        pohonId = pohonId;

        try{
            //pdUpload.show();
            String uploadid = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadid, UPLOAD_URL)
                    .addFileToUpload(path, "image")
                    .addParameter("name", name)
                    .addParameter("dir", makedir)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(5)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .startUpload();

            deletePohonfoto(pohonId);
            //pdUpload.dismiss();
        }
        catch (Exception e){
            deletePohonfoto(pohonId);
            //pdUpload.dismiss();
        }


    }

    private void deletePohonfoto(String pohonId){

        if(tbPohonfoto.ambilSemuaFotoWherePohon(pohonId).size() != 0) {

            tbPohonfoto.deleteFoto(String.valueOf(tbPohonfoto.ambilSemuaFotoWherePohon(pohonId).get(0).getFoto_id()));

        }
        pdUpload.dismiss();

        ListPersil.lp.refresh();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
