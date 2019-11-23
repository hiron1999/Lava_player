package com.example.hiron.media_player;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.hiron.media_player.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL =1 ;
    ListView lv,favlv;
    BottomAppBar appBar;
    FloatingActionButton btn;
    String[] items;
    ArrayList<File> myfiles,files=new ArrayList<File>();
    TextView songtitle,abouttxt;
   static MediaPlayer mymediaPlayer;
    ViewFlipper viewFlipper;
    ImageView albumimg;
    ImageButton media_play,next,prev,fevbtn;
    SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
     MediaMetadataRetriever metadataRetriever;
    byte[]art;
    ArrayAdapter songadapter,fevadapter,titleadapter;
    int flag,pos,febflg=0,v=0;
    SQLiteDatabase sqLiteDatabase;
    Cursor c,s;
    private plyardb user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(( MainActivity.this),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL );


            }
        } else
            {
                setfiles();


        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 setfiles();

                }
                return;
            }

        }
    }

    public void setfiles(){
        songtitle=(TextView)findViewById(R.id.songtitle);
        lv=(ListView)findViewById(R.id.lv);
        favlv=(ListView)findViewById(R.id.fevlv);
         viewFlipper=(ViewFlipper)findViewById(R.id.vf);
        registerForContextMenu(lv);
        appBar=(BottomAppBar)findViewById(R.id.appbar);
        btn=(FloatingActionButton)findViewById(R.id.fbtn);
        appBar.inflateMenu(R.menu.appbar_menu);
        media_play=(ImageButton) findViewById(R.id.pauseplay);
        next=(ImageButton) findViewById(R.id.next);
        prev=(ImageButton) findViewById(R.id.previous);
        albumimg=(ImageView)findViewById(R.id.playimg);
        seekBar=(SeekBar)findViewById(R.id.seek);
        handler=new Handler();
        metadataRetriever=new MediaMetadataRetriever();
        fevbtn=(ImageButton) findViewById(R.id.fevbtn);
        abouttxt=(TextView)findViewById(R.id.abouttxt);
        updatefiles();

//data base....................................................
        user=new plyardb(getApplicationContext());
        sqLiteDatabase=user.getReadableDatabase();
        sqLiteDatabase=user.getWritableDatabase();


//..............................................................




        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                 pos=i;

               titleadapter=songadapter;
              setupMediaplyar();


            }
        });
        favlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos=position;
                titleadapter=fevadapter;
                setupMediaplyar();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(appBar.getFabAlignmentMode()==BottomAppBar.FAB_ALIGNMENT_MODE_CENTER){
                    v=viewFlipper.getDisplayedChild();

                    btn.setImageResource(R.drawable.ic_reply_black_24dp);
                    appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                    viewFlipper.setDisplayedChild(1);
                }
                else {btn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                    viewFlipper.setDisplayedChild(v);


                }
            }
        });
        abouttxt.setText("\n\n\n\nversion=1.0\n" +
                "terget API:28\n" +
                "Devloped by: Hiron Saha\n" +
                "email:hironsaha0@gmail.com\n" +
                "contact- (+91)6290270886\n" +
                "whatsapp->(+91)8345871903");

   //Seekbar change.........................................................................
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mymediaPlayer.seekTo(i);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        media_play.setOnClickListener(new View.OnClickListener() {
            int c_position;
            @Override
            public void onClick(View view) {
                if(mymediaPlayer==null){
                    setupMediaplyar();
                }
                else {
                    if (mymediaPlayer.isPlaying()) {
                        mymediaPlayer.pause();
                        media_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                        c_position = mymediaPlayer.getCurrentPosition();
                    } else if (mymediaPlayer.isPlaying() == false) {
                        mymediaPlayer.seekTo(c_position);
                        media_play.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                        mymediaPlayer.start();
                    }
                }
            }
        });
         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               pos=(pos+1)%files.size();
               setupMediaplyar();
             }
         });
         prev.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                if(pos==0){
                    pos=files.size();
                }
                 pos=pos-1;
                setupMediaplyar();
             }
         });
         fevbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if(mymediaPlayer!=null){
                   if(febflg==0){
                   user.insertData(lv.getItemAtPosition(pos).toString(),files.get(pos).getPath(),sqLiteDatabase);
                     fevbtn.setBackgroundResource(R.drawable.fev);
                     Toast.makeText(getApplicationContext(),"add favorite",Toast.LENGTH_SHORT).show();
                   febflg=1;}
                     else {
                       int x= user.DELEET(titleadapter.getItem(pos).toString(),sqLiteDatabase);
                       fevbtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                      if(x!=0){
                          Toast.makeText(getApplicationContext(),"remove favorite",Toast.LENGTH_SHORT).show();
                          fetchfromDB();
                          febflg=0;
                      }

                   }
                 }


             }
         });



        appBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        Toast.makeText(getApplicationContext(),"working",Toast.LENGTH_LONG).show();
                        updatefiles();
                        return true;
                    }
                });

                switch (item.getItemId()) {
                    case R.id.location:
                        Toast.makeText(MainActivity.this, "loc", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.sdcard:
                        flag = 2;
                        updatefiles();
                        Toast.makeText(MainActivity.this, "sd", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.internal:
                        flag = 1;
                        updatefiles();
                        Toast.makeText(MainActivity.this, "internal", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.all:
                        flag = 0;
                        updatefiles();
                        Toast.makeText(MainActivity.this, "all", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.search:
                        SearchView searchView = (SearchView) item.getActionView();
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {

                                files.clear();
                                getfiles(s);

                                Createlist(files);
                                return false;
                            }
                        });


                        return true;
                    case R.id.favroit:
                        if(viewFlipper.getDisplayedChild()==2){
                            item.setIcon(R.drawable.ic_favorite_black_24dp);
                            updatefiles();
                            viewFlipper.setDisplayedChild(0);
                       }
                    else {
                        fetchfromDB();
                        item.setIcon(R.drawable.ic_playlist_play_black_24dp);
                            btn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                        viewFlipper.setDisplayedChild(2);
                         }

                        return true;
                    case R.id.about:
                        viewFlipper.setDisplayedChild(3);
                        btn.setImageResource(R.drawable.ic_reply_black_24dp);
                        appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                        return true;
            }
                return false;
            }
        });

    }


    //Plyar setup.....................................
    public void setupMediaplyar(){
        if(mymediaPlayer!=null){
            mymediaPlayer.stop();
            mymediaPlayer.release();

        }


        Uri uri = Uri.parse(files.get(pos).toString());
        metadataRetriever.setDataSource(files.get(pos).getPath());
        mymediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        s=user.SearchDAta(sqLiteDatabase,titleadapter.getItem(pos).toString());
        if(s.getCount()==0){
            febflg=0;
            fevbtn.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
        }
        else {febflg=1;
            fevbtn.setBackgroundResource(R.drawable.fev);
        }

        try{
            art=metadataRetriever.getEmbeddedPicture();
            Bitmap Songimage=BitmapFactory.decodeByteArray(art,0,art.length);
            albumimg.setImageBitmap(Songimage);
            //
        }catch (Exception e){
            albumimg.setImageResource(R.drawable.ic_music_note_black_24dp);
        }


        //Media plyar prepare..........................................................................

        mymediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {



                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                changeSeekbar();
                songtitle.setText("Title: "+titleadapter.getItem(pos).toString());

            }

        });
        mymediaPlayer.start();

    }



    //Get the sd card path
    public String getstcard(){
        String removableStoragepath = null;
        File filelist[]=new File("/storage/").listFiles();
        for(File file:filelist){
            if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())&&file.isDirectory()&&file.canRead()){
                removableStoragepath = file.getAbsolutePath();

            }

        }
        return removableStoragepath;
    }

