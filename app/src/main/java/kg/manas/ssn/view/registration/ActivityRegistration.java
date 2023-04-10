package kg.manas.ssn.view.registration;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import kg.manas.ssn.R;
import kg.manas.ssn.view.NavigationHost;

public class ActivityRegistration extends AppCompatActivity implements NavigationHost {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        RegistrationFragmentPage1 registrationFragmentPage1 = new RegistrationFragmentPage1();
        navigateTo(registrationFragmentPage1, true);
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
