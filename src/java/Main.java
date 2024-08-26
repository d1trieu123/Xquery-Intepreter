
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.tree.xpath.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
//import org.w3c.dom.Node;

//import QueryTransformer;



public class Main {

    public static ParseTree parseQuery(String path) throws Exception
    {
        CharStream queryText = CharStreams.fromFileName(path);

        XQueryLexer lexer = new XQueryLexer(queryText);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        XQueryParser parser = new XQueryParser(tokens);

        ParseTree tree = parser.xq();
        //System.out.println(tree.toStringTree(parser)); // print tree as text

        return tree;
    }
    

    public static void printResultsToFile(String queryDoc, LinkedList<Node> resultNodes) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } 
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("result");
        doc.appendChild(rootElement);

        for (int i = 0; i < resultNodes.size(); i++) {

            Node currNode = resultNodes.get(i);

            //if (currNode instanceof Element) {
                Node exportNode = doc.importNode(currNode, true);
                rootElement.appendChild(exportNode);
            //}
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } 
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        String[] pathComponents = queryDoc.split("/");
        String queryFile = pathComponents[pathComponents.length - 1];
        //String[] fnameSegs = queryFile.split(".");
        //String queryFname = fnameSegs[fnameSegs.length - 1];
        StreamResult result = new StreamResult(new File("build/output/RESULT-" + queryFile + ".xml"));

        try {
            transformer.transform(source, result);
        } 
        catch (TransformerException e) {
            e.printStackTrace();
        }
    }



    public static void main( String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println(args.length);
            throw new Exception("Too few args!");
        }
        
        String queryDoc = args[0];

        System.out.println(queryDoc);

        // parse the query
        ParseTree query = parseQuery(queryDoc);

        QueryTransformer trxformer = new QueryTransformer(query.getText());
        trxformer.visit(query);
        String trxQuery = trxformer.getTransformedQuery();

        if (trxQuery.length() > 0) {
            String trxQueryFile = queryDoc.replace(".txt", "-trx.txt");
            PrintWriter out = new PrintWriter(trxQueryFile);
            out.println(trxQuery);
            out.close();

            query = parseQuery(trxQueryFile);
        }

        
        // Evaluation Step
        XVisitor visitor = new XVisitor();

        LinkedList<Node> resultNodes;

        resultNodes = visitor.visit(query);
        
        
        if (resultNodes != null) {
            printResultsToFile(queryDoc, resultNodes);
        }
        else {
            System.out.println("No result.");
        }
        
    }
}
