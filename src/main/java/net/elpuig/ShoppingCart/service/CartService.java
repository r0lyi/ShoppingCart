package net.elpuig.ShoppingCart.service;

import net.elpuig.ShoppingCart.model.Cart;
import net.elpuig.ShoppingCart.model.CartItem;
import net.elpuig.ShoppingCart.model.Product;
import net.elpuig.ShoppingCart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CacheService cacheService;

    public CartService(CartRepository cartRepository, ProductService productService, CacheService cacheService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.cacheService = cacheService;
    }

    public Cart getCartBySession(String sessionId) {
        Optional<Cart> cartOptional = cartRepository.findBySessionId(sessionId);
        return cartOptional.orElseGet(() -> createNewCart(sessionId));
    }

    private Cart createNewCart(String sessionId) {
        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addToCart(String sessionId, Long productId, int quantity) {
        Cart cart = getCartBySession(sessionId);
        Product product = productService.getProductById(productId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.addItem(newItem);
        }

        // Actualizar caché
        cacheService.updateCartInCache(sessionId, cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(String sessionId, Long productId) {
        Cart cart = getCartBySession(sessionId);

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        // Actualizar caché
        cacheService.updateCartInCache(sessionId, cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(String sessionId, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(sessionId, productId);
        }

        Cart cart = getCartBySession(sessionId);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        // Actualizar caché
        cacheService.updateCartInCache(sessionId, cart);

        return cartRepository.save(cart);
    }

    public void clearCart(String sessionId) {
        Cart cart = getCartBySession(sessionId);
        cart.getItems().clear();
        cartRepository.save(cart);

        // Limpiar caché
        cacheService.removeCartFromCache(sessionId);
    }
}