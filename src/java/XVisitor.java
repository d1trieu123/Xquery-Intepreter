
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.antlr.v4.runtime.tree.ParseTree;


//import build.XQueryBaseVisitor;
//import build.XQueryParser;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

//import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class XVisitor extends XQueryBaseVisitor<LinkedList<Node>> {
    LinkedList<Node> availNodes = new LinkedList<Node>();

    Document doc;
    String docName = "";
    Node docRootNode;

    LinkedList<Node> answer = new LinkedList<Node>();

    HashMap<String, LinkedList<Node>> contextMap = new HashMap<String, LinkedList<Node>>();

    Stack <HashMap<String, LinkedList<Node>>> contextStack = new Stack<HashMap<String, LinkedList<Node>>>();



/**************************************************
 * XQuery Helpers
**************************************************/
public Node makeElem(String tag, LinkedList<Node> nodes) {
    Node result = doc.createElement(tag);
    for (Node elem : nodes) {
        Node childClone = doc.importNode(elem, true);
        result.appendChild(childClone);
    }

    return result;
}

public Node makeText(String str) {
    return doc.createTextNode(str);
}
/**************************************************
 * Join Expressions 
**************************************************/
@Override 
public LinkedList<Node> visitJoinXQ(XQueryParser.JoinXQContext ctx){

    return visit(ctx.joinClause());
}

@Override
public LinkedList<Node> visitJoinClause(XQueryParser.JoinClauseContext ctx) {
    System.out.println("VISIT JOIN XQ " + ctx.getText());

    LinkedList<Node> firstTable = visit(ctx.xq(0));
    LinkedList<Node> secondTable = visit(ctx.xq(1));

    List<String> firstTableKeys = ctx.joinVars(0).WORD().stream()
                                     .map(ParseTree::getText)
                                     .collect(Collectors.toList());
    List<String> secondTableKeys = ctx.joinVars(1).WORD().stream()
                                      .map(ParseTree::getText)
                                      .collect(Collectors.toList());

    HashMap<String, LinkedList<Node>> secondTableMap = makeHashMap(secondTable, secondTableKeys);
    return joinTables(secondTableMap, firstTable, firstTableKeys);
}

public LinkedList<Node> joinTables(HashMap<String, LinkedList<Node>> map, LinkedList<Node> table, List<String> keys) {
    LinkedList<Node> result = new LinkedList<>();
    System.out.println("JOIN TABLE " + table);
    for (Node tuple : table) {
        System.out.println("JOIN TUPLE " + nodeToString(tuple));
        List<Node> childNodes = getChildNodes(tuple);
        childNodes.stream()
                  .filter(node -> keys.contains(node.getNodeName()))
                  .map(node -> node.getFirstChild().getTextContent())
                  .forEach(key -> {
                      System.out.println("JOIN KEY " + key + " " + tuple);
                      if (map.containsKey(key)) {
                          System.out.println("FOUND KEY " + key + " " + tuple);
                          LinkedList<Node> matchedTuples = map.get(key);
                          result.addAll(combineKey(matchedTuples, tuple));
                      }
                  });
    }
    return result;
}

public LinkedList<Node> combineKey(LinkedList<Node> table1Tuples, Node table2Tuple) {
    LinkedList<Node> combinedList = new LinkedList<>();
    for (Node t1 : table1Tuples) {
        LinkedList<Node> combinedChildren = new LinkedList<>(getChildNodes(t1));
        combinedChildren.addAll(getChildNodes(table2Tuple));
        Node combinedTuple = makeElem("tuple", combinedChildren);
        if (!combinedList.contains(combinedTuple)) {
            combinedList.add(combinedTuple);
        }
    }
    return combinedList;
}

