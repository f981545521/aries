package cn.acyou.aries.controller.wx;

import cn.acyou.aries.common.Result;
import cn.acyou.aries.util.HttpClientUtil;
import cn.acyou.aries.util.redis.RedisUtils;
import cn.acyou.aries.wx.*;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Formatter;
import java.util.Map;
import java.util.UUID;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-16 下午 06:29]
 **/
@Api(value = "微信接口", tags = "微信")
@Controller
@RequestMapping("/api/wx")
public class WxApiController {

    private static final Logger log = LoggerFactory.getLogger(WxApiController.class);

    @Value("${resourceConf.wx.appid}")
    private String appid;

    @Value("${resourceConf.wx.appsecret}")
    private String appsecret;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 常见问题
     *  1.  Scope参数错误或没有Scope权限？
     *      网页帐号	网页授权获取用户基本信息	无上限	修改
     *      点击修改：授权回调页面域名配置:
     *                              wx.acyou.cn:8086
     *  2. wxConfig的时候：系统错误，错误码：40048,invalid url domain？
     *     配置JS接口安全域名修改：  wx.acyou.cn:8086
     *
     * @param request
     * @param model
     * @return
     */
    @ApiOperation(value = "微信回调", notes = "微信登录验证")
    @GetMapping(value = "/verification")
    public String verification(HttpServletRequest request, Model model) {
        //获取回调中的code参数
        String code = request.getParameter("code");
        String state = request.getParameter("state");//自定义参数
        log.info("weixin 登录(code)：" + code + "，（state）：" + state);
        //第二步：通过code换取网页授权access_token
        //code = "001GF24i1wpmkw0qpN5i13Be4i1GF24s";
        String getAccessTokenURL = RootConstant.WX_ACCESS_TOKEN + "?appid=" + appid + "&secret=" + appsecret + "&code=" + code + "&grant_type=authorization_code";
        String responseStr = HttpClientUtil.doGet(getAccessTokenURL);
        log.info("weixin 登录(获取accesstoken)：" + responseStr);
        AccessTokenResp accessTokenResp = JSON.parseObject(responseStr, AccessTokenResp.class);
        if (accessTokenResp.getOpenid() == null) {
            log.error("微信登录失败", accessTokenResp);
            return "redirect:/index";
        }
        UserInfoResp userInfoResp = getWxUserInfo(accessTokenResp.getAccess_token(), accessTokenResp.getOpenid());
        System.out.println(userInfoResp);
        if (StringUtils.isNotEmpty(state)) {
            return "redirect:" + state;
        } else {
            return "redirect:/index";
        }

    }

    @ApiOperation(value = "素材下载并上传到VOD:不支持视频文件的下载(GET)", notes = "素材下载")
    @RequestMapping(value = "/uploadMedia", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadMediaFromWxServer(String mediaId) {
        String accessToken = getAccessToken();
        String mediaURL =
                "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken + "&media_id=" + mediaId;
        log.warn("downloadMedia ----> mediaId : " + mediaId);
        log.warn("downloadMedia ----> URL : " + mediaURL);
        String fileName = mediaId + ".mp3";
        String audioId = "";//VodUploadUtil.uploadURLStream(mediaURL, fileName, VodUploadConstant.CATEID_MOOD_MUSIC, null);
        Map<String, Object> data = Maps.newHashMap();
        data.put("audioId", audioId);
        return Result.success(data);
    }


    @ApiOperation(value = "微信JSSDK配置", notes = "微信JSSDK配置")
    @RequestMapping(value = "/wxConfig", method = RequestMethod.POST)
    @ResponseBody
    public WxConfig getWxConfig(String url) {
        WxConfig wxConfig = new WxConfig();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().trim().replaceAll("-", ""); // 必填，生成签名的随机串
        String jsapiTicket = getJsapiTicket();
        String signature;
        String encryption = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
        signature = DigestUtils.sha1DigestAsHex(encryption);
        wxConfig.setAppId(appid);
        wxConfig.setNonceStr(nonceStr);
        wxConfig.setTimestamp(timestamp);
        wxConfig.setSignature(signature);
        return wxConfig;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }


    /**
     * 获取jsapi_ticket
     *
     * @return jsapi_ticket
     */
    private String getJsapiTicket() {
        //需要加缓存将ticket缓存起来
        String wxTicket = redisUtils.get("wxTicket");
        if (StringUtils.isNotEmpty(wxTicket)){
            //jsapi_ticket是公众号用于调用微信JS接口的临时票据。正常情况下，jsapi_ticket的有效期为7200秒，通过access_token来获取。
            //由于获取jsapi_ticket的api调用次数非常有限，频繁刷新jsapi_ticket会导致api调用受限，影响自身业务，开发者必须在自己的服务全局缓存jsapi_ticket 。
            String apiUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + getAccessToken() + "&type=jsapi";
            String responseStr = HttpClientUtil.doGet(apiUrl);
            JsapiTicket responseTicket = JSON.parseObject(responseStr, JsapiTicket.class);
            log.info("jsapiTicket >>>  : {}", responseTicket.getTicket());
            wxTicket  = responseTicket.getTicket();
            redisUtils.set("wxTicket", wxTicket, 7200);
        }
        return wxTicket;
    }


    /**
     * 根据APPID & appsecret 获取accesstoken
     *
     * @return AccessToken
     */
    private String getAccessToken() {
        String accessTokenUrl = RootConstant.WX_TOKEN + "?grant_type=client_credential&appid=" + appid + "&secret=" + appsecret;
        String responseStr = HttpClientUtil.doGet(accessTokenUrl);
        AccessTokenResp accessTokenResp = JSON.parseObject(responseStr, AccessTokenResp.class);
        log.warn("jsapi获取AccessToken {}", accessTokenResp);
        if (accessTokenResp.getAccess_token() != null) {
            return accessTokenResp.getAccess_token();
        } else {
            return "";
        }
    }

    private UserInfoResp getWxUserInfo(String accessToken, String openId) {
        //第四步：拉取用户信息(需scope为 snsapi_userinfo)
        String getUserInfoURL = RootConstant.WX_USER_INFO + "?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
        String responseStr = HttpClientUtil.doGet(getUserInfoURL);
        UserInfoResp userInfoResp = JSON.parseObject(responseStr, UserInfoResp.class);
        log.info("wx获取用户信息 ：" + userInfoResp.toString());
        return userInfoResp;
    }

    private boolean validateAccessToken(String accessToken, String openId) {
        String validateAccessTokenURL = RootConstant.WX_AUTH + "?access_token=" + accessToken + "&openid=" + openId;
        String responseStr = HttpClientUtil.doGet(validateAccessTokenURL);
        ValidateAccessTokenResp resp = JSON.parseObject(responseStr, ValidateAccessTokenResp.class);
        return "0".equals(resp.getErrcode());
    }


}
