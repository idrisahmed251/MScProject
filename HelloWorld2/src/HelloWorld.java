import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.dom.*;

public class HelloWorld {
	static LinkedList<Activity> activities = new LinkedList<Activity>();
	
	public static void main(String[] args) throws IOException {
		File root = new File(new File(".").getCanonicalPath() + File.separator+"src"+File.separator);
		File[] files = root.listFiles();
		 for (File file : files)
			 if (file.isFile()){
				 System.out.println(file);
				 parse(readFileToString(file.getAbsolutePath()));
			 }
		 
		 for (Object o : activities)	System.out.println(o.toString());
	}
	
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) { //-1 == eof
			fileData.append(new String(String.valueOf(buf, 0, numRead)));
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	}
	
	public static void parse(String fileToParse) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fileToParse.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		
		final Block block = (Block) parser.createAST(null);
		block.accept(new ASTVisitor() {
			public boolean visit(SimpleName node) {
				System.out.println(node.toString());
				return true;
			}
/*
			public boolean visit(IfStatement node) {
				Activity activity = new Activity(StatementType.CONDITIONAL_STATEMENT, node.toString());
				activities.addLast(activity);
				return true;
			}
/*
			public boolean visit(SwitchStatement node) {
				Activity activity = new Activity(StatementType.CONDITIONAL_STATEMENT, node.toString());
				activities.addLast(activity);
				return true;
			}
			
			public boolean visit(DoStatement node) {
				Activity activity = new Activity(StatementType.REPEATED_EXECUTION, node.toString());
				activities.addLast(activity);
				return true;
			}
			
			public boolean visit(ForStatement node) {
				Activity activity = new Activity(StatementType.REPEATED_EXECUTION, node.toString());
				activities.addLast(activity);
				return true;
			}
			
			public boolean visit(WhileStatement node) {
				Activity activity = new Activity(StatementType.REPEATED_EXECUTION, node.toString());
				activities.addLast(activity);
				return true;
			}

/*
			public boolean visit(Block node) {
				System.out.println("------------------");
				System.out.print(node.getStartPosition() + ":\t");
				System.out.println(node.toString());
				return true;
			}
*/
/*
			public boolean visit(ExpressionStatement node) {
				System.out.println("---------------------");
				System.out.println(node.getExpression());
				return true;
			}
*/
		});
	}
	
	enum StatementType { CONDITIONAL_STATEMENT, REPEATED_EXECUTION }

	static class Activity {
		StatementType statementType;
		String block;
		
		public Activity(StatementType statementType, String node) { 
			this.statementType = statementType;
			this.block = node;
		}
		
		public String toString() {
			return statementType + " is found. Code is:\n " + block;
		}

	}
}
