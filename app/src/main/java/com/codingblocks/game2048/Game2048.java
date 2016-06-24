package com.codingblocks.game2048;

import android.graphics.Color;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by shakshi on 09-06-2016.
 */
public class Game2048 {
    private int N;
    private int destination;

    private int score;
    private int highScore;
    private int[][] tiles;

    private boolean reachedDest;
    private int[][] prevState;
    private int prevScore;

    public static final int LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4;

    public Game2048(int N, int destination) {
        this.N = N;
        this.destination=destination;

        tiles = new int[N][N];
        prevState = new int[N][N];
    }

    public int getScore(){
        return score;
    }

    public int getHighScore(){
        return highScore;
    }

    public int[][] getTiles(){
        return tiles;
    }

    public void undoMove() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                tiles[i][j] = prevState[i][j];

        score = prevScore;
    }

    private boolean allTilesFilled() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private boolean isTileEmpty(int row, int col) {
        return (tiles[row][col] == 0);
    }

    Pair<Integer, Integer> generateRandomNo() {
        double row = Math.floor(Math.random() * N);
        double col = Math.floor(Math.random() * N);
        Pair<Integer, Integer> p = new Pair<>((int) row, (int) col);
        return p;
    }

    public Pair<Integer,Integer> createNewTile() {
        if (allTilesFilled())
            return null;

        Pair<Integer, Integer> p = generateRandomNo();
        while (!isTileEmpty(p.first, p.second))
            p = generateRandomNo();

        double temp = Math.floor(Math.random() * 9 + 1);
        if (temp < 8) {
            tiles[p.first][p.second] = 2;
        } else {
            tiles[p.first][p.second] = 4;
        }
        return p;
    }

    private boolean moveLeft() {
        //for each row
        for (int i = 0; i < N; i++) {
            int[] temp = new int[N];     //temp array- all nos of the row left to right without any blank spaces
            int k = 0;
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != 0)
                    temp[k++] = tiles[i][j];
            }

            while (k < N)
                temp[k++] = 0;

            for (int j = 0; j < N; j++)
                tiles[i][j] = temp[j];

            boolean merge = false;  //to keep track if merge has taken place then again have to shift
            for (int j = 0; j < N - 1; j++) {
                if (tiles[i][j] == tiles[i][j + 1] && tiles[i][j] != 0) {
                    //to reduce complexity update score here itself and check for 2048
                    score += 2 * tiles[i][j];     //score increases by the merged tile no
                    if (tiles[i][j] == destination/2)
                        reachedDest = true;
                    tiles[i][j] = 2 * tiles[i][j];
                    tiles[i][j + 1] = 0;

                    merge = true;
                }
            }

            if (merge) {
                k = 0;
                for (int j = 0; j < N; j++) {
                    if (tiles[i][j] != 0)
                        temp[k++] = tiles[i][j];
                }
                while (k < N)
                    temp[k++] = 0;
                for (int j = 0; j < N; j++)
                    tiles[i][j] = temp[j];
            }

        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != prevState[i][j])
                    return true;
            }
        }
        return false;
    }

    private boolean moveRight() {
        for (int i = 0; i < N; i++) {

            //for each row
            int[] temp = new int[N];     //temp array- all nos of the row left to right without any blank spaces
            int k = 0;
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != 0)
                    temp[k++] = tiles[i][j];
            }

            int j;
            for (j = 0; j < (N-k); j++)
                tiles[i][j]=0;
            for(int l=0;l<k;l++,j++)
                tiles[i][j] = temp[l];

            boolean merge = false;  //to keep track if merge has taken place then again have to shift
            for (j = N-1; j >0; j--) {
                if (tiles[i][j] == tiles[i][j - 1] && tiles[i][j] != 0) {
                    //to reduce complexity update score here itself and check for 2048
                    score += tiles[i][j];     //score increases by the merged tile no
                    if (tiles[i][j] == destination/2)
                        reachedDest = true;

                    tiles[i][j] = 2 * tiles[i][j];
                    tiles[i][j-1] = 0;
                    merge = true;
                }
            }

            if (merge) {
                k = 0;
                for (j = 0; j < N; j++) {
                    if (tiles[i][j] != 0)
                        temp[k++] = tiles[i][j];
                }

                for (j = 0; j < (N-k); j++)
                    tiles[i][j]=0;
                for(int l=0;l<k;l++,j++)
                    tiles[i][j] = temp[l];
            }


        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != prevState[i][j])
                    return true;
            }
        }
        return false;
    }

    private boolean moveUp() {

        for (int j = 0; j < N; j++) {

            //for each column
            int[] temp = new int[N];     //temp array- all nos of the row left to right without any blank spaces
            int k = 0;
            for (int i = 0; i < N; i++) {
                if (tiles[i][j] != 0)
                    temp[k++] = tiles[i][j];
            }

            while (k < N)
                temp[k++] = 0;
            for (int i = 0; i < N; i++)
                tiles[i][j] = temp[i];


            boolean merge = false;  //to keep track if merge has taken place then again have to shift
            for (int i = 0; i < N - 1; i++) {
                if (tiles[i][j] == tiles[i + 1][j] && tiles[i][j] != 0) {
                    //to reduce complexity update score here itself and check for 2048
                    if (tiles[i][j] == destination/2)
                        reachedDest = true;
                    score += tiles[i][j];     //score increases by the merged tile no

                    tiles[i][j] = 2 * tiles[i][j];
                    tiles[i + 1][j] = 0;
                    merge = true;

                }
            }

            if (merge) {
                k = 0;
                for (int i = 0; i < N; i++) {
                    if (tiles[i][j] != 0)
                        temp[k++] = tiles[i][j];
                }


                while (k < N)
                    temp[k++] = 0;
                for (int i = 0; i < N; i++)
                    tiles[i][j] = temp[i];
            }

        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != prevState[i][j])
                    return true;
            }
        }
        return false;

    }

    private boolean moveDown() {
        for (int j = 0; j < N; j++) {

            //for each col
            int[] temp = new int[N];     //temp array- all nos of the row left to right without any blank spaces
            int k = 0;
            for (int i = 0; i <N ; i++) {
                if (tiles[i][j] != 0)
                    temp[k++] = tiles[i][j];
            }

            int i;
            for (i = 0; i < (N-k); i++)
                tiles[i][j]=0;
            for(int l=0;l<k;l++,i++)
                tiles[i][j] = temp[l];


            boolean merge = false;  //to keep track if merge has taken place then again have to shift
            for (i = N-1; i >0; i--) {
                if (tiles[i][j] == tiles[i - 1][j] && tiles[i][j] != 0) {
                    //to reduce complexity update score here itself and check for 2048
                    if (tiles[i][j] == destination/2)
                        reachedDest = true;
                    score += tiles[i][j];     //score increases by the merged tile no

                    tiles[i][j] = 2 * tiles[i][j];
                    tiles[i-1][j] = 0;
                    merge = true;

                }
            }

            if (merge) {
                k = 0;
                for (i = 0; i <N ; i++) {
                    if (tiles[i][j] != 0)
                        temp[k++] = tiles[i][j];
                }

                for (i = 0; i < (N-k); i++)
                    tiles[i][j]=0;
                for(int l=0;l<k;l++,i++)
                    tiles[i][j] = temp[l];
            }

        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != prevState[i][j])
                    return true;
            }
        }
        return false;

    }

    public void startNewGame() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                tiles[i][j] = 0;
                prevState[i][j] = 0;
            }

        if (score > highScore)
            highScore = score;
        score = 0;

    }
    public void gameEnd(){
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                tiles[i][j] = 0;
                prevState[i][j] = 0;
            }

        if (score > highScore)
            highScore = score;
        score = 0;

    }

    public void setTiles(int tiles[][]){
        this.tiles=tiles;
    }

    public void setScore(int score){
        this.score=score;
    }
    public void setHighScore(int highScore){
        this.highScore=highScore;
    }

    public boolean move(int move) {

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                prevState[i][j] = tiles[i][j];
        prevScore = score;
        switch (move) {
            case LEFT:
                if (moveLeft()) return true;
                break;
            case RIGHT:
                if (moveRight()) return true;
                break;
            case UP:
                if (moveUp()) return true;
                break;
            case DOWN:
                if (moveDown()) return true;
                break;
        }
        return false;

    }

    public boolean isOver() {
        if (!allTilesFilled())
            return false;

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N - 1; j++) {
                if (tiles[i][j] == tiles[i][j + 1])
                    return false;
            }

        for (int i = 0; i < N - 1; i++)
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == tiles[i + 1][j])
                    return false;
            }
        return true;
    }

    public boolean reachedDest() {
        return reachedDest;
    }
    public void setReachedDest(boolean reachedDest){
        this.reachedDest=reachedDest;
    }

}
