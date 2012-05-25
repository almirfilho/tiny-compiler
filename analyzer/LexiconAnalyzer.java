package analyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import analyzer.Token;
import analyzer.TokenType;

public class LexiconAnalyzer {
	
	/*-----------------------------------------------------
	 *------------------ ATTRIBUTOS -----------------------
	---------------------------------------------------- */
	
	private static enum State {
		BEGIN, 
		READING_COMMENT, 
		READING_NUMBER, 
		READING_IDENTIFIER, 
		READING_ATTRIBUTION, 
		END
	};
	
	private String code;
	
	private String buffer;
	
	private int currentLine = 1;
	
	private int currentChar = 0;
	
	private State currentState = State.BEGIN;

	private boolean save = true;
	
	private boolean endFile = false;
	
	private ArrayList<Token> tokens;
	
	private static final int BUFFSIZE = 40;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public LexiconAnalyzer( String streamPath ){
		
		this.buffer = new String();
		this.tokens = new ArrayList<Token>();
		
		try {
			
			char[] buff = new char[ 1024*10 ]; // 10 KBs
			FileReader file = new FileReader( streamPath );
			file.read( buff );
			this.code = new String( buff ).trim();
			
		} catch( FileNotFoundException notfound ){
			
			System.out.println( "Arquivo nao encontrado." );
			System.exit( 0 );
			
		} catch( IOException io ) {
			
			System.out.println( "Erro na leitura." );
			System.exit( 0 );
		}
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	/*
	 * Retorna o proximo caractere do codigo
	 */
	private char nextChar(){
		
		char ch = this.code.charAt( this.currentChar++ );
		
		if( this.save ){
			
			if( this.buffer.length() >= BUFFSIZE ){
				System.out.println( "Tamanho maximo de buffer atingido. Abortando operacao." );
				System.exit( 0 );
			} else
				this.buffer = this.buffer + ch;
		}
		
		return ch;
	}
	
	/*
	 * Volta em 1 a posicao de leitura do codigo
	 */
	private void prevChar(){
		
		this.currentChar--;
		this.buffer = this.buffer.substring( 0, this.buffer.length()-1 );
	}
	
	/*
	 * Testa se ha mais caracteres do codigo a serem lidos
	 */
	private boolean hasMoreChars(){
		
		this.endFile = !( this.currentChar < this.code.length() );
		return !this.endFile;
	}
	
	/*
	 * Esvazia o buffer
	 */
	private void clearBuffer(){
		
		this.buffer = "";
	}
	
	/*
	 * retorna o tipo de token dependendo do estado atual
	 */
	private TokenType.Element stateToTokenType( State state ){
		
		TokenType.Element type;
		
		switch( state ){
		
			case READING_ATTRIBUTION:
				type = TokenType.Element.ATTRIBUTION;
				break;
				
			case READING_IDENTIFIER:
				type = TokenType.Element.IDENTIFIER;
				break;
				
			case READING_NUMBER:
				type = TokenType.Element.NUMBER;
				break;
				
			default:
				type = TokenType.Element.NONE;
		}
		
		return type;
	}
	
	/*
	 * retorna o tipo de token para caracteres especiais
	 */
	private TokenType.Element charToTokenType( char ch ){
		
		TokenType.Element type;
		
		switch( ch ){
		
			case '=':
				type = TokenType.Element.EQUALS;
				break;
				
			case '<':
				type = TokenType.Element.LESS_THAN;
				break;
				
			case '+':
				type = TokenType.Element.PLUS;
				break;
				
			case '-':
				type = TokenType.Element.MINUS;
				break;
				
			case '*':
				type = TokenType.Element.MULTIPLICATION;
				break;
				
			case '/':
				type = TokenType.Element.DIVISION;
				break;
				
			case '(':
				type = TokenType.Element.LEFT_BRACKET;
				break;
				
			case ')':
				type = TokenType.Element.RIGHT_BRACKET;
				break;
				
			case ';':
				type = TokenType.Element.INSTRUCTION_END;
				break;
				
			default:
				type = TokenType.Element.NONE;
		}
		
		return type;
	}
	
	/*
	 * retorna o proximo token do codigo
	 */
	private Token getNextToken(){
		
		TokenType.Element type = TokenType.Element.NONE;
		
		while( this.currentState != State.END && !this.endFile ){
			
			if( this.hasMoreChars() ){
				
				char ch = this.nextChar();
				
				switch( this.currentState ){
				
					case BEGIN:
						// no estado inicial pode ler qualquer simbolo
						if( Character.isDigit( ch ) ){
							
							this.currentState = State.READING_NUMBER;
							
						} else if( Character.isLetter( ch ) ){
							
							this.currentState = State.READING_IDENTIFIER;
							
						} else if( Character.isWhitespace( ch ) ){
							
							if( ch == '\n' ) //System.out.println("linha nova");
								this.currentLine++;
							
							this.clearBuffer();
							
						} else if( ch == ':' ){
							
							this.currentState = State.READING_ATTRIBUTION;
							
						} else if( ch == '{' ){
							
							this.currentState = State.READING_COMMENT;
							this.save = false;
							this.clearBuffer();
							
						} else {
							
							this.currentState = State.END;
							type = this.charToTokenType( ch );
						}
						
						break;
						
					case READING_COMMENT:
						
						if( ch == '}' ){
							
							this.currentState = State.END;
							this.save = true;
							type = TokenType.Element.COMMENT;
						
						} else if( ch == '\n' )
							this.currentLine++;
							
						break;
						
					case READING_IDENTIFIER:
						
						if( !Character.isLetter( ch ) ){
							
							if( Character.isDigit( ch ) ){
								
								this.currentState = State.END;
								type = TokenType.Element.ERROR;
								break;
							}
							
							this.prevChar();
							this.currentState = State.END;
							type = TokenType.Element.IDENTIFIER;
						}
						
						break;
						
					case READING_NUMBER:
						
						if( !Character.isDigit( ch ) ){
							
							if( Character.isLetter( ch ) ){
								
								this.currentState = State.END;
								type = TokenType.Element.ERROR;
								break;
							}
							
							this.prevChar();
							this.currentState = State.END;
							type = TokenType.Element.NUMBER;
						}

						break;
						
					case READING_ATTRIBUTION:
						
						if( ch == '=' ){
							
							type = TokenType.Element.ATTRIBUTION;
						
						} else {
							
							this.prevChar();
							type = TokenType.Element.ERROR;
						}
						
						this.currentState = State.END;
						break;
				}	
			}
		}
		
		Token token = null;
		
		if( this.currentState == State.END )
			token = new Token( type, this.buffer, this.currentLine );
		
		else if( !this.buffer.isEmpty() )
			token = new Token( this.stateToTokenType( this.currentState ), this.buffer, this.currentLine );
		
		this.currentState = State.BEGIN;
		this.clearBuffer();
		return token;
	}
	
	/*
	 * Comeca a analize
	 */
	public ArrayList<Token> analyze(){
		
		Token token = this.getNextToken();
		
		while( token != null ){
			
			if( token.getType() != TokenType.Element.COMMENT )
				this.tokens.add( token );
			
			token = this.getNextToken();
		}
		
		this.tokens.add( new Token( TokenType.Element.NONE, "" ) );
		return this.tokens;
	}

	/*
	 * Imprime na tela todos os tokens
	 */
	public String toString(){
		
		String string = "Total de Tokens: " + this.tokens.size() + "\n\n";
		
		for( Token thisToken : this.tokens )
			string += thisToken.toString() + "\n";
		
		return string;
	}
	
}