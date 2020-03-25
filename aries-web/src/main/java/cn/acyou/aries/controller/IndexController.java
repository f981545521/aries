package cn.acyou.aries.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public ModelAndView index(){
        log.info("访问首页--->");
        return new ModelAndView("index");
    }
}
