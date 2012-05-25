import generator.CodeGenerator;
import analyzer.LexiconAnalyzer;
import analyzer.SyntacticAnalyzer;
import analyzer.SyntaxTree;
import generator.symbol.TableBuilder;

public class TestDrive {

	public static void main( String args[] ){
		
		LexiconAnalyzer lexicon = new LexiconAnalyzer( "codigo.tny" );
		SyntacticAnalyzer syntactic = new SyntacticAnalyzer( lexicon.analyze() );
		SyntaxTree tree = syntactic.analyze();
		TableBuilder builder = new TableBuilder( tree );
		CodeGenerator generator = new CodeGenerator( tree, builder.build() );
		
		generator.generate( "codigo.tm" );
	}
}