package cn.acyou.aries.advice;

import cn.acyou.aries.execption.BusinessException;
import cn.acyou.aries.util.JsonResult;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import org.apache.commons.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-17 上午 09:50]
 **/
@ControllerAdvice
public class GlobalExceptionAdvice {

    private Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Exception e) {
        JsonResult resultInfo = new JsonResult();
        ModelAndView mv = new ModelAndView();
        Throwable t = Throwables.getRootCause(e);
        log.error("统一异常处理，{}", e.getMessage());
        e.printStackTrace();
        if (t instanceof BusinessException){
            resultInfo.setState(410);
            resultInfo.setMessage(t.getMessage());
        } else{
            resultInfo.setState(400);
            resultInfo.setMessage("未知错误！");
        }
        //响应Ajax请求
        if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){
            responseResult(response, resultInfo);
            return mv;
        }
        String requestUrl = request.getRequestURL().toString();
        mv.setViewName("error");
        mv.addObject("state", resultInfo.getState());
        mv.addObject("message", resultInfo.getMessage());
        mv.addObject("requestUrl", requestUrl);
        return mv;
    }

    private void responseResult(HttpServletResponse response, JsonResult result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.setStatus(200);
        try {
            PrintWriter pw = response.getWriter();
            pw.write(JSON.toJSONString(result));
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
