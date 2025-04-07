package net.elpuig.ShoppingCart.controller;

import net.elpuig.ShoppingCart.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(HttpServletRequest request, Model model) {
        String sessionId = request.getSession().getId();
        model.addAttribute("cart", cartService.getCartBySession(sessionId));
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(HttpServletRequest request,
                          @RequestParam Long productId,
                          @RequestParam(defaultValue = "1") int quantity) {
        String sessionId = request.getSession().getId();
        cartService.addToCart(sessionId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(HttpServletRequest request,
                               @RequestParam Long productId,
                               @RequestParam int quantity) {
        String sessionId = request.getSession().getId();
        cartService.updateQuantity(sessionId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(HttpServletRequest request,
                               @RequestParam Long productId) {
        String sessionId = request.getSession().getId();
        cartService.removeFromCart(sessionId, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        cartService.clearCart(sessionId);
        return "redirect:/cart";
    }
}