package com.example.dream.tetris;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import static com.example.dream.tetris.R.id.maxScore;
import static android.support.v4.os.LocaleListCompat.create;

public class MainActivity extends AppCompatActivity {

    Button pause_btn;
    Button leftMove,rightMove,downMove,rotateMove;
    int timeInterval=800;   //时间间隔
    BlockAdapter nextTetrisAdapter;
    int highestScore=0;
    CacheUtils cacheUtils;
    int [][] blockColor=new int[15][10];

    int score=0;
    public String TAG="MainActivity";
    Random random;
    int randColor;
    int rand;
    //表示4*4方块左上角坐标 positio[0]:纵坐标 position[1]:横坐标
    int []position=new int[]{-4,-4};
    int[]qu=new int[4];

    Timer timer;
    int stop=0;

    int ySize=15;
    int xSize=10;
    int [] allBlock=new int[ySize];
    GridView tetrisView;
    List<Integer>blockList=new ArrayList<>();
    List<Integer>nextTetrisList=new ArrayList<>();
    BlockAdapter blockAdapter;

    TextView scoreTextView,maxScoreTextView;
    GridView nextTetrisView;
    int nextRand,nextRandColor;

    int grade;
    boolean isPause=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        leftMove=findViewById(R.id.left_move);
        rightMove=findViewById(R.id.right_move);
        downMove=findViewById(R.id.down_move);
        rotateMove=findViewById(R.id.rotate_move);
        pause_btn=findViewById(R.id.pause);

        tetrisView=findViewById(R.id.tetrisView);
        nextTetrisView=findViewById(R.id.nextTetrisView);
        scoreTextView=findViewById(R.id.score);
        maxScoreTextView=findViewById(R.id.maxScore);

        cacheUtils=new CacheUtils(MainActivity.this,"UserInfo");
        String maxString="";
        try {
            maxString=cacheUtils.getValue("highestScore"+grade,String.valueOf(0));
        }catch (Exception e){e.printStackTrace();}
        try{
            highestScore=Integer.parseInt(maxString.toString());
        }catch (NumberFormatException e){
            highestScore=0;
        }

        maxScoreTextView.setText("最高分："+highestScore);
        scoreTextView.setText("分数："+score);

        leftMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=3;i>=0;i--)
                {
                    if((((leftMath(StateSquare.shape[rand][i],position[1]))>>1)<<1)!=(leftMath(StateSquare.shape[rand][i],position[1])))
                    {
                        return;
                    }
                }
                for(int i=3;i>=0;i--)
                {
                    int line=i+position[0];
                    if(line>=0 && StateSquare.shape[rand][i]!=0)
                    {
                        if((allBlock[line]&(leftMath(StateSquare.shape[rand][i],position[1])>>1))!=0)
                        {
                            return;
                        }
                    }
                }
                position[1]--;
                handler.sendEmptyMessage(1);
            }
        });

        rotateMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextRotate=StateSquare.nextShape[rand];
                for(int i=3;i>=0;i--)
                {
                    int line=i+position[0];
                    if(leftMath(StateSquare.shape[nextRotate][i],position[1])>0x3ff) {
                        return;
                    }else if(StateSquare.shape[nextRotate][i]>0 && line>=ySize){
                        return;
                    }else if(leftMath(leftMath(StateSquare.shape[nextRotate][i],position[1]),-position[1])!=StateSquare.shape[nextRotate][i]){
                        return;
                    }else if(line>0 && line<ySize && (leftMath(StateSquare.shape[nextRotate][i],position[1]) & allBlock[line])!=0){
                        return;
                    }
                }
                rand=nextRotate;
                handler.sendEmptyMessage(1);
            }
        });

        rightMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=3;i>=0;i--)
                {
                    if(((leftMath(StateSquare.shape[rand][i],position[1]))<<1)>0x3ff){
                        return;
                    }
                }
                for(int i=3;i>=0;i--)
                {
                    int line=i+position[0];
                    if(line>=0 && StateSquare.shape[rand][i]!=0){
                        if((allBlock[line] & (leftMath(StateSquare.shape[rand][i],position[1])<<1))!=0){
                            return;
                        }
                    }
                }
                position[1]++;
                handler.sendEmptyMessage(1);
            }
        });
        downMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int down=1<<10;
                for(int i=3;i>=0;i--)
                {
                    int line=i+position[0];
                    if(line>=0 && StateSquare.shape[rand][i]!=0){
                        down=Math.min(down,ySize-line-1);
                        for(int j=0;j<xSize;j++)
                        {
                            if(((1<<j) & (leftMath(StateSquare.shape[rand][i],position[1])))!=0){
                                for (int k=0;k+line<ySize;k++)
                                {
                                    if(blockColor[k+line][j]>0){
                                        down=Math.min(down,k-1);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(down<-0 || down==(1<<10)){
                    return;
                }else {
                    position[0]+=down;
                    handler.sendEmptyMessage(0);
                }
            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        for (int i=0;i<10;i++)
        {
            for(int j=0;j<15;j++)
            {
                blockList.add(0);
            }
        }

        blockAdapter=new BlockAdapter(MainActivity.this,blockList,R.layout.item_adapter);
        tetrisView.setAdapter(blockAdapter);

        random=new Random();
        rand=random.nextInt(19);
        position[0]=StateSquare.initPosition[rand][1];
        position[1]=StateSquare.initPosition[rand][0];
        randColor=random.nextInt(5)+1;
        nextRand=random.nextInt(19);
        nextRandColor=random.nextInt(5)+1;
        nextTetrisList.clear();
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                if(((1<<j)&StateSquare.shape[nextRand][i])!=0){
                    nextTetrisList.add(nextRandColor);
                }else {
                    nextTetrisList.add(0);
                }
            }
        }
        nextTetrisAdapter=new BlockAdapter(MainActivity.this,nextTetrisList,R.layout.item_adapter);
        nextTetrisView.setAdapter(nextTetrisAdapter);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },0,timeInterval);
    }

    private void pause()
    {
        isPause=!isPause;
        if(isPause){
            stopTimer();
            pause_btn.setText("继续");
            leftMove.setEnabled(false);
            rightMove.setEnabled(false);
            rotateMove.setEnabled(false);
            downMove.setEnabled(false);
        }else {
            startTimer();
            pause_btn.setText("暂停");
            leftMove.setEnabled(true);
            rightMove.setEnabled(true);
            rotateMove.setEnabled(true);
            downMove.setEnabled(true);
        }
    }
    private void stopTimer()
    {
        if(timer!=null)
        {
            timer.cancel();
            //一定要设置为null 否则定时器不会被回收
            timer=null;
        }
    }
    private void startTimer()
    {
        if(timer==null)
        {
            timer=new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },0,timeInterval);
    }

    int leftMath(int a,int b)
    {
        if(b<0) {
            return (a>>-b);
        }else {
            return (a<<b);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        stopTimer();
    }

    @SuppressLint("SetTextI18n")
    void stopDown()
    {
        for(int i=3;i>=0;i--)
        {
            int line=i+position[0];
            if(line>=0 && StateSquare.shape[rand][i]!=0)
            {
                allBlock[line]+=(leftMath(StateSquare.shape[rand][i],position[1]));
                for(int j=0;j<xSize;j++)
                {
                    if(((1<<j)&(leftMath(StateSquare.shape[rand][i],position[1])))!=0){
                        blockColor[line][j]=randColor;
                    }
                }
            }
        }
        for(int i=ySize-1;i>=0;)
        {
            if(allBlock[i]==0x3ff){
                score++;
                scoreTextView.setText("分数："+score);
                for(int j=i-1;j>=0;j--)
                {
                    allBlock[j+1]=allBlock[j];
                    for(int k=0;k<xSize;k++)
                    {
                        blockColor[j+1][k]=blockColor[j][k];
                    }
                }
                allBlock[0]=0;
                for(int j=0;j<xSize;j++)
                {
                    blockColor[0][j]=0;
                }
            }
            else {
                i--;
            }
        }
        if(allBlock[0]!=0)
        {
            if(score>highestScore)
            {
                cacheUtils.getValue("highestScore"+grade,score+"");
                highestScore=score;
                maxScoreTextView.setText("最高分："+score);
                scoreTextView.setText("分数："+score);
            }
            gameOver();
        }
        rand=nextRand;
        position[0]=StateSquare.initPosition[rand][1];
        position[1]=StateSquare.initPosition[rand][0];
        randColor=nextRandColor;

        nextRand=random.nextInt(19);
        nextRandColor=random.nextInt(5)+1;
        nextTetrisShow();
    }
    private void gameOver()
    {
        cacheUtils.putValue("highestScore"+grade,String.valueOf(highestScore));
        stopTimer();
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("游戏结束");
        dialog.setMessage("本局您获得"+score+"分");
        dialog.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stop=0;
                position[0]=-4;
                position[1]=4;
                for(int i=0;i<ySize;i++)
                {
                    allBlock[i]=0;
                    for(int j=0;j<xSize;j++)
                    {
                        blockColor[i][j]=0;
                    }
                }
                rand=random.nextInt(19);
                position[0]=StateSquare.initPosition[rand][1];
                position[1]=StateSquare.initPosition[rand][0];
                randColor=random.nextInt(5)+1;
                nextRand=random.nextInt(19);
                nextRandColor=random.nextInt(5)+1;
                timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                },0,timeInterval);
            }
        });
        dialog.setNegativeButton("退出",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                finish();
            }
        }).create();
        dialog.setCancelable(false);
        dialog.show();
    }
    private void nextTetrisShow()
    {
        nextTetrisList.clear();
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                if(((1<<j)&StateSquare.shape[nextRand][i])!=0){
                    nextTetrisList.add(nextRandColor);
                }else {
                    nextTetrisList.add(0);
                }
            }
        }
        nextTetrisAdapter.setmDatas(nextTetrisList);
        nextTetrisAdapter.notifyDataSetChanged();
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            boolean isNewTimer=true;
            for (int i=0;i<ySize;i++)
            {
               if(allBlock[i]==0){
                   for(int j=0;j<xSize;j++)
                   {
                       blockList.set(i*xSize+j,0);
                   }
               }else {
                   for(int j=0;j<xSize;j++)
                   {
                       blockList.set(i*xSize+j,blockColor[i][j]);
                   }
               }
            }
            boolean camMove=true;
            if(msg.what==0)
            {
                position[0]++;
                for(int i=3;i>=0;i--)
                {
                    int line=i+position[0];
                    if(line>=0 && StateSquare.shape[rand][i]!=0)
                    {
                        if(line>=ySize || ((allBlock[line]&(leftMath(StateSquare.shape[rand][i],position[1])))!=0))
                        {
                            camMove=false;
                            break;
                        }
                    }
                }
                if(!camMove)
                {
                    position[0]--;
                    for(int i=3;i>=0;i--)
                    {
                        int line=i+position[0];
                        if(line>=0 && StateSquare.shape[rand][i]!=0)
                        {
                            for(int j=0;j<xSize;j++)
                            {
                                if(((1<<j)&(leftMath(StateSquare.shape[rand][i],position[1])))!=0)
                                {
                                    blockList.set(line*xSize+j,randColor);
                                }
                            }
                        }
                    }
                    stopDown();
                }else {
                    for(int i=3;i>=0;i--)
                    {
                        int line=i+position[0];
                        if(line>=0 && StateSquare.shape[rand][i]!=0)
                        {
                            for(int j=0;j<xSize;j++)
                            {
                                if(((1<<j)&(leftMath(StateSquare.shape[rand][i],position[1])))!=0)
                                {
                                    blockList.set(line*xSize+j,randColor);
                                }
                            }
                        }
                    }
                }
            }else {
                for(int i=3;i>=0;i--)
                {
                    int line =i+position[0];
                    if(line>=0&&StateSquare.shape[rand][i]!=0)
                    {
                        for(int j=0;j<xSize;j++)
                        {
                            if(((1<<j)&(leftMath(StateSquare.shape[rand][i],position[1])))!=0)
                            {
                                blockList.set(line*xSize+j,randColor);
                            }
                        }
                    }
                }
            }
            blockAdapter.setmDatas(blockList);
            blockAdapter.notifyDataSetChanged();
        }
    };
}
