package backend.service.state;

import backend.service.user.CartService;
import backend.service.user.WishListService;
import model.user.Session;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Singleton service for managing cart and wishlist state synchronization
 * across all student pages (StudentMainPage, StudentExplorePage, StudentCourseDetailPage)
 */
public class StudentStateManager {
    private static StudentStateManager instance;
    private CartService cartService;
    private WishListService wishListService;
    
    // State tracking
    private Set<Integer> cartCourseIds = new HashSet<>();
    private Set<Integer> wishlistCourseIds = new HashSet<>();
    private int cartCount = 0;
    
    // Listeners for state changes
    private Set<Consumer<Integer>> cartCountListeners = new HashSet<>();
    private Set<Consumer<Set<Integer>>> cartStateListeners = new HashSet<>();
    private Set<Consumer<Set<Integer>>> wishlistStateListeners = new HashSet<>();
    
    private StudentStateManager() {
        cartService = new CartService();
        wishListService = new WishListService();
    }
      public static StudentStateManager getInstance() {
        if (instance == null) {
            instance = new StudentStateManager();
            System.out.println("DEBUG: Created new StudentStateManager instance");
        } else {
            System.out.println("DEBUG: Returning existing StudentStateManager instance");
        }
        return instance;
    }    /**
     * Force a complete state refresh from database
     * Use this when you need to ensure the most up-to-date state
     */
    public void forceStateRefresh() {
        System.out.println("DEBUG: forceStateRefresh() called");
        initializeState();
    }
    
    /**
     * Check if state is properly initialized
     */
    public boolean isStateInitialized() {
        boolean initialized = Session.getCurrentUser() != null && (cartCourseIds != null || wishlistCourseIds != null);
        System.out.println("DEBUG: isStateInitialized() = " + initialized);
        return initialized;
    }
    
    /**
     * Initialize state from database for current user
     */
    public void initializeState() {
        try {
            if (Session.getCurrentUser() != null) {
                int userId = Session.getCurrentUser().getUserID();
                
                System.out.println("DEBUG: StudentStateManager.initializeState() called for user ID: " + userId);
                System.out.println("DEBUG: Current user details - ID: " + userId + ", Name: " + Session.getCurrentUser().getUserFirstName());
                
                // Clear existing state first to ensure clean initialization
                cartCourseIds.clear();
                wishlistCourseIds.clear();
                cartCount = 0;
                
                // Load cart items
                System.out.println("DEBUG: Loading cart items...");
                cartCourseIds = getCartCourseIdsFromService(userId);
                cartCount = cartCourseIds.size();
                
                // Load wishlist items
                System.out.println("DEBUG: Loading wishlist items...");
                wishlistCourseIds = getWishlistCourseIdsFromService(userId);
                
                System.out.println("StudentStateManager initialized - Cart: " + cartCount + " items, Wishlist: " + wishlistCourseIds.size() + " items");
                System.out.println("DEBUG: Cart course IDs: " + cartCourseIds);
                System.out.println("DEBUG: Wishlist course IDs: " + wishlistCourseIds);
                
                // Notify all listeners
                notifyCartCountListeners();
                notifyCartStateListeners();
                notifyWishlistStateListeners();
                
                System.out.println("DEBUG: State initialization complete, listeners notified");
                
            } else {
                System.out.println("DEBUG: Cannot initialize state - no current user in session");
                // Clear state if no user
                cartCourseIds.clear();
                wishlistCourseIds.clear();
                cartCount = 0;
            }
        } catch (Exception e) {
            System.err.println("Error initializing student state: " + e.getMessage());
            e.printStackTrace();
        }
    }    /**
     * Helper method to get cart course IDs from service
     */
    private Set<Integer> getCartCourseIdsFromService(int userId) {
        Set<Integer> courseIds = new HashSet<>();
        try {
            System.out.println("DEBUG: Getting cart items for user ID: " + userId);
            
            // First, test direct database access
            cartService.testDirectDatabaseQuery(userId);
            
            var cartItems = cartService.getUserCartItems(userId);
            System.out.println("DEBUG: Found " + cartItems.size() + " cart items in database");
            
            for (var item : cartItems) {
                System.out.println("DEBUG: Cart item - Course ID: " + item.getCourseId() + ", User ID: " + item.getUserId() + ", isBuy: " + item.isBuy());
                courseIds.add(item.getCourseId());
            }
            
            System.out.println("DEBUG: Final cart course IDs set: " + courseIds);
        } catch (Exception e) {
            System.err.println("Error getting cart course IDs: " + e.getMessage());
            e.printStackTrace();
        }
        return courseIds;
    }
      /**
     * Helper method to get wishlist course IDs from service
     */
    private Set<Integer> getWishlistCourseIdsFromService(int userId) {
        Set<Integer> courseIds = new HashSet<>();
        try {
            var wishlistItems = wishListService.getUserWishListItems(userId);
            for (var item : wishlistItems) {
                courseIds.add(item.getCourseID());
            }
        } catch (Exception e) {
            System.err.println("Error getting wishlist course IDs: " + e.getMessage());
        }
        return courseIds;
    }
    
