package com.ejemplo.enrutamientoavanzado;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ejemplo.enrutamientoavanzado.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usamos View Binding para cargar la pantalla principal.
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Usamos nuestra Toolbar como barra superior.
        setSupportActionBar(binding.toolbar);

        // Buscamos el NavHost que contiene los fragmentos.
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(
                        R.id.nav_host_fragment_content_main
                );

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Conectamos la barra con el grafo de navegación.
            appBarConfiguration =
                    new AppBarConfiguration.Builder(navController.getGraph()).build();

            NavigationUI.setupActionBarWithNavController(
                    this,
                    navController,
                    appBarConfiguration
            );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Hacemos que la flecha de la barra vuelva atrás.
        if (navController != null && appBarConfiguration != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
