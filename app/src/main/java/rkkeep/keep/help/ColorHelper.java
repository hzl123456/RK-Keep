package rkkeep.keep.help;

import java.util.ArrayList;
import java.util.List;

import rkkeep.keep.R;

/**
 * Created by Au61 on 2016/4/27.
 */
public class ColorHelper {

    //要使用的颜色色值
    public static final String[] colors = {"#ffffffff", "#ff8a80", "#ffd180", "#ffff8d", "#ccff90", "#a7ffeb", "#80d8ff", "#cfd8dc"};

    public static final int[] colorRes = {R.drawable.btn_click_ripple, R.drawable.btn_click_ripple_ff8a80,
            R.drawable.btn_click_ripple_ffd180, R.drawable.btn_click_ripple_ffff8d,
            R.drawable.btn_click_ripple_ccff90, R.drawable.btn_click_ripple_a7ffeb,
            R.drawable.btn_click_ripple_80d8ff, R.drawable.btn_click_ripple_cfd8dc};

    public static List<String> getColors() {
        List<String> infos = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            infos.add(colors[i]);
        }
        return infos;
    }


    /**
     * 根据颜色获取对应的水波纹效果的资源
     **/
    public static int getCheckColor(String color) {
        for (int i = 0; i < colors.length; i++) {
            if (color.equals(colors[i])) {
                return colorRes[i];
            }
        }
        return colorRes[0];
    }
}
