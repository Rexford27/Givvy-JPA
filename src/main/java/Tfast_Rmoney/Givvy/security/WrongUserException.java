package Tfast_Rmoney.Givvy.security;

// This is a custom exception class.
// An exception is an error that we can throw when something goes wrong.
//
// This specific exception is used when a user tries to do something
// they are not allowed to do.
public class WrongUserException extends Exception {

    // This is the constructor.
    // It runs when we create a new WrongUserException.
    public WrongUserException() {

        // Calls the parent Exception class constructor
        // and gives it the error message.
        //
        // This message can later be shown, logged, or returned in a response.
        super("User is not allowed to perform this action");
    }
}