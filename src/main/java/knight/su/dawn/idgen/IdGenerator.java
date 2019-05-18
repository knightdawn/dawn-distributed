package knight.su.dawn.idgen;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sugengbin 2019/04/29
 */
public class IdGenerator {
    private ThreadLocal<String> txShard = new ThreadLocal();
    private AtomicInteger globalShardIndex = new AtomicInteger(-1);
    private int shardCount;

    public IdGenerator() {
    }

    public String genDefault(String libraryCode) {
        UUID uuid = UUID.randomUUID();
        byte[] data = new byte[24];
        write(data, 0, System.currentTimeMillis());
        write(data, 8, uuid.getMostSignificantBits());
        write(data, 16, uuid.getLeastSignificantBits());
        String id = Base64.getEncoder().encodeToString(data);
        return libraryCode + id;
    }

    public int setTxShard() {
        int shardValue = this.globalShardIndex.incrementAndGet();
        shardValue %= this.shardCount;
        if (shardValue < 0) {
            shardValue = -shardValue;
            this.globalShardIndex.set(-1);
        }

        String shard;
        if (shardValue < 10) {
            shard = "00" + shardValue;
        } else if (shardValue < 100) {
            shard = "0" + shardValue;
        } else {
            shard = String.valueOf(shardValue);
        }

        this.txShard.set(shard);
        return shardValue;
    }

    private static void write(byte[] data, int start, long value) {
        data[start + 0] = (byte)((int)(value >>> 56));
        data[start + 1] = (byte)((int)(value >>> 48));
        data[start + 2] = (byte)((int)(value >>> 40));
        data[start + 3] = (byte)((int)(value >>> 32));
        data[start + 4] = (byte)((int)(value >>> 24));
        data[start + 5] = (byte)((int)(value >>> 16));
        data[start + 6] = (byte)((int)(value >>> 8));
        data[start + 7] = (byte)((int)(value >>> 0));
    }

    public void setShardCount(int shardCount) {
        this.shardCount = shardCount;
    }
    
    public static void main(String[] args) {
		System.out.println(new IdGenerator().genDefault("755"));
	}
}