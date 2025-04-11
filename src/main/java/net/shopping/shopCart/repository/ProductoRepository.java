package net.shopping.shopCart.repository;

import net.shopping.shopCart.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // No es necesario declarar métodos básicos como findAll()
    // Spring Data JPA los provee automáticamente
}