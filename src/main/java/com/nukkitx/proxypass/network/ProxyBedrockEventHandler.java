package com.nukkitx.proxypass.network;

import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.proxypass.ProxyPass;
import com.nukkitx.proxypass.network.bedrock.session.UpstreamPacketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetSocketAddress;

@Log4j2
@ParametersAreNonnullByDefault
public class ProxyBedrockEventHandler implements BedrockServerEventHandler {
    private static final BedrockPong ADVERTISEMENT = new BedrockPong();

    private final ProxyPass proxy;

    static {
        ADVERTISEMENT.setEdition("MCPE");
        ADVERTISEMENT.setGameType("Survival");
        ADVERTISEMENT.setVersion(ProxyPass.MINECRAFT_VERSION);
        ADVERTISEMENT.setProtocolVersion(ProxyPass.PROTOCOL_VERSION);
        ADVERTISEMENT.setMotd("hardcore-servers.net");
        ADVERTISEMENT.setPlayerCount(0);
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
        session.setPacketHandler(new UpstreamPacketHandler());
    }
    public static int getPlayers(){
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("91.224.96.197", 25565), 1 * 1000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.write(0xFE);

            StringBuilder str = new StringBuilder();

            int b;
            while ((b = in.read()) != -1) {
                if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char) b);
                }
            }

            String[] data = str.toString().split("§");
            int onlinePlayers = Integer.valueOf(data[1]);
            int maxPlayers = Integer.valueOf(data[2]);
            String motd = String.valueOf(data[0]);
            
            ADVERTISEMENT.setPlayerCount(onlinePlayers);


        } catch (Exception evt) {
            evt.printStackTrace();
        }
        return 0;
    }
}
