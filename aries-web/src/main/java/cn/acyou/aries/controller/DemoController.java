package cn.acyou.aries.controller;

import cn.acyou.aries.service.BossService;
import cn.acyou.aries.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-10 上午 11:53]
 **/
@Controller
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private BossService bossService;

    @RequestMapping(value = "/all", method = {RequestMethod.GET})
    @ResponseBody
    public JsonResult dynamicSource(){
        return new JsonResult(bossService.getAllBoss());
    }

}
