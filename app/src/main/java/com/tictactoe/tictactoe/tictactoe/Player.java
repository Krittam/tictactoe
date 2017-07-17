package com.tictactoe.tictactoe.tictactoe;

/**
 * Created by krittam on 24/6/17.
 */

interface Player {
    String name = "playerName";
    static int PlayerCodeHuman = 0;
    static int PlayerCodeComputer = 1;
    public static String PlayerNameComputer = "Bot";
    Game game = null;
    int identifier = 0;
    int[] play();
    void input(int x, int y);
    int getIdentifier();
    String getName();
}
