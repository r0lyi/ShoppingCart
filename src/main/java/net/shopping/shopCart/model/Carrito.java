package net.shopping.shopCart.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Carrito implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private List<Producto> productos;

    @JsonCreator
    public Carrito(@JsonProperty("productos") List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}