//fatching files
    public ArrayList<File> FetchSong(File root){
        ArrayList<File> song=new ArrayList<File>();

        File files[]=root.listFiles();


            for (File singlefile : files) {
                if (singlefile.isDirectory()&&singlefile.isHidden()==false) {
                    song.addAll(FetchSong(singlefile));

                }
                else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                        song.add(singlefile);
                    }
                }
            }

        return song;
    }
//Update file list...........................................
    public void updatefiles (){

        pos=0;
        if(flag==0){
            myfiles =FetchSong(Environment.getExternalStorageDirectory());
            myfiles.addAll(FetchSong(new File(getstcard())));}
        else if(flag==1){
            myfiles =FetchSong(Environment.getExternalStorageDirectory());
        }
        else if(flag==2){
            myfiles =FetchSong(new File(getstcard()));
        }
        files.clear();
        files.addAll(myfiles);
        Createlist(files);
        titleadapter=new ArrayAdapter(this,R.layout.playlist,items);
    }


//Set list view
    public void Createlist(ArrayList<File>f){

        items=new String[f.size()];

        for(int i=0;i<f.size();i++){
            items[i]=f.get(i).getName().replace(".mp3","").toString();

        }
        songadapter=new ArrayAdapter(getApplicationContext(),R.layout.playlist,items);

        lv.setAdapter(songadapter);
       // Toast.makeText(this,getstcard(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.longpress_menu,menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index=info.position;
        DeleetFile(index);
        return super.onContextItemSelected(item);
    }

    private void DeleetFile(int i) {
        File Deleet_file=myfiles.get(i);


        Toast.makeText(this,Deleet_file.getName()+"\n"+Deleet_file.getPath()+"\n"+Deleet_file.getTotalSpace(),Toast.LENGTH_LONG).show();
    }

    public void changeSeekbar() {
        seekBar.setProgress(mymediaPlayer.getCurrentPosition());
        if(mymediaPlayer.isPlaying()){
            runnable=new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }
//function for searching....................................................................
//filter file......................
     public void getfiles(String txt){

    for(int i=0;i<myfiles.size();i++){
        if(myfiles.get(i).getName().toLowerCase().startsWith(txt.toLowerCase())){
            files.add(myfiles.get(i));
        }

    }

  }


    //fetch from database........................
    public void fetchfromDB() {

        c=user.ViewData(sqLiteDatabase);
        if (c.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "List is empty", Toast.LENGTH_LONG).show();
           fevadapter=null;
        }
        else {files.clear();
            String[] fvsong=new String[c.getCount()];
            c.moveToFirst();
            int i=0;
            do{

                fvsong[i]=c.getString(0);
                files.add(new File(c.getString(1)));


              i++;
            }
            while (c.moveToNext());
            fevadapter=new ArrayAdapter(getApplicationContext(),R.layout.playlist,fvsong);


        }
        favlv.setAdapter(fevadapter);
    }

}
