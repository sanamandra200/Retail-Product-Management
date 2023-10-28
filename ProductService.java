package com.test.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.entity.ApprovalQueue;
import com.test.entity.Product;
import com.test.exception.ProductNotFoundException;
import com.test.repository.ApprovalQueueRepository;
import com.test.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ApprovalQueueService approvalQueueService;
	
	@Autowired
	private ApprovalQueueRepository approvalQueueRepository;

    public List<Product> listActiveProducts() {
        return productRepository.findByStatusTrueOrderByPostedDateDesc();
    }

    public List<Product> searchProducts(String productName, Double minPrice, Double maxPrice, Date minPostedDate, Date maxPostedDate) {
    	List<Product> products = productRepository.searchByCriteria(productName, minPrice, maxPrice, minPostedDate, maxPostedDate);
        return products;
    }

	public Long createProduct(Product product) {
		// Set the postedDate to the current date and time
        product.setPostedDate(new Date());
		// Check if the product price exceeds $5,000
		if (product.getPrice() > 5000) {
			// If the price is above $5,000, add the product to the approval queue
			ApprovalQueue approval = new ApprovalQueue();
			approval.setProductId(product.getId());
			approval.setRequestDate(new Date());
			approval.setApproved(true);

			// Save the product to the database
			productRepository.save(product);

			// Save the approval request to the approval queue
			approvalQueueRepository.save(approval);

			return product.getId();
		} else {
			// If the price is within the acceptable range, save the product directly
			productRepository.save(product);
			return product.getId();
		}
	}

	public Product updateProduct(Long productId, Product product) {
        // Find the existing product by its ID
        Optional<Product> existingProductOptional = productRepository.findById(productId);
        
        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();
            
            // Check if the new price exceeds the threshold (e.g., $5000)
            double newPrice = product.getPrice();
            double existingPrice = existingProduct.getPrice();
            double priceChangeThreshold = 0.5; // 50% price change threshold

            if (newPrice > existingPrice * (1 + priceChangeThreshold)) {
                // Price exceeds the threshold, add to the approval queue
                approvalQueueService.addToApprovalQueue(existingProduct);
            }

            // Update the product's details
            existingProduct.setName(product.getName());
            existingProduct.setPrice(newPrice);
            existingProduct.setStatus(product.isStatus());
            existingProduct.setPostedDate(new Date()); // Update posted date if needed

            // Save the updated product
            return productRepository.save(existingProduct);
        } else {
            // Product with the given ID not found
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
    }
	public String deleteProduct(Long productId) {
        // Find the existing product by its ID
        Optional<Product> existingProductOptional = productRepository.findById(productId);

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();

            // Mark the product for deletion by setting its status
            existingProduct.setStatus(false); // Set the status to indicate deletion
            existingProduct.setPostedDate(new Date()); // Update posted date if needed

            // Add it to the approval queue for further processing
            approvalQueueService.addToApprovalQueue(existingProduct);

            // Save the updated product
            productRepository.delete(existingProduct);
        } else {
            // Product with the given ID not found
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
		return "Product is deleted wit Id: "+productId;
    }
}
