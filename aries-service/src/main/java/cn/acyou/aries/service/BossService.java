package cn.acyou.aries.service;


import cn.acyou.aries.entity.Boss;

import java.util.List;

/**
 * @author youfang
 * @date 2018-02-09 20:07
 **/
public interface BossService {

    List<Boss> getAllBoss();

    int addBoss(Boss boss);
}
