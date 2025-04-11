package net.shopping.shopCart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.shopping.shopCart.service.ListService;
import net.shopping.shopCart.model.Producto;
import net.shopping.shopCart.model.Usuario;
import net.shopping.shopCart.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListService listService;

    // Verificación centralizada de autenticación
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("usuario") != null;
    }

    // Redirige a home si está autenticado
    @GetMapping("/")
    public String rootRedirect(HttpSession session) {
        return isAuthenticated(session) ? "redirect:/home" : "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contraseña,
                                HttpSession session,
                                Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoAndContraseña(correo, contraseña);

        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuario", usuarioOpt.get());
            return "redirect:/home";
        }

        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    // Vista principal protegida
    @GetMapping("/home")
    public String mostrarHome(HttpSession session, Model model) throws JsonProcessingException {
        if (!isAuthenticated(session)) {
            return "redirect:/";
        }

        model.addAttribute("productos", listService.obtenerProductos());
        return "home";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Manejo de errores
    @GetMapping("/error")
    public String handleError() {
        return "redirect:/";
    }
}