package Tfast_Rmoney.Givvy.security;

public class WrongUserException extends Exception {
	public WrongUserException() {
		super("User is not allowed to perform this action");
	}
}
