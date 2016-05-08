package com.example.seth.scorekeeper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button buttonP1, buttonP2;
    TextView textViewP1, textViewP2;
    int P1Score, P2Score;
    int gamesPlayed;
    int dif;
    int amountItems;
    RecyclerView gameRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] dataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataset = new String[3];
        dataset[0] = "test1";
        dataset[1] = "test2";
        dataset[2] = "test3";

        mAdapter = new MyAdapter(dataset);
        gameRecyclerView = (RecyclerView) findViewById(R.id.gameList);
        textViewP1 = (TextView) findViewById(R.id.textViewP1);
        textViewP2 = (TextView) findViewById(R.id.textViewP2);
        buttonP1 = (Button) findViewById(R.id.buttonP1);
        buttonP2 = (Button) findViewById(R.id.buttonP2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //navigation drawer stuff
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Shared Preferences stuff
        final String PREFS_NAME = "scorekeeper";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            gamesPlayed = 0;

            textViewP1.setText(String.valueOf(P1Score));
            textViewP2.setText(String.valueOf(P2Score));

            saveInfo();
            settings.edit().putBoolean("my_first_time", false).commit();
        }else {
            SharedPreferences sharedPref = getSharedPreferences("TTscorekeeper"
                    , Context.MODE_PRIVATE);

            P1Score = sharedPref.getInt("p1score", P1Score);
            P2Score = sharedPref.getInt("p2score", P2Score);

            gamesPlayed= sharedPref.getInt("gamesplayed", gamesPlayed);


            textViewP1.setText(String.valueOf(P1Score));
            textViewP2.setText(String.valueOf(P2Score));

        }

        //recylerview stuff

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        gameRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        gameRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        gameRecyclerView.setAdapter(mAdapter);
    }

    public void onNewGameClick(View v){
        newGame();
    }

    public void newGame(){
        amountItems = 0;
        saveInfo();
    }

    public void game(int score, int oppoScore, TextView textView){
        dif = score-oppoScore;
        textView.setText(String.valueOf(score));
        saveInfo();
        if (score >= 11 && dif >= 2){
            amountItems += 1;
            P1Score = 0;
            P2Score = 0;
            textViewP1.setText(String.valueOf(P1Score));
            textViewP2.setText(String.valueOf(P2Score));
            gameOverDialog();
            saveInfo();
        }
    }

    public void gameOverDialog() {
        if (amountItems == 3) {
            final AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
            dialog2.setMessage("The game is finsished.");
            dialog2.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gamesPlayed += 1;
                    amountItems = 0;
                    newGame();
                    saveInfo();
                    dialog.dismiss();
                }
            });
            dialog2.create();
            dialog2.show();


        }
    }


    public void saveInfo(){
        SharedPreferences sharedPref = getSharedPreferences("TTscorekeeper", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("p1score", P1Score);
        editor.putInt("p2score", P2Score);
        editor.putInt("amountitems", amountItems);
        editor.putInt("gamesplayed", gamesPlayed);

        editor.apply();

    }

    public void onPlayer1Click(View v) {
        P1Score += 1;
        game(P1Score, P2Score, textViewP1);
    }

    public void onPlayer2Click(View v) {
        P2Score += 1;
        game(P2Score, P1Score, textViewP2);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