    /**
     * Add course to cart
     */
    public boolean addToCart(int courseId) {
        try {
            if (Session.getCurrentUser() != null) {
                int userId = Session.getCurrentUser().getUserID();
                boolean success = cartService.addToCart(userId, courseId);
                
                if (success) {
                    cartCourseIds.add(courseId);
                    cartCount++;
                    
                    // Notify all listeners
                    notifyCartCountListeners();
                    notifyCartStateListeners();
                    
                    System.out.println("Course " + courseId + " added to cart. New count: " + cartCount);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding course to cart: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Remove course from cart
     */
    public boolean removeFromCart(int courseId) {
        try {
            if (Session.getCurrentUser() != null) {
                int userId = Session.getCurrentUser().getUserID();
                boolean success = cartService.removeFromCart(userId, courseId);
                
                if (success) {
                    cartCourseIds.remove(courseId);
                    cartCount--;
                    
                    // Notify all listeners
                    notifyCartCountListeners();
                    notifyCartStateListeners();
                    
                    System.out.println("Course " + courseId + " removed from cart. New count: " + cartCount);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error removing course from cart: " + e.getMessage());
        }
        return false;
    }
      /**
     * Add course to wishlist
     */
    public boolean addToWishlist(int courseId) {
        try {
            if (Session.getCurrentUser() != null) {
                int userId = Session.getCurrentUser().getUserID();
                boolean success = wishListService.addToWishList(userId, courseId);
                
                if (success) {
                    wishlistCourseIds.add(courseId);
                    
                    // Notify all listeners
                    notifyWishlistStateListeners();
                    
                    System.out.println("Course " + courseId + " added to wishlist");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding course to wishlist: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Remove course from wishlist
     */
    public boolean removeFromWishlist(int courseId) {
        try {
            if (Session.getCurrentUser() != null) {
                int userId = Session.getCurrentUser().getUserID();
                boolean success = wishListService.removeFromWishList(userId, courseId);
                
                if (success) {
                    wishlistCourseIds.remove(courseId);
                    
                    // Notify all listeners
                    notifyWishlistStateListeners();
                    
                    System.out.println("Course " + courseId + " removed from wishlist");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error removing course from wishlist: " + e.getMessage());
        }
        return false;
    }
      // State getters
    public boolean isInCart(int courseId) {
        boolean result = cartCourseIds.contains(courseId);
        System.out.println("DEBUG: isInCart(" + courseId + ") = " + result + " | Current cartCourseIds: " + cartCourseIds);
        return result;
    }
    
    public boolean isInWishlist(int courseId) {
        return wishlistCourseIds.contains(courseId);
    }
    
    public int getCartCount() {
        return cartCount;
    }
    
    public Set<Integer> getCartCourseIds() {
        return new HashSet<>(cartCourseIds);
    }
    
    public Set<Integer> getWishlistCourseIds() {
        return new HashSet<>(wishlistCourseIds);
    }
    
    // Listener management
    public void addCartCountListener(Consumer<Integer> listener) {
        cartCountListeners.add(listener);
    }
    
    public void removeCartCountListener(Consumer<Integer> listener) {
        cartCountListeners.remove(listener);
    }
    
    public void addCartStateListener(Consumer<Set<Integer>> listener) {
        cartStateListeners.add(listener);
    }
    
    public void removeCartStateListener(Consumer<Set<Integer>> listener) {
        cartStateListeners.remove(listener);
    }
    
    public void addWishlistStateListener(Consumer<Set<Integer>> listener) {
        wishlistStateListeners.add(listener);
    }
    
    public void removeWishlistStateListener(Consumer<Set<Integer>> listener) {
        wishlistStateListeners.remove(listener);
    }
    
    // Notification methods
    private void notifyCartCountListeners() {
        for (Consumer<Integer> listener : cartCountListeners) {
            try {
                listener.accept(cartCount);
            } catch (Exception e) {
                System.err.println("Error notifying cart count listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyCartStateListeners() {
        Set<Integer> currentCartIds = new HashSet<>(cartCourseIds);
        for (Consumer<Set<Integer>> listener : cartStateListeners) {
            try {
                listener.accept(currentCartIds);
            } catch (Exception e) {
                System.err.println("Error notifying cart state listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyWishlistStateListeners() {
        Set<Integer> currentWishlistIds = new HashSet<>(wishlistCourseIds);
        for (Consumer<Set<Integer>> listener : wishlistStateListeners) {
            try {
                listener.accept(currentWishlistIds);
            } catch (Exception e) {
                System.err.println("Error notifying wishlist state listener: " + e.getMessage());
            }
        }
    }
      /**
     * Clear all state (for logout)
     */
    public void clearState() {
        cartCourseIds.clear();
        wishlistCourseIds.clear();
        cartCount = 0;
        
        // Notify all listeners
        notifyCartCountListeners();
        notifyCartStateListeners();
        notifyWishlistStateListeners();
        
        System.out.println("Student state cleared");
    }
    
    /**
     * Debug method to print current state
     */
    public void printDebugState() {
        System.out.println("=== StudentStateManager Debug State ===");
        System.out.println("Cart course IDs: " + cartCourseIds);
        System.out.println("Wishlist course IDs: " + wishlistCourseIds);
        System.out.println("Cart count: " + cartCount);
        System.out.println("Cart state listeners: " + cartStateListeners.size());
        System.out.println("Wishlist state listeners: " + wishlistStateListeners.size());
        System.out.println("======================================");
    }
}
