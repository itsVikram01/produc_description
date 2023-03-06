package com.productdesc.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.productdesc.entities.Product;
import com.productdesc.entities.User;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	@Query("from Product as p where p.user.id=:userId") // user from Product.java
	public Page<Product> findProductsByUser(@Param("userId")int userId, Pageable pageable);
	List<Product> findByPnameContainingAndUser(String pname, User user);

}
