package com.example.minimusic;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.util.ArrayList;

public class Search extends AppCompatActivity {

    SearchView searchView;
    ListView mylistView;
    ImageView imageHome,imageView6;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.searchView);
        mylistView = findViewById(R.id.mylistView);
        imageHome = findViewById(R.id.imagehome);
        imageView6 = findViewById(R.id.imageView6);

        mylistView.setVisibility(View.GONE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<File> songs = (ArrayList<File>) intent.getSerializableExtra("songList");

        String[] items = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            items[i] = songs.get(i).getName().replace(".mp3", "");
        }
        addToList(items,songs);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if( s.equals("")){
                    mylistView.setVisibility(View.INVISIBLE);
                }
                else{
                    Log.d("Debug",s);
                    mylistView.setVisibility(View.VISIBLE);

                    ArrayList<File> filterItemsArrayList = new ArrayList<>();
                    for (int i = 0; i < items.length; i++) {
                        if (songs.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                            filterItemsArrayList.add(songs.get(i));
                        }
                    }
                    String[] filterItems = new String[filterItemsArrayList.size()];
                    for (int i = 0; i < filterItemsArrayList.size(); i++) {
                        filterItems[i] = filterItemsArrayList.get(i).getName();
                    }

                    addToList(filterItems, filterItemsArrayList);
                }

                return false;
            }
        });

        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Search.this, MainActivity.class);
                startActivity(intent);
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    public void addToList(String[] items, ArrayList<File> mySongs) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Search.this, android.R.layout.simple_list_item_1,items);
        mylistView.setAdapter(adapter);
        mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Search.this, PlaySong.class);
                String currentSong = mylistView.getItemAtPosition(position).toString();
                intent.putExtra("songList", mySongs);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}