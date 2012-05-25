package analyzer;

public abstract class TokenType {
	
	// Elementos da linguagem
	public static enum Element {
		IF,
		THEN,
		ELSE,
		END,
		REPEAT,
		UNTIL,
		
		READ,
		WRITE,
		
		NUMBER,
		IDENTIFIER,
		COMMENT,
		
		EQUALS,
		PLUS,
		MINUS,
		MULTIPLICATION,
		DIVISION,
		ATTRIBUTION,
		LESS_THAN,
		
		LEFT_BRACKET,
		RIGHT_BRACKET,
		
		INSTRUCTION_END,
		ERROR,
		NONE
	};
		
}