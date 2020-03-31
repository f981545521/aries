package cn.acyou.aries.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-17 上午 11:06]
 **/
@Data
@ApiModel(value = "微信授权", description = "第二步：通过code换取网页授权access_token")
public class AccessTokenResp implements Serializable {

    private static final long serialVersionUID = 7320923406343556631L;
    //正确时返回的JSON数据包如下：
    @ApiModelProperty(name = "access_token", value = "", notes = "网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同")
    private String access_token;
    @ApiModelProperty(name = "expires_in", value = "", notes = "access_token接口调用凭证超时时间，单位（秒）")
    private String expires_in;
    @ApiModelProperty(name = "refresh_token", value = "", notes = "用户刷新access_token")
    private String refresh_token;
    @ApiModelProperty(name = "openid", value = "", notes = "用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID")
    private String openid;
    @ApiModelProperty(name = "scope", value = "", notes = "用户授权的作用域，使用逗号（,）分隔")
    private String scope;

    //错误时微信会返回JSON数据包如下
    @ApiModelProperty(name = "errcode", value = "", notes = "错误码")
    private String errcode;
    @ApiModelProperty(name = "errmsg", value = "", notes = "错误消息")
    private String errmsg;

}
