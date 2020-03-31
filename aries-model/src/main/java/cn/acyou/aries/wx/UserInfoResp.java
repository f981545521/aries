package cn.acyou.aries.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-17 上午 11:27]
 **/
@Data
@ApiModel(value = "微信授权", description = "第四步：拉取用户信息(需scope为 snsapi_userinfo)")
public class UserInfoResp implements Serializable {
    private static final long serialVersionUID = -472728430736529682L;

    //正确时返回的JSON数据包如下：
    @ApiModelProperty(name = "openid", value = "", notes = "用户的唯一标识")
    private String openid;
    @ApiModelProperty(name = "nickname", value = "", notes = "用户昵称")
    private String nickname;
    @ApiModelProperty(name = "sex", value = "", notes = "用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private String sex;
    @ApiModelProperty(name = "province", value = "", notes = "用户个人资料填写的省份")
    private String province;
    @ApiModelProperty(name = "city", value = "", notes = "普通用户个人资料填写的城市")
    private String city;
    @ApiModelProperty(name = "country", value = "", notes = "国家，如中国为CN")
    private String country;
    @ApiModelProperty(name = "headimgurl", value = "", notes = "用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    private String headimgurl;
    @ApiModelProperty(name = "privilege", value = "", notes = "用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）")
    private String privilege;
    @ApiModelProperty(name = "unionid", value = "", notes = "只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。")
    private String unionid;


    //错误时微信会返回JSON数据包如下
    @ApiModelProperty(name = "errcode", value = "", notes = "错误码")
    private String errcode;
    @ApiModelProperty(name = "errmsg", value = "", notes = "错误消息")
    private String errmsg;
}
