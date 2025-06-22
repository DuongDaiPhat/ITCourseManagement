package backend.service.user;

import backend.repository.user.MyCartRepository;
import model.user.MyCart;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CartService {
    private MyCartRepository cartRepository;

    public CartService() {
        this.cartRepository = new MyCartRepository();
    }    /**
     * Add course to cart
     */
    public boolean addToCart(int userId, int courseId) {
        try {
            // Check if course already exists in cart
            MyCart existingCart = getCartItem(userId, courseId);
            if (existingCart != null) {
                return false; // Already in cart
            }

            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            MyCart newCart = new MyCart(userId, courseId, false, LocalDateTime.now());
            int result = freshRepository.Insert(newCart);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }    /**
     * Remove course from cart
     */
    public boolean removeFromCart(int userId, int courseId) {
        try {
            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            MyCart cartToRemove = new MyCart(userId, courseId, false, LocalDateTime.now());
            int result = freshRepository.Delete(cartToRemove);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }/**
     * Check if course is in cart
     */
    public boolean isInCart(int userId, int courseId) {
        try {
            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            ArrayList<MyCart> cartItems = freshRepository.SelectByCondition(
                "userId = " + userId + " AND courseId = " + courseId + " AND isBuy = false"
            );
            return !cartItems.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error checking cart: " + e.getMessage());
            return false;
        }
    }    /**
     * Get specific cart item
     */
    public MyCart getCartItem(int userId, int courseId) {
        try {
            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            ArrayList<MyCart> cartItems = freshRepository.SelectByCondition(
                "userId = " + userId + " AND courseId = " + courseId + " AND isBuy = false"
            );
            return cartItems.isEmpty() ? null : cartItems.get(0);
        } catch (SQLException e) {
            System.err.println("Error getting cart item: " + e.getMessage());
            return null;
        }
    }/**
     * Get cart count for user
     */
    public int getCartCount(int userId) {
        try {
            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            ArrayList<MyCart> cartItems = freshRepository.SelectByCondition(
                "userId = " + userId + " AND isBuy = false"
            );
            return cartItems.size();
        } catch (SQLException e) {
            System.err.println("Error getting cart count: " + e.getMessage());
            return 0;
        }
    }    /**
     * Get all cart items for user
     */
    public ArrayList<MyCart> getUserCartItems(int userId) {
        try {
            // Create new repository instance to avoid connection issues
            MyCartRepository freshRepository = new MyCartRepository();
            System.out.println("DEBUG CartService: Getting cart items for user " + userId);
            
            // Use direct SQL query instead of relying on potentially stale connection
            String condition = "userId = " + userId + " AND isBuy = false";
            ArrayList<MyCart> items = freshRepository.SelectByCondition(condition);
            
            System.out.println("DEBUG CartService: Found " + items.size() + " cart items");
            for (MyCart item : items) {
                System.out.println("DEBUG CartService: Item - CourseID: " + item.getCourseId() + ", UserID: " + item.getUserId() + ", isBuy: " + item.isBuy());
            }
            return items;
        } catch (SQLException e) {
            System.err.println("Error getting user cart items: " + e.getMessage());
            e.printStackTrace();
            
            // Try one more time with a completely fresh approach
            try {
                System.out.println("DEBUG CartService: Retrying with fresh connection...");
                MyCartRepository retryRepository = new MyCartRepository();
                String retryCondition = "userId = " + userId + " AND isBuy = 0"; // Use 0 instead of false for compatibility
                ArrayList<MyCart> retryItems = retryRepository.SelectByCondition(retryCondition);
                System.out.println("DEBUG CartService: Retry found " + retryItems.size() + " items");
                return retryItems;
            } catch (SQLException retryException) {
                System.err.println("Retry also failed: " + retryException.getMessage());
                return new ArrayList<>();
            }
        }
    }

    /**
     * Clear user's cart
     */
    public boolean clearCart(int userId) {
        try {
            ArrayList<MyCart> cartItems = getUserCartItems(userId);
            for (MyCart item : cartItems) {
                cartRepository.Delete(item);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }/**
     * Test method to directly check database state
     */
    public void testDirectDatabaseQuery(int userId) {
        System.out.println("=== DIRECT DATABASE TEST FOR USER " + userId + " ===");
        try {
            MyCartRepository testRepo = new MyCartRepository();
            
            // Test various query formats
            String[] testConditions = {
                "userId = " + userId,
                "userId = " + userId + " AND isBuy = false",
                "userId = " + userId + " AND isBuy = 0",
                "userId = " + userId + " AND isBuy IS FALSE"
            };
            
            for (String condition : testConditions) {
                try {
                    System.out.println("Testing condition: " + condition);
                    ArrayList<MyCart> results = testRepo.SelectByCondition(condition);
                    System.out.println("  Result count: " + results.size());
                    for (MyCart item : results) {
                        System.out.println("  - Course: " + item.getCourseId() + ", User: " + item.getUserId() + ", isBuy: " + item.isBuy());
                    }
                } catch (SQLException e) {
                    System.out.println("  Error: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
        System.out.println("=== END DATABASE TEST ===");
    }
}
