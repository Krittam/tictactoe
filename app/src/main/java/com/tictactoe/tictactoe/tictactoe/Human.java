package com.tictactoe.tictactoe.tictactoe;
import android.util.Log;
import java.util.concurrent.CountDownLatch;

/**
 * Created by krittam on 24/6/17.
 */

public class Human implements Player {
    private String name = "";
    private int identifier;
    private Game game;
    private int[] move = new int[]{-1,-1};
    private CountDownLatch latch;
    private static final String TAG = "Human_Player";
    public Human(Game g,String n, int i){
        game = g;
        name = n;
        identifier =i;
    }
    @Override
    public int [] play(){
        latch = new CountDownLatch(1);
        try {
            Log.d(TAG,"Waiting for user input");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"user input recorded as "+move[0]+" "+move[1]);
        return move;
    }

    @Override
    public void input(int x, int y) {
        Log.d(TAG,this.getName()+" input recorded as "+x+" "+y);
        int[][] state = game.getGameState();
        if (state[x][y]==-1){
            move[0]=x;
            move[1]=y;
            latch.countDown();
            Log.d(TAG,"latch removed; move set to "+move[0]+" "+move[1]);
        }
        else {
            Log.d(TAG,"illegal move");
        }
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

}
