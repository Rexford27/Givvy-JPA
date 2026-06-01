package Tfast_Rmoney.Givvy.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuctionUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;
	private String userid;
	private List<GrantedAuthority> authorities;
	
	public AuctionUserDetails(String id) {
		userid = id;
		//holds the list of user details 
		authorities = new ArrayList<GrantedAuthority>();
		//we are adding user authority to the user details 
		authorities.add(new SimpleGrantedAuthority("USER"));
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return userid;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