public HashMap<String, LinkedList<Node>> makeHashMap(LinkedList<Node> table, List<String> keys) {
    System.out.println("CREATE HASHMAP " + table);
    System.out.println("VARS " + keys);
    HashMap<String, LinkedList<Node>> resultMap = new HashMap<>();
    table.forEach(tuple -> {
        getChildNodes(tuple).stream()
                            .filter(node -> keys.contains(node.getNodeName()))
                            .forEach(value -> {
                                String key = value.getFirstChild().getTextContent();
                                resultMap.computeIfAbsent(key, k -> new LinkedList<>()).add(tuple);
                            });
    });
    return resultMap;
}


/**************************************************
 * XQuery Expressions
**************************************************/
@Override
public LinkedList<Node> visitVarXQ(XQueryParser.VarXQContext ctx) {
    String var = ctx.getText();
    LinkedList<Node> res = contextMap.get(var);

    return res;
}

@Override
public LinkedList<Node> visitStrXQ(XQueryParser.StrXQContext ctx) {
    String str = ctx.strconst().getText();
    str = str.substring(1, str.length() - 1);
    Node node = makeText(str);
    LinkedList<Node> res = new LinkedList<Node>();
    res.add(node);
    return res;
}

@Override
public LinkedList<Node> visitApXQ(XQueryParser.ApXQContext ctx) {
    return visit(ctx.ap());
}

@Override
public LinkedList<Node> visitParenXQ(XQueryParser.ParenXQContext ctx) {
    return visit(ctx.xq());
}

@Override
public LinkedList<Node> visitCommaXQ(XQueryParser.CommaXQContext ctx) {
    
    LinkedList<Node> res = new LinkedList<Node>();
    LinkedList<Node> left = visit(ctx.xq(0));
    LinkedList<Node> right = visit(ctx.xq(1));
    makeCopy(left, res);
    res.addAll(right);
    return res;
}

public void makeCopy(LinkedList<Node> l1 , LinkedList<Node> l2){
    for(Node n : l1){
        l2.add(n);
    }
}

@Override
public LinkedList<Node> visitSlashXQ(XQueryParser.SlashXQContext ctx) {
    //System.out.println("CHILD XQ " + ctx.getText());
    LinkedList<Node> copier = new LinkedList<Node>();
    LinkedList<Node> tmp = visit(ctx.xq());
    makeCopy(tmp, copier);
    availNodes =copier;
    
    LinkedList<Node> res = new LinkedList<Node>();

    LinkedList<Node> children = visit(ctx.rp());
    res.addAll(children);

    return res;
    
    
    
}


@Override 
public LinkedList<Node> visitDoubleslashXQ(XQueryParser.DoubleslashXQContext ctx) {
    //System.out.println("DESCEND XQ " + ctx.getText());

    LinkedList<Node> res = new LinkedList<Node>();

    LinkedList<Node> copier = new LinkedList<Node>();
    LinkedList<Node> tmp = visit(ctx.xq());

    if (tmp != null) {
        makeCopy(tmp, copier);
        LinkedList<Node> descendants = getDescendants(tmp.get(0));
        copier.addAll(descendants);
        availNodes = copier;


        LinkedList<Node> children = visit(ctx.rp());
        res.addAll(children);

    }

    return res;
}


@Override
public LinkedList<Node> visitTagXQ(XQueryParser.TagXQContext ctx) {
    String tag = ctx.tagname(0).getText();
    //System.out.println("VISIT TAG XQ " + tag);
    LinkedList<Node> nodes = visit(ctx.xq());
    Node node = makeElem(tag, nodes);

    LinkedList<Node> res = new LinkedList<Node>();
    res.add(node);
    return res;

}

    @Override
    public LinkedList<Node> visitLetXQ(XQueryParser.LetXQContext ctx){
        //System.out.println("VISIT LET XQ " + ctx.getText());
        HashMap<String, LinkedList<Node>> old = new HashMap<>(contextMap);
        contextStack.push(old);
        LinkedList<Node> res = visitChildren(ctx);
        contextMap = contextStack.pop();
        return res;
    }


