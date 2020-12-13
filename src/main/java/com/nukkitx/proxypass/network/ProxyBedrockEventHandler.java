package com.nukkitx.proxypass.network;


import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.proxypass.ProxyPass;
import com.nukkitx.proxypass.network.bedrock.session.UpstreamPacketHandler;
import lombok.extern.log4j.Log4j2;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Log4j2
@ParametersAreNonnullByDefault
public class ProxyBedrockEventHandler implements BedrockServerEventHandler {
    private static final BedrockPong ADVERTISEMENT = new BedrockPong();

    private final ProxyPass proxy;

    public static int get(String url) throws Exception {
        StringBuilder sb = new StringBuilder();
        for(Scanner sc = new Scanner(new URL(url).openStream()); sc.hasNext(); )
            sb.append(sc.nextLine()).append('\n');
        //return sb.compareTo();
        return 0;
    }


static {
        ADVERTISEMENT.setEdition("MCPE");
        ADVERTISEMENT.setGameType("Survival");
        ADVERTISEMENT.setVersion(ProxyPass.MINECRAFT_VERSION);
        ADVERTISEMENT.setProtocolVersion(ProxyPass.PROTOCOL_VERSION);
        ADVERTISEMENT.setMotd("hardcore-servers.net");
    try {
        ADVERTISEMENT.setPlayerCount(get("https://minecraft-api.com/api/query/online/play.hardcore-servers.net/25565"));
    } catch (Exception e) {
        e.printStackTrace();
    }
    ADVERTISEMENT.setMaximumPlayerCount(100);
        ADVERTISEMENT.setSubMotd("hardcore-servers.net");
    }

    public ProxyBedrockEventHandler(ProxyPass proxy) {
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
