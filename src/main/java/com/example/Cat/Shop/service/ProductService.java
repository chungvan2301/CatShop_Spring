package com.example.Cat.Shop.service;

//import java.awt.print.Pageable;
import com.example.Cat.Shop.model.Address;
import com.example.Cat.Shop.model.Cart;
import com.example.Cat.Shop.model.ProductRating;
import com.example.Cat.Shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Array;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.example.Cat.Shop.model.Product;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class ProductService {
	@Autowired
	ProductRepo productRepo;
	@Autowired
	ProductRatingRepo productRatingRepo;
	@Autowired
	UserRepo userRepo;
	@Autowired
	AddressRepo addressRepo;
	@Autowired
	ReceiptRepo receiptRepo;

	private static final Set<String> productAllStatuses = new HashSet<>(Arrays.asList("new",""));
	private static final Set<String> productStatusNew = new HashSet<>(Arrays.asList("new"));

	public void addProduct(Product product) {
		productRepo.save(product);
	}
	
	public void deleteProduct(Long id) {
		productRepo.deleteById(id);
	}
	
	public Product getProduct(Long id) {
		Product product = productRepo.findById(id).get();
		return product;
	}

	public List<Product> getAllProduct(){
		List<Product> products = productRepo.findAll();
		return products;
	}

	//Pagination****************************
	public List<Product> getAllProduct(int statusProduct,
									   int minPrice,
									   int maxPrice){
		List<Product> products = productRepo.findAllByPriceBetweenAndProductStatusIn(minPrice, maxPrice, statusProduct==1 ? productStatusNew : productAllStatuses) ;
		return products;
	}

	public List<Product> getProductByCategory(int id,
											  int statusProduct,
											  int minPrice,
											  int maxPrice){
		List<Product> products = productRepo.findByCategoryIdAndPriceBetweenAndProductStatusIn(id, minPrice, maxPrice, statusProduct==1 ? productStatusNew : productAllStatuses);
		return products;
	}

	public List<Product> getProductPagination(Pageable pageable,
											  int statusProduct,
											  int minPrice,
											  int maxPrice) {
		List<Product> products = productRepo.findAllByPriceBetweenAndProductStatusIn(minPrice, maxPrice, statusProduct == 1 ?  productStatusNew : productAllStatuses, pageable);
		return products;
	}

	public List<Product> getProductByCategoryPagination(int id, Pageable pageable,
														int statusProduct,
														int minPrice,
														int maxPrice){
		List<Product> products = productRepo.findByCategoryIdAndPriceBetweenAndProductStatusIn(id, minPrice, maxPrice, statusProduct==1 ?  productStatusNew : productAllStatuses, pageable);
		return products;
	}

	public List<Product> getProductArrangement(int page, int size, int sortField, int sortDirection,
											   int statusProduct,
											   int minPrice,
											   int maxPrice){
		List<Product> products = null;
		Sort sort = Sort.unsorted();
		switch (sortField) {
			case 1:
				sort = Sort.by(Sort.Direction.DESC, "quantitySold");
				break;
			case 2:
				sort = Sort.by(sortDirection == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, "price");
				break;
		}
		Pageable pageable = PageRequest.of(page, size, sort);
		products = this.getProductPagination(pageable, statusProduct, minPrice, maxPrice);
		return products;
	}

	public List<Product> getProductByCategoryArrangement(int id, int page, int size, int sortField, int sortDirection,
														 int statusProduct,
														 int minPrice,
														 int maxPrice){
		List<Product> products = null;
		Sort sort = Sort.unsorted();
		switch (sortField) {
			case 1:
				sort = Sort.by(Sort.Direction.DESC, "quantitySold");
				break;
			case 2:
				sort = Sort.by(sortDirection == 0 ? Sort.Direction.DESC : Sort.Direction.ASC, "price");
				break;
		}
		Pageable pageable = PageRequest.of(page, size, sort);
		products = this.getProductByCategoryPagination(id,pageable,statusProduct,minPrice,maxPrice);
		return products;
	}

	public List<Product> getProductBySearch(String keyword) {
		List<Product> products = productRepo.findByNameContainingIgnoreCase(keyword);
		return products;
	}
	public List<Product> getProductBySearchAndCategory(String keyword, int id) {
		List<Product> products = productRepo.findByNameContainingIgnoreCaseAndCategory_Id(keyword, id);
		return products;
	}

	//Rating********************************
	public void saveRatingProduct(Long productId, Integer userId, int rating, String comment) {
		if (productRatingRepo.findByProductIdAndUserId(productId,userId)==null) {
			ProductRating productRating = new ProductRating();
			productRating.setProductId(productId);
			productRating.setUser(userRepo.findById(userId).orElse(null));
			productRating.setRating(rating);
			productRating.setComment(comment);
			productRating.setReportVotes(0);
			productRating.setDateTime(LocalDateTime.now());
			productRatingRepo.save(productRating);
		} else {
			ProductRating productRating = productRatingRepo.findByProductIdAndUserId(productId,userId);
			productRating.setProductId(productId);
			productRating.setUser(userRepo.findById(userId).orElse(null));
			productRating.setRating(rating);
			productRating.setComment(comment);
			productRating.setDateTime(LocalDateTime.now());
			productRatingRepo.save(productRating);
		}
		this.saveAvgProductRating(productId);
	}

	public void saveAvgProductRating(Long productId) {
		Product product = productRepo.findById(productId).orElse(null);
		List<ProductRating> allProductRating = productRatingRepo.findAllByProductIdOrderByDateTimeDesc(productId);
		if (allProductRating.isEmpty()) {
			product.setAvgRating(0);
			productRepo.save(product);
		}
		else {
			int sumRating = allProductRating.stream().collect(Collectors.summingInt(ProductRating::getRating));
			int countUserRating = allProductRating.size();
			double avgRating = (double) sumRating/countUserRating;
			product.setAvgRating(avgRating);
			productRepo.save(product);
		}
	}

	public void removeUserRating (Long productId, Integer userId) {
		//productRatingRepo.deleteByProductIdAndUserId(productId,userId);
		productRatingRepo.deleteById(productRatingRepo.findByProductIdAndUserId(productId,userId).getId());
		this.saveAvgProductRating(productId);
	}

	public List<ProductRating> getListRating(Long productid) {
		return productRatingRepo.findAllByProductIdOrderByDateTimeDesc(productid);
	}

	public void reportRating (Long id) {
		ProductRating productRating = productRatingRepo.findById(id).orElse(null);
		int Votes = productRating.getReportVotes();
		Votes++;
		productRating.setReportVotes(Votes);
		productRatingRepo.save(productRating);
	}

	//Index page
	public List<Product> getOutstandingProducts () {
		List<Product> products = new ArrayList<>();
		products = productRepo.findTop4ByProductStatusOrderByAddDateDesc("new");
		return products;
	}

	public List<Product> getSoldOutProducts () {
		List<Product> products = new ArrayList<>();
		products = productRepo.findTop4ByOrderByQuantitySoldDesc();
		return products;
	}

	//Others
	public static String sapXep(int sortField, int sortDirection) {
		String sapxep = new String();
		switch (sortField) {
			case 0:
				sapxep="Xếp theo: Mặc định";
				break;
			case 1	:
				sapxep="Xếp theo: Nổi bật";
				break;
			case 2:
				if (sortDirection==0) {
					sapxep="Xếp theo: Giá cao đến thấp";
				} else {
					sapxep="Xếp theo: Giá thấp đến cao";
				};
				break;
		}
		return sapxep;
	}
}
