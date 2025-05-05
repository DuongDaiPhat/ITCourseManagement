package model.course;

public class Lesson {
	private int id;
	  private String title;
	    private String author;
	    private double rating;
	    private int totalRatings;
	    private double price;
	    private String imagePath;
	    private Category category;

	    public Lesson(int id, String title, String author, double rating, int totalRatings, double price, String imagePath, Category category) {
	        this.id = id;
	        this.title = title;
	        this.author = author;
	        this.rating = rating;
	        this.totalRatings = totalRatings;
	        this.price = price;
	        this.imagePath = imagePath;
	        this.category = category;
	    }

	    public Lesson(String title, String author, double rating, int totalRatings, double price, String imagePath, Category category) {
	        this(0, title, author, rating, totalRatings, price, imagePath, category);
	    }

	    public int getId() {
	        return id;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public String getAuthor() {
	        return author;
	    }

	    public double getRating() {
	        return rating;
	    }

	    public int getTotalRatings() {
	        return totalRatings;
	    }

	    public double getPrice() {
	        return price;
	    }

	    public String getImagePath() {
	        return imagePath;
	    }

	    public Category getCategory() {
	        return category;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }
}
