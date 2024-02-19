package com.example.Cat.Shop.service;
import com.example.Cat.Shop.dto.AddressDTO;
import com.example.Cat.Shop.model.Address;
import com.example.Cat.Shop.model.Cart;
import com.example.Cat.Shop.model.Product;
import com.example.Cat.Shop.model.Receipt;
import com.example.Cat.Shop.repository.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Service
public class ReceiptService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptService.class);
    private static final String JSON_FILE_PATH = "static/json/address.json";
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    CartRepo cartRepo;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    ReceiptRepo receiptRepo;

    //Receipt
    public List<Cart> limitProductNames(List<Cart> carts) {
        for (Cart cart : carts) {
            if (cart.getProduct() != null) {
                String originalName = cart.getProduct().getName();
                String limitedName = limitWords(originalName, 15);
                cart.getProduct().setName(limitedName);
            }
        }
        return carts;
    }
    public Address getDefaultAddress (Integer userId) {
        Address address = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);
        if (address!=null) {
            String province = this.getNameById(address.getProvince(),"province");
            String district = this.getNameById(address.getDistrict(),"district");
            String ward = this.getNameById(address.getWard(),"commune");
            address.setProvince(province);
            address.setDistrict(district);
            address.setWard(ward);
        }
        return address;
    }

    public List<Address> getUserAddresses (Integer userId) {
        this.updateUserAddressDefault(userId);
        List <Address> userAddress = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId) ;
        return userAddress;
    }

    public List<AddressDTO> getUserAddressesWithName (Integer userId) {
        List <Address> userAddress = this.getUserAddresses(userId) ;
        List <AddressDTO> userAddressName = new ArrayList<>();
        for (Address address : userAddress) {
            String province = this.getNameById(address.getProvince(),"province");
            String district = this.getNameById(address.getDistrict(),"district");
            String ward = this.getNameById(address.getWard(),"commune");

            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(address.getId());
            addressDTO.setUserId(userId);
            addressDTO.setNameReceiver(address.getNameReceiver());
            addressDTO.setProvince(province);
            addressDTO.setDistrict(district);
            addressDTO.setWard(ward);
            addressDTO.setStreetAndDepartment(address.getStreetAndDepartment());
            addressDTO.setAddressDefault(address.getAddressDefault());
            addressDTO.setPhoneNumber(address.getPhoneNumber());
            addressDTO.setType(address.getType());
            addressDTO.setProvinceId(address.getProvince());
            addressDTO.setDistrictId(address.getDistrict());
            addressDTO.setWardId(address.getWard());

            userAddressName.add(addressDTO);
        }

        return userAddressName;
    }

    public void setUserAddressDefault (Integer userId, Long addressId) {
        Address address = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);
        if (address!=null) {
            address.setAddressDefault(false);
            addressRepo.save(address);
            address = addressRepo.findById(addressId).orElse(null);
            address.setAddressDefault(true);
            addressRepo.save(address);
        } else {
            System.out.println("Lỗi ở dòng này");
        }
    }

    public void addUserAddress (Integer userId, String nameReceiver, String province, String district, String ward, String streetAndDepartment, String phoneNumber, String type) {
        Address address = new Address();
        List <Address> addresses = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId);
        if (addresses.isEmpty()) {
            address.setAddressDefault(true);
        } else {
            address.setAddressDefault(false);
        }
        address.setUser(userRepo.findById(userId).orElse(null));
        address.setNameReceiver(nameReceiver);
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);
        address.setStreetAndDepartment(streetAndDepartment);
        address.setPhoneNumber(phoneNumber);
        address.setType(type);
        addressRepo.save(address);
        this.setUserAddressDefault(userId, address.getId());
    }

    public void editUserAddress (Long addressId, String nameReceiver, String province, String district, String ward, String streetAndDepartment, String phoneNumber, String type) {
        Address address = addressRepo.findById(addressId).orElse(null);
        address.setNameReceiver(nameReceiver);
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);
        address.setStreetAndDepartment(streetAndDepartment);
        address.setPhoneNumber(phoneNumber);
        address.setType(type);
        addressRepo.save(address);
    }

    public String generateRandomReceiptCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = Integer.toString(randomNumber);
        Receipt receipt = receiptRepo.findByReceiptCode(code).orElse(null);
        while (receipt!=null) {
            randomNumber = random.nextInt(900000) + 100000;
            code = Integer.toString(randomNumber);
            receipt = receiptRepo.findByReceiptCode(code).orElse(null);
        }
        return code;
    }

    public String getNameById(String id, String type) {
        try {
            ClassPathResource resource = new ClassPathResource(JSON_FILE_PATH);
            InputStream inputStream = resource.getInputStream();

            JsonFactory factory = new JsonFactory();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonParser parser = factory.createParser(inputStream);

            // Tìm tên tương ứng dựa trên ID
            while (!parser.isClosed()) {
                JsonNode node = objectMapper.readTree(parser);
                JsonNode listNode = node.get(type.toLowerCase());

                if (listNode != null && listNode.isArray()) {
                    Iterator<JsonNode> elements = listNode.elements();
                    while (elements.hasNext()) {
                        JsonNode item = elements.next();
                        if (id.equals(item.get("id" + type.substring(0, 1).toUpperCase() + type.substring(1)).asText())) {
                            return item.get("name").asText();
                        }
                    }
                }

                // Di chuyển tới phần tiếp theo trong file
                parser.nextToken();
            }
            inputStream.close(); // Đóng luồng sau khi sử dụng
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ, ví dụ: log hoặc thông báo lỗi
        }
        return null;
    }

    public void deleteUserAddress (Long id, Integer userId) {
        addressRepo.deleteById(id);
    }

    public void updateUserAddressDefault (Integer userId) {
        if (addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null) == null) {
            List<Address> addresses = addressRepo.findByUserIdOrderByAddressDefaultDesc(userId);
            if (!addresses.isEmpty()) {
                Address addressModify = addresses.get(0);
                addressModify.setAddressDefault(true);
                addressRepo.save(addressModify);
            }
        }
    }

    public List<Double> getShippingFee (Integer userId) {
        Address addressUserDefault = addressRepo.findByUserIdAndAddressDefault(userId, true).orElse(null);
        List<Double> result = new ArrayList<>();
        if (addressUserDefault!=null && addressUserDefault.getProvince().equals("Thành phố Hồ Chí Minh")) {
            result.add(12000.0);
            result.add(18000.0);
        } else if (addressUserDefault!=null) {
            result.add(22000.0);
            result.add(30000.0);
        } else {
            result.add(0.0);
            result.add(1.0);
        }
        return result;
    }

    public List<String> getDayShipping () {
        List<String> dayShippings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dayShippings.add(now.plusDays(3).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(6).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(2).toLocalDate().format(formatter));
        dayShippings.add(now.plusDays(4).toLocalDate().format(formatter));
        return dayShippings;
    }

    public double addShippingFee (Integer userId, int option) {
        Address addressUserDefault = addressRepo.findByUserIdAndAddressDefault(userId,true).orElse(null);

        double result = 0;
        switch (option) {
            case 0:
                if (addressUserDefault.getProvince().equals("79")) {
                    result = 12000;
                } else {
                    result = 22000;
                }
                break;
            case 1:
                if (addressUserDefault.getProvince().equals("79")) {
                    result = 18000;
                } else {
                    result = 30000;
                }
                break;
        }
        return result;
    }

    public List<String> addDayShipping (int option) {
        List<String> listDay = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        switch (option) {
            case 0:
                listDay.add(now.plusDays(3).toLocalDate().format(formatter));
                listDay.add(now.plusDays(6).toLocalDate().format(formatter));
                break;
            case 1:
                listDay.add(now.plusDays(2).toLocalDate().format(formatter));
                listDay.add(now.plusDays(4).toLocalDate().format(formatter));
                break;
        }
        return listDay;
}

    public void addReceipt (List<Long> cartsId, Integer userId,
                            Long addressDefaultId,
                            double goodsFee,
                            double transportFee,
                            double totalPrice,
                            String receiptCode,
                            String paymentMethod,
                            String dayReceived) {
        Receipt receipt = new Receipt();
        List<Cart> cartsList = new ArrayList<>();
        for (Long id : cartsId) {
            Cart cart = cartRepo.findById(id).orElse(null);
            Product product = cart.getProduct();

            cart.setSold(2);
            cartRepo.save(cart);
            cartsList.add(cart);

            int newQuantitySold = product.getQuantitySold() + cart.getQuantity();
            int newQuantityInStore = product.getQuantityAdd() - newQuantitySold;
            product.setQuantitySold(newQuantitySold);
            product.setQuantityInStore(newQuantityInStore);
            productRepo.save(product);
        }
        receipt.setCartsList(cartsList);
        receipt.setUser(userRepo.findById(userId).orElse(null));

        LocalDateTime dateCreated = LocalDateTime.now();
        String formattedDate = dateCreated.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        receipt.setDateCreated(formattedDate);
        receipt.setDateCreatedDateFormat(dateCreated);

        receipt.setAddress(addressRepo.findById(addressDefaultId).orElse(null));
        receipt.setGoodsFee(goodsFee);
        receipt.setTransportFee(transportFee);
        receipt.setTotalPrice(totalPrice);
        receipt.setReceiptCode(receiptCode);
        receipt.setPaymentMethod(paymentMethod);
        receipt.setDayReceived(dayReceived);
        receiptRepo.save(receipt);
    }

    public List<Receipt> getReceiptOfUser (Integer userId) {
        List<Receipt> receipts = new ArrayList<>();
        receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        return receipts;
    }

    public Receipt getOneReceiptOfUser (Integer userId, int index) {
        List<Receipt> receipts = new ArrayList<>();
        receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        Receipt receipt = null;
        if (!receipts.isEmpty()) {
            receipt = receipts.get(index);
        }
        return receipt;
    }

    public Address getDefaultAddressInReceipt (Integer userId, int index) {
        List<Receipt> receipts = receiptRepo.findByUserIdOrderByDateCreatedDateFormatDesc(userId);
        Receipt receipt = receipts.get(index);
        Address address = receipt.getAddress();

        String province = this.getNameById(address.getProvince(),"province");
        String district = this.getNameById(address.getDistrict(),"district");
        String ward = this.getNameById(address.getWard(),"commune");
        address.setProvince(province);
        address.setDistrict(district);
        address.setWard(ward);

        return address;
    }

    private static String limitWords(String input, int wordLimit) {
        String[] words = input.split("\\s+");
        if (words.length > wordLimit) {
            StringBuilder limitedText = new StringBuilder();
            for (int i = 0; i < wordLimit; i++) {
                limitedText.append(words[i]).append(" ");
            }
            return limitedText.toString().trim() + " ...";
        }
        return input;
    }
}
