package com.wifi.model;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.wifi.Config;

public class MapsModel {

    //方块大小
    private int boxSize;
    //地图
    public boolean[][] maps;
    //地图宽度
    private int xWidth;
    //地图高度
    private int yHeight;

    //地图画笔
    private Paint mapPaint;
    //地图辅助线画笔
    private Paint linePaint;
    //状态画笔
    private Paint statePaint;

    public MapsModel(int xWidth, int yHeight, int boxSize) {
        this.xWidth = xWidth;
        this.yHeight = yHeight;
        this.boxSize = boxSize;
        //初始化地图
        maps = new boolean[Config.MAPX][Config.MAPY];
        //初始化画笔
        mapPaint = new Paint();
        mapPaint.setColor(0x50000000);
        mapPaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        linePaint.setAntiAlias(true);
        statePaint = new Paint();
        statePaint.setColor(0xff000000);
        statePaint.setTextSize(100);
        statePaint.setAntiAlias(true);

    }

    /** 地图绘制 */
    public void drawMaps(Canvas canvas) {
        for (int x = 0; x < maps.length; x++) {
            for (int y = 0; y < maps[x].length; y++) {
                if (maps[x][y]) {
                    canvas.drawRect(x*boxSize, y*boxSize,
                            (x+1)*boxSize, (y+1)*boxSize, mapPaint);
                }
            }
        }
    }

    /** 地图辅助线绘制 */
    public void drawMapsLine(Canvas canvas) {
        for (int x = 0; x < maps.length; x++) {
            canvas.drawLine(x*boxSize, 0,
                    x*boxSize, yHeight, linePaint);
        }
        for (int y = 0; y < maps[0].length; y++) {
            canvas.drawLine(0, y*boxSize,
                    xWidth, y*boxSize, linePaint);
        }
    }

    /** 状态绘制 */
    public void drawState(Canvas canvas, boolean isOver, boolean isPause) {
        //游戏结束画面
        if (isOver)
            canvas.drawText("游戏结束", xWidth/2-statePaint.measureText("游戏结束")/2, yHeight/2+50, statePaint);
        //游戏暂停画面
        if (isPause && !isOver)
            canvas.drawText("暂停", xWidth/2-statePaint.measureText("暂停")/2, yHeight/2+50, statePaint);
    }

    /** 清除地图 */
    public void cleanMaps() {
        for (int x = 0; x < Config.MAPX; x++) {
            for (int y = 0; y <Config.MAPY; y++) {
                maps[x][y] = false;
            }
        }
    }

    /** 消行处理 */
    public int cleanLine() {
        int lines = 0;
        for (int y = maps[0].length-1; y > 0; y--) {
            //消行判断
            if (checkLine(y)) {
                //执行消行
                deleteLine(y);
                //已消除行数加一
                lines++;
                //从消掉的那一行开始重新遍历
                y++;
            }
        }
        return lines;
    }

    /** 执行消行 */
    private void deleteLine(int dy) {
        for (int y = dy; y > 0; y--) {
            for (int x = 0; x < Config.MAPX; x++) {
                maps[x][y] = maps[x][y-1];
            }
        }
    }

    /** 消行判断 */
    private boolean checkLine(int y) {
        for (int x = 0; x < Config.MAPX; x++) {
            //如果有一个不为true，则该行不能消除
            if (!maps[x][y])
                return false;
        }
        //如果每一个都为true，则需要执行消行
        return true;
    }

}