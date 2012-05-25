package analyzer;

import java.util.ArrayList;

public class SyntaxTree {
	
	/*-----------------------------------------------------
	 *------------------ ATRIBUTOS ------------------------
	---------------------------------------------------- */

	private SyntaxType.Tree type;
	
	private SyntaxType.StatementKind statement = null;
	
	private SyntaxType.ExpressionKind expression = null;
	
	private Token token;
	
	private SyntaxType.ExpressionType expressionType = SyntaxType.ExpressionType.VOID;
	
	public SyntaxTree next = null;
	
	private ArrayList<SyntaxTree> children;
	
	/*-----------------------------------------------------
	 *------------------ CONSTRUTOR -----------------------
	---------------------------------------------------- */
	
	private SyntaxTree( SyntaxType.Tree type, Token token ){
		
		this.type = type;
		this.token = token;
		this.children = new ArrayList<SyntaxTree>();
	}

	public SyntaxTree( SyntaxType.Tree type, SyntaxType.StatementKind kind, Token token ){
		
		this( type, token );
		this.statement = kind;
	}
	
	public SyntaxTree( SyntaxType.Tree type, SyntaxType.ExpressionKind kind, Token token ){
		
		this( type, token );
		this.expression = kind;
	}
	
	/*-----------------------------------------------------
	 *------------------ METODOS --------------------------
	---------------------------------------------------- */
	
	public boolean appendChild( SyntaxTree node ){
		
		return this.children.add( node );
	}
	
	public String toString(){
		
		return this.toString( 0 );
	}
	
	public String toString( int indent ){
		
		String string = this.indent( indent );
		
		if( this.type == SyntaxType.Tree.STATEMENT )
			string += this.statement;
		else
			string += this.expression;
		
		string += " " + this.token.toString() + "\n";
		
		for( SyntaxTree child : this.children )
			string += child.toString( indent + 1 );
		
		if( !this.children.isEmpty() )
			string += this.indent( indent ) + "\n";
		
		if( this.next != null )
			string += this.next.toString( indent );
		
		return string;
	}
	
	private String indent( int num ){
		
		String str = "";
		
		for( int i = 0; i < num; i++ )
			str += "    ";
		
		return str + " |- ";
	}
	
	public boolean isStatement(){
		
		return this.type == SyntaxType.Tree.STATEMENT;
	}
	
	public SyntaxType.StatementKind getStatementKind(){
		
		return this.statement;
	}

	public SyntaxType.ExpressionKind getExpressionKind(){
		
		return this.expression;
	}
	
	public SyntaxType.ExpressionType getExpressionType(){
		
		return this.expressionType;
	}

	public void setExpressionType( SyntaxType.ExpressionType exp ){
		
		this.expressionType = exp;
	}
	
	public String getName(){
		
		return this.token.getString();
	}

	public int getLine(){
		
		return this.token.getLine();
	}
	
	public TokenType.Element getTokenType(){
		
		return this.token.getType();
	}
	
	public SyntaxTree getChild( int index ){
		
		if( index < this.children.size() )
			return this.children.get( index );
		
		return null;
	}

	public ArrayList<SyntaxTree> getChildren(){
		
		return this.children;
	}
	
}