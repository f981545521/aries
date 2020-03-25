package cn.acyou.aries.service.impl;

import cn.acyou.aries.constant.AriesConstant;
import cn.acyou.aries.entity.Boss;
import cn.acyou.aries.execption.BusinessException;
import cn.acyou.aries.mappers.BossMapper;
import cn.acyou.aries.service.BossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author youfang
 * @date 2018-02-09 20:08
 **/
@Service("bossService")
public class BossServiceImpl implements BossService {

    @Autowired
    private BossMapper bossMapper;


    @Override
    public List<Boss> getAllBoss() {
        Boss boss = new Boss();
        boss.setId(1);
        boss.setName("小明");
        boss.setAge(23);
        return bossMapper.getAllTBoss();
    }

    @Override
    public int addBoss(Boss boss) {
        int result = bossMapper.addBoss(boss);
        //其他业务处理
        System.out.println("添加成功：" + boss);
        if (result == 1){
            throw new BusinessException(AriesConstant.OPERATE_SUCCESS);
        }
        return result;
    }


}
