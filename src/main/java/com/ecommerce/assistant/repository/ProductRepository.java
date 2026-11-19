
package com.ecommerce.assistant.repository;

import com.ecommerce.assistant.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

 List<Product> findByIdInAndActiveTrue(Collection<Long> ids);

 List<Product> findByActiveTrue();

 List<Product> findByNameIgnoreCaseAndActiveTrue(String name);
}