@Override
public LinkedList<Node> visitFlwrXQ(XQueryParser.FlwrXQContext ctx) {
    System.out.println("VISITING FLWR");
    LinkedList<Node> res = new LinkedList<>();
    HashMap<String, LinkedList<Node>> old = new HashMap<>(contextMap);
    contextStack.push(old);
    FLWR(0, res, ctx);
    //System.out.println(res);
    contextMap = contextStack.pop();
    return res;
}

public void FLWR(int i, LinkedList<Node> res, XQueryParser.FlwrXQContext ctx) {
    int forLoops = ctx.forClause().var().size();
    System.out.println("FOR LOOPS " + forLoops);
    if(i == forLoops) {
        if(ctx.letClause() != null) {
            visit(ctx.letClause());
        }
        if(ctx.whereClause() != null) {
            LinkedList<Node> whereRes = visit(ctx.whereClause());
            if (whereRes != null) {
                if(whereRes.isEmpty()) {
                    return;
                }
            }
        }
        LinkedList<Node> returnNodes = visit(ctx.returnClause());
        res.addAll(returnNodes);
    }
    else{
        String var = ctx.forClause().var(i).getText();
        //System.out.println("VARIABLE " + var);
        LinkedList<Node> values = visit(ctx.forClause().xq(i));

        for(Node value: values) {
            //System.out.println("VALUE: " + value);
            HashMap<String, LinkedList<Node>> old = new HashMap<>(contextMap);

            contextStack.push(old);
            LinkedList<Node> temp = new LinkedList<>(); //maybe error from pushing empty map
            temp.add(value);
            contextMap.put(var, temp);
            if(i+1 <= forLoops) {
                FLWR(i+1, res, ctx);
            }
            //System.out.println("POPPING");
            contextMap = contextStack.pop();
        }
        
    }
}





/**************************************************
 * Clause Expressions
**************************************************/


@Override
public LinkedList<Node> visitForClause(XQueryParser.ForClauseContext ctx) {
    System.out.println("VISIT FOR CLAUSE " + ctx.getText());
    return null;
}


@Override
public LinkedList<Node> visitLetClause(XQueryParser.LetClauseContext ctx) {
    for(int i =0 ; i<ctx.var().size(); i++) {
        String var = ctx.var(i).getText();
        LinkedList<Node> value = visit(ctx.xq(i));
        contextMap.put(var, value);
    }

    return null;
}

@Override
public LinkedList<Node> visitWhereClause(XQueryParser.WhereClauseContext ctx) {
    return visit(ctx.condition());
}

@Override
public LinkedList<Node> visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
    return visit(ctx.xq());
}

@Override 
public LinkedList<Node> visitSomeClause(XQueryParser.SomeClauseContext ctx) {
    LinkedList<Node> res = new LinkedList<>();
    if(evalCond(0, ctx)){
        res.add(makeText("true"));
    }
    return res;
}

public boolean evalCond(int i, XQueryParser.SomeClauseContext ctx){
    int varNum = ctx.var().size();
    if(i == varNum){
        if(visit(ctx.condition()).size() != 0){ //only has elements if the condition is true
            return true;
        }
    }
    else{
        String var = ctx.var(i).getText();
        LinkedList<Node> values = visit(ctx.xq(i));
        for(Node value: values){
            HashMap<String, LinkedList<Node>> old = new HashMap<String, LinkedList<Node>>(contextMap);
            contextStack.push(old);
            LinkedList<Node> temp = new LinkedList<Node>();
            temp.add(value);
            contextMap.put(var, temp);
            if(evalCond(i+1, ctx)){
                contextMap = contextStack.pop();
                return true;
            }
            contextMap = contextStack.pop();
        }
       
    }
    return false;
}





