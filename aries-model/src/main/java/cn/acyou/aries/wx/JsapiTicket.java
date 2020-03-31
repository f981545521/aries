package cn.acyou.aries.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-23 上午 09:56]
 **/
@Data
@ApiModel(value = "调用微信JS接口的临时票据", description = "jsapi_ticket是公众号用于调用微信JS接口的临时票据。")
public class JsapiTicket implements Serializable {
    private static final long serialVersionUID = -1803860272436488738L;

    @ApiModelProperty(name = "errcode", value = "", notes = "错误码")
    private String errcode;

    @ApiModelProperty(name = "errmsg", value = "", notes = "错误消息")
    private String errmsg;

    @ApiModelProperty(name = "ticket", value = "", notes = "jsapi_ticket是公众号用于调用微信JS接口的临时票据。")
    private String ticket;

    @ApiModelProperty(name = "expires_in", value = "", notes = "有效期7200秒")
    private String expires_in;
}
