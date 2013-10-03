package ru.thegoncharov.authwatch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import ru.thegoncharov.authwatch.R;

public class DonationActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private static final String[] donationCodes =
            new String[] {"donate1", "donate2", "donate3", "donate4", "donate5"};
    private static final String[] donationTitles =
            new String[] {"$1", "$2", "$3", "$4", "$5"};

    private TextView donationPrice;
    private SeekBar donationBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donationPrice = (TextView)findViewById(R.id.donation_price);

        donationBar = (SeekBar)findViewById(R.id.donation_bar);
        donationBar.setMax(donationCodes.length - 1);
        donationBar.setOnSeekBarChangeListener(this);

        Button donateButton = (Button) findViewById(R.id.donation_confirm);

        Button cancelButton = (Button) findViewById(R.id.donation_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        onProgressChanged(donationBar, 0, false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        donationPrice.setText(donationTitles[progress]);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}