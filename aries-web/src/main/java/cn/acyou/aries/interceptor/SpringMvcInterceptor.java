package cn.acyou.aries.interceptor;

import cn.acyou.aries.util.UserAgentUtil;
import cn.acyou.aries.wx.RootConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author youfang
 * @version [1.0.0, 2018-07-26 下午 03:21]
 **/
@Slf4j
public class SpringMvcInterceptor extends HandlerInterceptorAdapter {

    @Value("${resourceConf.wx.wxCallback}")
    private String wxCallback;

    @Value("${resourceConf.wx.appid}")
    private String appid;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if (handler instanceof HandlerMethod && !request.getRequestURI().contains("/wx")) {
            if (UserAgentUtil.isWechat(request) && requestURI.equals("wxLogin")) {
                String requestUrl = request.getRequestURL().toString();
                String queryParam = request.getQueryString();
                if (StringUtils.isNotEmpty(queryParam)) {
                    requestUrl = requestUrl + "?" + queryParam;
                }
                String wxCallbackUrl = RootConstant.WX_AUTHORIZE + "?appid=" + appid + "&redirect_uri=" + URLEncoder.encode(wxCallback, "UTF-8")
                        + "&state=" + requestUrl + "&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect";
                log.info("拦截器获取weixin callback：" + wxCallbackUrl);
                response.sendRedirect(wxCallbackUrl);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
