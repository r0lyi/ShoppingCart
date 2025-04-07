package net.elpuig.ShoppingCart.config;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;

@Configuration
public class CacheConfig {

    @Value("${memcached.servers}")
    private String memcachedServers;

    @Bean
    public MemcachedClient memcachedClient() throws IOException {
        String[] servers = memcachedServers.split(",");
        InetSocketAddress[] addresses = new InetSocketAddress[servers.length];

        for (int i = 0; i < servers.length; i++) {
            String[] parts = servers[i].split(":");
            addresses[i] = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
        }

        return new MemcachedClient(addresses);
    }
}