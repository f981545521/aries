package cn.acyou.aries.util.redis;

import java.io.Serializable;

/**
 * @author acyou
 * @version [1.0.0, 2020-3-21 下午 10:46]
 **/
public class ZSetItem implements Serializable {
    private static final long serialVersionUID = 2932598815261168647L;
    private String value;
    private Double score;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
