package com.wifi;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wifi.control.GameControl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //游戏区域控件
    public View gamePanel;
    //当前分数TextView
    public TextView scoreCurrentTextView;
    //最高分数TextView
    public TextView scoreHighestTextView;

    //游戏控制器
    public GameControl gameControl;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String type = (String) msg.obj;
            if (type == null) {
                return;
            }
            if (type.equals("invalidate")) {
                //刷新重绘view
                gamePanel.invalidate();
                //刷新当前分数
                gameControl.scoreModel.showCurrentScore(scoreCurrentTextView);
                //刷新最高分数
                gameControl.scoreModel.showHighestScore(scoreHighestTextView);
            } else if (type.equals("pause")) {
                ((Button)findViewById(R.id.btnPause)).setText("pause");
            } else if (type.equals("continue")) {
                ((Button)findViewById(R.id.btnPause)).setText("continue");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //实例化游戏控制器
        gameControl = new GameControl(handler, this);
        initView();
        initListener();
    }

    /** 初始化视图 */
    public void initView() {
        //1.得到父容器
        FrameLayout layoutGame = findViewById(R.id.LayoutGame);
        //2.实例化游戏区域
        gamePanel = new View(this) {
            //游戏区域绘制
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                //绘制
                gameControl.draw(canvas);
            }
        };
        //3.设置游戏区域大小
        gamePanel.setLayoutParams(new FrameLayout.LayoutParams(Config.XWIDTH, Config.YHEIGHT));
        //4.设置背景颜色
        gamePanel.setBackgroundColor(0x10000000);
        //5.添加至父容器
        layoutGame.addView(gamePanel);

        //得到当前分数文本框
        scoreCurrentTextView = findViewById(R.id.textCurrentScore);
        //得到最高分数文本框
        scoreHighestTextView = findViewById(R.id.textHighestScore);
    }

    /** 初始化监听器 */
    public void initListener() {
        findViewById(R.id.btnLeft).setOnClickListener(this);
        findViewById(R.id.btnRotate).setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        findViewById(R.id.btnBottom).setOnClickListener(this);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnPause).setOnClickListener(this);
    }

    /** 捕捉点击事件 */
    @Override
    public void onClick(View v) {
        gameControl.onClick(v.getId());
        //调用刷新视图方法
        gamePanel.invalidate();
    }

}