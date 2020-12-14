package com.nukkitx.proxypass.network;


import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.proxypass.ProxyPass;
import com.nukkitx.proxypass.network.bedrock.session.UpstreamPacketHandler;
import com.tekgator.queryminecraftserver.api.QueryException;
import lombok.extern.log4j.Log4j2;
import com.tekgator.queryminecraftserver.api.Protocol;
import com.tekgator.queryminecraftserver.api.QueryStatus;


import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Log4j2
@ParametersAreNonnullByDefault
public class ProxyBedrockEventHandler implements BedrockServerEventHandler {
    private static final BedrockPong ADVERTISEMENT = new BedrockPong();

    private static ProxyPass proxy;

public static int getPlayersCount() throws QueryException {
    return new QueryStatus.Builder(proxy.online.toString()).build().getStatus().getPlayers().getOnlinePlayers();
}

static {
        ADVERTISEMENT.setEdition("MCPE");
        ADVERTISEMENT.setGameType("Survival");
        ADVERTISEMENT.setVersion(ProxyPass.MINECRAFT_VERSION);
        ADVERTISEMENT.setProtocolVersion(ProxyPass.PROTOCOL_VERSION);
        ADVERTISEMENT.setMotd(proxy.motd);
    try {
        Timer timer = new Timer();
        int begin = 0;
        int timeInterval = 1000;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                while(true){
                    try {
                        ADVERTISEMENT.setPlayerCount(getPlayersCount());
                    } catch (QueryException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, begin, timeInterval);
    } catch (Exception e) {
        e.printStackTrace();
    }
    ADVERTISEMENT.setMaximumPlayerCount(100);
        ADVERTISEMENT.setSubMotd(proxy.motd);
    }

    public ProxyBedrockEventHandler(ProxyPass proxy) throws QueryException {
        this.proxy = proxy;
        int port = this.proxy.getProxyAddress().getPort();
        ADVERTISEMENT.setIpv4Port(port);
        ADVERTISEMENT.setIpv6Port(port);
    }

    @Override
    public boolean onConnectionRequest(InetSocketAddress address) {
        return !this.proxy.isFull();
    }

    @Nonnull
    public BedrockPong onQuery(InetSocketAddress address) {
        return ADVERTISEMENT;
    }

    @Override
    public void onSessionCreation(BedrockServerSession session) {
        session.setPacketHandler(new UpstreamPacketHandler(session, this.proxy));
    }
}
