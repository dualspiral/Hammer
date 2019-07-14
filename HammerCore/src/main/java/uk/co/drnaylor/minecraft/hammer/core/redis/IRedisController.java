package uk.co.drnaylor.minecraft.hammer.core.redis;

import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;

public interface IRedisController extends AutoCloseable {

    void init();

    void pushBan(HammerBan ban);

    void handleBan(HammerBan ban);

}
