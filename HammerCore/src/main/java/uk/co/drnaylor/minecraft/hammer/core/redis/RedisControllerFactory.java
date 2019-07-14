package uk.co.drnaylor.minecraft.hammer.core.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;

public class RedisControllerFactory {

    public final static RedisControllerFactory INSTANCE = new RedisControllerFactory();
    private final static IRedisController DUMMY = new IRedisController() {
        @Override public void init() { }

        @Override public void pushBan(HammerBan ban) { }

        @Override public void handleBan(HammerBan ban) { }

        @Override public void close() throws Exception { }
    };

    private RedisControllerFactory() { }

    public IRedisController create(HammerCore core, String host, int port) throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setLifo(true);

        JedisPool pool = new JedisPool(host, port);
        return new RedisController(pool, core);
    }

    public IRedisController getDummy() {
        return DUMMY;
    }

}
