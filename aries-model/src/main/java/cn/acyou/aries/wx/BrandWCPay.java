package cn.acyou.aries.wx;

import lombok.Data;

import java.io.Serializable;

/**
 * @author youfang
 * @version [1.0.0, 2018-10-29 下午 02:06]
 * @since [天天健身/运动模块]
 **/
@Data
public class BrandWCPay implements Serializable {
    private static final long serialVersionUID = 1201987367283045573L;

    private String appId;//公众号名称，由商户传入
    private String timeStamp;//时间戳，自1970年以来的秒数
    private String nonceStr;//随机串
    private String packageStr;
    private String signType = "MD5";//微信签名方式：
    private String paySign;//微信签名
}
