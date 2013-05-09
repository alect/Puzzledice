package utils;

public class StringPair {

	private String _string1, _string2;
	
	public String getString1() { 
		return _string1; 
	}
	
	public String getString2() { 
		return _string2; 
	}
	
	public StringPair(String string1, String string2) {
		_string1 = string1; 
		_string2 = string2; 
	}
	
	@Override 
	public String toString() { 
		return String.format("(%s, %s)", _string1, _string2);
	}
	
	
}
