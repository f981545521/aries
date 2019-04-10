package cn.acyou.aries.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author youfang
 * @version [1.0.0, 2019-04-10 下午 06:39]
 * @since [司法公证]
 **/
public class MongoDBHelper {
    private static String MONGODB_IP = "192.168.1.1";

    private static int MONGODB_PORT = 27017;

    private static String MONGODB_DB = "aries";


    /**
     * 存储文件，返回mongodb的图片访问路径
     *
     * @param filename 文件名
     * @param file     文件
     * @throws Exception
     */
    public static String saveFile(String filename, File file) throws Exception {

        // 连接服务器
        Mongo mongo = new Mongo(MONGODB_IP, MONGODB_PORT);
        // 连接数据库
        DB db = mongo.getDB(MONGODB_DB);
        // 文件操作是在DB的基础上实现的，与表和文档没有关系
        GridFS gridFS = null;
        gridFS = new GridFS(db);
        GridFSInputFile mongofile = gridFS.createFile(file);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String datetime = DateUtil.getDateFormatLong(new Date());
        String fileName = datetime + "_" + filename;
        // 可以再添加属性
        mongofile.put("uuid", uuid);
        mongofile.put("filename", fileName);
        mongofile.put("time", System.currentTimeMillis());
        // 保存
        mongofile.save();
        String fileUrlPath = "http://" + MONGODB_IP + "/" + MONGODB_DB + "/" + fileName;
        System.out.println("图片访问路径：" + fileUrlPath);
        return fileUrlPath;
    }

    /**
     * 读文件，读到磁盘上
     */
    public static void readFile() throws Exception {
        // 链接服务器
        Mongo mongo = new Mongo();
        // 连接数据库
        DB db = mongo.getDB(MONGODB_DB);
        GridFS gridFs = null;
        gridFs = new GridFS(db);
        // 查找条件
        DBObject query = new BasicDBObject();
        // 查询的结果：
        List<GridFSDBFile> listfiles = gridFs.find(query);
        GridFSDBFile gridDBFile = listfiles.get(0);

        // 获得其中的文件名
        // 注意 ： 不是fs中的表的列名，而是根据调试gridDBFile中的属性而来
        String fileName = (String) gridDBFile.get("filename");
        System.out.println("从Mongodb获得文件名为：" + fileName);
        File writeFile = new File("d:/" + fileName);
        if (!writeFile.exists()) {
            writeFile.createNewFile();
        }
        // 把数据写入磁盘中
        // 写入文件中
        gridDBFile.writeTo(writeFile);
    }

    /**
     * 存储文件
     */
    public String saveFileToMongo(String mondb_ip, int mondb_port, String mongodb_db, String uploadLoclPath, String fuuid, Map<String, String> map) throws Exception {
        // 连接服务器
        Mongo mongo = new Mongo(mondb_ip, mondb_port);
        // 连接数据库
        DB db = mongo.getDB(mongodb_db);
        System.out.println(mongo + ":" + db);

        // 文件操作是在DB的基础上实现的，与表和文档没有关系
        GridFS gridFS = null;
        gridFS = new GridFS(db);
        File readFile = new File(uploadLoclPath);
        GridFSInputFile mongofile = gridFS.createFile(readFile);
        //获取原文件名
        String oldFilename = uploadLoclPath.substring(uploadLoclPath.lastIndexOf("\\") + 1, uploadLoclPath.length());

        // 可以再添加属性
        mongofile.put("filename", fuuid + "-" + oldFilename);//组合新的文件名存储文件
        mongofile.put("name", map.get("name").toString());
        mongofile.put("time", System.currentTimeMillis());
        // 保存
        mongofile.save();
        return "";
    }

    /**
     * 根据文件名，查询获取文件
     */
    public boolean findFileByFilename(String mondb_ip, int mondb_port, String mongodb_db, String filename) throws Exception {
        boolean flag = false;
        Mongo mongo = new Mongo(mondb_ip, mondb_port);
        DB db = mongo.getDB(mongodb_db);
        GridFS fs = new GridFS(db);
        List<GridFSDBFile> list = fs.find(filename);
        if (list.size() > 0)
            flag = true;
        return flag;
    }

    /**
     * 根据文件名删除文件
     */
    public static void delFileByFilename(String filename) throws Exception {
        Mongo mongo = new Mongo(MONGODB_IP, MONGODB_PORT);
        DB db = mongo.getDB(MONGODB_DB);
        GridFS fs = new GridFS(db);
        fs.remove(filename);
    }

    /**
     * 查询出一个db数据库里面的所有文件
     */
    public static List<GridFSDBFile> query(String mondb_ip, int mondb_port, String mongodb_db, DBObject query) throws Exception {
        Mongo mongo = new Mongo(mondb_ip, mondb_port);
        DB db = mongo.getDB(mongodb_db);
        GridFS fs = new GridFS(db);
        List<GridFSDBFile> files = fs.find(query);
        return files;
    }

    /**
     * 根据查询条件删除对应文件
     */
    public static void delFileByDBObject(String mondb_ip, int mondb_port, String mongodb_db, DBObject query) throws Exception {
        Mongo mongo = new Mongo(mondb_ip, mondb_port);
        DB db = mongo.getDB(mongodb_db);
        GridFS fs = new GridFS(db);
        fs.remove(query);
    }

    /**
     * 删除对应db的所有文件
     */
    public static void delAllFile(String mondb_ip, int mondb_port, String mongodb_db) throws Exception {
        Mongo mongo = new Mongo(mondb_ip, mondb_port);
        DB db = mongo.getDB(mongodb_db);
        GridFS fs = new GridFS(db);
        DBObject query = new BasicDBObject();
        List<GridFSDBFile> findList = fs.find(query);
        System.out.println(findList.size());
        //删除全部
        for (GridFSDBFile gridFSDBFile : findList) {
            System.out.println(gridFSDBFile);
            fs.remove(gridFSDBFile);
        }
    }

    /**
     * main方法测试
     */
    public static void main(String[] args) throws Exception {
        //删除
        String filename1 = "20170112102054-fire.jpg";
        delFileByFilename(filename1);
        //删除所有
        delAllFile(MONGODB_IP, MONGODB_PORT, MONGODB_DB);
    }
}
