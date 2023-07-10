package org.skydom.chosen.server.skydomvelocityplugin.ping;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.util.ArrayList;
import java.util.List;

public class GlobalPing {
    private final ProxyServer server;
    public GlobalPing(ProxyServer server) {
        this.server = server;
    }
    @Subscribe
    public void onPing(ProxyPingEvent event) {
        List<ServerPing.SamplePlayer> samplePlayers = new ArrayList<>();
        for (RegisteredServer s : server.getAllServers()) {
            for (Player p : s.getPlayersConnected()) {
                samplePlayers.add(new ServerPing.SamplePlayer(p.getUsername(), p.getUniqueId()));
            }
        }
        ServerPing ping = event.getPing();
        ServerPing newPing = ping.asBuilder().samplePlayers(samplePlayers.toArray(ServerPing.SamplePlayer[]::new)).build();
        event.setPing(newPing);
    }
}
