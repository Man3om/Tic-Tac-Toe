package com.example.tic_tac_toe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private boolean IsPlayerXTurn = false;

    private ImageView shape;

    private TextView PlayerTurn;

    private String[] Shapes = {
            "", "", "",
            "", "", "",
            "", "", ""
    };

    private int[] Ids = {
            R.id.Shape0, R.id.Shape1, R.id.Shape2,
            R.id.Shape3, R.id.Shape4, R.id.Shape5,
            R.id.Shape6, R.id.Shape7, R.id.Shape8
    };

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boolean IS_X_PLAYER_FIRST = getIntent().getBooleanExtra(MainActivity.EXTRA_PLAYER_IS_X, true);
        PlayerTurn = findViewById(R.id.PlayerTurn);

        if (IS_X_PLAYER_FIRST) {
            Log.d(TAG, "X Player First");
            PlayerTurn.setText(R.string.turn_of_x);
            IsPlayerXTurn = true;
        } else {
            Log.d(TAG, "O Player First");
            PlayerTurn.setText(R.string.turn_of_o);
            IsPlayerXTurn = false;
        }
    }

    public void ShapeClicked(View view) {
        if (!(view instanceof ImageView)) {
            // Should not happen if onClick is only on ImageViews, but good for safety
            Log.w(TAG, "ShapeClicked called on a non-ImageView: " + view);
            return;
        }

        ImageView clickedShapeView = (ImageView) view; // Use a local variable

        int tempBoardIndex = -1;
        for (int i = 0; i < Ids.length; i++) {
            if (Ids[i] == clickedShapeView.getId()) {
                tempBoardIndex = i;
                break;
            }
        }

        if (tempBoardIndex != -1 && !Shapes[tempBoardIndex].isEmpty()) {
            Log.d(TAG, "Cell already played at index: " + tempBoardIndex);
            return; // Don't process if cell is already taken
        }

        shape = clickedShapeView; // Now update the class member 'shape'

        String currentPlayerSymbol;
        int nextPlayerTurnStringRes;
        int currentPlayerWinsStringRes;

        if (IsPlayerXTurn) {
            clickedShapeView.setImageResource(R.drawable.x_shape);
            currentPlayerSymbol = "X";
            nextPlayerTurnStringRes = R.string.turn_of_o;
            currentPlayerWinsStringRes = R.string.x_wins;
        } else {
            clickedShapeView.setImageResource(R.drawable.o_shape);
            currentPlayerSymbol = "O";
            nextPlayerTurnStringRes = R.string.turn_of_x;
            currentPlayerWinsStringRes = R.string.o_wins;
        }

        updateBoardState(currentPlayerSymbol); // Pass the correct player symbol
        clickedShapeView.setClickable(false); // Disable after the move
        counter++;

        boolean isWinner = CheckWinner(currentPlayerSymbol);

        if (isWinner) {
            Log.d(TAG, "Winner: " + currentPlayerSymbol);
            PlayerTurn.setText(currentPlayerWinsStringRes);
            // Consider a short delay before reset so user sees the winning move
             new Handler(Looper.getMainLooper()).postDelayed(this::ResetGame, 1500);
        } else if (counter == 9) { // No winner, and all cells are filled
            Log.d(TAG, "Game is a Draw");
            PlayerTurn.setText(R.string.draw);

            // Consider a short delay
            new Handler(Looper.getMainLooper()).postDelayed(this::ResetGame, 1500);
        } else {
            // Game continues, switch turns
            IsPlayerXTurn = !IsPlayerXTurn;
            PlayerTurn.setText(nextPlayerTurnStringRes);
        }
    }

    private void ResetGame() {
        Log.d(TAG, "Resetting game...");
        for (int i = 0; i < Ids.length; i++) {
            int shapeId = Ids[i];
            ImageView currentShapeView = findViewById(shapeId);

            if (currentShapeView != null) {
                currentShapeView.setImageResource(0); // Clears the image
                currentShapeView.setClickable(true);  // Re-enable clicking for the new game
            } else {
                Log.w(TAG, "ImageView with ID " + shapeId + " not found during reset at index " + i);
            }

            Shapes[i] = ""; // Reset the internal board state for this cell
        }
        counter = 0;        // Reset the move counter *after* the loop

         IsPlayerXTurn = true;            // Reset the turn flag
         PlayerTurn.setText(R.string.turn_of_x); // Update the turn display

        Log.d(TAG, "Game reset complete. Board: " + java.util.Arrays.toString(Shapes) + ", Counter: " + counter);
    }


    private void updateBoardState(String playerSymbol) {
        if (shape == null) {
            Log.e(TAG, "updateBoardState called but 'shape' (ImageView) is null. Cannot proceed.");
            return;
        }
        if (playerSymbol == null || playerSymbol.isEmpty()) {
            Log.e(TAG, "updateBoardState called with null or empty playerSymbol. Cannot proceed.");
            return;
        }

        int clickedShapeId = shape.getId();
        int boardIndex = -1;

        // Find the index in the Ids array that matches the clicked ImageView's ID
        for (int i = 0; i < Ids.length; i++) {
            if (Ids[i] == clickedShapeId) {
                boardIndex = i;
                break; // Found the index, no need to continue looping
            }
        }

        if (boardIndex != -1) {
            // Check if the cell is already taken before updating
            if (Shapes[boardIndex].isEmpty()) { // Assuming empty string "" means unoccupied
                Shapes[boardIndex] = playerSymbol;
                Log.d(TAG, "Board updated at index " + boardIndex + " with symbol: " + playerSymbol);
            } else {
                Log.w(TAG, "Attempted to update cell at index " + boardIndex + " which is already occupied by: " + Shapes[boardIndex]);
            }
        } else {
            Log.e(TAG, "Clicked ImageView ID " + clickedShapeId + " not found in Ids array. This should not happen.");
        }

        Log.d(TAG, "Current board state: " + java.util.Arrays.toString(Shapes));
    }

    private boolean CheckWinner(String symbol) {
        // First, ensure the symbol we are checking for is not an empty string.
        // If we are checking for an empty string, it means no one has played, so no winner.
        if (symbol == null || symbol.isEmpty()) {
            return false;
        }

        // Define winning combinations using indices for clarity
        // Each inner array represents a winning line (3 indices)
        int[][] winningCombinations = {
                // Rows
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                // Columns
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                // Diagonals
                {0, 4, 8}, {2, 4, 6}
        };

        // Iterate through each winning combination
        for (int[] combination : winningCombinations) {
            // Check if all three cells in this combination are occupied by the current symbol
            if (Shapes[combination[0]].equals(symbol) &&
                    Shapes[combination[1]].equals(symbol) &&
                    Shapes[combination[2]].equals(symbol)) {
                return true; // Found a winner
            }
        }

        return false; // No winner found
    }

}

