package net.elpuig.ShoppingCart.service;

import net.elpuig.ShoppingCart.model.Cart;
import net.elpuig.ShoppingCart.model.Product;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Eliminamos el import no utilizado
// import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    private final MemcachedClient memcachedClient;
    
    @Value("${memcached.expiration}")
    private int expirationTime;


    public CacheService(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public Cart getCartFromCache(String sessionId) {
        return (Cart) memcachedClient.get("cart_" + sessionId);
    }

    public void updateCartInCache(String sessionId, Cart cart) {
        memcachedClient.set("cart_" + sessionId, expirationTime, cart);
    }

    public void removeCartFromCache(String sessionId) {
        memcachedClient.delete("cart_" + sessionId);
    }

    public void cacheProduct(Product product) {
        memcachedClient.set("product_" + product.getId(), expirationTime, product);
    }

    public Product getProductFromCache(Long productId) {
        return (Product) memcachedClient.get("product_" + productId);
    }
}