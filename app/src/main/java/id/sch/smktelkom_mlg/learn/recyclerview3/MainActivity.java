package id.sch.smktelkom_mlg.learn.recyclerview3;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import id.sch.smktelkom_mlg.learn.recyclerview3.adapter.HotelAdapter;
import id.sch.smktelkom_mlg.learn.recyclerview3.model.Hotel;

public class MainActivity extends AppCompatActivity  implements HotelAdapter,HotelAdapter.IHotelAdapter{

    private static final String HOTEL = "hotel";
    private static final int REQUEST_CODE_ADD = 88;
    ArrayList<Hotel> mList= new ArrayList<>();
    boolean isFiltered;
    ArrayList<Integer> mListMapFilter = new ArrayList<>();
    String mQuery;
    HotelAdapter mAdapter;
    int itemPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerVIew);
        LinearLayoutManager layoutManager= new LinearLayoutManager (this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HotelAdapter(this.mList);
        recyclerView.setAdapter(mAdapter);

        fillData();

        hotel = (HOTEL) getIntent().getSerializableExtra(MainActivity.HOTEL);
        if (hotel!=null)
        {
            setTitle("Edit "+hotel.judul);
            fillData();
        }

        else
        {
            setTitle("New Hotel");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                goAdd();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAdd();
            }


        });
    }

    private void goAdd() {
        startActivityForResult(new Intent(this,InputActivity.class), REQUEST_CODE_ADD);

    }

    private void fillData() {

        etJudul.setText(hotel.judul);
        etDeskripsi.setText(hotel.deskripsi);
        etDetail.setText(hotel.detail);
        etLokasi.setText(hotel.lokasi);
        urifoto = Uri.parse(hotel.foto);
        Resources resources = getResources();
        String [] arJudul = resources.getStringArray(R.array.places);
        String [] arDeskripsi = resources.getStringArray(R.array.place_desc);
        String [] arDetail = resources.getStringArray(R.array.place_details);
        String [] arLokasi = resources.getStringArray(R.array.place_Locations);
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        Drawable[] arFoto  = new Drawable[a.length()];
        for (int i = 0; i < arFoto.length; i++){
            int id = a.getResourceId(i, 0);

            arFoto[i] = ContentResolver.SCHEME_ANDROID_RESOURCE+"://"
                        +resources.getResourcePackageName(id)+'/'
                        +resources.getResourceTypeName(id)+'/'
                    +resources.getResourceEntryName(id);
            a.recycle();

            for (int i=0;i< arJudul.length; i++)
            {
                mList.add(new Hotel(arJudul[i], arDeskripsi[i],
                        arDetail[i], arLokasi[i], arFoto[i]));

            }
            mAdapter.notifyDataSetChanged();
        }
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String quey)
                    {
                        return false;
                    }

                    @Override
                    public  boolean onQueryTextChange(String newText)
                    {
                        mQuery = newText.toLowerCase();
                        doFilter(mQuery);
                        return  true;
                    }

                    private void doFilter(String mQuery) {
                        if (!isFiltered)
                        {
                            mListAll.clear();
                            mListAll.addAll(mList);
                            isFiltered = true
                        }

                        mList.clear();
                        if(query==null| mQuery.isEmpty())
                        {
                            mList.addAll(mListAll);
                            isFiltered = true;
                        }

                        else
                        {
                            mListMapFilter.clear();
                            for ((int i = 0; i < mListAll.size(); i++)
                            {
                                Hotel hotel = mListAll.get(i);
                                if (hotel.judul.toLowerCase().contains(query) ||
                                        hotel.deskripsi.toLowerCase().contains(quey)
                                        hotel.lokasi.toLowerCase().contains(query))
                                {
                                    mList.add(hotel);
                                    mListMapFilter.add(i);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );
        return true;



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD && requestCode ==  RESULT_OK)
        {
            Hotel hotel= (Hotel) data.getSerializableExtra(HOTEL);
            mList.add(hotel);
            if(isFiltered) mListAll.add(hotel);
            doFilter(mQuery);
        }

            else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK)
        {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mList.remove(itemPos);
            if(isFiltered) mLlistAll.remmove(mListMapFilter,get(itemPos).intValue());
            mList.add(itemPos, hotel);
            if (isFiltered) mListAll.add(mListMapFilter.get(itemPos), hotel);
            mAdapter.notifyDataSetChanged();
        }

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
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void doClick(int pos) {
        Intent intent= new Intent(this, DetailActivity.class);
        intent.putExtra(HOTEL,mList.get(pos));
    }




    @Override
    public  void  doEdit(int pos)
    {
        itemPos = pos;
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(Hotel, mList.get(pos));
        startActivityForResult(intent,  99);
    }


    @Override
    public void doDelete(int pos){
        itemPos = pos;
        final Hotel hotel = mList.get(pos);
        mList.remove(itemPos);
        if (isFiltered) mListAll.remove(mListMapFilter.get(itemPos), intValue());

        mAdapter.notifyDataSetChanged();

        Snackbar.make(findViewById(R.id.fab),hotel.judul+"Terhapus",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mList.add(itemPos,hotel);
                        if (isFiltered) mListAll.add(mListMapFilter.get(itemPos), hotel )
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }
}

