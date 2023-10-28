package com.test.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.entity.ApprovalQueue;
import com.test.entity.Product;
import com.test.repository.ApprovalQueueRepository;
import com.test.repository.ProductRepository;


@Service
public class ApprovalQueueService {
    @Autowired
    private ApprovalQueueRepository approvalQueueRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public List<ApprovalQueue> getProductsInApprovalQueue() {
        // Retrieve and return a list of products in the approval queue, sorted by request date (earliest first)
        return approvalQueueRepository.findByApprovedFalseOrderByRequestDateAsc();
    }

    public void approveProduct(Long approvalId) {
        // Find the approval request by ID
        ApprovalQueue approval = approvalQueueRepository.findById(approvalId)
                .orElseThrow(() -> new EntityNotFoundException("Approval request not found"));
//        Product p = new Product();
//        // Update the approval status
//        approval.setProductId(p.getId());
        approval.setApproved(false);
        approvalQueueRepository.save(approval);
        
        // Update the corresponding product's status
        Product product = productRepository.findById(approval.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setStatus(true); // Approve the product
        productRepository.save(product);
    }

    public void rejectProduct(Long approvalId) {
        // Find the approval request by ID
        ApprovalQueue approval = approvalQueueRepository.findById(approvalId)
                .orElseThrow(() -> new EntityNotFoundException("Approval request not found"));
        
        // Update the approval status
        approval.setApproved(true);
        approvalQueueRepository.save(approval);
    }

		public void addToApprovalQueue(Product existingProduct) {
	        // Create a new ApprovalQueue entry and associate it with the existingProduct
	        ApprovalQueue approvalQueueEntry = new ApprovalQueue();
	        approvalQueueEntry.setProductId(existingProduct.getId());
	        approvalQueueEntry.setRequestDate(new Date());
	        approvalQueueEntry.setApproved(false);

	        // Save the entry to the approval queue
	        approvalQueueRepository.save(approvalQueueEntry);
	    }
}
