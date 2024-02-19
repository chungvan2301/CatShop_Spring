package com.example.Cat.Shop.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Cat.Shop.dto.ProductDTO;
import com.example.Cat.Shop.model.Category;
import com.example.Cat.Shop.model.Product;
import com.example.Cat.Shop.service.CategoryService;
import com.example.Cat.Shop.service.ProductService;



@Controller
public class adminController {
	public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
	
	@Autowired
	private CategoryService cateService;
	@Autowired
	private ProductService productService;
	
	@GetMapping("/admin")
	public String adminHome() {
		return "adminHome";
	}
	
	//Category
	@GetMapping("/admin/categories")
	public String getCategories(Model model) {
		model.addAttribute("categories", cateService.getAllCategories());
		return "categories";
	}
	
	@GetMapping("/admin/categories/add")
	public String getCategoriesAdd(Model model) {
		model.addAttribute("category", new Category());
		return "categoriesAdd";
	}
	
	@PostMapping("/admin/categories/add")
	public String addNewCategory(@ModelAttribute("category") Category category) {
		cateService.addCategory(category);
		return "redirect:/admin/categories";
	}
	
	@GetMapping("/admin/categories/delete/{id}")
	public String deleteCategory(@PathVariable(required = true) Integer id) {
		cateService.deleteCategory(id);
		return "redirect:/admin/categories";
	}
	
	@GetMapping("/admin/categories/edit/{id}")
	public String editCategory(@PathVariable(required = true) Integer id, Model model) {
		model.addAttribute("category",cateService.getCategory(id));
		return "categoriesAdd";
	}
	
	//Product
	@GetMapping("/admin/products")
	public String getProducts(Model model) {
		model.addAttribute("products", productService.getAllProduct());
		return "products";
	}
	@GetMapping("/admin/products/add")
	public String getProductAdd(Model model) {
		model.addAttribute("productDTO", new ProductDTO());
		model.addAttribute("categories", cateService.getAllCategories());
		return "productsAdd";
	}
	
	@PostMapping("/admin/products/add")
	public String addProduct(@ModelAttribute("productDTO") ProductDTO  productDTO, @RequestParam("productImage") MultipartFile file, @RequestParam("imageName") String imageName) throws IOException {
		Product product = new Product();
		product.setId(productDTO.getId());
		product.setName(productDTO.getName());
		product.setPrice(productDTO.getPrice());
		product.setCategory(cateService.getCategory(productDTO.getCategoryId()));
		product.setDescription(productDTO.getDescription());
		product.setQuantityAdd(productDTO.getQuantityAdd());
		product.setAddDate(LocalDateTime.now());
		product.setProductStatus("new");

		String imageUUID;
		
		if(!file.isEmpty()) {
			imageUUID = file.getOriginalFilename();
			Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
			Files.write(fileNameAndPath, file.getBytes());
		} else {
			imageUUID = imageName;
		}
		product.setImageName(imageUUID);
		productService.addProduct(product);
		return "redirect:/admin/products";
	}
	
	@GetMapping("/admin/products/delete/{id}")
	public String deleteProduct(@PathVariable(required = true) Long id) {
		productService.deleteProduct(id);
		return "redirect:/admin/products";
	}
	
	@GetMapping("/admin/products/edit/{id}")
	public String editProduct(@PathVariable(required = true) Long id, Model model) {
		ProductDTO productDTO = new ProductDTO();
		Product product = productService.getProduct(id);
		productDTO.setId(product.getId()); 
		productDTO.setName(product.getName());
		productDTO.setPrice(product.getPrice());
		productDTO.setCategoryId((int)product.getCategory().getId());
		productDTO.setDescription(product.getDescription());
		productDTO.setImageName(product.getImageName());
		product.setQuantityAdd(productDTO.getQuantityAdd());
		
		model.addAttribute("categories", cateService.getAllCategories());
		model.addAttribute("productDTO",productDTO);
		 
		return "productsAdd";
	}
}

