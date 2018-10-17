package android.example.com.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.OnCountryPickerListener;

public class HomeActivity extends AppCompatActivity implements OnCountryPickerListener {

    private Button mCountrySelectionButton;
    private TextView mCountryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mCountryTextView = findViewById(R.id.country_textView);

        mCountrySelectionButton = findViewById(R.id.change_country_button);
        mCountrySelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //using  country-picker-android library
                //from https://github.com/mukeshsolanki/country-picker-android
                //create Country Picker Builder
                CountryPicker.Builder builder =
                        new CountryPicker.Builder().with(HomeActivity.this)
                                .listener(HomeActivity.this)
                                .canSearch(true)
                                .sortBy(CountryPicker.SORT_BY_NAME);

                //using the builder, create Country Picker object
                CountryPicker picker = builder.build();

                //show the picker dialogue
                picker.showDialog(HomeActivity.this);
            }
        });
    }

    @Override
    public void onSelectCountry(Country country) {
        //change button text to Change
        mCountrySelectionButton.setText(getString(R.string.change));

        //make country text view visible
        mCountryTextView.setVisibility(View.VISIBLE);

        //set country text view text to the name of the selected country
        mCountryTextView.setText(country.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.meun_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            //sign user out
            FirebaseAuth.getInstance().signOut();

            //navigate to LoginActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);

            //finish this activity
            HomeActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
