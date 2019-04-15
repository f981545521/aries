package cn.acyou.aries.controller;

import cn.acyou.aries.IdListSo;
import cn.acyou.aries.service.BossService;
import cn.acyou.aries.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public JsonResult all(){
        return new JsonResult(bossService.getAllBoss());
    }
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JsonResult delete(IdListSo so){
        System.out.println(String.join(",", so.getIds()));
        return new JsonResult(so);
    }
    @RequestMapping(value = "/delete2", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JsonResult delete2(@RequestBody List<String> ids){
        System.out.println(String.join(",", ids));
        return new JsonResult(ids);
    }

}