/**************************************************
 * Condition Expressions
**************************************************/
@Override
public LinkedList<Node> visitValueEqualityCond(XQueryParser.ValueEqualityCondContext ctx) {
    //System.out.println("VALUE EQUALITY COND " + ctx.getText());
    LinkedList<Node> res = new LinkedList<>();
        LinkedList<Node> first = visit(ctx.xq(0));
        LinkedList<Node> second = visit(ctx.xq(1));
        if(first.isEmpty() || second.isEmpty()){  //if either xq is empty, return false
            return res;
        }
        else{
            for(Node f: first){
                for(Node s: second){
                    //System.out.println("COMPARING " + f + " " + s); //idk why this isnt working
                    if(f.isEqualNode(s)){  //if the nodes are equal, return true

                        res.add(makeText("true"));
                        //System.out.println("RES " + res);
                        return res;
                    }
                }
            }
        }
        
        //System.out.println("RES " + res);
    return res;
}


@Override
public LinkedList<Node> visitIdEqualityCond(XQueryParser.IdEqualityCondContext ctx) {
    LinkedList<Node> res = new LinkedList<>();
        LinkedList<Node> left = visit(ctx.xq(0));
        LinkedList<Node> right = visit(ctx.xq(1));
        if(left.isEmpty() || right.isEmpty()){  //if either xq is empty, return false
            return res;
        }
        if(left.get(0).isSameNode(right.get(0))){  //if the nodes are the same, return true
            res.add(makeText("true"));
        }
        return res;
}

@Override
public LinkedList<Node> visitEmptyCond(XQueryParser.EmptyCondContext ctx){
    //System.out.println("EMPTY COND " + ctx.getText());
    LinkedList<Node> res = new LinkedList<>();
    LinkedList<Node> tmp = visit(ctx.xq());
    //System.out.println("EMPTY COND " + tmp);
    if(tmp.isEmpty()){  //if the xq is empty, return true
        res.add(makeText("true"));
    }
    return res;
}

@Override
public LinkedList<Node> visitSomeCond(XQueryParser.SomeCondContext ctx) {
    return visit(ctx.someClause());
}

@Override
public LinkedList<Node> visitParenCond(XQueryParser.ParenCondContext ctx) {
    return visit(ctx.condition());
}

//Intersection
@Override
public LinkedList<Node> visitAndCond(XQueryParser.AndCondContext ctx){
    //System.out.println("AND COND " + ctx.getText());
    LinkedList<Node> res = new LinkedList<>();
    LinkedList<Node> left = visit(ctx.condition(0));
    //System.out.println("AND LEFT " + left);
    LinkedList<Node> right = visit(ctx.condition(1));
    //System.out.println("AND RIGHT " + right);
    if(left.size() != 0 && right.size() != 0){  //if both conditions are true, return true
        res.add(makeText("true"));
    }
    //System.out.println("AND RES " + res);
    return res;
}

// Union
@Override
    public LinkedList<Node> visitOrCond(XQueryParser.OrCondContext ctx){
        LinkedList<Node> res = new LinkedList<>();
        LinkedList<Node> left = visit(ctx.condition(0));
        LinkedList<Node> right = visit(ctx.condition(1));
        if(left.size() != 0 || right.size() != 0){  //if either condition is true, return true
            res.add(makeText("true"));
        }
        return res;
    }

    @Override
    public LinkedList<Node> visitNotCond(XQueryParser.NotCondContext ctx){
        //System.out.println("NOT COND " + ctx.getText());
        LinkedList<Node> res = new LinkedList<>();
        LinkedList<Node> cond = visit(ctx.condition());
        //System.out.println("NOT CONDITION : " + cond);
        if(cond.isEmpty()){  //if the condition is false, return true
            res.add(makeText("true"));
        }
        return res;
    }




