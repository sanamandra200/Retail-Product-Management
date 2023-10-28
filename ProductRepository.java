package com.test.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.test.entity.ApprovalQueue;
import com.test.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByStatusTrueOrderByPostedDateDesc();

	// Define the custom query method to search products based on criteria
	@Query("SELECT p FROM Product p " + "WHERE (:productName IS NULL OR p.name LIKE %:productName%) "
			+ "AND (:minPrice IS NULL OR p.price >= :minPrice) " + "AND (:maxPrice IS NULL OR p.price <= :maxPrice) "
			+ "AND (:minPostedDate IS NULL OR p.postedDate >= :minPostedDate) "
			+ "AND (:maxPostedDate IS NULL OR p.postedDate <= :maxPostedDate)")
	List<Product> searchByCriteria(String productName, Double minPrice, Double maxPrice, Date minPostedDate,
			Date maxPostedDate);
}
