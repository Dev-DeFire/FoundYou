package com.nishantnitinmishra.foundyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    CardView findPeopleBtn,addFaceBtn,historyBtn,foundPeopleBtn,detailBtn,restartBtn;
    int OUTPUT_SIZE=192; //Output size of model
    Context context=MainActivity.this;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    LinearLayout uploadBtn,downloadBtn,infoBtn;
    String downloadedDataSet=null;
    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findPeopleBtn=findViewById(R.id.findPeopleBtn);
        addFaceBtn=findViewById(R.id.addFaceBtn);
        historyBtn=findViewById(R.id.historyBtn);
        uploadBtn=findViewById(R.id.uploaddataBtn);
        downloadBtn=findViewById(R.id.downloadBtn);
        foundPeopleBtn=findViewById(R.id.foundPeopleBtn);
        detailBtn=findViewById(R.id.detailBtn);
        restartBtn=findViewById(R.id.restartBtn);
        infoBtn=findViewById(R.id.infoBtn);
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference().child("Missing Peoples").child("peoples");
        String dataString = new Gson().toJson(readFromSP("HashMap","local"));

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessDialog("Long Press on each Button to check their working info.","Instructions !");
            }
        });
        foundPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatenameListview("FoundPeoples");
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()){
                    try {
                        if (dataString.length()>10){
                            reference.child("data").setValue(dataString);
                            showSuccessDialog("Your Device Data is successfully sync to Global Database.","Success !");
                        }
                        else{
                            showSuccessDialog("There is no data to upload.","Error !...");
                        }
                    }catch(Exception e){
                        showSuccessDialog("Server error.","Error !...");
                    }
                }else{
                    showSuccessDialog("Internet Connection is not Available !","Oops !...");
                }

            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnectivity()){
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                downloadedDataSet=snapshot.child("data").getValue(String.class);
                                insertToSP(registered,"server","HashMap","local");
                                showSuccessDialog("Globally Data is successfully sync to your device.","Success !");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    showSuccessDialog("Internet Connection is not Available !","Oops !...");
                }
            }
        });
        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FindPerson.class);
                startActivity(intent);
            }
        });
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessDialog("Total No. of Missing Peoples : "+readFromSP("HashMap","local").size()+"\n\nTotal No. of Found Peoples : "+readFromSP("FoundPeoples","Found").size(),"DATASET DETAILS :");
            }
        });
        addFaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddPerson.class);
                startActivity(intent);
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatenameListview("HashMap");
            }
        });
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                overridePendingTransition(1,1);
            }
        });


        
        addFaceBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.","ADD FACE :");
                return false;
            }
        });
        findPeopleBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("1.Bring Person's Face in view of Camera.\n\n2.The Judgement Box will appear.\n\n3.The Judgement box tells you, the missing people found or not.  ","FIND PEOPLE :");
                return false;
            }
        });
        historyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("By accessing this button you can see, update and delete missing peoples data.","MISSING PEOPLE DATA :");
                return false;
            }
        });
        uploadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("This button uploads the missing people's data to global server.","Upload :");
                return false;
            }
        });
        downloadBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("This button download the missing people's data from global server.","Download :");
                return false;
            }
        });
        detailBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("Total No. of missing and found peoples visible.","DETAILS :");
                return false;
            }
        });
        restartBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("Refresh your app for better performance.","REFRESH :");
                return false;
            }
        });
        foundPeopleBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSuccessDialog("By accessing this button you can see, update and delete found peoples data.","Found Peoples :");
                return false;
            }
        });
    }



    private void updatenameListview(String parent)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String location="",message="";
        if(parent=="HashMap"){
            registered=readFromSP(parent,"local");
            location="local";
            message="DataSet of Missing People!";
        }
        else{
            location="Found";
            registered=readFromSP("FoundPeoples","Found");
            message="DataSet of Found People!";

        }
        if(registered.isEmpty()) {
            builder.setTitle("Empty Database !");
            builder.setPositiveButton("OK",null);
        }
        else{
            builder.setTitle(message);

            // add a checkbox list
            String[] names= new String[registered.size()];
            boolean[] checkedItems = new boolean[registered.size()];
            int i=0;
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
            {
                //System.out.println("NAME"+entry.getKey());
                names[i]=entry.getKey();
                checkedItems[i]=false;
                i=i+1;

            }

            builder.setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // user checked or unchecked a box
                    //Toast.makeText(MainActivity.this, names[which], Toast.LENGTH_SHORT).show();
                    checkedItems[which]=isChecked;

                }
            });


            String finalLocation = location;
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // System.out.println("status:"+ Arrays.toString(checkedItems));
                    for(int i=0;i<checkedItems.length;i++)
                    {
                        //System.out.println("status:"+checkedItems[i]);
                        if(checkedItems[i])
                        {
//                                Toast.makeText(MainActivity.this, names[i], Toast.LENGTH_SHORT).show();
                            registered.remove(names[i]);
                        }

                    }
                    insertToSP(registered,"local",parent, finalLocation); //mode: 0:save all, 1:clear all, 2:update all
                }
            });
            builder.setNegativeButton("Cancel", null);
        }
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap,String check,String parent ,String child) {
        String jsonString="";
        if(check=="server"){
            jsonString=downloadedDataSet;
        }
        else{
            jsonString = new Gson().toJson(jsonMap);
        }


//        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        SharedPreferences sharedPreferences = getSharedPreferences(parent, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(child, jsonString);
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply();
    }
    private HashMap<String, SimilarityClassifier.Recognition> readFromSP(String parent,String child){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(parent, MODE_PRIVATE);
            String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
            String json=sharedPreferences.getString(child,defValue);
            // System.out.println("Output json"+json.toString());
            TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
            HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
            // System.out.println("Output map"+retrievedMap.toString());

            //During type conversion and save/load procedure,format changes(eg float converted to double).
            //So embeddings need to be extracted from it in required format(eg.double to float).
            for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
            {
                float[][] output=new float[1][OUTPUT_SIZE];
                ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
                arrayList = (ArrayList) arrayList.get(0);
                for (int counter = 0; counter < arrayList.size(); counter++) {
                    output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
                }
                entry.getValue().setExtra(output);

                //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );

            }
//        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
            return retrievedMap;
        }catch (Exception e){
            return null;
        }

    }
    public boolean checkInternetConnectivity() {
        Context context=this;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI||activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
    private void showSuccessDialog(String message , String title){
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);
        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}