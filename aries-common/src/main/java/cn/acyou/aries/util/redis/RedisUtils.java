package cn.acyou.aries.util.redis;


import cn.acyou.aries.util.Md5Util;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 命令参考
 * http://doc.redisfans.com/
 *
 * @author acyou
 * @version [1.0.0, 2020-3-21 下午 10:44]
 **/
@Component
public class RedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    //加锁时间，单位纳秒， 即：加锁时间内执行完操作，如果未完成会有并发现象
    private static final Long LOCK_TIMEOUT = 10000000000L;

    //等待超时时间 3秒
    private static final Long LOCAK_WAITING = 3000L;

    private static final String LOCK_SUCCESS = "OK";

    private static final String SET_IF_NOT_EXIST = "NX";

    //PX 命令执行有问题，所以这里用EX。 PX命令 返回ok，但是实际缺没有插入值。
    private static final String SET_WITH_EXPIRE_TIME = "EX";

    // 要确保上述操作是原子性的,要使用Lua语言来实现.
    // 首先获取锁对应的value值，检查是否与token相等，如果相等则删除锁（解锁）
    private static final String LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 通过key查询缓存中对应的String类型value
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        String strValue = redisTemplate.opsForValue().get(key);
        logger.debug("接口调用详情：参数K-V： " + key + "=" + strValue);
        return strValue;
    }

    public void set(String key, String value, Long timeout, TimeUnit unit) {
        logger.debug("{}|{}|{}|{}|{}", "set方法入参：", "键:" + key, "值:" + value, "存活时间:" + timeout, "时间单位:" + unit);
        if (timeout != null) {
            redisTemplate.opsForValue().set(key, value, timeout, unit != null ? unit : TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void set(String key, String value, long timeout) {
        logger.debug("接口调用详情：参数K-V： " + key + "=" + value);
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 通过key插入缓存中对应的String类型value
     */
    public void set(String key, String value) {
        logger.debug("接口调用详情：参数K-V： " + key + "=" + value);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据key删除缓存中的记录
     * @param key key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 根据多个key批量删除缓存中的value
     * @param keys keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     *
     * @param key     key
     * @param timeout 出栈操作的连接阻塞保护时间,时间单位为秒
     * @return v
     */
    public String listLeftPop(String key, long timeout) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     *
     * @return 执行 LPUSH 命令后，列表的长度。
     */
    public Long listLeftPush(String key, String value) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     *
     * @return 执行 LPUSH 命令后，列表的长度。
     */
    public Long listLeftPushAll(String key, Collection<String> values) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 移除并返回列表 key 的尾元素。

     *
     * @param key     key
     * @param timeout 出队操作的连接阻塞保护时间,时间单位为秒
     * @return v
     */
    public String listRightPop(String key, long timeout) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().rightPop(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 移除并返回列表 key 的尾元素。
     *
     * @param key key
     * @return 列表的尾元素。
     */
    public String listRightPop(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().rightPop(key);
    }

    public <T> T listRightPop2Object(String key, Class<T> clazz) {
        logger.debug("接口调用详情：参数K： " + key);
        try {
            String strValue = redisTemplate.opsForList().rightPop(key);
            if (StringUtils.isNotEmpty(strValue)) {
                return JSON.parseObject(strValue, clazz);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Json转对象失败!", e);
            return null;
        }
    }

    /**
     * 将一个 value 插入到列表 key 的表尾(最右边)。
     *
     * @param key 键
     * @return 执行 RPUSH 操作后，表的长度。
     */
    public Long listRightPush(String key, String value) {
        logger.debug("接口调用详情：参数K-V： " + key + "=" + value);
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     *
     * @param key 键
     * @return 执行 RPUSH 操作后，表的长度。
     */
    public Long listRightPushAll(String key, Collection<String> values) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 移除并返回集合中的一个随机元素。
     *
     *
     * @param key 键
     * @return 被移除的随机元素。
     *         当 key 不存在或 key 是空集时，返回 nil 。
     */
    public String setPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 对Set的添加操作
     *
     * @param key 键
     * @param values 插入Set的String数组
     * @return Long
     */
    public Long setAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 移除集合key中的一个或多个member元素，不存在的member元素会被忽略。
     *
     * @param key    键
     * @param values 值
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 随机获取多个key无序集合中的元素（去重），count表示个数
     *
     * @param key  键
     * @param count 数量
     * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil 。
     *         如果提供了 count 参数，那么返回一个数组；如果集合为空，返回空数组。
     */
    public Set<String> setDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 根据key获取  hashKey对应的value的值 并转换为Bean
     *
     * @param key     键
     * @param hashKey hashKey
     * @return Bean
     */
    public <T> T hashGet(String key, String hashKey, Class<T> clazz) {
        String o = (String) redisTemplate.opsForHash().get(key, hashKey);
        return JSON.parseObject(o, clazz);
    }

    /**
     * 根据key获取  hashKey对应的value的List
     *
     * @param key 键
     * @return hashKey对应的value的List
     */
    public List<Object> hashMultiGet(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 返回键上的所有hashKey与value
     *
     * @param key 键
     * @return 键上所有的hashKey与value
     */
    public Map<Object, Object> hashGetAll(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 根据hashkey向key对应的HashMap中添加value
     *
     * @param key 键
     * @param hashKey 对应的HashMap中的Key
     * @param value 值
     */
    public void hashPut(String key, Object hashKey, Object value) {
        logger.debug("接口调用详情：参数K： " + key + " HashMap中的key： " + hashKey);
        redisTemplate.opsForHash().put(key, hashKey, JSON.toJSONString(value));
    }

    /**
     * 根据key向缓存中插入整个HashMap
     *
     * @param key 键
     * @param map 要插入的HashMap对象
     */
    public void hashPutAll(String key, Map<? extends Object, ? extends Object> map) {
        logger.debug("接口调用详情：参数K： " + key);
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key      键
     * @param hashKeys hash值
     * @return 被成功移除的域的数量，不包括被忽略的域。
     */
    public void hashDelete(String key, String... hashKeys) {
        if (hashKeys.length > 1) {
            Object[] objKeys = hashKeys.clone();
            redisTemplate.opsForHash().delete(key, objKeys);
        } else {
            redisTemplate.opsForHash().delete(key, (Object) hashKeys[0]);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("接口调用详情：参数K： " + key);
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key     键 不能为null
     * @param hashKey hash值 不能为null
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 根据key获取Map中所有的记录条数
     *
     * @param key 键
     * @return 返回哈希表 key 中域的数量。
     */
    public Long hashSize(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
     * <p>
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * score 值可以是整数值或双精度浮点数。
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     *
     * @param key   key
     * @param value value
     * @param score 分数
     * @return true/false
     */
    public Boolean zSetAdd(String key, String value, double score) {
        logger.debug("接口调用详情：参数K： " + key + "值： " + value + "");
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 根据分数从大到小 获取前几个
     *
     * @param key   key
     * @param limit 前几
     * @return value集合
     */
    public Set<String> zSetReverseRangeLimit(String key, long limit) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().reverseRange(key, 0, limit);
    }


    /**
     * ZSet 类型操作
     */
    public ZSetOperations<String, String> opsForZSet() {
        return redisTemplate.opsForZSet();
    }

    /**
     * String 类型操作
     */
    public ValueOperations<String, String> opsForValue() {
        return redisTemplate.opsForValue();
    }

    /**
     * Hash 类型操作
     */
    public HashOperations<String, Object, Object> opsForHash() {
        return redisTemplate.opsForHash();
    }

    /**
     * List 类型操作
     */
    public ListOperations<String, String> opsForList() {
        return redisTemplate.opsForList();
    }

    /**
     * Set 类型操作
     */
    public SetOperations<String, String> opsForSet() {
        return redisTemplate.opsForSet();
    }

    /**
     * Geo 类型操作 (经纬度)
     */
    public GeoOperations<String, String> opsForGeo() {
        return redisTemplate.opsForGeo();
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
     *
     * @param key    key 键
     * @param values values 值
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     */
    public Long zSetRemove(String key, Object... values) {
        logger.debug("接口调用详情：参数K： " + key + "值： " + Arrays.toString(values) + "");
        return redisTemplate.opsForZSet().remove(key, values);
    }


    /**
     * 返回一个成员范围的有序集合，通过索引，以分数排序，从低分到高分
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return value 集合
     */
    public Set<String> zSetRange(String key, long start, long end) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 返回一个成员范围的有序集合（由字典范围）
     *
     * @param key   key
     * @param range range
     * @param limit limit
     * @return 范围的Value集合
     */
    public Set<String> zSetRangeByLex(String key, Range range, Limit limit) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().rangeByLex(key, range, limit);
    }

    /**
     * 按分数返回一个成员范围的有序集合。
     * <p>
     * 返回Value集合，以分数排序从低分到高分排序
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 开始
     * @param count  数量
     * @return 范围的Value集合。
     */
    public Set<String> zSetRangeByScore(String key, double min, double max, long offset, long count) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 返回一个成员范围的有序集合，通过索引，以分数排序，从高分到低分
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return value 集合
     */
    public Set<String> zSetReverseRange(String key, long start, long end) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 返回Value集合，以分数排序从高分到低分排序
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 开始
     * @param count  数量
     * @return 范围的Value集合。
     */
    public Set<String> zSetReverseRangeByScore(String key, double min, double max, long offset, long count) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * 根据分数获取value的排名
     * <p>
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
     * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
     * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
     *
     * @param key    键
     * @param member value
     * @return 如果 member 是有序集 key 的成员，返回 member 的排名。
     * 如果 member 不是有序集 key 的成员，返回 nil 。
     */
    public Long zSetReverseRank(String key, String member) {
        if (logger.isDebugEnabled()) {
            logger.debug("接口调用详情：参数K： " + key);
        }
        return redisTemplate.opsForZSet().reverseRank(key, member);
    }

    /**
     * 返回有序集key中成员member的排名。
     * 其中有序集成员按score值递增(从小到大)顺序排列。排名以0为底，也就是说，score值最小的成员排名为0。
     * 使用ZREVRANK命令可以获得成员按score值递减(从大到小)排列的排名。
     *
     * @param key    键
     * @param member value
     * @return 如果member是有序集key的成员，返回integer-reply：member的排名。
     * 如果member不是有序集key的成员，返回bulk-string-reply: nil。
     */
    public Long zSetRank(String key, String member) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().rank(key, member);
    }

    /**
     * 返回有序集key中，成员member的score值。
     * 如果member元素不是有序集key的成员，或key不存在，返回nil。
     *
     * @param key    键
     * @param member value
     * @return member成员的score值（double型浮点数）
     */
    public Double zSetScore(String key, String member) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     * <p>
     * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     * 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
     * 当 key 不是有序集类型时，返回一个错误。
     * score 值可以是整数值或双精度浮点数。
     *
     * @param key    键
     * @param member value
     * @param deal   value
     * @return member 成员的新 score 值。
     */
    public Double zSetIncrementScore(String key, String member, double deal) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().incrementScore(key, member, deal);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * <p>
     * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE {@link RedisUtils#zSetReverseRangeWithScores}命令。
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。
     * 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。
     * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 成员和它的 score 值 集合
     */
    public List<ZSetItem> zSetRangeWithScores(String key, long start, long end) {
        if (logger.isDebugEnabled()) {
            logger.debug("接口调用详情：参数K： " + key);
        }
        Set<TypedTuple<String>> sset = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        return buildZSetList(sset);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * <p>
     * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，
     * 注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
     *
     * @param key    键
     * @param min    最小值
     * @param max    最大值
     * @param offset 偏移
     * @param count  数量
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     */
    public List<ZSetItem> zSetRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        logger.debug("接口调用详情：参数K： " + key + ",min:" + min + ",max:" + max + ",offset:" + offset + ",count:" + count);
        Set<TypedTuple<String>> sset = redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
        return buildZSetList(sset);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从大到小)来排序。
     * 参考； {@link RedisUtils#zSetRangeWithScores}
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 成员和它的 score 值 集合
     */
    public List<ZSetItem> zSetReverseRangeWithScores(String key, long start, long end) {
        if (logger.isDebugEnabled()) {
            logger.debug("接口调用详情：参数K： " + key);
        }
        Set<TypedTuple<String>> sset = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        return buildZSetList(sset);
    }

    /**
     * 判定当前key是否存在
     *
     * @param key 键
     * @return true/false
     */
    public Boolean hasKey(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.hasKey(key);
    }

    /**
     * 返回有序集 key 的基数。
     *
     * @param key 键
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。
     * 当 key 不存在时，返回 0 。
     */
    public Long zSetZCard(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 返回列表key的长度。
     * <p>
     * 如果key不存在，则key被解释为一个空列表，返回0. 如果key不是列表类型，返回一个错误。
     *
     * @param key 键
     * @return 列表 key 的大小。
     */
    public Long listSize(String key) {
        logger.debug("接口调用详情：参数K： " + key);
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 返回列表key中指定区间内的元素，区间以偏移量start和stop指定。
     * <p>
     * 下标(index)参数start和stop都以0为底，也就是说，以0表示列表的第一个元素，以1表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以-1表示列表的最后一个元素，-2表示列表的倒数第二个元素，以此类推。
     *
     * @param key   键
     * @param start 开始
     * @param end   结束
     * @return 一个列表，包含指定区间内的元素。
     */
    public List<String> listRange(String key, long start, long end) {
        if (logger.isDebugEnabled()) {
            logger.debug("接口调用详情：参数K： " + key);
        }
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。
     * 若给定的 key 已经存在，则 SETNX 不做任何动作。
     *
     * @param key     键
     * @param value   值
     * @param timeout 超时时间
     * @return
     */
    public Boolean setNx(String key, String value, long timeout) {
        Boolean b = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (b) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            return b;
        }
        return b;
    }

    public Set<String> deleteKeysInPattern(String pattern) {
        logger.debug("{}|{}|{}", "获取所有匹配pattern参数的Keys", "[KEYS pattern]:", pattern);
        Set<String> keySets = redisTemplate.keys(pattern);
        if (!CollectionUtils.isEmpty(keySets)) {
            redisTemplate.delete(keySets);
        } else {
            logger.info("{}|{}|{}", "根据匹配pattern参数获取的Keys集合为空", "[KEYS pattern]:" + pattern, keySets.size());
        }

        return keySets;
    }

    /**
     * 封装Zset 查询结果
     *
     * @param zset zset
     * @return List<ZSetItem>
     */
    private List<ZSetItem> buildZSetList(Set<TypedTuple<String>> zset) {
        List<ZSetItem> result = new ArrayList<>();
        Iterator<TypedTuple<String>> it = zset.iterator();
        while (it.hasNext()) {
            ZSetItem item = new ZSetItem();
            TypedTuple<String> typedTuple = it.next();
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            item.setValue(value);
            item.setScore(score);
            result.add(item);
        }
        return result;
    }

    /**
     * 自增 / 自减
     *
     * @param key   key
     * @param delta 1自增1 -1减少1
     * @return 执行 INCR 命令之后 key 的值。
     */
    public Long increment(String key, long delta) {
        logger.debug("{}|{}|{}", "increment接口开始调用：", "key:" + key, "delta:" + delta);
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Boolean expire(String key, long timeout, TimeUnit timeUnit) {
        logger.debug("{}|{}|{}|{}", "expire接口开始调用：", "key:" + key, "timeout:" + timeout, "timeUnit:" + timeUnit);
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    public Set<String> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Long zSetAdd(String key, Set<TypedTuple<String>> typedTuples) {
        return redisTemplate.opsForZSet().add(key, typedTuples);
    }

    public Long zSetRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    public Long zSetRemoveRange(String key, Long start, Long end) {
        long startValue = start != null ? start : 0;
        long endValue = end != null ? end : -1;
        return redisTemplate.opsForZSet().removeRange(key, startValue, endValue);
    }

    /**
     * 根据表达式查询所有key
     *
     * @return key Set
     */
    public Set<String> keys(String pattern) {
        logger.debug("接口调用详情：参数K： " + pattern);
        return redisTemplate.keys(pattern);

    }

    /* ***********************************重写锁**************************** */
    /**
     * 加锁
     * 取到锁加锁，取不到锁，则等待超时时间1秒，如果取不到，则返回
     *
     * @param lockKey 锁Key
     * @return
     */
    public Long lock(String lockKey) {
        Long waitingStartTime = System.nanoTime();
        try {
            do { //循环获取锁
                //锁时间
                Long lockTimeout = System.nanoTime() + LOCK_TIMEOUT + 1;
                if (setnx(lockKey, lockTimeout.toString())) {
                    //设置超时时间，释放内存
                    redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.NANOSECONDS);
                    return lockTimeout;
                } else {
                    //获取redis里面的时间
                    String result = redisTemplate.opsForValue().get(lockKey);
                    Long currtLockTimeoutStr = result == null ? null : Long.parseLong(result);
                    //锁已经失效
                    if (currtLockTimeoutStr != null && currtLockTimeoutStr < System.nanoTime()) {
                        //判断是否为空，不为空时，说明已经失效，如果被其他线程设置了值，则第二个条件判断无法执行
                        //获取上一个锁到期时间，并设置现在的锁到期时间
                        Long oldLockTimeoutStr =
                                Long.valueOf(redisTemplate.opsForValue().getAndSet(lockKey, lockTimeout.toString()));
                        if (oldLockTimeoutStr != null && oldLockTimeoutStr.equals(currtLockTimeoutStr)) {
                            //多线程运行时，多个线程签好都到了这里，但只有一个线程的设置值和当前值相同，它才有权利获取锁
                            //设置超时间，释放内存
                            redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.NANOSECONDS);
                            //返回加锁时间
                            return lockTimeout;
                        }
                    }
                }
            } while ((System.nanoTime() - waitingStartTime) < LOCAK_WAITING);
        } catch (Throwable e) {
            return null;
        }
        return null;
    }

    /**
     * 解锁
     *
     * @param lockKey
     * @param lockValue
     */
    public void unlock(String lockKey, Long lockValue) {
        if (lockValue == null) {
            return;
        }
        //获取redis中设置的时间
        String result = redisTemplate.opsForValue().get(lockKey);
        Long currtLockTimeoutStr = result == null ? null : Long.valueOf(result);
        //如果是加锁者，则删除锁， 如果不是，则等待自动过期，重新竞争加锁
        if (currtLockTimeoutStr != null && currtLockTimeoutStr.longValue() == lockValue.longValue()) {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public boolean setnx(final String key, final String value) {
        BoundValueOperations<String, String> boundValueOperations = redisTemplate.boundValueOps(key);
        return boundValueOperations.setIfAbsent(value);
    }

    /**
     * 尝试获取锁，默认设置锁的超时时间为30s，获取到锁返回token；获取不到锁，返回null
     *
     * @param key
     * @return
     */
    public String tryLock(String key) {
        return tryLock(key, 30, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁，获取到锁返回token；获取不到锁，返回null
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public String tryLock(String key, long timeout, TimeUnit unit) {
        Jedis jedis = null;
        try {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            long millseconds = timeUnit.convert(timeout, unit);
            // 加锁失败, 抛出异常
            long nowTime = System.currentTimeMillis();
            jedis = getJedis();
            String token = Md5Util.getMD5(key + "_" + nowTime);
            Assert.notNull(jedis, "can not get jedis connection");
            String result = jedis.set(key, token, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, millseconds);
            if (LOCK_SUCCESS.equals(result)) {
                return token;
            }
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 根据token释放锁
     *
     * @param lockKey
     * @param token
     */
    public boolean releaseLockByToken(String lockKey, String token) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Object result = jedis.eval(LUA_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(token));
            return RELEASE_SUCCESS.equals(result);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    /**
     * 获取jedis 对象
     */
    private Jedis getJedis() {
        RedisConnection jedisConnection = redisTemplate.getConnectionFactory().getConnection();
        Jedis jedis = (Jedis) jedisConnection.getNativeConnection();
        return jedis;
    }
}
