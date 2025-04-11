
package net.shopping.shopCart.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.shopping.shopCart.model.Producto;
import net.shopping.shopCart.repository.ProductoRepository;
import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ListService {


    @Autowired
    private ProductoRepository productoRepository;


    @Autowired
    private MemcachedClient memcachedClient;


    private static final String PRODUCTOS_KEY = "lista_productos";
    private static final int EXPIRATION_TIME = (int) TimeUnit.HOURS.toSeconds(1); // 1 hora de expiración


    public List<Producto> obtenerProductos() throws JsonProcessingException {
        // Intentar obtener de Memcached primero
        String jsonProductos = (String) memcachedClient.get(PRODUCTOS_KEY);


        List<Producto> productos;


        if (jsonProductos != null) {
            // Si está en Memcached, deserializar el JSON a la lista de productos
            ObjectMapper mapper = new ObjectMapper();
            try {
                productos = mapper.readValue(jsonProductos, TypeFactory.defaultInstance().constructCollectionType(List.class, Producto.class));
            } catch (IOException e) {
                // Si ocurre un error al deserializar, lo manejamos y buscamos los productos en la base de datos
                productos = null;
            }
        } else {
            productos = null;
        }


        if (productos == null) {
            // Si no está en Memcached o hubo un error de deserialización, obtener de MySQL
            productos = productoRepository.findAll();


            // Almacenar en Memcached para futuras consultas
            if (productos != null && !productos.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonProductosNuevo = mapper.writeValueAsString(productos);
                memcachedClient.set(PRODUCTOS_KEY, EXPIRATION_TIME, jsonProductosNuevo);
            }
        }


        return productos;
    }
}



