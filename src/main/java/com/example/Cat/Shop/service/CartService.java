package com.example.Cat.Shop.service;

import com.example.Cat.Shop.model.Cart;
import com.example.Cat.Shop.model.Product;
import com.example.Cat.Shop.model.User;
import com.example.Cat.Shop.repository.CartRepo;
import com.example.Cat.Shop.repository.ProductRepo;
import com.example.Cat.Shop.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CartRepo cartRepo;

    public void addProductToCart (Integer userId, Long productId, int quantity) {
        User user = userRepo.findById(userId).orElse(null);
        Product product = productRepo.findById(productId).orElse(null);
        this.updateCartSold(userId);
        Cart cart = new Cart();

        if(cartRepo.findByUserIdAndProductIdAndSold(userId,productId,0)!=null) {
            cart = cartRepo.findByUserIdAndProductIdAndSold(userId,productId,0);
            cart.setQuantity(cart.getQuantity()+quantity);
        } else {
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setSold(0);
        }
        cartRepo.save(cart);
    }

    public List<Cart> viewCart (Integer userId) {
        return cartRepo.findByUserIdAndSold(userId,0);
    }

    public void updateCartSold (Integer userId) {
        List<Cart> carts = cartRepo.findByUserIdAndSold(userId,1);
        if (!carts.isEmpty()) {
            for (Cart cart : carts) {
                cart.setSold(0);
                cartRepo.save(cart);
            }
        }
    }



    public List<Cart> viewCartSold (Integer userId) {
        return cartRepo.findByUserIdAndSold(userId,1);
    }

    public Cart findCartById (Long id) {
        return cartRepo.findById(id).orElse(null);
    }
    public double sumPriceProductsInCart (Integer userId) {
        List<Cart> carts = cartRepo.findByUserIdAndSold(userId,1);
        return carts.stream().mapToDouble(cart->cart.getProduct().getPrice()*cart.getQuantity()).sum();
    }

    public void saveCart(Cart cart) {
        cartRepo.save(cart);
    }
    public void deleteProductInCart(Long id) { cartRepo.deleteById(id); }

    public void updateSoldCart (Long cartId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        cart.setSold(1);
        cartRepo.save(cart);
    }
    public void updateNotSoldCart (Long cartId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        cart.setSold(0);
        cartRepo.save(cart);
    }
}
