package org.zza.codegenerator.threeaddresscode;

import java.util.HashMap;
import java.util.Map;

import org.zza.parser.semanticstack.nodes.*;
import org.zza.visitor.NodeVisitor;


public class TerribleImplementationToGetTempUsageVisitor extends NodeVisitor {
    
    private int tempCount = 0;
    private int lineNumber =0;
    private int labelCount;
    private HashMap<String, Map<String, Integer>> usage;
    private int varDec =0;
    private String scope;
    
    public TerribleImplementationToGetTempUsageVisitor() {
        usage = new HashMap<String, Map<String, Integer>>();
        addToUsage("program");
    }
    
    private void addToUsage(String name) {
        System.out.println("adding "+name+" to usage");
        Map<String, Integer> newMap = new HashMap<String, Integer>();
        newMap.put("locals", 0);
        newMap.put("temps", 0);
        usage.put(name, newMap);
    }

    private void addLocalsInUsage(String function, int count) {
        usage.get(function).put("locals", usage.get(function).get("locals") + count);
    }
    
    private void addTempsInUsage(String function, int count) {
        usage.get(function).put("temps", usage.get(function).get("temps") + count);        
    }
    
    @Override
    public String visit(ProgramNode node) {
        scope = "program";
        String parameters = node.getHeader().accept(this);
        String[] splitParam = parameters.split(",");
        addLocalsInUsage(scope, splitParam.length);
        
        String body = node.getbody().accept(this);
        addTempsInUsage(scope, tempCount);
        tempCount = 0;
        System.out.println("*end main program. Used: "+tempCount );
        String declarations = node.getDeclarations().accept(this);
        System.out.println("*program used "+(splitParam.length + varDec));
        System.out.println(lineNumber  + ":   HALT  0,0,0");
        System.out.println("\n\nusage map: \n"+usage);
        return "program: \n"+declarations +"\n" +body;
    }

    @Override
    public String visit(VariableDeclarationNode node) {
        varDec ++;
        return "vardec";
    }
    
    @Override
    public String visit(FunctionNode node) {
        String oldScope = scope;
        String header = node.getHeader().accept(this);
        
        System.out.println("*Entry function: " +header + " tempcount: "+tempCount + " " +scope);
        String body = node.getBody().accept(this); 
        addTempsInUsage(scope, tempCount);
        tempCount = 0;
        System.out.println("*Finish function: "+header + " tempcount: "+tempCount);
        scope = oldScope;
        return "function : "+header + " " + body;
    }
    
    @Override
    public String visit(ParameterNode node) {
        
        return node.getLeftHand().accept(this);
//        return handleTwoFieldNode(node, "param");
    }
    
    @Override
    public String visit(AssignmentExpressionNode node) {
        String left = node.acceptVisitorLeftHand(this);
        String right = node.acceptVisitorRightHand(this);
        return "assignment";
    }
    
    @Override
    public String visit(CompoundStatementNode node) {
        String toReturn = "(";
        for (SemanticNode n : node.getStatements()) {
            toReturn += n.accept(this) + ",";
        }
        return trimEnd(toReturn)+")";
    }
    
    @Override
    public String visit(DivisionExpressionNode node) {
        return handleTwoFieldNode(node, "/");
    }
    
    @Override
    public String visit(IdentifierNode node) {
        return node.getValue();
    }
    
    @Override
    public String visit(IntegerNode node) {
        return node.getValue();
    }
    
    @Override
    public String visit(MinusExpressionNode node) {
        return handleTwoFieldNode(node, "-");
    }
    
    
    @Override
    public String visit(MultiplicationExpressionNode node) {
        return handleTwoFieldNode(node, "*");
    }
    
    @Override
    public String visit(PlusExpressionNode node) {
        return handleTwoFieldNode(node, "+");
    }
    
    @Override
    public String visit(RealNode node) {
        return node.getValue();
    }
    
    @Override
    public String visit(TypeNode node) {
        return "";
    }
    
    @Override
    public String visit(AllParametersNode node) {
        String toReturn = "";
        for (SemanticNode n : node.getArray()){
            toReturn += n.accept(this) + ",";
        }
        if (toReturn.length() > 0 ) {
            toReturn = toReturn.substring(0, toReturn.length()-1);
        }
        return toReturn;
    }
    
    @Override
    public String visit(AllVariableDeclarationsNode node) {
        String toReturn = "";
        
        for (SemanticNode n : node.getArray()) {
            toReturn += n.accept(this) + ",";
        }
        
        return trimEnd(toReturn);
    }
    
    @Override
    public String visit(ArgumentNode node) {
        String toReturn = "";
        for (SemanticNode n : node.getArguments()) {
            toReturn += n.accept(this) + ",";
        }
        return trimEnd(toReturn);
    }
    
