package cn.acyou.aries.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-10 下午 02:08]
 **/
@Controller
@RequestMapping
public class IndexController {

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }
}
