package backend.service.user;

import backend.repository.user.MyWishListRepository;
import model.user.MyWishList;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class WishListService {
    private MyWishListRepository wishListRepository;

    public WishListService() {
        this.wishListRepository = MyWishListRepository.getInstance();
    }    /**
     * Add course to wishlist
     */
    public boolean addToWishList(int userId, int courseId) {
        try {
            // Check if course already exists in wishlist
            MyWishList existingItem = getWishListItem(userId, courseId);
            if (existingItem != null) {
                return false; // Already in wishlist
            }

            // Create new repository instance to avoid connection issues
            MyWishListRepository freshRepository = MyWishListRepository.getInstance();
            MyWishList newItem = new MyWishList(userId, courseId, LocalDateTime.now());
            int result = freshRepository.Insert(newItem);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error adding to wishlist: " + e.getMessage());
            return false;
        }
    }    /**
     * Remove course from wishlist
     */
    public boolean removeFromWishList(int userId, int courseId) {
        try {
            // Create new repository instance to avoid connection issues
            MyWishListRepository freshRepository = MyWishListRepository.getInstance();
            MyWishList itemToRemove = new MyWishList(userId, courseId, LocalDateTime.now());
            int result = freshRepository.Delete(itemToRemove);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error removing from wishlist: " + e.getMessage());
            return false;
        }
    }/**
     * Check if course is in wishlist
     */
    public boolean isInWishList(int userId, int courseId) {
        try {
            // Create new repository instance to avoid connection issues
            MyWishListRepository freshRepository = MyWishListRepository.getInstance();
            MyWishList item = freshRepository.SelectByCompositeID(userId, courseId);
            return item != null;
        } catch (SQLException e) {
            System.err.println("Error checking wishlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get specific wishlist item
     */
    public MyWishList getWishListItem(int userId, int courseId) {
        try {
            return wishListRepository.SelectByCompositeID(userId, courseId);
        } catch (SQLException e) {
            System.err.println("Error getting wishlist item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get wishlist count for user
     */
    public int getWishListCount(int userId) {
        try {
            ArrayList<MyWishList> wishListItems = wishListRepository.SelectByUserID(userId);
            return wishListItems.size();
        } catch (SQLException e) {
            System.err.println("Error getting wishlist count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all wishlist items for user
     */
    public ArrayList<MyWishList> getUserWishListItems(int userId) {
        try {
            return wishListRepository.SelectByUserID(userId);
        } catch (SQLException e) {
            System.err.println("Error getting user wishlist items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Clear user's wishlist
     */
    public boolean clearWishList(int userId) {
        try {
            ArrayList<MyWishList> wishListItems = getUserWishListItems(userId);
            for (MyWishList item : wishListItems) {
                wishListRepository.Delete(item);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing wishlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Toggle wishlist status (add if not exist, remove if exists)
     */
    public boolean toggleWishList(int userId, int courseId) {
        if (isInWishList(userId, courseId)) {
            return removeFromWishList(userId, courseId);
        } else {
            return addToWishList(userId, courseId);
        }
    }
}
