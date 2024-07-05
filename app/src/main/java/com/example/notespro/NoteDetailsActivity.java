package com.example.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText,contenEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_txt);
        contenEditText = findViewById(R.id.notes_content_txt);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        //recive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }


        titleEditText.setText(title);
        contenEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

       // saveNoteBtn.setOnClickListener((v)-> saveNote());
        saveNoteBtn.setOnClickListener((v)-> showImpMenu());
       // deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());
        deleteNoteTextViewBtn.setOnClickListener((v)-> showDelMenu());

    }

    void showImpMenu(){
        //to Display menu
        PopupMenu popupMenu = new PopupMenu(NoteDetailsActivity.this,saveNoteBtn);
        popupMenu.getMenu().add("Is this note most importent ?");
        popupMenu.getMenu().add("Yes");
        popupMenu.getMenu().add("No");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="No"){
                    saveNote();
                    finish();
                    return true;
                }

                if (menuItem.getTitle().equals("Yes")) {
                    titleEditText.setTextColor(getResources().getColor(R.color.red));
                    contenEditText.setTextColor(getResources().getColor(R.color.red));
                   // note_title_text_view.setTextColor(getResources().getColor(R.color.red));
                    saveNote();
                    finish();
                    return true;
                }

                return false;
            }
        });
    }
    void saveNote(){
            String noteTitle = titleEditText.getText().toString();
            String notecontent = contenEditText.getText().toString();
            if(noteTitle == null || noteTitle.isEmpty()){
                titleEditText.setError("Title is required");
                return;
            }

            Note note = new Note();
            note.setTitle(noteTitle);
            note.setContent(notecontent);
            note.setTimestamp(Timestamp.now());

            saveNoteTOFirebase(note);
    }
    void saveNoteTOFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            //update the mode
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }



        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note addes Successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while adding note");
                }
            }
        });
    }

    void showDelMenu(){
        //to Display menu
        PopupMenu popupMenu = new PopupMenu(NoteDetailsActivity.this,deleteNoteTextViewBtn);
        popupMenu.getMenu().add("Are you sure delete this content ?");
        popupMenu.getMenu().add("Yes");
        popupMenu.getMenu().add("No");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="No"){
                    saveNote();
                    finish();
                    return true;
                }

                if (menuItem.getTitle().equals("Yes")) {
                    deleteNoteFromFirebase();
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionReferenceForNotes().document(docId);




        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted Successfully");
                    finish();
                }else{
                    Utility.showToast(NoteDetailsActivity.this,"Failed while deleting note");
                }
            }
        });
    }

}