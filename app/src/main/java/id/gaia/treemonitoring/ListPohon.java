package id.gaia.treemonitoring;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.gaia.treemonitoring.adapater.PohonLoadAdapter;
import id.gaia.treemonitoring.adapater.PohonRVAdapter;
import id.gaia.treemonitoring.database.TBPersil;
import id.gaia.treemonitoring.database.TBPersilpemilik;
import id.gaia.treemonitoring.database.TBPohon;
import id.gaia.treemonitoring.helper.ILoadMore;
import id.gaia.treemonitoring.helper.RecyclerItemClickListener;
import id.gaia.treemonitoring.model.Persilpemilik;
import id.gaia.treemonitoring.model.Pohon;
import me.anwarshahriar.calligrapher.Calligrapher;

public class ListPohon extends AppCompatActivity implements View.OnClickListener {
    private TextView tvPersilSelect, tvListpohon;
    private EditText etsearch;
    private RecyclerView rvPohon;
    private String persilId;
    private int gapoktanId;
    private LinearLayoutManager layoutManager;

    private TBPersilpemilik tbPersilpemilik;
    private TBPersil tbPersil;
    private TBPohon tbPohon;
    private PohonRVAdapter adapter;
    private PohonLoadAdapter pohonLoadAdapter;
    private List<Pohon> pohonList = new ArrayList<>();
    private List<Persilpemilik> persilPemilik = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pohon);

        // SET FONT
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(ListPohon.this, "VarelaRound-Regular.ttf", true);

        // BLUEPRINT
        tbPersil = new TBPersil(this);
        tbPersilpemilik = new TBPersilpemilik(this);
        tbPohon = new TBPohon(this);

        // INIT
        tvPersilSelect = (TextView) findViewById(R.id.tvpersilpilih);
        tvListpohon = (TextView) findViewById(R.id.tvlistpohon);
        etsearch = (EditText) findViewById(R.id.etsearch);
        rvPohon = (RecyclerView) findViewById(R.id.rvlistpohon);
        //rvPohon.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        rvPohon.setLayoutManager(layoutManager);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            persilId = bundle.getString("persilId");
            gapoktanId = bundle.getInt("gapoktanId");

            //Log.d("PEMILIK PERSIL", "Pemilik Persil No : " + persilId + " = " + tbPersilpemilik.ambilSemuaPemilikWherePersil(persilId).size());
            String namaPemilikpersil;
            if(tbPersilpemilik.ambilSemuaPemilikWherePersil(persilId).size() != 0) {
                namaPemilikpersil = tbPersilpemilik.ambilSemuaPemilikWherePersil(persilId).get(0).getPemilik_name();
            } else {
                namaPemilikpersil = "Belum Ada";
            }

            tvPersilSelect.setText("Persil, " + persilId + " | Pemilik, " + namaPemilikpersil);

            // SET RECYCLE
            pohonList = tbPohon.ambilSemuaPohonWherePersil(persilId);
            pohonLoadAdapter = new PohonLoadAdapter(rvPohon, this, pohonList);
            rvPohon.setAdapter(pohonLoadAdapter);

            // SET LOAD MORE EVENT
            pohonLoadAdapter.setLoadMore(new ILoadMore() {
                @Override
                public void onLoadMore() {
                    if(pohonList.size() <= 20){
                        pohonList.add(null);
                        adapter.notifyItemInserted(pohonList.size()-1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pohonList.remove(pohonList.size()-1);
                                pohonLoadAdapter.notifyItemRemoved(pohonList.size());
                                // Handler more data
                                /*int index = pohonList.size();
                                int end = index+10;
                                for(int i=index;i<end;i++){


                                }*/
                                pohonLoadAdapter.notifyDataSetChanged();
                                pohonLoadAdapter.setLoaded();
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(ListPohon.this, "Data telah ditampilkan semua", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            /*adapter = new PohonRVAdapter(pohonList);
            rvPohon.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            */

            etsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    filter(editable.toString());
                }
            });

            if (pohonList.size() != 0){
                rvPohon.setVisibility(View.VISIBLE);
                tvListpohon.setText("");

                rvPohon.addOnItemTouchListener(
                        new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override public void onItemClick(View view, int position) {
                                // TODO Handle item click
                                Pohon pohon = pohonList.get(position);
                                int pohonId = pohon.getPohon_id();

                                Intent intent = new Intent(getApplicationContext(), FormPohon.class);
                                intent.putExtra("pohonId", pohonId);
                                intent.putExtra("gapoktanId", gapoktanId);
                                intent.putExtra("persilId", persilId);
                                startActivity(intent);

                                //Toast.makeText(ListPersil.this, "Klik di " + persilId, Toast.LENGTH_SHORT).show();
                            }
                        })
                );

            } else {
                rvPohon.setVisibility(View.INVISIBLE);
                tvListpohon.setText("Pohon belum tersedia untuk saat ini");
            }
        } else {
            tvPersilSelect.setText("Persil Tidak ditemukan ");
            rvPohon.setVisibility(View.INVISIBLE);
            tvListpohon.setText("Persil tidak dapat diproses sistem. Silahkan hubungi administrator");
        }

    }

    private void filter(String text){
        final ArrayList<Pohon> filterpohonList = new ArrayList<>();
        for(Pohon pohon : pohonList){
            if(pohon.getPohon_kode().toLowerCase().contains(text.toLowerCase())){
                filterpohonList.add(pohon);
            }
        }

        pohonLoadAdapter.filterList(filterpohonList);
        rvPohon.setAdapter(pohonLoadAdapter);

        rvPohon.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Pohon pohon = filterpohonList.get(position);
                        int pohonId = pohon.getPohon_id();

                        Intent intent = new Intent(getApplicationContext(), FormPohon.class);
                        intent.putExtra("pohonId", pohonId);
                        intent.putExtra("gapoktanId", gapoktanId);
                        intent.putExtra("persilId", persilId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //Toast.makeText(ListPersil.this, "Klik di " + persilId, Toast.LENGTH_SHORT).show();
                    }
                })
        );


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.goTopersil:
                this.finish();
                Intent in = new Intent(getApplicationContext(), ListPersil.class);
                in.putExtra("gapoktanId", gapoktanId);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            default:
        }
    }

    public void onBackPressed() {
        this.finish();
        Intent in = new Intent(getApplicationContext(), ListPersil.class);
        in.putExtra("gapoktanId", gapoktanId);
        startActivity(in);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
