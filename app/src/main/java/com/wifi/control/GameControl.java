package com.wifi.control;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wifi.Config;
import com.wifi.R;
import com.wifi.model.BoxesModel;
import com.wifi.model.MapsModel;
import com.wifi.model.ScoreModel;

public class GameControl {

    Context context;
    //与主线程通信
    private Handler handler;

    //方块模型
    private BoxesModel boxesModel;
    //地图模型
    private MapsModel mapsModel;
    //分数模型
    public ScoreModel scoreModel;

    //自动下落线程
    private Thread downThread;

    //暂停状态
    private boolean isPause;
    //结束状态
    private boolean isOver;

    public GameControl(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        initData();
    }

    /** 初始化数据 */
    private void initData() {
        //游戏区域宽度 = 屏幕宽度 * 2 / 3
        Config.XWIDTH = getScreenWidth(context) * 2 / 3;
        //游戏区域高度 = 游戏区域宽度 * 13 / 5
        Config.YHEIGHT = Config.XWIDTH * 13 / 5;
        //设置间距 = 屏幕宽度 / 20
        Config.PADDING = getScreenWidth(context) / 20;
        //初始化方块大小 = 游戏区域宽度 / 10
        int boxSize = Config.XWIDTH / Config.MAPX;
        //实例化方块模型
        boxesModel = new BoxesModel(boxSize);
        //实例化地图模型
        mapsModel = new MapsModel(Config.XWIDTH, Config.YHEIGHT, boxSize);
        //实例化分数模型
        scoreModel = new ScoreModel();
    }

    /** 获得屏幕宽度 */
    private static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /** 开始游戏 */
    private void startGame() {
        //清除地图
        mapsModel.cleanMaps();
        if (downThread == null) {
            downThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            //休眠500毫秒
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //判断是否处于结束状态
                        //判断是否处于暂停状态
                        if (isPause || isOver) {
                            //继续循环
                            continue;
                        }
                        //执行一次下落
                        moveBottom();
                        //通知主线程刷新view
                        Message msg = new Message();
                        msg.obj = "invalidate";
                        handler.sendMessage(msg);
                    }
                }
            };
            downThread.start();
        }
        //生成方块
        boxesModel.newBoxes();
        //重置游戏状态
        isOver = false;
        setPause(true);
    }

    /** 下落 */
    private boolean moveBottom() {
        //移动成功，不做处理
        if (boxesModel.move(0, 1, mapsModel)) {
            return true;
        }
        //移动失败，堆积处理
        for (int i = 0; i < boxesModel.boxes.length; i++) {
            mapsModel.maps[boxesModel.boxes[i].x][boxesModel.boxes[i].y] = true;
        }
        //消行处理
        int lines = mapsModel.cleanLine();
        //加分
        scoreModel.addScore(lines);
        //更新最高分
        scoreModel.updateHighestScore(context);
        //堆积后生成新的方块
        boxesModel.newBoxes();
        //游戏结束判断
        isOver = checkOver();
        return false;
    }

    /** 结束判断 */
    private boolean checkOver() {
        for (int i = 0; i < boxesModel.boxes.length; i++) {
            if (mapsModel.maps[boxesModel.boxes[i].x][boxesModel.boxes[i].y]) {
                //把暂停状态设为false
                setPause(true);
                return true;
            }
        }
        return false;
    }

    /** 控制绘制 */
    public void draw(Canvas canvas) {
        //投影绘制(一定要放在方块绘制之前，颜色覆盖问题)
        boxesModel.drawProjectBoxes(canvas, mapsModel);
        //方块绘制
        boxesModel.drawBoxes(canvas);
        //地图绘制
        mapsModel.drawMaps(canvas);
        //地图辅助线绘制
        mapsModel.drawMapsLine(canvas);
        //状态绘制
        mapsModel.drawState(canvas, isOver, isPause);
    }

    /** 设置暂停状态 */
    private void setPause(boolean reset) {
        if (reset) {
            isPause = true;
        }
        if (isPause) {
            isPause = false;
            Message msg = new Message();
            msg.obj = "pause";
            handler.sendMessage(msg);
        } else {
            isPause = true;
            Message msg = new Message();
            msg.obj = "continue";
            handler.sendMessage(msg);
        }
    }

    /** 捕捉事件 */
    public void onClick(int id) {
        switch (id) {
            case R.id.btnLeft://左
                if (isPause) {
                    return;
                }
                boxesModel.move(-1, 0, mapsModel);
                break;
            case R.id.btnRotate://旋转
                if (isPause) {
                    return;
                }
                boxesModel.rotate(mapsModel);
                break;
            case R.id.btnRight://右
                if (isPause) {
                    return;
                }
                boxesModel.move(1, 0, mapsModel);
                break;
            case R.id.btnBottom://快速下落
                if (isPause) {
                    return;
                }
                while (true) {
                    //如果下落失败，则结束循环
                    if (!moveBottom()) {
                        break;
                    }
                }
                break;
            case R.id.btnStart://开始
                startGame();
                break;
            case R.id.btnPause://暂停
                setPause(false);
                break;
            default:
                break;
        }
    }

}