/**************************************************
 * Abspath Helpers
**************************************************/
public LinkedList<Node> getDescendants(Node node) {
    LinkedList<Node> children = new LinkedList<Node>();
    Node child;
    NodeList childrenNodes = node.getChildNodes();
    for (int i = 0; i < childrenNodes.getLength(); i++) {
        child = childrenNodes.item(i);
        children.add(child);
        children.addAll(getDescendants(child));
    }
    return children;
}

public LinkedList<Node> getChildNodes(Node node) {
    LinkedList<Node> children = new LinkedList<Node>();
    Node child;
    NodeList childrenNodes = node.getChildNodes();
    for (int i = 0; i < childrenNodes.getLength(); i++) {
        child = childrenNodes.item(i);
        children.add(child);
    }
    return children;
}

public String nodeToString(Node node){
    String res = "";
    if(node.getNodeType() == Node.ELEMENT_NODE){
        res += "<" + node.getNodeName() + ">";
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            res += nodeToString(children.item(i));
        }
        res += "</" + node.getNodeName() + ">";
    }
    else if(node.getNodeType() == Node.TEXT_NODE){
        res += node.getTextContent();
    }
    return res;
}

public LinkedList<Node> visitDescendant(XQueryParser.RpContext ctx) {
    //System.out.println("visit descendant " + ctx.getText());

    LinkedList<Node> tmp = new LinkedList<Node>();

    for (Node node: this.availNodes) {
        //System.out.println(node);
        tmp.addAll(getDescendants(node));
    }
    for (Node node : tmp) {
        if(!this.availNodes.contains(node)) {
            this.availNodes.add(node);
        }
    }
    return visit(ctx);
}






/**************************************************
* Abspath Expressions
**************************************************/
@Override
public LinkedList<Node> visitChildAP(XQueryParser.ChildAPContext ctx) {
    this.availNodes = visit(ctx.doc());
    answer = visit(ctx.rp());
    return answer;
}
@Override
public LinkedList<Node> visitDescendAP(XQueryParser.DescendAPContext ctx) {
    System.out.println("VISIT DESCEND AP " + ctx.getText());
    this.availNodes =  visit(ctx.doc());
    System.out.println("doc ");
    answer = visitDescendant(ctx.rp());
    System.out.println("VISITED DESCENDANT " + answer);

    return answer;

}

@Override
public LinkedList<Node> visitDocName(XQueryParser.DocNameContext ctx) {
    String fileName = ctx.filename().getText().replace("\"", "");
    fileName = "test/data/" + fileName;
    System.out.println("FILENAME " + fileName);
    File xmlFile = new File(fileName);
    docName = fileName;

    LinkedList<Node> res = new LinkedList<Node>();
    DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
    db.setIgnoringElementContentWhitespace(true);
    try{
        DocumentBuilder dbuilder = db.newDocumentBuilder();
        doc = dbuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        docRootNode = doc.getDocumentElement();
        Element dummy = doc.createElement("dummy");
        dummy.appendChild(docRootNode);
        res.add(dummy);
    } 
    catch (ParserConfigurationException | SAXException | IOException e) {
        e.printStackTrace();
    }
    System.out.println("DOC " + res);
    return res;
    
    
}





/**************************************************
* Relpath Expressions
**************************************************/
@Override 
public LinkedList<Node> visitDescendRP(XQueryParser.DescendRPContext ctx) {
   this.availNodes = visit(ctx.rp(0));
   return visitDescendant(ctx.rp(1));
}

@Override
public LinkedList<Node> visitTextRP(XQueryParser.TextRPContext ctx) {
    NodeList children;
    Node child;
    LinkedList<Node> res = new LinkedList<Node>();

    for(Node node: this.availNodes) {
        children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if(child.getNodeType() == Node.TEXT_NODE) {
                res.add(child);
            }
        }
    }
    return res;
}

