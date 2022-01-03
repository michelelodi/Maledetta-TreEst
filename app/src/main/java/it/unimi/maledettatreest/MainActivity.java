package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationBarView;
import java.util.Objects;
import it.unimi.maledettatreest.model.Line;

public class MainActivity extends AppCompatActivity {

    public static final String DOESNT_EXIST = "-1";
    public static final String ERROR = "ERROR";
    public static final String OK = "Ok";
    public static final String TAG_BASE = "MALEDETTATREEST_";
    public static final String APP_PREFS = "prefs";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        ((NavigationBarView)findViewById(R.id.bottom_navigation)).setOnItemSelectedListener(item -> {
            Class<? extends Fragment> currentFragmentClass = Objects.requireNonNull(navHostFragment)
                    .getChildFragmentManager().getFragments().get(0).getClass();
            switch (item.getItemId()) {
                case R.id.linesBottomNav:
                    if(currentFragmentClass == BoardFragment.class) {
                        SharedPreferences prefs = getSharedPreferences(MainActivity.APP_PREFS,0);
                        prefs.edit().remove(Line.DID)
                                .remove(Line.SNAME)
                                .remove(Line.LNAME)
                                .remove(Line.REVERSE_DID)
                                .remove(Line.REVERSE_SNAME).apply();

                        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(BoardFragmentDirections.actionBoardFragmentToLinesFragment());
                    }
                    else if(currentFragmentClass == UserFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(UserFragmentDirections.actionUserFragmentToLinesFragment());
                    else if(currentFragmentClass == AddPostFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(AddPostFragmentDirections.actionAddPostFragmentToLinesFragment());
                    return true;
                case R.id.userBottomNav:
                    if(currentFragmentClass == BoardFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(BoardFragmentDirections.actionBoardFragmentToUserFragment());
                    else if(currentFragmentClass == UserFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(LinesFragmentDirections.actionLinesFragmentToUserFragment());
                    else if(currentFragmentClass == AddPostFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(AddPostFragmentDirections.actionAddPostFragmentToUserFragment());
                    return true;
                default:
                    return false;
            } });
    }
}