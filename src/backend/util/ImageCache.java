package backend.util;

import javafx.scene.image.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for caching images to prevent memory leaks and improve performance.
 * This class provides thread-safe image caching across the application.
 */
public class ImageCache {
    
    private static final Map<String, Image> cache = new ConcurrentHashMap<>();
    
    /**
     * Load an image from the given path, using cache if available.
     * 
     * @param imagePath The path to the image file
     * @return The cached or newly loaded Image, or null if loading fails
     */
    public static Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // Check cache first
        if (cache.containsKey(imagePath)) {
            return cache.get(imagePath);
        }
        
        try {
            Image image = new Image(imagePath);
            if (!image.isError()) {
                // Only cache successfully loaded images
                cache.put(imagePath, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Load an image from an InputStream, using cache if available.
     * Note: InputStream-based loading cannot be cached effectively due to stream consumption.
     * This method is provided for compatibility but does not use caching.
     * 
     * @param inputStream The InputStream to load from
     * @param cacheName A unique name to identify this image in cache
     * @return The loaded Image, or null if loading fails
     */
    public static Image loadImageFromStream(java.io.InputStream inputStream, String cacheName) {
        if (inputStream == null) {
            return null;
        }
        
        // For InputStreams, we can't cache effectively since streams are consumed
        // But we can check if we already have this cached by name
        if (cacheName != null && cache.containsKey(cacheName)) {
            return cache.get(cacheName);
        }
        
        try {
            Image image = new Image(inputStream);
            if (!image.isError() && cacheName != null) {
                cache.put(cacheName, image);
                return image;
            }
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image from stream: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Clear all cached images to free memory.
     */
    public static void clearCache() {
        cache.clear();
    }
    
    /**
     * Get the current cache size.
     * 
     * @return Number of cached images
     */
    public static int getCacheSize() {
        return cache.size();
    }
    
    /**
     * Remove a specific image from cache.
     * 
     * @param imagePath The path of the image to remove
     */
    public static void removeFromCache(String imagePath) {
        cache.remove(imagePath);
    }
}
