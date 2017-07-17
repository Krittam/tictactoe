package com.tictactoe.tictactoe.tictactoe;
import android.util.Log;
import com.tictactoe.tictactoe.MainActivity;
import java.util.ArrayList;

/**
 * Created by krittam on 24/6/17.
 */

public class Game {
    private Player playerA;
    private Player playerB;
    private Player currentPlayer;
    private static int STATUS_TIE = 2;
    private static int STATUS_ON = -1;
    private int[][] status = new int[][]{{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
    private ArrayList<Triplet> winningTriplets;
    private MainActivity gameScreen;
    private int winner = -1;


    private static final String TAG = "TicTacToe";
    public Game(MainActivity instance){
        gameScreen = instance;
        winningTriplets = new ArrayList<Triplet>();
        winningTriplets.add(new Triplet(new int[]{0,0},new int[]{0,1},new int[]{0,2}));
        winningTriplets.add(new Triplet(new int[]{1,0},new int[]{1,1},new int[]{1,2}));
        winningTriplets.add(new Triplet(new int[]{2,0},new int[]{2,1},new int[]{2,2}));
        winningTriplets.add(new Triplet(new int[]{0,0},new int[]{1,0},new int[]{2,0}));
        winningTriplets.add(new Triplet(new int[]{0,1},new int[]{1,1},new int[]{2,1}));
        winningTriplets.add(new Triplet(new int[]{0,2},new int[]{1,2},new int[]{2,2}));
        winningTriplets.add(new Triplet(new int[]{0,0},new int[]{1,1},new int[]{2,2}));
        winningTriplets.add(new Triplet(new int[]{0,2},new int[]{1,1},new int[]{2,0}));
    }

    public String start(){
        while (nextMove()<0){

        }
        if (winner==0){
            return playerA.getName();
        }
        else if (winner==1){
            return playerB.getName();
        }
        else{
            return null;
        }
    }

    public int nextMove(){
        Log.d(TAG,"waiting for "+currentPlayer.getName()+" to make next move");
        int[] move = currentPlayer.play();
        status[move[0]][move[1]]= currentPlayer.getIdentifier();
        int gameStatus = checkStatus();
        gameScreen.notifyMove(currentPlayer.getIdentifier(),move[0],move[1]);
        switchTurns();
        if (gameStatus!=STATUS_ON & gameStatus!=STATUS_TIE){
            winner = gameStatus;
        }
        return gameStatus;
    }

    public int[][] getGameState(){
        return status;
    }
    public int checkStatus(){
        for (Triplet t : winningTriplets){
            int result = decideWinner(t);
            if (result >=0){
                Log.d(TAG,"Player "+result+" identified as winner");
                return result;
            }
        }
        if (!movesLeft()){
            return STATUS_TIE;
        }
        return STATUS_ON;
    }
    private boolean movesLeft(){
        for (int x=0;x<status.length;x++){
            for (int y=0;y<status[x].length;y++){
                if (status[x][y]==-1){
                    return true;
                }
            }
        }
        return false;
    }

    private int decideWinner(Triplet t){
        int [] values = t.getValues();
        if (values[0]!=-1 & values[0] ==values[1] & values[1]==values[2]){
            return values[0];
        }
        else {
            return -1;
        }
    }
    private void switchTurns(){
        if (currentPlayer==playerA){
            currentPlayer = playerB;
        }
        else{
            currentPlayer = playerA;
        }
    }

    public void input (int x, int y){
        currentPlayer.input(x,y);
    }

    private class Triplet{
        int[] values=new int[] {-1,-1,-1} ;
        ArrayList<int[]> indices = new ArrayList<int[]>();
        private Triplet(int[] positionA, int[] positionB, int[] positionC){
            indices.add(positionA);
            indices.add(positionB);
            indices.add(positionC);
        }
        private int[] getValues(){
            for(int i=0; i<3;i++){
                int[] p = indices.get(i);
                values[i] = status[p[0]][p[1]];
            }
            return values;
        }
    }

    public void setPlayerA(int playerType , String playerName) {
        Log.i(TAG,playerName+" set as playerA");
        if (playerType==Player.PlayerCodeHuman){
            this.playerA = new Human(this,playerName,0);
        }
        else{
            this.playerA = new Computer(this,0);
        }
        currentPlayer = playerA;
    }

    public void setPlayerB(int playerType , String playerName) {
        Log.i(TAG,playerName+" set as playerB");
        if (playerType==Player.PlayerCodeHuman){
            this.playerB = new Human(this,playerName,1);
        }
        else{
            this.playerB = new Computer(this,1);
        }
    }
}
