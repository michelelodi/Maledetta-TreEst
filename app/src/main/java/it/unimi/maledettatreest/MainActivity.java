package it.unimi.maledettatreest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationBarView;
import java.util.Objects;
import it.unimi.maledettatreest.model.Direction;

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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

            if (getSharedPreferences(APP_PREFS,0).contains(Direction.DID))
                navGraph.setStartDestination(R.id.boardFragment);
            else
                navGraph.setStartDestination(R.id.linesFragment);

            navController.setGraph(navGraph);
        }

        ((NavigationBarView)findViewById(R.id.bottom_navigation)).setOnItemSelectedListener(item -> {
            Class<? extends Fragment> currentFragmentClass = Objects.requireNonNull(navHostFragment)
                    .getChildFragmentManager().getFragments().get(0).getClass();
            switch (item.getItemId()) {
                case R.id.linesBottomNav:
                    if(currentFragmentClass == BoardFragment.class) {
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(BoardFragmentDirections.actionBoardFragmentToLinesFragment());
                    }
                    else if(currentFragmentClass == UserFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(UserFragmentDirections.actionUserFragmentToLinesFragment());
                    else if(currentFragmentClass == AddPostFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(AddPostFragmentDirections.actionAddPostFragmentToLinesFragment());
                    else if(currentFragmentClass == MapsFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(MapsFragmentDirections.actionMapsFragmentToLinesFragment());
                    return true;
                case R.id.userBottomNav:
                    if(currentFragmentClass == BoardFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(BoardFragmentDirections.actionBoardFragmentToUserFragment());
                    else if(currentFragmentClass == LinesFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(LinesFragmentDirections.actionLinesFragmentToUserFragment());
                    else if(currentFragmentClass == AddPostFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(AddPostFragmentDirections.actionAddPostFragmentToUserFragment());
                    else if(currentFragmentClass == MapsFragment.class)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(MapsFragmentDirections.actionMapsFragmentToUserFragment());
                    return true;
                default:
                    return false;
            } });
    }
}