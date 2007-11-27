package workcraft;

public class DuplicateIdException extends Exception {
	public DuplicateIdException(Integer id) {
		super(id.toString());		
	}
}
