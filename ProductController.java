package com.test.controller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.entity.ApprovalQueue;
import com.test.entity.Product;
import com.test.service.ApprovalQueueService;
import com.test.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private ApprovalQueueService approvalQueueService;

	@GetMapping("/allProducts")
	public List<Product> listActiveProducts() {
		return productService.listActiveProducts();
	}

	@GetMapping("/searchProduct")
	public List<Product> searchProducts(@RequestParam(required = false) String productName,
			@RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Date minPostedDate, @RequestParam(required = false) Date maxPostedDate) {
		return productService.searchProducts(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
	}

	@PostMapping("/saveProduct")
	public ResponseEntity<?> createProduct(@RequestBody Product product) {
		long id = productService.createProduct(product);
		return ResponseEntity.ok().body("New Product has been saved with ID:" + id);
	}

	@PutMapping("/updateProduct/{productId}")
	public Product updateProduct(@PathVariable Long productId, @RequestBody Product product) {
		return productService.updateProduct(productId, product);
	}

	@DeleteMapping("deleteProduct/{productId}")
	public String deleteProduct(@PathVariable Long productId) {
		return productService.deleteProduct(productId);
		 
	}

	@GetMapping("/approval-queue")
	public List<ApprovalQueue> getProductsInApprovalQueue() {
		return approvalQueueService.getProductsInApprovalQueue();
	}

	@PutMapping("/approval-queue/{approvalId}/approve")
	public void approveProductInQueue(@PathVariable Long approvalId) {
		approvalQueueService.approveProduct(approvalId);
	}

	@PutMapping("/approval-queue/{approvalId}/reject")
	public void rejectProductInQueue(@PathVariable Long approvalId) {
		approvalQueueService.rejectProduct(approvalId);
	}
}
