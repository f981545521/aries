package cn.acyou.aries;

import java.io.Serializable;
import java.util.List;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-11 上午 10:28]
 * @since [司法公证]
 **/
public class IdListSo implements Serializable {
    private static final long serialVersionUID = 234094271544127355L;
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
