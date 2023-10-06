package dev.apposed.prime.packet;

import dev.apposed.prime.packet.type.RefreshType;
import dev.apposed.prime.proxy.PrimeProxy;
import dev.apposed.prime.proxy.module.database.redis.packet.Packet;
import dev.apposed.prime.proxy.module.rank.Rank;
import dev.apposed.prime.proxy.module.rank.RankHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RankRefreshPacket extends Packet {

    private Rank rank;
    private RefreshType type;

    @Override
    public void onSend() {
    }

    @Override
    public void onReceive() {
        final RankHandler rankHandler = PrimeProxy.getInstance().getModuleHandler().getModule(RankHandler.class);
        switch(type) {
            case UPDATE: {
                rankHandler.updateRank(rank);
                break;
            }
            case REMOVE: {
                rankHandler.getCache().remove(rank);
                break;
            }
        }
    }
}