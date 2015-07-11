package es.zgzappstore.equipoa.handicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_next = (Button) findViewById(R.id.btn_main_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
