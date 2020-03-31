package cn.acyou.aries.wx;

public class RootConstant {

    public static final String ROOT_PATH = "/";

    /**
     * restful接口前缀
     */
    public static final String API_PATH = "/api";

    /**
     * 默认跳转页
     */
    public static final String LOGIN_PATH = "/login";

    /**
     * 用户主页
     */
    public static final String INDEX_PATH = "/index";

    /**
     * 砍价单页
     */
    public static final String BARGAIN_PATH = "/mkt/bargain";

    /**
     * 用户session key
     */
    public static final String USER_SESSION_KEY = "user_session_key";

    /**
     * 微信授权URL
     */
    public static final String WX_AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize";
    /**
     * 通过code换取网页授权access_token
     */
    public static final String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 根据APPID & appsecret 获取accesstoken
     */
    public static final String WX_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     */
    public static final String WX_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 验证access_token
     */
    public static final String WX_AUTH = "https://api.weixin.qq.com/sns/auth";

    /**
     * OSS中视频的封面
     */
    public static final String OSS_VIDEO_2_IMG_SUFIX = "?x-oss-process=video/snapshot,t_5000,f_jpg,m_fast";

    /**
     * 响应状态码
     */
    public class ResponseState {
        public static final int SUCCESS = 0;//请求成功
        public static final int LOGON_EXPIRES = 3;//登录过期
        public static final int PAY_FAIL_CODE = 510000;//支付异常
        public static final int BUSINESS_FAIL_CODE = 520000;//业务异常
    }

    /**
     * 通知模版类型 (1 开门通知)
     */
    public static final Long MESSAGE_OPEN_DOOR = 1L;


    public static final Integer FLAG_TRUE_1 = 1;
    public static final Integer FLAG_FALSE_0 = 0;

    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
    public static final Integer TWO = 2;
    public static final Integer THREE = 3;
    public static final Integer FOUR = 4;

    public static final String SHORT_DATE_PATTERN = "yyyyMMdd";
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String SPECIFIC_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_SERIES_FORMAT_PATTERN = "yyyyMMddHHmmss";

    /**
     * 订单支付状态//1-待支付 2-支付关闭(支付取消/超时)  3支付成功 4退款
     */
    public interface OrderPayStatus {
        /**
         * 待支付
         */
        int WAITING_PAY = 1;

        /**
         * 支付关闭/支付取消
         */
        int CLOSE_PAY = 2;

        /**
         * 支付成功
         */
        int SUCCESS_PAY = 3;

        /**
         * 退款
         */
        int ERROR_PAY = 4;
    }

    /**
     * CMS枚举
     */
    public interface CMS {
        /**
         * 顶部广告
         */
        Long TOP_AD = 10000014L;

        /**
         * 功能按钮
         */
        Long FUN_BUT = 10000015L;

        /**
         * 中部广告
         */
        Long MID_AD = 10000016L;

        /**
         * 底部导航
         */
        Long BOTTOM_NAV = 10000017L;
        /**
         * 推荐
         */
        Long RECOMMEND = 10000018L;

        /**
         * 折扣促销广告
         */
        Long ZKCX_AD = 10000022L;
    }

    /**
     * 分享cookie名称
     */
    public interface CookieName {
        String SHARE_ID = "shareId";
        String INVITE_CODE = "inviteCode";
    }

    /**
     * actionType 分享行为类型(1-浏览,2-注册,3-购买)
     */
    public interface ActionType {
        Integer BROWSE = 1;
        Integer REGISTER = 2;
        Integer BUY = 3;
    }


}
