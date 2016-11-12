package com.youtube.data;


// This class already is present in Parse as a Class, so we dont need to conver
// this object to a parse objects. We pass these values directly to ParseUser
// and upload to the cloud.
public class AppUser {
	public String username;
	public String password;
	public String email;
}
