package com.example.apdatenote.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apdatenote.R;
import com.example.apdatenote.database.NoteDataBase;
import com.example.apdatenote.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Create extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION= 1;
    private static final int REQUEST_CODE_SELECT_IMAGE= 2;

    EditText editTextTitle, editTextSubtitle, editTextNotesText;
    TextView textViewTime, textWebURI;
    LinearLayout layoutWebURI;
    ImageView imageViewBack, imageViewSave, imageViewSelect;
    String selectColor;
    String selectImagePath;
    View view;
    AlertDialog dialogUrl;
    AlertDialog dialogDelete;
    Note alreadyAvailable;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        intViews();


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textViewTime.setText(
                new SimpleDateFormat("EEE, dd MMM YYY HH:mm a", Locale.getDefault()).format(new Date())
        );
        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectColor= "#333333";//default not color
        selectImagePath= "";

        if(getIntent().getBooleanExtra("viewUpdate", false)){
            alreadyAvailable= (Note) getIntent().getSerializableExtra("note");
            setViewUpdateNote();
        }

        findViewById(R.id.imageRemoveImage_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewSelect.setImageBitmap(null);
                imageViewSelect.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage_create).setVisibility(View.GONE);
                selectImagePath= "";
            }
        });

        findViewById(R.id.imageRemoveUri_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebURI.setText(null);
                layoutWebURI.setVisibility(View.GONE);
            }
        });

        initMiscellaneous();
        setSubTitle();
    }

    private void setViewUpdateNote(){
        editTextTitle.setText(alreadyAvailable.getTitle());
        editTextSubtitle.setText(alreadyAvailable.getSubtitle());
        editTextNotesText.setText(alreadyAvailable.getNoteText());
        textViewTime.setText(alreadyAvailable.getDataTime());

        if(alreadyAvailable.getImage() != null && !alreadyAvailable.getImage().trim().isEmpty()){
            imageViewSelect.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailable.getImage()));
            imageViewSelect.setVisibility(View.VISIBLE);

            findViewById(R.id.imageRemoveImage_create).setVisibility(View.VISIBLE);

            selectImagePath= alreadyAvailable.getImage();
        }
        if(alreadyAvailable.getWebLink() != null && !alreadyAvailable.getWebLink().trim().isEmpty()){
            textWebURI.setText(alreadyAvailable.getWebLink());
            layoutWebURI.setVisibility(View.VISIBLE);
        }
    }
    private void intViews() {
        editTextTitle= findViewById(R.id.et_noteTitle_create_activity);
        editTextSubtitle= findViewById(R.id.et_noteSubtitle_create_activity);
        editTextNotesText= findViewById(R.id.et_inputNote_create_activity);
        textViewTime= findViewById(R.id.tv_data_create_activity);
        imageViewBack= findViewById(R.id.iv_back_create_activity);
        imageViewSave= findViewById(R.id.btn_save_create_activity);
        view= findViewById(R.id.view);
        imageViewSelect= findViewById(R.id.imageNote_Create);
        textWebURI= findViewById(R.id.tv_textWebURI);
        layoutWebURI= findViewById(R.id.layoutWebURI);
    }
    private void saveNote(){
        if(editTextTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "the title field is empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(editTextSubtitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "the subtitle field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note= new Note();
        note.setTitle(editTextTitle.getText().toString());
        note.setSubtitle(editTextSubtitle.getText().toString());
        note.setNoteText(editTextNotesText.getText().toString());
        note.setDataTime(textViewTime.getText().toString());
        note.setColor(selectColor);
        note.setImage(selectImagePath);

        if(layoutWebURI.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebURI.getText().toString());
        }

        if(alreadyAvailable != null){
            note.setID(alreadyAvailable.getID());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NoteDataBase.getNoteDataBase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent= new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private void initMiscellaneous(){
        LinearLayout layoutMisc= findViewById(R.id.layout_miscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior= BottomSheetBehavior.from(layoutMisc);
        layoutMisc.findViewById(R.id.tv_textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        ImageView imageViewColor1= layoutMisc.findViewById(R.id.imageColor1);
        ImageView imageViewColor2= layoutMisc.findViewById(R.id.imageColor2);
        ImageView imageViewColor3= layoutMisc.findViewById(R.id.imageColor3);
        ImageView imageViewColor4= layoutMisc.findViewById(R.id.imageColor4);
        ImageView imageViewColor5= layoutMisc.findViewById(R.id.imageColor5);

        layoutMisc.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColor= "#333333";
                imageViewColor1.setImageResource(R.drawable.ic_baseline_done_24);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubTitle();
            }
        });
        layoutMisc.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColor= "#FDBE3B";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(R.drawable.ic_baseline_done_24);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubTitle();
            }
        });
        layoutMisc.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColor= "#FF4842";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(R.drawable.ic_baseline_done_24);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(0);
                setSubTitle();
            }
        });
        layoutMisc.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColor= "#3A52Fc";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(R.drawable.ic_baseline_done_24);
                imageViewColor5.setImageResource(0);
                setSubTitle();
            }
        });
        layoutMisc.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectColor= "#FF000000";
                imageViewColor1.setImageResource(0);
                imageViewColor2.setImageResource(0);
                imageViewColor3.setImageResource(0);
                imageViewColor4.setImageResource(0);
                imageViewColor5.setImageResource(R.drawable.ic_baseline_done_24);
                setSubTitle();
            }
        });

        if(alreadyAvailable != null && alreadyAvailable.getColor() != null && !alreadyAvailable.getColor().trim().isEmpty()){
            switch (alreadyAvailable.getColor()){
                case "#FDBE3B":
                    layoutMisc.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMisc.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutMisc.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#FF000000":
                    layoutMisc.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        layoutMisc.findViewById(R.id.add_image_miscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                if(ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            Create.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION
                    );
                }else {
                    selectImage();
                }
            }
        });

        layoutMisc.findViewById(R.id.layoutAddUri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddUriDialog();
            }
        });

        if( alreadyAvailable != null){
            layoutMisc.findViewById(R.id.layoutDeleteNote_layout_miscellaneous).setVisibility(View.VISIBLE);
            layoutMisc.findViewById(R.id.layoutDeleteNote_layout_miscellaneous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNote();
                }
            });
        }

    }

    private void showDeleteNote(){

            if(dialogDelete == null){
                AlertDialog.Builder builder= new AlertDialog.Builder(Create.this);
                View view= LayoutInflater.from(this).inflate(R.layout.layout_delete_note,
                        (ViewGroup) findViewById(R.id.layoutDeleteContainer_delete_note)
                );
                builder.setView(view);
                dialogDelete= builder.create();
                if(dialogDelete.getWindow() != null){
                    dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

            view.findViewById(R.id.textDelete_layout_delete_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDataBase.getNoteDataBase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailable);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent= new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel_layout_delete_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDelete.dismiss();
                }
            });
        }
        dialogDelete.show();
    }
    private void selectImage() {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    private void setSubTitle(){
        GradientDrawable gradientDrawable= (GradientDrawable) view.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectColor));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Defied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode ==RESULT_OK){
            if(data != null){
                Uri selectedImageUri= data.getData();
                if(selectedImageUri != null){
                    try {
                        InputStream inputStream= getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        imageViewSelect.setImageBitmap(bitmap);
                        imageViewSelect.setVisibility(View.VISIBLE);

                        findViewById(R.id.imageRemoveImage_create).setVisibility(View.VISIBLE);

                        selectImagePath= getPathFromUri(selectedImageUri);

                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contextUri){
        String filePath;
        Cursor cursor= getContentResolver().query(contextUri, null,null,null,null);
        if(cursor == null){
            filePath= contextUri.getPath();
        }else {
            cursor.moveToFirst();
            int index= cursor.getColumnIndex("_data");
            filePath= cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    private void showAddUriDialog(){
        if(dialogUrl == null){
            AlertDialog.Builder builder= new AlertDialog.Builder(Create.this);
            View view= LayoutInflater.from(this).inflate(R.layout.add_uri_layout,
                    (ViewGroup) findViewById(R.id.layoutAddUriContainer)
            );
            builder.setView(view);
            dialogUrl= builder.create();
            if(dialogUrl.getWindow() != null){
                dialogUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText editTextInputUri= view.findViewById(R.id.et_inputUri_add_uri_layout);
            editTextInputUri.requestFocus();

            view.findViewById(R.id.tv_textAdd_add_uri_layout).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View view) {
                    if(editTextInputUri.getText().toString().trim().isEmpty()){
                        Toast.makeText(Create.this, "Enter Uri", Toast.LENGTH_SHORT).show();
                    }else if (!Patterns.WEB_URL.matcher(editTextInputUri.getText().toString()).matches()){
                        Toast.makeText(Create.this, "Enter Valid Uri", Toast.LENGTH_SHORT).show();
                    }else {
                        textWebURI.setText(editTextInputUri.getText().toString());
                        layoutWebURI.setVisibility(View.VISIBLE);
                        dialogUrl.dismiss();
                    }
                }
            });

            view.findViewById(R.id.tv_textCancel_add_uri_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogUrl.dismiss();
                }
            });
        }
        dialogUrl.show();
    }
}