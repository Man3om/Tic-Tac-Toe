package com.example.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

// Removed unused imports: EdgeToEdge, Insets, ViewCompat, WindowInsetsCompat
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private  static final String TAG = "MainActivity";
    // It's good practice to define keys for Intent extras as constants
    // You could also place this in GameActivity or a separate Constants class
    public static final String EXTRA_PLAYER_IS_X = "X_Player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView xImage = findViewById(R.id.X_Image); // Renamed for convention (camelCase)
        ImageView oImage = findViewById(R.id.O_Image); // Renamed for convention (camelCase)

        xImage.setOnClickListener(this);
        oImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Using a local variable for the intent can make the logic slightly cleaner
        // if you had more complex conditions or more actions before starting the activity.
        Intent intent = new Intent(this, GameActivity.class);
        boolean isPlayerX = false; // Default to O player

        int viewId = v.getId();
        if (viewId == R.id.X_Image) {
            Log.d(TAG, "User Choose X");
            isPlayerX = true;
        } else if (viewId == R.id.O_Image) {
            Log.d(TAG, "User Choose O");
            isPlayerX = false; // Explicitly set, though it's the default
        } else {
            // Handle unexpected view clicks if necessary, or simply return
            return;
        }

        intent.putExtra(EXTRA_PLAYER_IS_X, isPlayerX);
        startActivity(intent);
    }
}
