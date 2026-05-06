package Tfast_Rmoney.Givvy.services;

import org.springframework.stereotype.Service;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

@Service
public class PasswordService {
	
	//salt vlaue
	static private final String secret="Givvy";
	
	public String hashPassword(String password) {
		BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B, 12);
//converts regular strings into hash numbers
		Hash hash = Password.hash(password)
		                    .addPepper(secret)
		                    .with(bcrypt);

		return hash.getResult();
	}
	
	public boolean verifyHash(String password,String hash) {
		BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.B, 12);
//check is the password.hash is the same as the hash in the databse 
		return Password.check(password, hash)
		               .addPepper(secret)
		               .with(bcrypt);
	}
}