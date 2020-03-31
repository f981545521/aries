package cn.acyou.aries.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author youfang
 * @version [1.0.0, 2018-07-26 下午 04:22]
 **/
public class UserAgentUtil {

    /**
     * 判断 移动端/PC端
     */
    public static boolean isMobile(HttpServletRequest request) {
        List<String> mobileAgents = Arrays.asList("ipad", "iphone os", "rv:1.2.3.4", "ucweb", "android", "windows ce", "windows mobile");
        String ua = request.getHeader("User-Agent").toLowerCase();
        for (String sua : mobileAgents) {
            if (ua.contains(sua)) {
                return true; //手机端
            }
        }
        return false;//PC端
    }

    /**
     * 是否微信浏览器
     */
    public static boolean isWechat(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent").toLowerCase();
        if (ua.contains("micromessenger")) {
            return true;//微信
        }
        return false;//非微信手机浏览器
    }


    /**
     * 判断是否是IE浏览器
     *
     * @param request
     * @return
     */
    public static boolean isIEBrowser(HttpServletRequest request) {
        String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String signal : IEBrowserSignals) {
            if (userAgent.contains(signal)) {
                return true;
            }
        }
        return false;
    }

}
