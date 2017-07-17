package com.tictactoe.tictactoe.tictactoe;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static android.R.attr.y;

/**
 * Created by krittam on 24/6/17.
 */

class Computer implements Player {
    private String name = "Bot";
    private int identifier;
    private Game game;
    private int[] move = new int[]{-1,-1};
    private static final String TAG = "Computer_Player";
    static int TYPE_MINI = 0;
    static int TYPE_MAX = 1;
    Computer(Game g, int i){
        game = g;
        identifier =i;
    }
    static int traversals = 0;

    @Override
    public int[] play() {
        Log.d(TAG,"searching for best move");
        traversals= 0;
        GameNode currentState =new GameNode(this.game.getGameState(),new int[]{-1,-1},Computer.TYPE_MAX,this);
        int [] bestMoveDetails = findBestMove(currentState,Integer.MAX_VALUE);
        Log.d(TAG,"Best move found in "+traversals+" traversals!");
        return new int[]{bestMoveDetails[1],bestMoveDetails[2]};
    }

    @Override
    public void input(int x, int y) {
    //no fucks given
        Log.d(TAG,"Computer player doesn't accept user input");
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    private int[] findBestMove(GameNode node, int bestValueAtParent){
//      Log.d(TAG,"finding best move from node "+node.print());
        traversals++;
        if (node.getWinner()==-1 & node.movesLeft()>0){
//            Log.d(TAG,"node is not terminal; traversing successsors...");
            int bestValue;
            if (node.node_type==Computer.TYPE_MAX){
                bestValue =Integer.MIN_VALUE;
            }
            else{
                bestValue =Integer.MAX_VALUE;
            }
            int moveX = -1;
            int moveY = -1;
            ArrayList<GameNode> successors =  node.getSuccessors();
//            Log.d(TAG,"current node has "+successors.size()+" successors!");
            for (GameNode newState: successors){
//              Log.d(TAG,"traversing child "+newState.print());
                int [] nodeStats = findBestMove(newState, bestValue);
                if (node.node_type==Computer.TYPE_MAX){
                    if (nodeStats[0]>bestValue){
                        bestValue = nodeStats[0];
                        moveX = newState.moveFromParent[0];
                        moveY = newState.moveFromParent[1];
                        if (bestValueAtParent<bestValue){
                            break;
                        }
                    }
                }
                else{
                    if (nodeStats[0]<bestValue){
                        bestValue = nodeStats[0];
                        moveX = newState.moveFromParent[0];
                        moveY = newState.moveFromParent[1];
                        if (bestValueAtParent>bestValue){
                            break;
                        }
                    }
                }
            }
//            Log.d(TAG,"node "+node.print()+" returning"+bestValue+" "+moveX+" "+moveY);
            return new int[]{bestValue,moveX,moveY};
        }
//        Log.d(TAG,"node "+node.print()+" returning"+node.quantify()+" "+-1+" "+-1);
        return  new int[]{node.quantify(),-1,-1};
    }
    private class GameNode{
        final int[][] state;
        int node_type;
        int [] moveFromParent ;
        Computer instance;
        int winner=-2;
        int movesLeft = -1;
        GameNode(int[][] s, int[] m, int t, Computer i){
            this.state =s;
            this.node_type = t;
            this.moveFromParent = m;
            this.instance = i;
        }
        int quantify(){
//            Log.d(TAG,"quantify called on node "+this.print());
            if(winner==-2){
                Log.e(TAG, "You should not be seeing this; getWinner not called before quantify function !!");
                winner = getWinner();
            }
            if (movesLeft==-1){
                Log.e(TAG, "You should not be seeing this; movesLeft not called before quantify function !!");
                movesLeft = movesLeft();
            }
            if (winner>=0){
                if (winner==instance.getIdentifier()){
//                    Log.d(TAG,"quantify returning "+utility);
                    return 1+movesLeft;
                }
                else{
//                    Log.d(TAG,"quantify returning "+ utility);
                    return -1-movesLeft;
                }
            }
            if(movesLeft==0){
//                Log.d(TAG,"quantify returning 0");
                return 0;
            }
            else{
                Log.e(TAG,"You should not be seeing this; Method quantify called on a non terminal node !!");
                return 0;
            }
        }
        ArrayList<GameNode> getSuccessors(){
            ArrayList<GameNode> successors = new ArrayList<GameNode>();
            for(int x=0;x< state.length;x++){
                for(int y=0;y< state[x].length;y++){
                    if (state[x][y]==-1){
                        int[][] newState =  new int[3][3];
                        for(int m=0;m< newState.length;m++){
                            for(int n=0;n< newState[m].length;n++){
//                                Log.d(TAG,"m n "+m+" "+n);
                                newState[m][n]=state[m][n];
                            }
                        }
//                        Log.d(TAG,"cloned state "+Arrays.deepToString(newState));
                        if (this.node_type==Computer.TYPE_MAX){
                            newState[x][y] = instance.getIdentifier();
                        }
                        else{
                            newState[x][y] = (instance.getIdentifier()+1)%2;
                        }
//                        Log.d(TAG,"earlier state "+Arrays.deepToString(state));
                        successors.add (new GameNode(newState,new int[]{x,y},(this.node_type+1)%2,instance));
                    }
                }
            }
            return successors;
        }
        int getWinner(){
            ArrayList<int[][]> winningTriplets = new ArrayList<int[][]>();
            winningTriplets.add(new int[][]{{0,0},{0,1},{0,2}});
            winningTriplets.add(new int[][]{{1,0},{1,1},{1,2}});
            winningTriplets.add(new int[][]{{2,0},{2,1},{2,2}});
            winningTriplets.add(new int[][]{{0,0},{1,0},{2,0}});
            winningTriplets.add(new int[][]{{0,1},{1,1},{2,1}});
            winningTriplets.add(new int[][]{{0,2},{1,2},{2,2}});
            winningTriplets.add(new int[][]{{0,0},{1,1},{2,2}});
            winningTriplets.add(new int[][]{{0,2},{1,1},{2,0}});

            for (int[][] triplet : winningTriplets){
                if (state[triplet[0][0]][triplet[0][1]]!=-1 & state[triplet[0][0]][triplet[0][1]] == state[triplet[1][0]][triplet[1][1]] & state[triplet[1][0]][triplet[1][1]]==state[triplet[2][0]][triplet[2][1]]){
//                    Log.d(TAG,"game node "+this.print()+" identified as terminal on "+ Arrays.deepToString(triplet)+" triplet");
                    winner = state[triplet[0][0]][triplet[0][1]];
                    return winner;
                }

            }
            winner = -1;
            return winner;
        }

        int movesLeft(){
            movesLeft = 0;
            for (int[] aState : state) {
                for (int anAState : aState) {
                    if (anAState == -1) {
                        movesLeft++;
                    }
                }
            }
            return movesLeft;
        }
        String print(){
            String ans="";
            for (int[] aState : state) {
                for (int anAState : aState) {
                    ans += anAState;
                }
            }
            return ans;
        }
    }
}
