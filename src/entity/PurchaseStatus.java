package entity;

public enum PurchaseStatus {
	NOT_BOUGHT(0),
    BOUGHT(1);
    
    private final int value;
    
    PurchaseStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
