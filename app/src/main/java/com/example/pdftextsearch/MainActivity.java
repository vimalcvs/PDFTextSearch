package com.example.pdftextsearch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.xeoh.android.texthighlighter.TextHighlighter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    PDFView pdfView;
    String extractedText="";
    FloatingActionButton nextWord;
    ImageButton btn_search;
    
    Integer j;
    List<Integer>aparitii= new ArrayList<>();
    EditText searchPDF;
    List<Integer> listapagini= new ArrayList<>();

    public static final String SAMPLE_FILE = "res/raw/sample.pdf";
    public static final String SAMPLE_FILE2 = "sample.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        pdfView= findViewById(R.id.pdfview);

        pdfView.fromAsset(SAMPLE_FILE2)
                .load();

        searchPDF = findViewById(R.id.searchInPDF);
        nextWord = findViewById(R.id.nextword);
        btn_search = findViewById(R.id.btn_searchinpdf);
        nextWord.setVisibility(View.GONE);

        searchPDF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btn_search.setOnClickListener(v -> {
                    s.toString();
                    if (s.toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this,"You have to enter a word!",Toast.LENGTH_LONG).show();
                    }else {
                        if(listapagini.size()==0){
                            matchWord(s.toString().toLowerCase(),extractedText);
                        }else {
                            Toast.makeText(MainActivity.this,"You have to go through all occurrences of the word already searched to search for another word!",Toast.LENGTH_LONG).show();
                        }
                    }});
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_search.setOnClickListener(v -> {
                    s.toString();
                    if (s.toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "You have to enter a word!", Toast.LENGTH_LONG).show();
                    } else {
                        if (listapagini.size() == 0) {
                            matchWord(s.toString().toLowerCase(), extractedText);
                        } else {
                            Toast.makeText(MainActivity.this, "You have to go through all occurrences of the word already searched to search for another word!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
                btn_search.setOnClickListener(v -> {
                    s.toString();
                    if (s.toString().trim().isEmpty()){
                        Toast.makeText(MainActivity.this,"You have to enter a word!",Toast.LENGTH_LONG).show();
                    }else {
                        if(listapagini.size()==0){
                            matchWord(s.toString().toLowerCase(),extractedText);
                        }else {
                            Toast.makeText(MainActivity.this,"You have to go through all occurrences of the word already searched to search for another word!",Toast.LENGTH_LONG).show();
                        }
                    }});
            }
        });
        extractTextPDF();
    }

    HashMap<Integer,String> map = new HashMap<>();

    private void extractTextPDF(){
        new Thread(() -> {
            try {
                PdfReader reader = new PdfReader(MainActivity.SAMPLE_FILE);
                int n = reader.getNumberOfPages();
                for (int i = 0; i < n; i++) {
                    String extractText = PdfTextExtractor.getTextFromPage(reader, i + 1).trim().toLowerCase();
                    extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim().toLowerCase() + "\n";map.put(i + 1, extractText);
                }
                reader.close();
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }
        }).start();
    }
    private void matchWord(String cuvant,String extractedText){
        nextWord.setVisibility(View.VISIBLE);
        if(extractedText.contains(cuvant)){
            for (Map.Entry<Integer, String> integerStringEntry : map.entrySet()) {
                String text = ((Map.Entry) integerStringEntry).getValue().toString();
                Integer pagina = (Integer) ((Map.Entry) integerStringEntry).getKey();
                if (text.contains(cuvant)) {
                    listapagini.add(pagina);
                    aparitii.add(pagina);

                }
            }
        }
        if (listapagini.size()==1){
            Toast.makeText(this, "The searched word has only one appearance.", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "The searched word has"+ listapagini.size() + " ISSUES", Toast.LENGTH_LONG).show();}
        nextWord.setOnClickListener(v -> {
            if(aparitii.size()!=0 && listapagini.size()!=0){
                j=0;
                pdfView.jumpTo(listapagini.get(j)-1);
                if(aparitii.contains(listapagini.get(j))){
                    aparitii.remove(listapagini.get(j));
                    listapagini.remove(listapagini.get(j));
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Look for another word!" , Toast.LENGTH_LONG).show();
                nextWord.setVisibility(View.GONE);
            }
        });
    }
}
