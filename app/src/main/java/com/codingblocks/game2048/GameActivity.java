package com.codingblocks.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private Game2048 game;
    private int N = 5;
    private int destTile = 2048;

    private int[][] tiles;
    private int score;
    private int highScore;

    private LinearLayout gridBox;
    private TextView scoreBox, bestScoreBox;

    private int threshold = 100;
    private Long lastTouchTime;
    private float lastTouchX;
    private float lastTouchY;

    private SharedPreferences.Editor editor;
    String TAG = "Game2048";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            N = b.getInt(String.valueOf(R.string.gridSize));
            destTile = b.getInt(String.valueOf(R.string.destination));
        }

        //initialising tiles matrix
        tiles=new int[N][N];
        game = new Game2048(N, destTile);

        gridBox = (LinearLayout) findViewById(R.id.gridBox);
        setUpGrid();

        scoreBox = (TextView) findViewById(R.id.scoreBox);
        bestScoreBox = (TextView) findViewById(R.id.bestScoreBox);


        //set up shared preferences
        SharedPreferences sp=getSharedPreferences("game", Context.MODE_PRIVATE);
        highScore=sp.getInt("highScore", 0);
        game.setHighScore(highScore);
        editor=sp.edit();

        if (savedInstanceState != null) {
            score=savedInstanceState.getInt("score");
            highScore=savedInstanceState.getInt("highScore");
            tiles= (int[][]) savedInstanceState.getSerializable("tiles");
            game.setTiles(tiles);
            game.setScore(score);

        }
        else {
            //setting up if savedInstance is null
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    tiles[i][j] = 0;
            score = 0;
            highScore = 0;
            game.startNewGame();
            newTile();
            newTile();
        }


        playGame();

        Button newGameButton = (Button) findViewById(R.id.newGame);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder winDialog = new AlertDialog.Builder(GameActivity.this);
                winDialog.setMessage("Are you sure you want to restart?");
                winDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.startNewGame();
                        newTile();
                        newTile();
                        highScore=game.getHighScore();
                        editor.putInt("highScore", highScore);
                        editor.commit();

                        updateTiles();
                    }
                });

                winDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                winDialog.show();

            }
        });

        Button undoButton = (Button) findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.undoMove();
                updateTiles();
            }
        });

    }

    private void setUpGrid() {
        for (int i = 0; i < N; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            linearLayout.setLayoutParams(lp);

            for (int j = 0; j < N; j++) {

                int id = i * N + j;
                TextView tv = new TextView(this);
                tv.setId(id);
                LinearLayout.LayoutParams textview_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                tv.setLayoutParams(textview_lp);

                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(null, Typeface.BOLD);

                linearLayout.addView(tv);
            }
            gridBox.addView(linearLayout);
        }

    }

    private void playGame() {

        updateTiles();

        gridBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Long currentTime = System.currentTimeMillis();

                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    //Log.i(TAG, "touch up " + currentTime);
                    //Log.i(TAG, "diff: " + (currentTime - lastTouchTime));

                    if ((currentTime - lastTouchTime) >= threshold) {

                        float diffX = event.getX() - lastTouchX;
                        float diffY = event.getY() - lastTouchY;

                        int action = 0;
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (diffX < 0) {
                                action = Game2048.LEFT;

                            } else if (diffX > 0) {
                                action = Game2048.RIGHT;
                            }
                        } else {

                            if (diffY < 0) {
                                action = Game2048.UP;
                            } else if (diffY > 0) {
                                action = Game2048.DOWN;
                            }
                        }
                        if (action != 0) {
                            if(game.move(action))
                                newTile();

                            updateTiles();
                        }

                        if (game.isOver()) {

                            if (game.reachedDest()) {
                                AlertDialog.Builder winDialog = new AlertDialog.Builder(GameActivity.this);
                                winDialog.setTitle("Congratulations-You win");
                                winDialog.setMessage("You have reached the destination tile!!");

                                winDialog.setNeutralButton("New Game", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        game.startNewGame();
                                        newTile();
                                        newTile();
                                        highScore=game.getHighScore();
                                        editor.putInt("highScore", highScore);
                                        editor.commit();

                                        updateTiles();
                                    }
                                });

                                winDialog.show();
                            } else {
                                AlertDialog.Builder loseDialog = new AlertDialog.Builder(GameActivity.this);
                                loseDialog.setTitle("Game Over");
                                loseDialog.setMessage("OOPS you couldn't reach the destination tile");
                                loseDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        game.gameEnd();
                                        highScore=game.getHighScore();
                                        editor.putInt("highScore",highScore);
                                        editor.commit();

                                        GameActivity.this.finish();
                                    }
                                });
                                loseDialog.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        game.startNewGame();
                                        newTile();
                                        newTile();
                                        highScore=game.getHighScore();
                                        editor.putInt("highScore",highScore);
                                        editor.commit();

                                        updateTiles();
                                    }
                                });

                                loseDialog.show();
                            }
                        }

                        if (game.reachedDest()) {
                            //Congratulations ,ask if want to continue or want to start new game

                            AlertDialog.Builder winDialog = new AlertDialog.Builder(GameActivity.this);
                            winDialog.setTitle("Congratulations -You win");
                            winDialog.setMessage("You have reached the destination tile!");
                            winDialog.setPositiveButton("Keep Playing", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    game.setReachedDest(false);
                                }
                            });

                            winDialog.setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    game.startNewGame();
                                    newTile();
                                    newTile();

                                    highScore=game.getHighScore();
                                    editor.putInt("highScore",highScore);
                                    editor.commit();

                                    updateTiles();
                                }
                            });

                            winDialog.show();
                        }
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    // Log.i(TAG, "touch down " + currentTime);

                    lastTouchTime = currentTime;
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                }
                return true;
            }
        });

    }

    private void updateTiles() {

        tiles=game.getTiles();
        score=game.getScore();
        highScore=game.getHighScore();
        scoreBox.setText(String.valueOf(score));
        bestScoreBox.setText(String.valueOf(highScore));

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                int id = i * N + j;
                TextView tv = (TextView) gridBox.findViewById(id);

                int no = tiles[i][j];

                if (tv != null) {
                    tv.setText(String.valueOf(no));

                    switch (no) {
                        case 0:
                            tv.setText("");
                            tv.setBackgroundResource(R.drawable.rectangle);
                            break;
                        case 2:
                            tv.setBackgroundResource(R.drawable.tile2);
                            break;
                        case 4:
                            tv.setBackgroundResource(R.drawable.tile4);
                            break;
                        case 8:
                            tv.setBackgroundResource(R.drawable.tile8);
                            break;
                        case 16:
                            tv.setBackgroundResource(R.drawable.tile16);
                            break;
                        case 32:
                            tv.setBackgroundResource(R.drawable.tile32);
                            break;
                        case 64:
                            tv.setBackgroundResource(R.drawable.tile64);
                            break;
                        case 128:
                            tv.setBackgroundResource(R.drawable.tile128);
                            break;
                        case 256:
                            tv.setBackgroundResource(R.drawable.tile256);
                            break;
                        case 512:
                            tv.setBackgroundResource(R.drawable.tile512);
                            break;
                        case 1024:
                            tv.setBackgroundResource(R.drawable.tile1024);
                            break;
                        case 2048:
                            tv.setBackgroundResource(R.drawable.tile2048);
                            break;
                        default:
                            tv.setBackgroundResource(R.drawable.tile1024);
                            break;
                    }
                    if (no >= 8)
                        tv.setTextColor(Color.WHITE);
                    else
                        tv.setTextColor(Color.argb(255, 137, 125, 113));

                }

            }
        }

    }

    private void newTile() {
        Pair<Integer, Integer> p = game.createNewTile();

        tiles = game.getTiles();

        int i = p.first;
        int j = p.second;

        int id = i * N + j;
        TextView tv = (TextView) gridBox.findViewById(id);
        tv.setText(String.valueOf(tiles[i][j]));

        if(tiles[i][j]==2)
            tv.setBackgroundResource(R.drawable.tile2);
        else if(tiles[i][j]==4)
            tv.setBackgroundResource(R.drawable.tile4);

        //ANIMATION TO MAKE THE TILE APPEAR
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(100);

        tv.startAnimation(scaleAnimation);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("score", score);
        outState.putInt("highScore",highScore);

        outState.putSerializable("tiles",tiles);
    }
}
