package analyzer;

public abstract class SyntaxType {

	public static enum Tree {
		STATEMENT,
		EXPRESSION
	}
	
	public static enum StatementKind {
		IF,
		REPEAT,
		ATTRIBUTION,
		READ,
		WRITE
	}
	
	public static enum ExpressionKind {
		OPERATION,
		CONSTANT,
		IDENTIFIER
	}
	
	public static enum ExpressionType {
		VOID,
		INTEGER,
		BOOLEAN
	}
	
	public static enum Error {
		SYNTAX,
		ENDFILE
	}
	
}
