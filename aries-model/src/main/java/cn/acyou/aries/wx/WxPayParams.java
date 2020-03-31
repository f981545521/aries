package cn.acyou.aries.wx;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信支付参数
 *
 * @author youfang
 * @version [1.0.0, 2018-11-17 下午 01:57]
 * @since [天天健身/前端工程]
 **/
@Data
public class WxPayParams implements Serializable {
    private static final long serialVersionUID = 2468499214404578896L;

    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String package_name;
    private String signType = "MD5";
    private String paySign;

    private String returnUrl;
}
