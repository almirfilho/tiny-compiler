package analyzer;

import analyzer.TokenType;;

public class Token {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS ------------------------
	---------------------------------------------------- */

	private TokenType.Element type;
	
	private String string;
	
	private int line = 0;
	
	/*-----------------------------------------------------
	 *------------------ CONTRUTOR ------------------------
	---------------------------------------------------- */
	
	public Token( TokenType.Element type, String string ){
		
		this.type = type;
		this.string = string;
		
		// verificando se eh palavra reservada
		if( type == TokenType.Element.IDENTIFIER )
			this.verifyReservedWords();
		
		// se for um token de erro, aborta o analizador
		else if( type == TokenType.Element.ERROR ){
			System.out.println( "Erro lexico no codigo: \"" + string + "\"" );
			System.exit( 0 );
		}
	}
	
	public Token( TokenType.Element type, String string, int line ){
		
		this( type, string );
		this.line = line;
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	private void verifyReservedWords(){
		
		if( this.string.equals( "if" ) )
			this.type = TokenType.Element.IF;
		
		else if( this.string.equals( "then" ) )
			this.type = TokenType.Element.THEN;
			
		else if( this.string.equals( "else" ) )
			this.type = TokenType.Element.ELSE;
		
		else if( this.string.equals( "end" ) )
			this.type = TokenType.Element.END;
		
		else if( this.string.equals( "read" ) )
			this.type = TokenType.Element.READ;
		
		else if( this.string.equals( "write" ) )
			this.type = TokenType.Element.WRITE;
		
		else if( this.string.equals( "repeat" ) )
			this.type = TokenType.Element.REPEAT;
		
		else if( this.string.equals( "until" ) )
			this.type = TokenType.Element.UNTIL;
	}
	
	public String toString(){
		
		return "[" + this.type + ": " + this.string + "]";
	}
	
	public TokenType.Element getType(){
		
		return this.type;
	}
	
	public String getString(){
		
		return this.string;
	}
	
	public int getLine(){
		
		return this.line;
	}
	
}