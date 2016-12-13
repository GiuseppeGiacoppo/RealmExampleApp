package me.giacoppo.realmexampleapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.ui.comparison.ComparisonActivity;
import me.giacoppo.realmexampleapp.ui.portafogli.WalletActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView sampleApp, comparison;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.item_example).setOnClickListener(this);
        findViewById(R.id.item_comparison).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_example:
                startActivity(WalletActivity.getIntent(this));
                break;
            case R.id.item_comparison:
                startActivity(ComparisonActivity.getIntent(this));
                break;
        }
    }
}
