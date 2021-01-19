package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private CartController cartController = new CartController(userRepository, cartRepository, itemRepository);

    // test add to cart failure
    @Test
    public void testAddToCartFailure(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("emptyUser");
        modifyCartRequest.setItemId(101L);
        modifyCartRequest.setQuantity(1);

        // user not found
        when(userRepository.findByUsername("emptyUser")).thenReturn(null);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // item not found
        when(userRepository.findByUsername("emptyUser")).thenReturn(new User());
        when(itemRepository.findById(101L)).thenReturn(Optional.empty());
        responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    // test add to cart Success
    @Test
    public void testAddToCartSuccess(){
        User user = new User();
        user.setUsername("testUser");
        user.setCart(new Cart());

        Item item = new Item();
        item.setName("Apple");
        item.setId(101L);
        item.setPrice(new BigDecimal(10));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("testUser", 101L, 2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(101L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Cart cartRetrieved = responseEntity.getBody();
        assertEquals(Arrays.asList(item, item), cartRetrieved.getItems());
    }

    // test remove from cart
    @Test
    public void testRemoveFromCartSuccess(){
        Cart cart = new Cart();
        User user = new User();
        user.setUsername("testUser");
        user.setCart(cart);

        Item apple = new Item();
        apple.setName("Apple");
        apple.setId(101L);
        apple.setPrice(new BigDecimal(10));

        Item pear = new Item();
        pear.setName("pear");
        pear.setId(102L);
        pear.setPrice(new BigDecimal(10));

        cart.addItem(apple);
        cart.addItem(pear);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(101L)).thenReturn(Optional.of(apple));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("testUser", 101L, 1);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Cart cartRetrieved = responseEntity.getBody();
        assertEquals(1, cartRetrieved.getItems().size());
        assertEquals(Arrays.asList(pear), cartRetrieved.getItems());

    }

}