@Override
public LinkedList<Node> visitAttrRP(XQueryParser.AttrRPContext ctx) {
    LinkedList<Node> res = new LinkedList<Node>();

    NamedNodeMap attributes;

    for(Node node : this.availNodes) {
        attributes = node.getAttributes();
        for(int i = 0; i<attributes.getLength(); i++) {
            res.add(attributes.item(i));
        }
    }
    this.availNodes = res;
    String attr = ctx.WORD().getText();
    LinkedList<Node> res2 = new LinkedList<Node>();
    for(Node node: this.availNodes) {
        if(node.getNodeName().equals(attr)) {
            res2.add(node);
        }
    }
    return res2;
}

@Override
public LinkedList<Node> visitParentRP(XQueryParser.ParentRPContext ctx) {
    LinkedList<Node> res = new LinkedList<Node>();
    Node parentNode;
    for(Node node: this.availNodes) {
        parentNode = node.getParentNode();
        if(!res.contains(parentNode)) {
            res.add(parentNode);
            
        }
    }
    return res;
}

@Override
public LinkedList<Node> visitSelfRP(XQueryParser.SelfRPContext ctx) {
    return this.availNodes;
}
@Override 
public LinkedList<Node> visitFilterPath(XQueryParser.FilterPathContext ctx) {
    //System.out.println("filter path " + ctx.getText());
    this.availNodes = visit(ctx.rp());
    return visit(ctx.filter());
}

@Override
public LinkedList<Node> visitCommaRP(XQueryParser.CommaRPContext ctx) {
    LinkedList<Node> res1 = visit(ctx.rp(0));
    LinkedList<Node> res2 = visit(ctx.rp(1));
    for(Node node: res2) {
        if(!res1.contains(node)) {
            res1.add(node);
        }
    }
    return res1;
}

@Override
public LinkedList<Node> visitParenRP(XQueryParser.ParenRPContext ctx) {
    return visit(ctx.rp());
}

@Override
public LinkedList<Node> visitChildrenRP(XQueryParser.ChildrenRPContext ctx) {
   LinkedList<Node> res = new LinkedList<Node>();
   NodeList children;
   for(Node node: this.availNodes) {
       children = node.getChildNodes();
       for(int i = 0; i < children.getLength(); i++) {
           res.add(children.item(i));
       }
   }
   return res;
}

@Override
public LinkedList<Node> visitTagRP(XQueryParser.TagRPContext ctx) {
    System.out.println("TAG RP " + ctx.getText());
    LinkedList<Node> tmp = new LinkedList<Node>();
    NodeList children;
    Node child;

    for (Node node : this.availNodes) {
        children = node.getChildNodes();
        // iterate the children to find the nodes with the right tag
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            // Only element nodes have tag names.
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                tmp.add(child);
            }
        }
    }
    this.availNodes = tmp;
    return visit(ctx.tagname());
}

@Override
public LinkedList<Node> visitTagname(XQueryParser.TagnameContext ctx) {
    LinkedList<Node> res = new LinkedList<Node>(); // result nodes

    for (Node node: this.availNodes) {
    
        if (node.getNodeName().equals(ctx.getText())) {
        
            res.add(node);
        }
        
    }
    return res;
}

@Override
public LinkedList<Node> visitChildRP(XQueryParser.ChildRPContext ctx) {
    
    System.out.println("VISITING CHILD RP " + ctx.getText());
    this.availNodes = visit(ctx.rp(0));
    return visit(ctx.rp(1));
    
}




/**************************************************
* Filter Expressions
**************************************************/
@Override
public LinkedList<Node> visitEqualsPF(XQueryParser.EqualsPFContext ctx) {

    LinkedList<Node> tmp = this.availNodes;
    LinkedList<Node> res = new LinkedList<Node>();

    for (Node node: tmp) {
        LinkedList<Node> evalNode = new LinkedList<Node>();
        evalNode.add(node);

        this.availNodes = evalNode;
        LinkedList<Node> l = visit(ctx.rp(0)); // left nodes

        this.availNodes = evalNode;
        LinkedList<Node> r = visit(ctx.rp(1)); // right nodes

        for (Node ln: l)
            for (Node rn: r)
                if (ln.isEqualNode(rn) && !res.contains(node))
                    res.add(node);
    }

    this.availNodes = res;
    return res;
}

