package analyzer;

import java.util.ArrayList;
import analyzer.SyntaxTree;

public class SyntacticAnalyzer {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS ------------------------
	---------------------------------------------------- */
	
	private Token currentToken;
	
	private ArrayList<Token> tokens;
	
	private SyntaxTree tree;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	public SyntacticAnalyzer( ArrayList<Token> tokens ){
		
		this.tokens = tokens;
		this.getNextToken();
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	/*
	 * producao de uma sequencia de declaracoes
	 */
	private SyntaxTree statementSequence(){
		
		SyntaxTree node = this.statement();
		SyntaxTree node2 = node;
		TokenType.Element type = this.currentToken.getType();
		
		while( type != TokenType.Element.NONE && type != TokenType.Element.END && type != TokenType.Element.ELSE && type != TokenType.Element.UNTIL ){
			
			SyntaxTree node3 = null;
			this.match( TokenType.Element.INSTRUCTION_END );
			node3 = this.statement();
			
			if( node3 != null ){
				
				if( node == null )
					node = node2 = node3;
				else {
					node2.next = node3;
					node2 = node3;
				}
			}
			
			type = this.currentToken.getType();
		}
		
		return node;
	}
	
	/*
	 * producao de uma declaracao
	 */
	private SyntaxTree statement(){
		
		SyntaxTree node = null;
		
		switch( this.currentToken.getType() ){
		
			case IF:
				node = this.statementIf();
				break;
				
			case REPEAT:
				node = this.statementRepeat();
				break;
				
			case IDENTIFIER:
				node = this.statementAttribution();
				break;
				
			case READ:
				node = this.statementRead();
				break;
				
			case WRITE:
				node = this.statementWrite();
				break;
				
			default:
				this.raiseError( SyntaxType.Error.SYNTAX, this.currentToken );
				this.getNextToken();
		}
		
		return node;
	}
	
	/*
	 * producao de uma declaracao IF
	 */
	private SyntaxTree statementIf(){
		
		SyntaxTree node = new SyntaxTree( SyntaxType.Tree.STATEMENT, SyntaxType.StatementKind.IF, this.currentToken );
		
		this.match( TokenType.Element.IF );
		node.appendChild( this.expression() );
		
		this.match( TokenType.Element.THEN );
		node.appendChild( this.statementSequence() );
		
		if( this.currentToken.getType() == TokenType.Element.ELSE ){
			
			this.match( TokenType.Element.ELSE );
			node.appendChild( this.statementSequence() );
		}
		
		this.match( TokenType.Element.END );
		return node;
	}
	
	/*
	 * producao de uma declaracao REPEAT
	 */
	private SyntaxTree statementRepeat(){
		
		SyntaxTree node = new SyntaxTree( SyntaxType.Tree.STATEMENT, SyntaxType.StatementKind.REPEAT, this.currentToken );
		
		this.match( TokenType.Element.REPEAT );
		node.appendChild( this.statementSequence() );
		
		this.match( TokenType.Element.UNTIL );
		node.appendChild( this.expression() );
		
		return node;
	}
	
	/*
	 * producao de uma declaracao de atribuicao
	 */
	private SyntaxTree statementAttribution(){
		
		SyntaxTree node = new SyntaxTree( SyntaxType.Tree.STATEMENT, SyntaxType.StatementKind.ATTRIBUTION, this.currentToken );
		
		this.match( TokenType.Element.IDENTIFIER );		
		this.match( TokenType.Element.ATTRIBUTION );
		node.appendChild( this.expression() );
		
		return node;
	}
	
	/*
	 * producao de uma declaracao READ
	 */
	private SyntaxTree statementRead(){
		
		this.match( TokenType.Element.READ );
		SyntaxTree node = new SyntaxTree( SyntaxType.Tree.STATEMENT, SyntaxType.StatementKind.READ, this.currentToken );
		this.match( TokenType.Element.IDENTIFIER );
		
		return node;
	}
	
	/*
	 * producao de uma declaracao WRITE
	 */
	private SyntaxTree statementWrite(){
		
		SyntaxTree node = new SyntaxTree( SyntaxType.Tree.STATEMENT, SyntaxType.StatementKind.WRITE, this.currentToken );
		
		this.match( TokenType.Element.WRITE );
		node.appendChild( this.expression() );
		
		return node;
	}
	
	/*
	 * producao de uma expressao
	 */
	private SyntaxTree expression(){
		
		SyntaxTree node = this.simpleExpression();
		TokenType.Element type = this.currentToken.getType();
		
		while( type == TokenType.Element.LESS_THAN || type == TokenType.Element.EQUALS ){
			
			SyntaxTree child = new SyntaxTree( SyntaxType.Tree.EXPRESSION, SyntaxType.ExpressionKind.OPERATION, this.currentToken );
			child.appendChild( node );
			node = child;
			this.match( type );
			node.appendChild( this.simpleExpression() );
			
			type = this.currentToken.getType();
		}
		
		return node;
	}
	
	/*
	 * producao de uma expressao simples
	 */
	private SyntaxTree simpleExpression(){
		
		SyntaxTree node = this.term();
		TokenType.Element type = this.currentToken.getType();
		
		while( type == TokenType.Element.PLUS || type == TokenType.Element.MINUS ){
			
			SyntaxTree child = new SyntaxTree( SyntaxType.Tree.EXPRESSION, SyntaxType.ExpressionKind.OPERATION, this.currentToken );
			child.appendChild( node );
			node = child;
			this.match( type );
			node.appendChild( this.term() );
			
			type = this.currentToken.getType();
		}
		
		return node;
	}
	
	/*
	 * producao de um termo para expressao
	 */
	private SyntaxTree term(){
		
		SyntaxTree node = this.factor();
		TokenType.Element type = this.currentToken.getType();
		
		while( type == TokenType.Element.MULTIPLICATION || type == TokenType.Element.DIVISION ){
			
			SyntaxTree child = new SyntaxTree( SyntaxType.Tree.EXPRESSION, SyntaxType.ExpressionKind.OPERATION, this.currentToken );
			child.appendChild( node );
			node = child;
			this.match( type );
			child.appendChild( this.factor() );
			
			type = this.currentToken.getType();
		}
		
		return node;
	}
	
	/*
	 * producao de um fator para termo
	 */
	private SyntaxTree factor(){
		
		SyntaxTree node = null;
		TokenType.Element type = this.currentToken.getType();
		
		switch( type ){
		
			case NUMBER:
				node = new SyntaxTree( SyntaxType.Tree.EXPRESSION, SyntaxType.ExpressionKind.CONSTANT, this.currentToken );
				this.match( type );
				break;
				
			case IDENTIFIER:
				node = new SyntaxTree( SyntaxType.Tree.EXPRESSION, SyntaxType.ExpressionKind.IDENTIFIER, this.currentToken );
				this.match( type );
				break;
				
			case LEFT_BRACKET:
				this.match( type );
				node = this.expression();
				this.match( TokenType.Element.RIGHT_BRACKET );
				break;
				
			default:
				this.raiseError( SyntaxType.Error.SYNTAX, this.currentToken );
				this.getNextToken();
				break;
		}
		
		return node;
	}
	
	/*
	 * efetua o casamento do token esperado, caso contrario chama raiseError
	 */
	private void match( TokenType.Element expected ){
		
		if( this.currentToken.getType() == expected )
			this.getNextToken();
		else
			this.raiseError( SyntaxType.Error.SYNTAX, this.currentToken );
	}

	/*
	 * imprime na tela o erro detectado
	 */
	private void raiseError( SyntaxType.Error error, Token token ){
		
		switch( error ){
		
			case SYNTAX:
				System.out.println( "Erro de sintaxe (linha" + token.getLine() + "): Token inesperado -> " + token.toString() );
				break;
			
			case ENDFILE:
				break;
		}
	}
	
	/*
	 * retorna o proximo token a ser lido e o remove do array list,
	 * caso nao haja mais tokens, retorna null
	 */
	private void getNextToken(){
		
		if( !this.tokens.isEmpty() )
			this.currentToken = this.tokens.remove(0);
		else
			this.currentToken = null;
		
	}
	
	/*
	 * comeca a analise
	 */
	public SyntaxTree analyze(){
		
		this.tree = statementSequence();
		return this.tree;
	}
	
	public String toString(){
		
		return this.tree.toString(0);
	}

}
