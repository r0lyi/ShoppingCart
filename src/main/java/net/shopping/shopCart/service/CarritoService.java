package net.shopping.shopCart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shopping.shopCart.model.Carrito;
import net.shopping.shopCart.model.Producto;
import net.shopping.shopCart.repository.ProductoRepository;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CarritoService {

    private final MemcachedClient memcachedClient;
    private final ProductoRepository productoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CARRITO_PREFIX = "list_carrito_";
    private static final int EXPIRATION = (int) TimeUnit.HOURS.toSeconds(24);

    @Autowired
    public CarritoService(MemcachedClient memcachedClient, ProductoRepository productoRepository) {
        this.memcachedClient = memcachedClient;
        this.productoRepository = productoRepository;
    }

    public void agregarProductoAlCarrito(int userId, int productId) throws JsonProcessingException {
        String cacheKey = CARRITO_PREFIX + userId;
        Producto producto = productoRepository.findById((long) productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Carrito carrito = obtenerCarrito(userId);
        carrito.getProductos().add(producto); // Añade el producto completo al carrito

        memcachedClient.set(cacheKey, EXPIRATION, objectMapper.writeValueAsString(carrito));
    }

    public Carrito obtenerCarrito(int userId) throws JsonProcessingException {
        String cacheKey = CARRITO_PREFIX + userId;
        String jsonCarrito = (String) memcachedClient.get(cacheKey);

        if (jsonCarrito == null) {
            return new Carrito(new ArrayList<>()); // Carrito vacío
        }
        return objectMapper.readValue(jsonCarrito, Carrito.class);
    }

    public void eliminarProductoDelCarrito(int userId, int productId) throws JsonProcessingException {
        String cacheKey = CARRITO_PREFIX + userId;
        Carrito carrito = obtenerCarrito(userId);

        // Eliminar todas las ocurrencias del producto
        List<Producto> nuevosProductos = carrito.getProductos().stream()
                .filter(p -> p.getId() != productId)
                .toList();

        if (nuevosProductos.isEmpty()) {
            memcachedClient.delete(cacheKey); // Elimina completamente la clave
        } else {
            memcachedClient.set(cacheKey, EXPIRATION,
                    objectMapper.writeValueAsString(new Carrito(nuevosProductos)));
        }
    }

    public BigDecimal calcularTotal(int userId) throws JsonProcessingException {
        return obtenerCarrito(userId).getProductos().stream()
                .map(p -> p.getPrecio() != null ? p.getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}