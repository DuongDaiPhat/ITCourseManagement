package model.user;

public class Permissions {
	private int permissionID;
	private String permissionName;
	
	
	public Permissions() {
		super();
	}
	public Permissions(int permissionID, String permissionName) {
		super();
		this.permissionID = permissionID;
		this.permissionName = permissionName;
	}

	public int getPermissionID() {
		return permissionID;
	}
	public void setPermissionID(int permissionID) {
		this.permissionID = permissionID;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	@Override
	public String toString() {
		return "Permissions [permissionID=" + permissionID + ", permissionName=" + permissionName + "]";
	}
	
	public void print() {
		System.out.println(this.toString());
	}
}
