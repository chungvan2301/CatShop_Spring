package com.example.Cat.Shop.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.Cat.Shop.model.*;
import com.example.Cat.Shop.repository.CartRepo;
import com.example.Cat.Shop.repository.RoleRepo;
import com.example.Cat.Shop.repository.UserRepo;
import com.example.Cat.Shop.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
	@Autowired
	CategoryService cateService;

	@Autowired
	UserRepo userRepo;
	@Autowired
	RoleRepo roleRepo;

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	CartService cartService;

	@Autowired
	ReceiptService receiptService;

	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private static int countProducts = 0;

	public static int getCount() {
		return countProducts;
	}

	private final static String pageSize = "6";

	@GetMapping({"/", "/home"})
	public String homePage(Model model, HttpServletRequest request) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);

			String role = user.getRoles().get(0).getName();
			model.addAttribute("user", user);
			model.addAttribute("role", role);
			return "index";

		} else {
			model.addAttribute("user", user);
			return "index";
		}
	}

	//Page shop mặc định
	@GetMapping("/shop")
	public String shopPage(Model model, HttpServletRequest request,
						   @RequestParam(defaultValue = "0") int page,
						   @RequestParam(defaultValue = pageSize) int size,
						   @RequestParam(defaultValue = "0") int sortField,
						   @RequestParam(defaultValue = "0") int sortDirection,
						   @RequestParam(defaultValue = "0") int statusProduct,
						   @RequestParam(defaultValue = "0") int minPrice,
						   @RequestParam(defaultValue = "5000000") int maxPrice
							) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}

			model.addAttribute("user", user);
			model.addAttribute("categories", cateService.getAllCategories());
			model.addAttribute("products", productService.getProductArrangement(page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice));
			model.addAttribute("productsSize", productService.getAllProduct(statusProduct,minPrice,maxPrice).size());
			model.addAttribute("sapxep", productService.sapXep(sortField,sortDirection));
			model.addAttribute("minPrice", minPrice);
			model.addAttribute("maxPrice", maxPrice);
			model.addAttribute("statusProduct", statusProduct);
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDirection", sortDirection);
			return "shop";
	}
	@GetMapping("/shop/products")
	public String getProducts(@RequestParam int page,
							  @RequestParam int size,
							  @RequestParam int sortField,
							  @RequestParam int sortDirection,
							  @RequestParam(defaultValue = "0") int statusProduct,
							  @RequestParam(defaultValue = "0") int minPrice,
							  @RequestParam(defaultValue = "5000000") int maxPrice,
							  Model model, HttpServletRequest request) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			List<Product> products = productService.getProductArrangement(page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice);
			model.addAttribute("products", products);
			countProducts+=products.size();
			if(countProducts+Integer.valueOf(pageSize)==productService.getAllProduct(statusProduct,minPrice,maxPrice).size()) {
				countProducts=0;
				model.addAttribute("coutProducts", countProducts);
			}
			return "productsPage";
	}


	@GetMapping("/shop/category/{id}")
	public String shopPage(Model model,
						   @PathVariable int id, HttpServletRequest request,
						   @RequestParam(defaultValue = "0") int page,
						   @RequestParam(defaultValue = pageSize) int size,
						   @RequestParam(defaultValue = "0") int sortField,
						   @RequestParam(defaultValue = "0") int sortDirection,
						   @RequestParam(defaultValue = "0") int statusProduct,
						   @RequestParam(defaultValue = "0") int minPrice,
						   @RequestParam(defaultValue = "5000000") int maxPrice
							) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			model.addAttribute("user",user);
			model.addAttribute("categories", cateService.getAllCategories());
			model.addAttribute("products", productService.getProductByCategoryArrangement(id,page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice));
			model.addAttribute("productsSize", productService.getProductByCategory(id,statusProduct,minPrice,maxPrice).size());
			model.addAttribute("categoryDef", cateService.getCategory(id));
			model.addAttribute("sapxep", productService.sapXep(sortField,sortDirection));
			model.addAttribute("minPrice", minPrice);
			model.addAttribute("maxPrice", maxPrice);
			model.addAttribute("statusProduct", statusProduct);
			model.addAttribute("sortField", sortField);
			model.addAttribute("sortDirection", sortDirection);
			return "shop";
	}

	@GetMapping("/shop/category/products/{id}")
	public String getProductsByCategory(@RequestParam int page,
										@RequestParam int size,
										@RequestParam int sortField,
										@RequestParam int sortDirection,
										@PathVariable int id,
										@RequestParam(defaultValue = "0") int statusProduct,
										@RequestParam(defaultValue = "0") int minPrice,
										@RequestParam(defaultValue = "5000000") int maxPrice,
										Model model, HttpServletRequest request) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			List<Product> products = productService.getProductByCategoryArrangement(id,page,size,sortField,sortDirection,statusProduct,minPrice,maxPrice);
			model.addAttribute("products", products);
			countProducts+=products.size();
			if(countProducts+Integer.valueOf(pageSize)==productService.getProductByCategory(id,statusProduct,minPrice,maxPrice).size()) {
				countProducts=0;
				model.addAttribute("coutProducts", countProducts);
			}
			return "productsPage";
	}

	//Page sản phẩm
	@GetMapping("/shop/viewproduct/{id}")
	public String viewProductPage(Model model, @PathVariable Long id, HttpServletRequest request) {
		User user = new User();
		List<ProductRating> listRating = productService.getListRating(id);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			model.addAttribute("listRating", listRating);
			model.addAttribute("user", user);
			model.addAttribute("categories", cateService.getAllCategories());
			model.addAttribute("products", productService.getProduct(id));
			return "viewProduct";
	}

	//Rating
	@PostMapping("/shop/viewproduct/rating/add")
	public String userRatingProduct(@RequestParam String productId,
									@RequestParam String userId,
									@RequestParam String rating,
									@RequestParam String comment) {
		Integer userIdInt = Integer.valueOf(userId);
		Long productIdLong = Long.valueOf(productId);
		int ratingInt = Integer.valueOf(rating);
		productService.saveRatingProduct(productIdLong,userIdInt,ratingInt,comment);
		return "redirect:/shop/viewproduct/" + productIdLong;
	}

	@PostMapping("shop/viewproduct/rating/delete")
	public String deleteUserRatingProduct(@RequestParam String productId,
										  @RequestParam String userId) {
		Integer userIdInt = Integer.valueOf(userId);
		Long productIdLong = Long.valueOf(productId);
		productService.removeUserRating(productIdLong,userIdInt);
		return "redirect:/shop/viewproduct/" + productIdLong;
	}

	@PostMapping("/shop/viewproduct/rating/report")
	public String reportRating(@RequestParam String id,
							   @RequestParam String productId,
							   RedirectAttributes redirectAttributes) {
		Long productRatingId = Long.valueOf(id);
		Long productIdLong = Long.valueOf(productId);
		productService.reportRating(productRatingId);
		redirectAttributes.addFlashAttribute("messageReport", "messageReport");
		return "redirect:/shop/viewproduct/" + productIdLong;
	}


	@GetMapping("/shop/search")
	public String viewSearchPage(Model model, @RequestParam String keyword,@RequestParam String categoryId, HttpServletRequest request) {

		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			int id = Integer.parseInt(categoryId);
			if ("0".equals(categoryId)) {
				model.addAttribute("products", productService.getProductBySearch(keyword));
			} else {
				model.addAttribute("products", productService.getProductBySearchAndCategory(keyword,id));
				model.addAttribute("categoryDef", cateService.getCategory(id));
			}
			model.addAttribute("user",user);
			model.addAttribute("categories", cateService.getAllCategories());
			model.addAttribute("keyword", keyword);
			return "search";
	}

	@GetMapping("/about")
	public String about(Model model, HttpServletRequest request){
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			model.addAttribute("user",user);
			return "about";
	}

	@GetMapping("/contact")
	public String contact(Model model, HttpServletRequest request) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
			model.addAttribute("user",user);
			return "contact";
	}

	//Giỏ hàng **************************************
	@GetMapping("/cart")
	public String viewCart(HttpServletRequest request, Model model) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
		cartService.updateCartSold(user.getId());

		model.addAttribute("carts", cartService.viewCart(user.getId()));
		model.addAttribute("user", user);
		model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
		return "cart";
	}

	@PostMapping("/cart/add")
	public String addCart(@RequestParam String userId, @RequestParam String productId, @RequestParam int quantity, Model model, RedirectAttributes redirectAttributes) {
		Integer user = Integer.valueOf(userId);
		Long product = Long.valueOf(productId);
		cartService.addProductToCart(user,product,quantity);
		User userNew = userRepo.findById(user).orElse(null);
		redirectAttributes.addFlashAttribute("message", "success");
		model.addAttribute("user", "userNew");
		return "redirect:/shop/viewproduct/" + product;
	}

	@PostMapping("/cart/update/{cartId}")
	public String updateCartQuantity(@PathVariable Long cartId, @RequestParam int quantity, Model model) {
		Integer idUser = cartService.findCartById(cartId).getUser().getId();
		Cart cart = cartService.findCartById(cartId);
		if (cart != null) {
			cart.setQuantity(quantity);
			cartService.saveCart(cart);
		}
		return "redirect:/cart";
	}

	@PostMapping("/cart/delete/{id}")
	public String deleteProductInCart(@PathVariable Long id) {
		Integer idUser = cartService.findCartById(id).getUser().getId();
		cartService.deleteProductInCart(id);
		return "redirect:/cart";
	}

	@PostMapping("/cart/sold/{cartId}")
	public ResponseEntity<?> updateSelectSoldCart(@PathVariable Long cartId, Model model) {
		cartService.updateSoldCart(cartId);
		return ResponseEntity.ok().build();
	}
	@PostMapping("/cart/not-sold/{cartId}")
	public ResponseEntity<?> updateSelectNotSoldCart(@PathVariable Long cartId) {
		cartService.updateNotSoldCart(cartId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/cart/update-sold-cart/{userId}")
	public String updateCartSold(@PathVariable Integer userId) {
		cartService.updateCartSold(userId);
		return "redirect:/cart";
	}


	//Thanh toán ****************************************
	@GetMapping("/thanhtoan")
	public String getReceiptPage(HttpServletRequest request, Model model) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
		List <Cart> carts = receiptService.limitProductNames(cartService.viewCartSold(user.getId()));
		Address addressDefault = receiptService.getDefaultAddress(user.getId());
		Long addressDefaultId = (addressDefault==null) ? 0 : addressDefault.getId();

		model.addAttribute("carts", carts);
		model.addAttribute("user", user);
		model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
		model.addAttribute("addressList", receiptService.getUserAddresses(user.getId()));
		model.addAttribute("addressDefault", addressDefault);
		model.addAttribute("shippingFee", receiptService.getShippingFee(user.getId()));
		model.addAttribute("dayShipping", receiptService.getDayShipping());
		model.addAttribute("receiptCode", receiptService.generateRandomReceiptCode());

		receiptService.getReceiptOfUser(user.getId());
		return "checkout";
	}

	@GetMapping("/thanhtoan/diachi")
	public String getAddressPage(HttpServletRequest request, Model model) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}
		model.addAttribute("carts", cartService.viewCart(user.getId()));
		model.addAttribute("user", user);
		model.addAttribute("sum", cartService.sumPriceProductsInCart(user.getId()));
		model.addAttribute("addressList", receiptService.getUserAddresses(user.getId()));
		model.addAttribute("addressListName", receiptService.getUserAddressesWithName(user.getId()));
		model.addAttribute("addressDefault", receiptService.getDefaultAddress(user.getId()));
		return "address";
	}

	@PostMapping("/thanhtoan/diachi/addAddress")
	public String addUserAddress(@RequestParam String userId,
							   @RequestParam String nameReceiver,
							   @RequestParam String province,
							   @RequestParam String district,
							   @RequestParam String ward,
							   @RequestParam String streetAndDepartment,
							   @RequestParam String phoneNumber,
							   @RequestParam String type
							   ) {
		Integer userIdInt = Integer.valueOf(userId);
		receiptService.addUserAddress(userIdInt,nameReceiver,province,district,ward,streetAndDepartment,phoneNumber,type);
		return "redirect:/thanhtoan/diachi";
	}

	@PostMapping("/thanhtoan/diachi/editAddress")
	public String editUserAddress(@RequestParam String addressId,
								@RequestParam String nameReceiver,
							    @RequestParam String province,
							    @RequestParam String district,
							    @RequestParam String ward,
							    @RequestParam String streetAndDepartment,
							    @RequestParam String phoneNumber,
							    @RequestParam String type
	) {
		Long addressIdLong = Long.valueOf(addressId);
		receiptService.editUserAddress(addressIdLong,nameReceiver,province,district,ward,streetAndDepartment,phoneNumber,type);
		return "redirect:/thanhtoan/diachi";
	}

	@PostMapping("/thanhtoan/diachi/deleteAddress")
	public String deleteUserAddress(@RequestParam String addressId,
									@RequestParam String userId) {
		Integer userIdInteger = Integer.valueOf(addressId);
		Long addressIdLong = Long.valueOf(addressId);
		receiptService.deleteUserAddress(addressIdLong,userIdInteger);
		return "redirect:/thanhtoan/diachi";
	}

	@PostMapping("/thanhtoan/diachi/setUserAddressDefault")
	public String setUserAddressDefault(@RequestParam String addressId,
									    @RequestParam String userId) {
		Integer userIdInteger = Integer.valueOf(userId);
		Long addressIdLong = Long.valueOf(addressId);
		receiptService.setUserAddressDefault(userIdInteger,addressIdLong);
		return "redirect:/thanhtoan";
	}

	@PostMapping("/thanhtoan/setShipping")
	public String setShpipingFee (Integer userId, int option, RedirectAttributes redirectAttributes) {
		double shippingFeeResult = receiptService.addShippingFee(userId,option);
		List<String> shippingDayResult = receiptService.addDayShipping(option);
		redirectAttributes.addFlashAttribute("shippingFeeResult", shippingFeeResult);
		redirectAttributes.addFlashAttribute("shippingDayResult", shippingDayResult);
		redirectAttributes.addFlashAttribute("optionResult", option);
		return "redirect:/thanhtoan";
	}

	@PostMapping("/thanhtoan/receipt")
	public String addReceipt (@RequestParam List<Long> cartIds,
							  @RequestParam Integer userId,
							  @RequestParam Long addressDefaultId,
							  @RequestParam double goodsFee,
							  @RequestParam double transportFee,
							  @RequestParam double totalPrice,
							  @RequestParam String receiptCode,
							  @RequestParam String paymentMethod,
							  @RequestParam String dayReceived,
							  RedirectAttributes redirectAttributes) {
		receiptService.addReceipt(cartIds,userId,addressDefaultId,goodsFee,transportFee,totalPrice,receiptCode,paymentMethod,dayReceived);
		redirectAttributes.addFlashAttribute("sucessfullPaid", "sucessfullPaid");
		return "redirect:/receipt";
	}

	@GetMapping("/receipt")
	public String getAllReceiptOfUserPage (HttpServletRequest request, Model model) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}

		model.addAttribute("user", user);
		model.addAttribute("receipts", receiptService.getReceiptOfUser(user.getId()));
		return "receipt";
	}

	@GetMapping("/receipt/{index}")
	public String getOneReceipt (HttpServletRequest request, Model model, @PathVariable int index) {
		User user = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!Objects.equals(authentication.getName(), "anonymousUser")) {
			user = userService.userLoggedIn(request);
		}

		Receipt receipt = receiptService.getOneReceiptOfUser(user.getId(),index);
		model.addAttribute("user", user);
	    model.addAttribute("receipt", receipt);
		if (receipt==null) return "pageNotFound";
		else {
			model.addAttribute("addressDefault", receiptService.getDefaultAddressInReceipt(user.getId(),index));
			return "viewReceipt";
		}
	}

	//User infor ****************************************
	@GetMapping("/user")
	public String userPage(HttpServletRequest request, Model model) {
			model.addAttribute("user", userService.userLoggedIn(request));
			return "userPage";
	}

	@PostMapping("/user/update")
	public String userUpdate(@RequestParam String userId, @RequestParam String firstName, @RequestParam String lastName, RedirectAttributes redirectAttributes) {
		Integer userIdInt = Integer.valueOf(userId);
		User user = userRepo.findById(userIdInt).orElse(null);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		userRepo.save(user);
		redirectAttributes.addFlashAttribute("messageInfor","successInforChange");
		return "redirect:/user";
	}

	@GetMapping("/user/changepassword")
	public String passwordChage(HttpServletRequest request, Model model) {
			model.addAttribute("user", userService.userLoggedIn(request));
			return "passwordChage";
	}

	@PostMapping("/user/password/update")
	public String passwordSubmit(@RequestParam String passwordOld, @RequestParam String passwordFinal, @RequestParam String userId, RedirectAttributes redirectAttributes) {
		Integer userIdInt = Integer.valueOf(userId);
		User user = userRepo.findById(userIdInt).orElse(null);
		if (!passwordEncoder.matches(passwordOld,user.getPassword())) {
			redirectAttributes.addFlashAttribute("message", "fail");
			return "redirect:/user/changepassword";
		} else {
			user.setPassword(passwordEncoder.encode(passwordFinal));
			userRepo.save(user);
			redirectAttributes.addFlashAttribute("messagePass", "successPassChange");
			return "redirect:/user";
		}
	}
}
