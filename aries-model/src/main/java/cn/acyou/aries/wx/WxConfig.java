package cn.acyou.aries.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-23 上午 09:56]
 **/
@Data
@ApiModel(value = "微信使用JSAPI先配置config", description = "通过config接口注入权限验证配置。")
public class WxConfig implements Serializable {
    private static final long serialVersionUID = -1803860272436488738L;

    @ApiModelProperty(name = "appId", value = "", notes = "公众号的唯一标识")
    private String appId;

    @ApiModelProperty(name = "timestamp", value = "", notes = "生成签名的时间戳")
    private String timestamp;

    @ApiModelProperty(name = "nonceStr", value = "", notes = "生成签名的随机串")
    private String nonceStr;

    @ApiModelProperty(name = "signature", value = "", notes = "签名")
    private String signature;

    @ApiModelProperty(name = "jsApiList", value = "", notes = "需要使用的JS接口列表")
    private List<String> jsApiList;
}