    private String trimEnd(String toReturn) {
        int max = 0;
        if(toReturn.length() > 1) {
            max = toReturn.length() - 1;
        }
        return toReturn.substring(0, max);
    }

    @Override
    public String visit(CompareOperatorNode node) {
        return node.getValue();
    }
    
    @Override
    public String visit(ComparisonNode node) {
        return node.acceptVisitorLeftHand(this) + "," + node.acceptVisitorMiddle(this) + "," +
                node.acceptVisitorRightHand(this);
    }
    
    @Override
    public String visit(WhileExpressionNode node) {
        return handleTwoFieldNode(node, "do");
    }

    @Override
    public String visit(NegativeExpressionNode node) {
        return "negative:"+node.getContent().accept(this);
    }
    
    @Override
    public String visit(ProgramHeaderNode node) {
        return node.getParameters().accept(this);
    }
    
    @Override
    public String visit(DeclarationsNode node) {
        String variableDec = node.getVariableDeclarations().accept(this);
        addLocalsInUsage(scope, varDec);
        varDec = 0;
        String functionDec = node.getFunctionDeclarations().accept(this);
        return functionDec;
    }
    
    @Override
    public String visit(PrintStatementNode node) {
        String parameters = node.getArgument().accept(this);
        return "print";
    }
    
    @Override
    public String visit(FunctionCallNode node) {
        String params = node.acceptVisitorRightHand(this);
        String name = node.acceptVisitorLeftHand(this);
        System.out.println("BEGIN_CALL: \nPARAMS "+params + "\nCALL "+name);
        return "RETURNVALUE("+name+params+")";//"call "+name+params;
    }
    
    @Override
    public String visit(FunctionHeadingNode node) {
        scope = "function"+node.acceptVisitorLeftHand(this);
        addToUsage(scope);
        String[] count = node.getMiddle().accept(this).split(",");
        addLocalsInUsage(scope, count.length);
        return node.acceptVisitorLeftHand(this);
        
    }

    @Override
    public String visit(AllFunctionDeclarationsNode node) {
        String toReturn = "";
        for (SemanticNode n : node.getArray()) {
            toReturn += n.accept(this) + ", ";
        }
        return toReturn;
    }
    
    @Override
    public String visit(FunctionBodyNode node) {
        
        String oldScope = scope;
        String variableDec = node.getVariables().accept(this);
        addLocalsInUsage(scope, varDec);
        
        String functionBody = node.getBody().accept(this);
        return functionBody;
    }
    
    @Override
    public String visit(ReturnStatementNode node) {
        System.out.println("return: "+node.getArguments().accept(this));
        return "return";
    }
    
    @Override
    public String visit(IfStatementNode node) {
        return handleThreeFieldNode(node, "", "");
////        String ifPart = node.acceptVisitorLeftHand(this);
////        String[] ifParts = ifPart.split(",");
////        int oldLineNumber = lineNumber;
////        lineNumber += 4;
////        node.acceptVisitorMiddle(this);
////        IfHeader3AC ifHeader = new IfHeader3AC(oldLineNumber, manager);
////        ifHeader.setParameters(ifParts[0], ifParts[2], ifParts[1], lineNumber - oldLineNumber - 2);
////        ifHeader.emitCode();
////        oldLineNumber = lineNumber;
////        lineNumber += 2;
////        node.acceptVisitorRightHand(this);
////        
////        IfRest3AC ifRest = new IfRest3AC(oldLineNumber, manager);
////        ifRest.setParameters("", "", "", lineNumber - oldLineNumber - 2);
////        ifRest.emitCode();
//        return "if";
    }
    
    @Override
    public String visit(EmptyNode node) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    private String handleTwoFieldNode(TwoFieldNode node, String op) {
        String left = node.acceptVisitorLeftHand(this);
        String right = node.acceptVisitorRightHand(this);
        String nextTemp = getNextTemporary();
        System.out.println(nextTemp + " := " + left +" "+ op +" "+ right);
//        return "twofield:\n"+left + " "+op + " " + right;
        return nextTemp;
    }

    private String getNextTemporary() {
        return "t"+tempCount++;
    }

    
    private String getNextLabel() {
        return "LABEL"+labelCount++;
    }
    
    private String handleThreeFieldNode(ThreeFieldNode node, String op1, String op2) {
        String left = node.acceptVisitorLeftHand(this);
        String middle = node.acceptVisitorMiddle(this);
        String right = node.acceptVisitorRightHand(this);        
        String nextTemp = getNextTemporary();
        System.out.println(nextTemp + " := " + left +" "+ middle +" "+ right);
//        System.out.println(getNextTemporary() + " := " + middle + op2 + right);
//        return "threefield: \n"+left + " " +op1 + " " +middle + " " + op2 + " " +right;
        return nextTemp;
    }
}

