package es.zgzappstore.equipoa.handicapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btn_next = (Button) findViewById(R.id.btn_home_maps);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        Button btn_list = (Button) findViewById(R.id.btn_home_lists);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ListsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                break;
            case R.id.action_lists:
                Intent list_intent = new Intent();
                list_intent.setClass(getApplicationContext(), ListsActivity.class);
                startActivity(list_intent);
                finish();
                break;
            case R.id.action_map:
                Intent map_intent = new Intent();
                map_intent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(map_intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final boolean[] exit = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.lbl_exit);
        builder.setMessage(R.string.txt_sure_exit);
        builder.setPositiveButton(R.string.button_text_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                customBack();
            }
        });
        builder.setNegativeButton(R.string.button_text_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
    }

    private void customBack() {
        super.onBackPressed();
    }
}
