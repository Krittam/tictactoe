package com.tictactoe.tictactoe;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tictactoe.tictactoe.tictactoe.Game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> inputButtonIDs = new ArrayList<String>(Arrays.asList("x00","x01","x02","x10","x11","x12","x20","x21","x22"));
    public TextView result;
    private static final String TAG = "GameScreen";
    public Game game= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView) findViewById(R.id.result);
        game = new Game(this);
        for(final String id : inputButtonIDs){
            TextView tv = (TextView)findViewById(getResources().getIdentifier(id,"id",getPackageName()));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"Click registered on text view with id "+id);
                    int x = Integer.parseInt(""+id.charAt(1));
                    int y = Integer.parseInt(""+id.charAt(2));
                    Log.d(TAG,"game instance give input "+x+" "+y);
                    game.input(x,y);
                }
            });
        }
        showPlayerOptions();
    }

    public void announceResult(final String winner){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String r = "Its a tie!";
                if (winner !=null){
                    r = winner+ " wins !!";
                }
                else{
                    r = "Its a tie !!";
                }
                result.setText(r);
            }
        });

    }

    public void notifyMove(final int playerCode, final int x, final int y){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Player "+playerCode+" made a move "+x+" "+y);
                String id = "x"+x+""+y;
                TextView tv = (TextView)findViewById(getResources().getIdentifier(id,"id",getPackageName()));
                if (playerCode==0){
                    tv.setText("X");
                }
                else{
                    tv.setText("O");
                }
            }
        });
    }


    public void showPlayerOptions(){
        final Dialog playerDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.select_player,null);
        playerDialog.setContentView(view);
        playerDialog.setTitle("Select Players ");
        final RadioGroup playerAList = (RadioGroup) view.findViewById(R.id.playerA_types);
        final RadioGroup playerBList = (RadioGroup) view.findViewById(R.id.playerB_types);
        final EditText playerAName = (EditText) view.findViewById(R.id.playerA_name);
        final EditText playerBName = (EditText) view.findViewById(R.id.playerB_name);
        playerAName.setVisibility(View.INVISIBLE);
        playerBName.setVisibility(View.INVISIBLE);
        final LinkedHashMap<RadioGroup,EditText> playerLists = new LinkedHashMap<RadioGroup,EditText>();
        playerLists.put(playerAList,playerAName);
        playerLists.put(playerBList,playerBName);
        for (Map.Entry<RadioGroup,EditText> e :playerLists.entrySet()){
            final RadioGroup l = e.getKey();
            final EditText t = e.getValue();
            l.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    int checkedIndex = l.indexOfChild(view.findViewById(checkedId));
                    Log.d(TAG, "Player type changed to "+checkedIndex);
                    if (checkedIndex==0){
                        t.setVisibility(View.VISIBLE);
                        t.requestFocus();
                    }
                    else {
                        t.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        Button done = (Button) view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                for (Map.Entry<RadioGroup,EditText> e :playerLists.entrySet()){
                    int radioID =e.getKey().getCheckedRadioButtonId();
                    if (radioID>-1){
                        Log.d(TAG,"value of i is "+i);
                        String selected = ((RadioButton)view.findViewById(radioID)).getText().toString();
                        if (selected==getString(R.string.player_human)){
                            sendPlayerDetails(i , 0, e.getValue().getText().toString());
                        }
                        else if(selected==getString(R.string.player_computer)){
                            sendPlayerDetails(i,1 , e.getValue().getText().toString());
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Please select player types !",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i++;
                }
                playerDialog.dismiss();
                startGame();
            }
        });
        playerDialog.show();
    }
    public void sendPlayerDetails(int playerCode, int playerType, String name){
        Log.d(TAG,"Player details recorded as playercode"+playerCode+" playertype "+playerType+" name "+name);
        switch (playerCode){
            case 0:
                game.setPlayerA(playerType,name);
                Log.i(TAG,name+" set as playerA");
                break;
            case 1:
                game.setPlayerB(playerType,name);
                Log.i(TAG,name+" set as playerB");
                break;
        }
    }

    public void startGame(){
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Listeners set;Game started !");
                String winner = game.start();
                Log.d(TAG,"Winner is "+winner);
                announceResult(winner);
            }
        });
        gameThread.start();
    }
}
