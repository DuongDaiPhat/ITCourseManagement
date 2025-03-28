package model.user;

public class RolePermissions {
	private int roleID;
	private int permissionID;
	
	public RolePermissions(int roleID, int permissionID) {
		super();
		this.roleID = roleID;
		this.permissionID = permissionID;
	}

	public RolePermissions() {
		super();
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public int getPermissionID() {
		return permissionID;
	}

	public void setPermissionID(int permissionID) {
		this.permissionID = permissionID;
	}

	@Override
	public String toString() {
		return "RolePermissions [roleID=" + roleID + ", permissionID=" + permissionID + "]";
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
}
