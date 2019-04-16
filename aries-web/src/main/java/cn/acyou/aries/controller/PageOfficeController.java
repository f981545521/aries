package cn.acyou.aries.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.zhuozhengsoft.pageoffice.*;
import com.zhuozhengsoft.pageoffice.wordwriter.DataTag;
import com.zhuozhengsoft.pageoffice.wordwriter.WordDocument;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-16 上午 10:17]
 **/
@Controller
@RequestMapping("/office")
public class PageOfficeController {

    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private MongoDbFactory mongoDbFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("")
    public String index() {
        return "/office/index";
    }

    @RequestMapping("create")
    public String create(HttpServletRequest request) {
        PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
        poCtrl1.setServerPage(request.getContextPath() + "/poserver.zz");
        //隐藏菜单栏 隐藏工具栏
        poCtrl1.setMenubar(false);
        poCtrl1.setCustomToolbar(false);
        poCtrl1.setJsFunction_BeforeDocumentSaved("BeforeDocumentSaved()");
        //设置保存页面
        poCtrl1.setSaveFilePage("/office/save");
        //新建Word文件，webCreateNew方法中的两个参数分别指代“操作人”和“新建Word文档的版本号”
        poCtrl1.webCreateNew("张佚名", DocumentVersion.Word2003);
        poCtrl1.setTagId("PageOfficeCtrl1");
        return "office/createWord";
    }

    @RequestMapping("/word")
    public String openWord(HttpServletRequest request, Map<String, Object> map) {
        String path = request.getServletContext().getRealPath("");

        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        //设置服务页面
        poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
        //添加自定义保存按钮
        poCtrl.addCustomToolButton("保存", "Save", 1);
        //添加自定义盖章按钮
        poCtrl.addCustomToolButton("盖章", "AddSeal", 2);
        //设置处理文件保存的请求方法
        poCtrl.setSaveFilePage("/office/save");
        //打开word
        poCtrl.webOpen(path + "\\doc\\test.doc", OpenModeType.docAdmin, "张三");
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return "word";
    }

    @RequestMapping("/read")
    public String readWord(HttpServletRequest request, Map<String, Object> map) throws Exception {
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        //设置服务页面
        poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
        poCtrl.addCustomToolButton("保存", "Save", 1);
        poCtrl.setSaveFilePage("/office/save");
        //从MongoDB中查询文件 下载文件
        GridFSFile fsFile = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is("5cb56d619245e250606ec1e8")));
        GridFSBucket bucket = GridFSBuckets.create(mongoDbFactory.getDb(), "office");
        File file = File.createTempFile("file", "temp.doc");
        FileOutputStream baos = new FileOutputStream(file);
        bucket.downloadToStream(fsFile.getObjectId(), baos);
        poCtrl.webOpen(file.getPath(), OpenModeType.docAdmin, "张三");
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        map.put("objectId", fsFile.getObjectId().toString());
        map.put("fileSubject", fsFile.getFilename());
        return "office/word";
    }
    @RequestMapping("/update")
    public void update(HttpServletRequest request, HttpServletResponse response) {
        FileSaver fs = new FileSaver(request, response);
        String objectId = fs.getFormField("objectId").trim();
        System.out.println("修改之前的Object Id" + objectId);
    }

    //查询所有资源
    //GridFsResource[] resources = gridFsTemplate.getResources("*");
    //for (GridFsResource txtFile : resources) {
    //    System.out.println(txtFile.getFilename());
    //}

    // 删除objectId
    // gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5cb56d619245e250606ec1e8")));


    @RequestMapping("/save")
    public void save(HttpServletRequest request, HttpServletResponse response) {
        FileSaver fs = new FileSaver(request, response);
        String fileSubject = fs.getFormField("fileSubject").trim();
        fileSubject = fileSubject + fs.getFileExtName();
        ObjectId newFileObjectId = gridFsTemplate.store(fs.getFileStream(), fileSubject, fs.getFileExtName());
        System.out.println("保存Object Id" + newFileObjectId);
        //设置保存结果
        fs.setCustomSaveResult("ok");
        fs.close();
    }


    @RequestMapping("/template")
    public String templateWord(HttpServletRequest request, Map<String, Object> map) throws Exception {
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        //设置服务页面
        poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
        poCtrl.addCustomToolButton("保存", "Save()", 1);
        poCtrl.setSaveFilePage("/office/save");
        //从MongoDB中查询文件 下载文件
        GridFSFile fsFile = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is("5cb59d3d9245e235e0c5c1df")));
        GridFSBucket bucket = GridFSBuckets.create(mongoDbFactory.getDb(), "office");
        File file = File.createTempFile("file", "temp.doc");
        FileOutputStream baos = new FileOutputStream(file);
        bucket.downloadToStream(fsFile.getObjectId(), baos);

        /* 模板变量 **/
        WordDocument doc = new WordDocument();
        doc.getTemplate().defineDataTag("{ 甲方 }");
        doc.getTemplate().defineDataTag("{ 乙方 }");
        doc.getTemplate().defineDataTag("{ 担保人 }");
        doc.getTemplate().defineDataTag("【 合同日期 】");
        doc.getTemplate().defineDataTag("【 合同编号 】");
        //替换存在的 DataTag的值
        DataTag deptTag = doc.openDataTag("{甲方}");
        deptTag.setValue("范冰冰");
        poCtrl.setWriter(doc);
        poCtrl.webOpen(file.getPath(), OpenModeType.docAdmin, "张三");
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        map.put("objectId", fsFile.getObjectId().toString());
        map.put("fileSubject", fsFile.getFilename());


        return "office/template";
    }


    /**
     * 在服务器上生成文档的同时并填充数据，客户端的页面不显示打开文档。
     */
    @RequestMapping("/fileMaker")
    public void fileMaker(HttpServletRequest request, HttpServletResponse response){
        FileMakerCtrl fmCtrl = new FileMakerCtrl(request);
        fmCtrl.setServerPage(request.getContextPath()+"/poserver.zz");
        WordDocument doc = new WordDocument();
        //给数据区域赋值，即把数据填充到模板中相应的位置
        doc.openDataTag("{甲方}").setValue("富士康");
        fmCtrl.setWriter(doc);
        fmCtrl.setSaveFilePage("SaveMaker.jsp");
        //OnProgressComplete为回调函数,文档生成之后在页面里触发此js事件。
        //如果您需要响应此事件，您需要在当前 JSP 页面里定义一个 JavaScript 函数。
        fmCtrl.setJsFunction_OnProgressComplete("OnProgressComplete()");
        fmCtrl.fillDocument("doc/template.doc", DocumentOpenType.Word);
    }
}