@Override
public LinkedList<Node> visitSamePF(XQueryParser.SamePFContext ctx) {
    LinkedList<Node> tmp = this.availNodes;
    LinkedList<Node> res = new LinkedList<Node>();

    for (Node node: tmp) {
        LinkedList<Node> evalNode = new LinkedList<Node>();
        evalNode.add(node);

        this.availNodes = evalNode;
        LinkedList<Node> l = visit(ctx.rp(0));

        this.availNodes = evalNode;
        LinkedList<Node> r = visit(ctx.rp(1));

        // Why not break the loop after finding the first equal node?
        for (Node ln: l)
            for (Node rn: r)
                if (ln.isSameNode(rn) && !res.contains(node))
                    res.add(node);
    }

    this.availNodes = res;
    return res;
}

@Override
public LinkedList<Node> visitRpPF(XQueryParser.RpPFContext ctx) {
    LinkedList<Node> res = new LinkedList<Node>();
    LinkedList<Node> tmp = this.availNodes;

    for (Node node: tmp) {
        LinkedList<Node> evalNode = new LinkedList<Node>();
        evalNode.add(node);
        this.availNodes = evalNode;
        if (visit(ctx.rp()).size() != 0)
            
            res.add(node);
    }

    this.availNodes = res;
    return res;
}

@Override
public LinkedList<Node> visitParenPF(XQueryParser.ParenPFContext ctx) {
    return visit(ctx.filter());
}

@Override
public LinkedList<Node> visitApPF(XQueryParser.ApPFContext ctx) {
    LinkedList<Node> res;
    LinkedList<Node> tmp = this.availNodes;

    HashSet<Node> ls = new HashSet<Node>(visit(ctx.filter(0)));

    this.availNodes = tmp;
    HashSet<Node> rs = new HashSet<Node>(visit(ctx.filter(1)));

    ls.retainAll(rs);
    res = new LinkedList<>(ls);

    return res;
}

@Override
public LinkedList<Node> visitOrPF(XQueryParser.OrPFContext ctx) {
    LinkedList<Node> res;
    LinkedList<Node> tmp = this.availNodes;

    HashSet<Node> ls = new HashSet<Node>(visit(ctx.filter(0)));

    this.availNodes = tmp;
    HashSet<Node> rs = new HashSet<Node>(visit(ctx.filter(1)));

    ls.addAll(rs);
    res = new LinkedList<>(ls);

    return res;
}

@Override
public LinkedList<Node> visitNotPF(XQueryParser.NotPFContext ctx) {
    LinkedList<Node> res;
    HashSet<Node> frontier = new HashSet<Node>(this.availNodes);
    HashSet<Node> remover = new HashSet<Node>(visit(ctx.filter()));

    frontier.removeAll(remover);
    res = new LinkedList<>(frontier);


    return res;
}

@Override
public LinkedList<Node> visitStringPF(XQueryParser.StringPFContext ctx) {
    LinkedList<Node> res = new LinkedList<Node>();

    LinkedList<Node> temp = new LinkedList<Node>();
    temp.addAll(this.availNodes);

    String str = ctx.strconst().getText();
    str = str.replace("\"", "");


    for(Node node: temp) {

        LinkedList<Node> comparisons = new LinkedList<Node>();
        comparisons.add(node);
        this.availNodes = comparisons;
        LinkedList<Node> check = visit(ctx.rp());
        
        for(Node node2 : check) {
            String str2 = node2.getNodeValue();
            String noquotes = str2.replaceAll("\\s", "");
            if(noquotes.equals(str) && !res.contains(node)) {
                res.add(node);
            }
        }
    }
    this.availNodes = res;
    return res;
}

}
