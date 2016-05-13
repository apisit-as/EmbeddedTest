package rmuti.embeddedtest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     private SQLiteDatabase db;
    private Cursor c;
    int id;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database
        db = this.openOrCreateDatabase("mydatabase",MODE_PRIVATE,null);
        String sql = ""
                + " CREATE TABLE IF NOT EXISTS note("
                + "   id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + "   topic VARCHAR," + " detail VARCHAR" + " )";
        db.execSQL(sql);
        bindData();
    }

    private void bindData(){
        String sql = "SELECT * FROM note";
        c = db.rawQuery(sql, null);

        int item = android.R.layout.simple_list_item_1;
        ArrayList data = new ArrayList();

        while(c.moveToNext()){
            int index = c.getColumnIndex("topic");
            data.add(c.getString(index));
        }

        adapter = new ArrayAdapter(this, item, data);

        ListView myList = (ListView) findViewById(R.id.myList);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int i, long l) {
                itemClick(i);
            }
        });
        bindSearch();
    }
    public void bindSearch(){
        //SEARCH
        EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(arg0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void doNew(View v){
        setContentView(R.layout.form);
    }

    public void doSave(View v){
        EditText txtTopic = (EditText) findViewById(R.id.txtTopic);
        EditText txtDetail = (EditText) findViewById(R.id.txtDetail);

        String topic = txtTopic.getText().toString();
        String detail = txtDetail.getText().toString();

        String sql = "";

        if(id == 0){
            sql = "INSERT INTO note VALUES(null, ':topic', ':detail')";
            sql = sql.replace(":topic",topic);
            sql = sql.replace(":detail",detail);
            db.execSQL(sql);
        }else{
            ContentValues value = new ContentValues();
            value.put("topic", topic);
            value.put("detail",detail);
            db.update("note", value, "id = "+ id, null);
            id = 0;
          }
        setContentView(R.layout.activity_main);
          bindData();
    }

    public void doHome(View v){
        setContentView(R.layout.activity_main);
        id = 0;
        bindData();
    }
    public void doDelete(View v){
        db.delete("note", "id = " + id, null);
        doHome(v);
    }

    public void itemClick(int index){
        c.moveToPosition(index);
        id = c.getInt(c.getColumnIndex("id"));

        setContentView(R.layout.form);
        EditText txtTopic = (EditText) findViewById(R.id.txtTopic);
        EditText txtDetail = (EditText) findViewById(R.id.txtDetail);

        txtTopic.setText(c.getString(c.getColumnIndex("topic")));
        txtDetail.setText(c.getString(c.getColumnIndex("detail")));
    }

}
