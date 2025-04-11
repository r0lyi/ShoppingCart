package net.shopping.shopCart.controller;

import net.shopping.shopCart.model.Carrito;
import net.shopping.shopCart.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    @Autowired
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping("/agregar/{productId}")
    public String agregarAlCarrito(@PathVariable int productId) throws Exception {
        int userId = obtenerUsuarioId(); // Implementar lógica de usuario
        carritoService.agregarProductoAlCarrito(userId, productId);
        return "redirect:/";
    }

    @GetMapping
    public String verCarrito(Model model) throws Exception {
        int userId = obtenerUsuarioId();
        model.addAttribute("carrito", carritoService.obtenerCarrito(userId));
        model.addAttribute("total", carritoService.calcularTotal(userId));
        return "carrito";
    }

    @PostMapping("/eliminar/{productId}")
    public String eliminarDelCarrito(@PathVariable int productId) throws Exception {
        int userId = obtenerUsuarioId();
        carritoService.eliminarProductoDelCarrito(userId, productId);
        return "redirect:/carrito";
    }

    // Método temporal para simular usuario
    private int obtenerUsuarioId() {
        return 1; // Reemplazar con lógica real de autenticación
    }
}