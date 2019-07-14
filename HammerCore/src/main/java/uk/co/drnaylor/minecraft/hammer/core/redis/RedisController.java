package uk.co.drnaylor.minecraft.hammer.core.redis;

import org.checkerframework.checker.nullness.qual.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;

public class RedisController implements IRedisController {

    private final static String CHANNEL = "hammer";

    private boolean initComplete = false;
    private boolean shuttingDown = false;
    @Nullable private Thread subscribeThread;
    private final JedisPool pool;
    private final HammerCore core;
    @Nullable private Subscriber subscribingObject;

    RedisController(JedisPool pool, HammerCore core) {
        this.pool = pool;
        this.core = core;
    }

    @Override
    public void init() {
        if (!this.initComplete) {
            this.initComplete = true;

            this.subscribeThread = new Thread(this::redisSubscription);
            this.subscribeThread.setDaemon(true);
            this.subscribeThread.setName("Hammer: Redis Subscription");
            this.subscribeThread.start();
        }
    }

    @Override
    public void pushBan(HammerBan ban) {
        // TODO
    }

    @Override
    public void handleBan(HammerBan ban) {

    }

    @Override
    public void close() throws Exception {
        this.shuttingDown = true;
        this.pool.close();
        if (this.subscribeThread != null) {
            this.subscribeThread.interrupt();
            if (this.subscribingObject != null) {
                this.subscribingObject.unsubscribe();
            }
        }
    }

    private void redisSubscription() {
        while (!this.shuttingDown) {
            this.subscribingObject = new Subscriber();
            try (Jedis jedis = this.pool.getResource()) {
                jedis.subscribe(this.subscribingObject, CHANNEL);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class Subscriber extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            if (channel.equals(CHANNEL)) {
                // TODO: then do something
            }
        }
    }
}
