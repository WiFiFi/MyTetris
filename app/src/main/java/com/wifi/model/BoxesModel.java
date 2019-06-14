package com.wifi.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.wifi.Config;

import java.util.Random;

public class BoxesModel {

    //方块
    public Point[] boxes;
    //方块大小
    private int boxSize;
    //方块类型
    private int boxType;
    //方块画笔
    private Paint boxPaint;
    //投影画笔
    private Paint projectPaint;

    public BoxesModel(int boxSize) {
        this.boxSize = boxSize;
        //初始化方块
        boxes = new Point[]{};
        //初始化画笔
        boxPaint = new Paint();
        boxPaint.setColor(0xff000000);
        boxPaint.setAntiAlias(true);
        projectPaint = new Paint();
        projectPaint.setColor(0xff69b40f);
        projectPaint.setAntiAlias(true);
    }

    /** 方块绘制 */
    public void drawBoxes(Canvas canvas) {
        for (int i = 0; i < boxes.length; i++) {
            canvas.drawRect(boxes[i].x*boxSize, boxes[i].y*boxSize,
                    (boxes[i].x+1)*boxSize, (boxes[i].y+1)*boxSize,
                    boxPaint);
        }
    }

    /** 投影绘制 */
    public void drawProjectBoxes(Canvas canvas, MapsModel mapsModel) {
        boolean flag;
        int dy;
        for (dy = 1; dy < Config.MAPY; dy++) {
            flag = true;
            for (int i = 0; i < boxes.length; i++) {
                if (checkBoundary(boxes[i].x, boxes[i].y+dy, mapsModel)) {
                    flag = false;
                    dy--;
                    break;
                }
            }
            if (flag == false) {
                break;
            }
        }
        for (int i = 0; i < boxes.length; i++) {
            canvas.drawRect(boxes[i].x*boxSize, (boxes[i].y+dy)*boxSize,
                    (boxes[i].x+1)*boxSize, (boxes[i].y+dy+1)*boxSize,
                    projectPaint);
        }
    }

    /** 新建方块 */
    public void newBoxes() {
        Random random = new Random();
        boxType = random.nextInt(Config.TYPE);
        switch (boxType) {
            case 0://田
                boxes = new Point[]{new Point(4, 0), new Point(5, 0),
                        new Point(4, 1), new Point(5, 1)};
                break;
            case 1://L
                boxes = new Point[]{new Point(5, 1), new Point(6, 0),
                        new Point(4, 1), new Point(6, 1)};
                break;
            case 2://反L
                boxes = new Point[]{new Point(5, 1), new Point(4, 0),
                        new Point(4, 1), new Point(6, 1)};
                break;
            case 3://一
                boxes = new Point[]{new Point(4, 0), new Point(3, 0),
                        new Point(5, 0), new Point(6, 0)};
                break;
            case 4://土
                boxes = new Point[]{new Point(5, 1), new Point(5, 0),
                        new Point(4, 1), new Point(6, 1)};
                break;
            case 5://折
                boxes = new Point[]{new Point(5, 0), new Point(6, 0),
                        new Point(5, 1), new Point(4, 1)};
                break;
            case 6://反折
                boxes = new Point[]{new Point(5, 0), new Point(4, 0),
                        new Point(5, 1), new Point(6, 1)};
                break;
        }
    }

    /** 移动 */
    public boolean move(int x, int y, MapsModel mapsModel) {
        //对于方块数组中的每一块进行预移动后的位置，进行边界判断
        for (int i = 0; i < boxes.length; i++) {
            if (checkBoundary(boxes[i].x + x, boxes[i].y + y, mapsModel)) {
                return false;
            }
        }
        //不会出界，则遍历方块数组，均加上偏移量
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].x += x;
            boxes[i].y += y;
        }
        return true;
    }

    /** 旋转 */
    public boolean rotate(MapsModel mapsModel) {
        //如果当前方块是田字形，则不进行旋转
        if (boxType == 0) {
            return false;
        }
        //对于方块数组中的每一块进行预旋转后的位置，进行边界判断
        for (int i = 0; i < boxes.length; i++) {
            int checkX = -boxes[i].y + boxes[0].y + boxes[0].x;
            int checkY = boxes[i].x - boxes[0].x + boxes[0].y;
            if (checkBoundary(checkX, checkY, mapsModel)) {
                return false;
            }
        }
        //不会出界，则遍历方块数组，使每一个绕中心点顺时针旋转90度
        for (int i = 0; i < boxes.length; i++) {
            //旋转算法（笛卡尔公式）
            int checkX = -boxes[i].y + boxes[0].y + boxes[0].x;
            int checkY = boxes[i].x - boxes[0].x + boxes[0].y;
            boxes[i].x = checkX;
            boxes[i].y = checkY;
        }
        return true;
    }

    /** 边界判断 */
    private boolean checkBoundary(int x, int y, MapsModel mapsModel) {
        return (x < 0 || y < 0 || x >= mapsModel.maps.length || y >= mapsModel.maps[0].length || mapsModel.maps[x][y]);
    }

}