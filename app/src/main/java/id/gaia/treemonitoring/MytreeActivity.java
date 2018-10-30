package id.gaia.treemonitoring;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.srx.widget.PullToLoadView;

import id.gaia.treemonitoring.database.TBPetani;
import id.gaia.treemonitoring.helper.Paginator;
import id.gaia.treemonitoring.helper.Session;

public class MytreeActivity extends AppCompatActivity implements View.OnClickListener {
    private String petUname, petaniId, petName;
    private TextView tvMyname;
    private Session session;
    private RecyclerView rv;
    PullToLoadView pullToLoadView;

    // DATABASE
    private TBPetani tbPetani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytree);

        session = new Session(this);
        tbPetani = new TBPetani(this);

        // INITIAL
        initComponent();

        // AMBIL DATA PETANI YANG LOGIN, UNAME DISET DI SESSION
        petUname = session.PetaniUname();
        petaniId = String.valueOf(tbPetani.ambilDataPetani(petUname).getPetani_id());
        petName = tbPetani.ambilDataPetani(petUname).getPetani_name();

        tvMyname.setText(petName);

        // RECYCLEVIEW
        rv = pullToLoadView.getRecyclerView();
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        new Paginator(this,pullToLoadView,rv).initializePaginator();
    }

    private void initComponent() {
        pullToLoadView = (PullToLoadView) findViewById(R.id.pullToLoadView);
        tvMyname = (TextView) findViewById(R.id.tvmytree_nama);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.goTohome:
                this.finish();
                Intent in = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(in);
                break;
            default:
        }
    }
}
