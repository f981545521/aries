package cn.acyou.aries.wx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author youfang
 * @version [1.0.0, 2018-05-17 上午 11:21]
 **/
@Data
@ApiModel(value = "微信授权", description = "附：检验授权凭证（access_token）是否有效")
public class ValidateAccessTokenResp implements Serializable {
    private static final long serialVersionUID = 2717779941895989988L;

    @ApiModelProperty(name = "errcode", value = "", notes = "")
    private String errcode;
    @ApiModelProperty(name = "errmsg", value = "", notes = "")
    private String errmsg;
}
