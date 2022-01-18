package com.example.uasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProdukAdapter.ItemClickListener {
    RecyclerView mRecyclerview;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    ArrayList<Produk> mItems;
    Button btnInsert, btnDelete;
    ProgressDialog pd;
    private ProdukAdapter adapter;
    private ArrayList<Produk> produkArrayList;
    private ArrayList<String> listGambar;

    /*firebase*/
    private FirebaseUser firebaseUser;
    private TextView txtName, btn_logout;


    ViewFlipper v_flipper;
    public double tot=0;
    String dataProduk[] = null;
    String dS[] = null;
    int images[] = {R.drawable.bukitkunci, R.drawable.jerukbali, R.drawable.mancing, R.drawable.outbound, R.drawable.puncakserut};


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = findViewById(R.id.nama);
        btn_logout = findViewById(R.id.btn_logout);

        /*firebase*/
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            txtName.setText(firebaseUser.getDisplayName());
        }
        else{
            txtName.setText("Login Gagal");
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        /**/

        v_flipper = findViewById(R.id.v_flipper);
        for (int i =0; i<images.length; i++){
            fliverImages(images[i]);
        }
        for (int image: images) fliverImages(image);
        mRecyclerview = (RecyclerView) findViewById(R.id.recycler_view);
        pd = new ProgressDialog(MainActivity.this);
        mItems = new ArrayList<>();
        // Membaca Semua Barang
        loadJson();

        //mManager = newLinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        //mRecyclerview.setLayoutManager(mManager);
        mAdapter = new ProdukAdapter(mItems,this);
        adapter= new ProdukAdapter(mItems,this);
        //mRecyclerview.setAdapter(mAdapter);
        //GRID 2 kolom

        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        //STAGGER 4 KOLOM
        // StaggeredGridLayoutManager llm=new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(adapter);
        //mAdapter.setClickListener(this);
        adapter.setClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
            //Intent intent = new Intent(MainActivity.this,TambahActivity.class);
            //startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void postTotals()
    {
        TextView txtTot=(TextView) findViewById(R.id.totalPrice);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        txtTot.setText("Rp. "+decimalFormat.format(tot));
    }
    public void onClick(View view, int position) {
        //final Produk mhs = produkArrayList.get(position);
        final Produk mhs = mItems.get(position);
        switch (view.getId()) {
            case R.id.txt_nama_produk:
                Toast.makeText(this,"okkkkkk" + mhs.getNama(),Toast.LENGTH_SHORT).show();
                return;
            case R.id.img_card:
                tot=tot+Double.parseDouble(mhs.getHarga());
                Toast.makeText(this,"gambare....." + mhs.getNama() ,Toast.LENGTH_SHORT).show();
                postTotals();
                return;
            default:
                Toast.makeText(this,"lainnya..... -> " + mhs.getNama()+ " Rp. "+tot ,Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void loadJson() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest reqData = new JsonArrayRequest(Request.Method.POST, ServerAPI.URL_DATA,null, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                pd.cancel();
                Log.d("volley","response : " + response.toString());
                for(int i = 0 ; i < response.length(); i++){
                    try {
                        JSONObject data = response.getJSONObject(i);
                        Produk md = new Produk();
                        md.setKode(data.getString("kd_brg"));
                        md.setNama(data.getString("nm_brg"));
                        md.setHarga(data.getString("harga"));
                        md.setImg(data.getString("gambar"));
                        mItems.add(md);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //mAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            }
        }, error -> {
            pd.cancel();
            Log.d("volley", "error : " + error.getMessage());
        });
        android.example.loginuas.Util.AppController.getInstance().addToRequestQueue(reqData);
    }
    /*    public void ambildata(){listGambar= new ArrayList<String>();
            listGambar.add("indosat1");
            listGambar.add("indosat2");
            listGambar.add("indosat3");
            listGambar.add("indosat4");
            listGambar.add("indosat5");
        }*/
    public void checkout(View view) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        Toast.makeText(this,"Totol Rp. "+ decimalFormat.format(tot) ,Toast.LENGTH_SHORT).show();
    }
    public void fliverImages(int images){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(images);
        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);

        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }
}