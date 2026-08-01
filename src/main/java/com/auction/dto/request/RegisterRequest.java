package com.auction.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	  @NotBlank(message = "First name is required")
	    @Size(max = 100, message = "First name cannot exceed 100 characters")
	    private String firstName;

	    @NotBlank(message = "Last name is required")
	    @Size(max = 100, message = "Last name cannot exceed 100 characters")
	    private String lastName;

	    @NotBlank(message = "Username is required")
	    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
	    private String username;

	    @NotBlank(message = "Email is required")
	    @Email(message = "Invalid email address")
	    private String email;

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
	    private String password;

	    @NotBlank(message = "Mobile number is required")
	    @Pattern(regexp = "^[6-9]\\d{9}$",
	             message = "Invalid mobile number")
	    private String mobileNo;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
}
