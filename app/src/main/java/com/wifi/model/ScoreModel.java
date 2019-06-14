package com.wifi.model;

import android.content.Context;
import android.widget.TextView;

public class ScoreModel {

    //当前分数
    public int scoreCurrent = 0;
    //最高分数
    public int scoreHighest;

    /** 根据一次性消除的行数加分 */
    public void addScore(int lines) {
        if (lines == 0) {
            return;
        }
        scoreCurrent += 2 * lines - 1;
    }

    /** 更新最高分（未完） */
    public void updateHighestScore(Context context) {
        if (scoreHighest == 0) {
            //scoreHighest = (int) SPUtils.get(context,"scoreHighest",0);
        }
        if (scoreCurrent > scoreHighest) {
            scoreHighest = scoreCurrent;
            //储存到本地
           // SPUtils.put(context,"scoreHighest",scoreHighest);
        }
    }

    /** 显示当前分数 */
    public void showCurrentScore(TextView scoreCurrentTextView) {
        if (scoreCurrentTextView != null) {
            scoreCurrentTextView.setText(String.valueOf(scoreCurrent));
        }
    }

    /** 显示最高分数 */
    public void showHighestScore(TextView scoreHighestTextView) {
        if (scoreHighestTextView != null) {
            scoreHighestTextView.setText(String.valueOf(scoreHighest));
        }
    }

}
