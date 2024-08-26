

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;





public class QueryTransformer extends XQueryBaseVisitor<LinkedList<String>>  {

    LinkedList<String> independentVars = new LinkedList<String>();
    HashMap<String, LinkedList<String>> variableDependencies = new HashMap<String, LinkedList<String>>();

    //HashMap<String, LinkedList<String>> dependentSubClauses = new HashMap<String, LinkedList<String>>();

    HashMap<String, LinkedList<String>> dependentForSubClauses = new HashMap<String, LinkedList<String>>();
    
    HashMap<String, LinkedList<String>> dependentLetSubClauses = new HashMap<String, LinkedList<String>>();
    
    HashMap<String, LinkedList<String>> dependentWhereSubClauses = new HashMap<String, LinkedList<String>>();
    //HashMap<String, LinkedList<String>> valEqualWhereClauses = new HashMap<String, LinkedList<String>>();
    //LinkedList<ParserRuleContext>
    HashMap<String, LinkedList<String>> sortedWhereConds = new HashMap<String, LinkedList<String>>();

    LinkedList<LinkedList<String>> varGroups = new LinkedList<LinkedList<String>>();
    
    HashMap<String, LinkedList<String>> dependentReturnSubClauses = new HashMap<String, LinkedList<String>>();

    String originalQuery;
    String transformedQuery = "";

    public QueryTransformer(String origQuery) {
        super();
        originalQuery = origQuery;
    }

    public String getTransformedQuery() {
        return transformedQuery;
    }

    public void makeCopy(LinkedList<String> l1 , LinkedList<String> l2){
        for(String n : l1){
            l2.add(n);
        }
    }

    /**************************************************
     * XQuery Expressions
    **************************************************/
    @Override
    public LinkedList<String> visitVarXQ(XQueryParser.VarXQContext ctx) {
        LinkedList<String> res = new LinkedList<String>();

        res.add(ctx.getText());

        return res;
    }



    @Override
    public LinkedList<String> visitStrXQ(XQueryParser.StrXQContext ctx) {
        return new LinkedList<String>();
    }



    @Override
    public LinkedList<String> visitApXQ(XQueryParser.ApXQContext ctx) {
        return visit(ctx.ap());
    }



    @Override
    public LinkedList<String> visitSlashXQ(XQueryParser.SlashXQContext ctx) {
        LinkedList<String> res = new LinkedList<String>();

        LinkedList<String> left = visit(ctx.xq());
        LinkedList<String> right = visit(ctx.rp());
        makeCopy(left, res);
        makeCopy(right, res);

        return res;
    }



    @Override
    public LinkedList<String> visitDoubleslashXQ(XQueryParser.DoubleslashXQContext ctx) {
        LinkedList<String> res = new LinkedList<String>();

        LinkedList<String> left = visit(ctx.xq());
        LinkedList<String> right = visit(ctx.rp());
        makeCopy(left, res);
        makeCopy(right, res);

        return res;
    }



    @Override
    public LinkedList<String> visitParenXQ(XQueryParser.ParenXQContext ctx) {
        return visit(ctx.xq());
    }



    @Override
    public LinkedList<String> visitTagXQ(XQueryParser.TagXQContext ctx) {
        return visit(ctx.xq());
    }



    @Override
    public LinkedList<String> visitLetXQ(XQueryParser.LetXQContext ctx) {
        LinkedList<String> res = new LinkedList<String>();

        LinkedList<String> left = visit(ctx.letClause());
        LinkedList<String> right = visit(ctx.xq());
        makeCopy(left, res);
        makeCopy(right, res);

        return res;
    }



    @Override
    public LinkedList<String> visitJoinXQ(XQueryParser.JoinXQContext ctx) {
        return new LinkedList<String>();
    }



    @Override
    public LinkedList<String> visitCommaXQ(XQueryParser.CommaXQContext ctx) {
        LinkedList<String> res = new LinkedList<String>();

        LinkedList<String> left = visit(ctx.xq(0));
        LinkedList<String> right = visit(ctx.xq(1));
        makeCopy(left, res);
        makeCopy(right, res);

        return res;
    }



    private LinkedList<String> sortVarsByDependency(LinkedList<String> varsInScope, LinkedList<String> toSort) {

        LinkedList<String> sorted = new LinkedList<String>();
        makeCopy(varsInScope, sorted);

        LinkedList<String> notSorted = new LinkedList<String>();

        for (int i = 0; i < toSort.size(); i++) {
            String currentVar = toSort.get(i);
            LinkedList<String> currVarDeps = variableDependencies.get(currentVar);
            boolean success = true;

            for (int j = 0; j < currVarDeps.size(); j++) {
                if (varsInScope.contains(currVarDeps.get(j))) { }
                else {
                    success = false;
                    break;
                }
            }
            if (!success) {
                notSorted.add(currentVar);
            }
            else {
                sorted.add(currentVar);
                varsInScope.add(currentVar);
            }
        }

        if (notSorted.size() > 0) {
            return sortVarsByDependency(sorted, notSorted);
        }
        else {
            return sorted;
        }

    }



    @Override
    public LinkedList<String> visitFlwrXQ(XQueryParser.FlwrXQContext ctx) {

        //System.out.println("FLWR");

        LinkedList<String> res = new LinkedList<String>();

        // declaring vars and possibly their dependencies on other vars
        LinkedList<String> forVars = visit(ctx.forClause());
        HashMap<String, LinkedList<String>> dependentVars = new HashMap<String, LinkedList<String>>();

        for (int i = 0; i < forVars.size(); i++) {
            dependentVars.put(forVars.get(i), new LinkedList<String>());
        }

        Set<String> depVars = variableDependencies.keySet();

        for (String dVar : depVars) {
            LinkedList<String> varIsDependentOn = variableDependencies.get(dVar);

            for (int i = 0; i < varIsDependentOn.size(); i++) {
                String parentVar = varIsDependentOn.get(i);

                if (!forVars.contains(parentVar)) {
                    LinkedList<String> parentVarDeps = variableDependencies.get(parentVar);
                    
                    for (String s : parentVarDeps) {
                        if (!varIsDependentOn.contains(s)) {
                            varIsDependentOn.add(s);
                        }
                    }
                }
                else {
                    dependentVars.get(parentVar).add(dVar);
                }
            }

            for (int i = 0; i < varIsDependentOn.size(); i++) {
                if (forVars.contains(varIsDependentOn.get(i))) {
                    if (!dependentVars.get(varIsDependentOn.get(i)).contains(dVar)) {
                        dependentVars.get(varIsDependentOn.get(i)).add(dVar);
                    }
                }
            }
        }

        // now go back thru the variable dependency groups you've created in dependentVars and separate out the for subclauses 
        HashMap<String, String> newForClauses = new HashMap<String, String>();

        for (int i = 0; i < forVars.size(); i++) {
            String forClauseBuilder = "for ";

            LinkedList<String> parentWrapper = new LinkedList<String>();
            parentWrapper.add(forVars.get(i));
            LinkedList<String> sortedDVars = sortVarsByDependency(parentWrapper, dependentVars.get(forVars.get(i)));
            // save list of vars to collection
            varGroups.add(sortedDVars);

            for (int j  = 0; j < sortedDVars.size(); j++) {
                LinkedList<String> depSubClauses = dependentForSubClauses.get(sortedDVars.get(j));

                for (int k = 0; k < depSubClauses.size(); k++) {
                    if (j > 0) {
                        forClauseBuilder = forClauseBuilder + " , \n";
                    }
                    forClauseBuilder = forClauseBuilder + depSubClauses.get(k);
                }
            }
            forClauseBuilder = forClauseBuilder + "\n";
            newForClauses.put(forVars.get(i), forClauseBuilder);
        }
        //for (String v : forVars) {
        //    System.out.println(newForClauses.get(v));
        //}


        HashMap<String, String> returnStatementForNewBlocks = buildReturnStatement(newForClauses, forVars);
        // <b1, <tuple> <aj>{$aj}</aj> <tuple>

        LinkedList<String> letVars;
        if (ctx.letClause() != null) {
            letVars = visit(ctx.letClause());
        }
        else {
            letVars = new LinkedList<String>();
        }



        // transform the where condition into join axes
        LinkedList<String> whereVars;
        String constructedBlock = "";
        if (ctx.whereClause() != null) {

            for (int i = 0; i < forVars.size(); i++) {
                sortedWhereConds.put(forVars.get(i), new LinkedList<String>());

                for (int j = 0; j < forVars.size(); j++) {
                    if (i == j) {
                        continue;
                    } 
                    else {
                        sortedWhereConds.put(forVars.get(i) + " " + forVars.get(j), new LinkedList<String>());
                    }
                }
            }

            HashMap<String, LinkedList<String>> singleVarGroupConds = new HashMap<String, LinkedList<String>>();
            HashMap<String, LinkedList<String>> joinAxes = new HashMap<String, LinkedList<String>>();
            LinkedList<String> outerJoinCandidate = new LinkedList<String>();

            whereVars = visit(ctx.whereClause());
    
            Set<String> varGroupCombos = sortedWhereConds.keySet();

            for (String vgc : varGroupCombos) {
                LinkedList<String> possibleJoinAxes = sortedWhereConds.get(vgc);

                if (possibleJoinAxes.size() == 0) {
                    continue;
                }
                
                if (forVars.contains(vgc)) {
                    //System.out.println(vgc);
                    singleVarGroupConds.put(vgc, possibleJoinAxes);
                }
                else {
                    joinAxes.put(vgc, possibleJoinAxes);
                }
            }

            LinkedList<String> varGroupTreesUsed = new LinkedList<String>();
            HashMap<String, LinkedList<String>> joinBlocks = new HashMap<String, LinkedList<String>>();
            Set<String> joinAxisRootVars = joinAxes.keySet();

            // putting together for and where
            for (String v : joinAxisRootVars) {

                String joinBuilder = "for $tuple in join(\n";

                String[] vars = v.split(" ", 0);
                for (int i = 0; i < vars.length; i++) {

                    if (varGroupTreesUsed.contains(vars[i])  ) {
                        // we're teed up for an outer join, place constructed block in here
                        constructedBlock = constructedBlock.replace("for $tuple in ", "");
                        joinBuilder = joinBuilder + constructedBlock;
                        constructedBlock = "";
                    }
                    else {
                        joinBuilder = joinBuilder + newForClauses.get(vars[i]);

                        //make sure single-axis conditions get packaged in with their blocks
                        if (singleVarGroupConds.containsKey(vars[i])) {
                            boolean whereWord = false;
                            LinkedList<String> ithVarSpecificConds = singleVarGroupConds.get(vars[i]);
                            for (String c : ithVarSpecificConds) {
                                if (!whereWord) {
                                    joinBuilder = joinBuilder + "where ";
                                }
                                joinBuilder = joinBuilder + c;
                            }
                        }

                        joinBuilder = joinBuilder + "\nreturn " + returnStatementForNewBlocks.get(vars[i]);

                        varGroupTreesUsed.add(vars[i]);
                    }

                    joinBuilder = joinBuilder + " , \n";

                }
                LinkedList<String> leftJoinAxis = new LinkedList<String>();
                LinkedList<String> rightJoinAxis = new LinkedList<String>();
                // define join axes
                for (String a : joinAxes.get(v)) {
                    String[] axisSplitter = a.split("=", 0);
                    leftJoinAxis.add(axisSplitter[0].strip().replace("$", ""));
                    rightJoinAxis.add(axisSplitter[1].strip().replace("$", ""));
                }
                String leftAxis = "";
                String rightAxis = "";
                for (int i = 0; i < leftJoinAxis.size(); i++) {
                    if (i > 0) {
                        leftAxis = leftAxis + ", ";
                        rightAxis = rightAxis + ", ";
                    }
                    leftAxis = leftAxis + leftJoinAxis.get(i);
                    rightAxis = rightAxis + rightJoinAxis.get(i);
                }

                joinBuilder = joinBuilder + "[" + leftAxis + "], [" + rightAxis + "]\n)";

                constructedBlock = constructedBlock + joinBuilder;
            }
        }
        else {
            whereVars = new LinkedList<String>();
        }

        // revise return clause if necessary
        if(constructedBlock.length() < 1){
            transformedQuery = "";
            return res;
        }
        LinkedList<String> retVars = visit(ctx.returnClause());
        String orignalRetClause = ctx.returnClause().getText();
        String newRetClause = orignalRetClause;

        for (String r : retVars) {
            String varName = r.replace("$", "");
            String withTuplePrefix = "$tuple/" + varName + "/*";
            newRetClause = newRetClause.replace(r, withTuplePrefix);
        }

        constructedBlock = constructedBlock + "\n" + newRetClause;

        //System.out.println(constructedBlock);

        //TODO: set new transformedQuery value so it can be retrieved
        transformedQuery = constructedBlock;

        return res;
    }

    public HashMap<String, String> buildReturnStatement(HashMap<String, String> newForClauses, LinkedList<String> forVars) {
        //System.out.println("BUILDING RETURNS");
        HashMap<String, String> returnStatementForNewBlocks = new HashMap<>();
        //System.out.println(forVars + "\n");

        // Extract unique variables from the newForClauses values
        

        for (int i = 0; i < forVars.size(); i++) {
            String forVar = forVars.get(i);
            String forClause = newForClauses.get(forVar);
            LinkedList<String> returnStatement = extractUniqueVariables(forClause);
            String tuple = buildTuple(returnStatement);
            returnStatementForNewBlocks.put(forVar, tuple);

        }

        // Optionally, use the uniqueVariables for further processing or output 
        //System.out.println("RETURN STATEMENT FOR NEW BLOCKS" + returnStatementForNewBlocks);
        return returnStatementForNewBlocks;
    }

    public LinkedList<String> extractUniqueVariables(String clause) {
        LinkedHashSet<String> variables = new LinkedHashSet<>();
        Pattern pattern = Pattern.compile("\\$\\w+");


        Matcher matcher = pattern.matcher(clause);
        while (matcher.find()) {
            variables.add(matcher.group());
        }
    

        return new LinkedList<>(variables);
    }

    public String buildTuple (LinkedList<String> variables) {
        StringBuilder tuple = new StringBuilder();
        tuple.append("<tuple> \n");
        for (int i = 0 ; i< variables.size(); i++) {
            String variable = variables.get(i);
            String variableName = variable.substring(1);
            if(i != variables.size()-1 ){
                tuple.append("<" + variableName + ">{" + variable + "}</" + variableName + ">,\n");

            }
            else {
                tuple.append("<" + variableName + ">{" + variable + "}</" + variableName + ">\n");
            }
        }
        tuple.append("</tuple>");
        return tuple.toString();
    }



    /**************************************************
     * Clause Expressions
     * the return value of each visitor should be the variables that are declared/used in that clause
    **************************************************/

    // declaring vars and possibly their dependencies on other vars
    public LinkedList<String> visitForClause(XQueryParser.ForClauseContext ctx) {
        //System.out.println("ForClause");

        LinkedList<String> forVars = new LinkedList<String>();

        for(int i = 0 ; i < ctx.var().size(); i++) {
            String var = ctx.var(i).getText();

            LinkedList<String> dependencies = visit(ctx.xq(i));

            LinkedList<String> depClauses = new LinkedList<String>();
            depClauses.add(var + " in " + ctx.xq(i).getText());

            dependentForSubClauses.put(var, depClauses);
            
            if (dependencies != null && dependencies.size() > 0) {
                variableDependencies.put(var, dependencies);
            }
            else {
                forVars.add(var); // return list of vars with no dependencies
            }
        }

        return forVars;

    }

    // declaring vars and possibly their dependencies on other vars
    public LinkedList<String> visitLetClause(XQueryParser.LetClauseContext ctx) {

        //System.out.println("LetClause");

        for(int i = 0 ; i < ctx.var().size(); i++) {
            String var = ctx.var(i).getText();
            LinkedList<String> dependencies = visit(ctx.xq(i));

            /*
            if (dependentSubClauses.containsKey(var)) {
                dependentSubClauses.get(var).add("let " + var + " := " + ctx.xq(i).toString());
            }
         
            dependentSubClauses.put(var, ctx.xq(i).toString());
            */

            dependentLetSubClauses.put(var, dependencies);
        }

        return new LinkedList<String>();
    }

    // transform the where condition into join axes
    public LinkedList<String> visitWhereClause(XQueryParser.WhereClauseContext ctx) {

        //System.out.println("WhereClause");

        // evaluate conditions so we can discover conditions to use as join axes
        return visit(ctx.condition());
    }

    public LinkedList<String> visitReturnClause(XQueryParser.ReturnClauseContext ctx) {
        
        String text = ctx.xq().getText();
        String replaced = text.replaceAll("\\$(\\w+)", "\\$tuple/$1/*");
        //System.out.println(replaced);

        return visit(ctx.xq());
    }



    /**************************************************
     * Abspath Expressions
    **************************************************/

    @Override
    public LinkedList<String> visitDocName(XQueryParser.DocNameContext ctx) {
        return new LinkedList<String>();
    }

    @Override
    public LinkedList<String> visitChildAP(XQueryParser.ChildAPContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public LinkedList<String> visitDescendAP(XQueryParser.DescendAPContext ctx) {
        return visit(ctx.rp());
    }



    /**************************************************
     * Relpath Expressions
    **************************************************/
    @Override
    public LinkedList<String> visitDescendRP(XQueryParser.DescendRPContext ctx) {
        LinkedList<String> results = new LinkedList<String>();

        LinkedList<String> first = visit(ctx.rp(0));
        LinkedList<String> second = visit(ctx.rp(1));

        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitChildRP(XQueryParser.ChildRPContext ctx) {
        LinkedList<String> results = new LinkedList<String>();

        LinkedList<String> first = visit(ctx.rp(0));
        LinkedList<String> second = visit(ctx.rp(1));

        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitTagname(XQueryParser.TagnameContext ctx) {
        return new LinkedList<String>();
    }

    @Override
    public LinkedList<String> visitCommaRP(XQueryParser.CommaRPContext ctx) {
        LinkedList<String> results = new LinkedList<String>();

        LinkedList<String> first = visit(ctx.rp(0));
        LinkedList<String> second = visit(ctx.rp(1));

        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitParenRP(XQueryParser.ParenRPContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public LinkedList<String> visitFilterPath(XQueryParser.FilterPathContext ctx) {
        LinkedList<String> results = new LinkedList<String>();

        LinkedList<String> rpVars = visit(ctx.rp());
        LinkedList<String> filterVars = visit(ctx.filter());

        makeCopy(rpVars, results);
        makeCopy(filterVars, results);

        return results;
    }

    @Override
    public LinkedList<String> visitTextRP(XQueryParser.TextRPContext ctx) { return new LinkedList<String>(); }

    @Override
    public LinkedList<String> visitAttrRP(XQueryParser.AttrRPContext ctx) { return new LinkedList<String>(); }

    @Override
    public LinkedList<String> visitParentRP(XQueryParser.ParentRPContext ctx) { return new LinkedList<String>(); }

    @Override
    public LinkedList<String> visitSelfRP(XQueryParser.SelfRPContext ctx) { return new LinkedList<String>(); }

    @Override
    public LinkedList<String> visitChildrenRP(XQueryParser.ChildrenRPContext ctx) { return new LinkedList<String>(); }

    @Override
    public LinkedList<String> visitTagRP(XQueryParser.TagRPContext ctx) { return new LinkedList<String>(); }




    /**************************************************
     * Condition Expressions
    **************************************************/
    @Override
    public LinkedList<String> visitValueEqualityCond(XQueryParser.ValueEqualityCondContext ctx) {
        LinkedList<String> first = visit(ctx.xq(0));
        LinkedList<String> second = visit(ctx.xq(1));

        LinkedList<String> firstXQParents = new LinkedList<String>();
        LinkedList<String> secondXQParents = new LinkedList<String>();

        for (int i = 0; i < varGroups.size(); i++) {
            LinkedList<String> ithGroup = varGroups.get(i);
            String ithParent = ithGroup.get(0);
            boolean ithParentAdded = false;

            for (int j = 0; j < first.size(); j++) {
                if (ithGroup.contains(first.get(j)) && !ithParentAdded) {
                    firstXQParents.add(ithParent);
                    ithParentAdded = true;
                }
            }

            ithParentAdded = false;

            for (int j = 0; j < second.size(); j++) {
                if (ithGroup.contains(second.get(j)) && !ithParentAdded) {
                    secondXQParents.add(ithParent);
                }
            }
        }

    

        String formattedSubClause = ctx.xq(0).getText() + " = " + ctx.xq(1).getText();

        LinkedList<String> results = new LinkedList<String>();

        makeCopy(first, results);
        results.add("#####"); //divider 
        makeCopy(second, results);

        if ( (ctx.xq(0) instanceof XQueryParser.StrXQContext)) {
            sortedWhereConds.get(secondXQParents.get(0)).add(formattedSubClause);
            return results;

        }
        else if (ctx.xq(1) instanceof XQueryParser.StrXQContext) {
            sortedWhereConds.get(firstXQParents.get(0)).add(formattedSubClause);
            return results;
        }

        sortedWhereConds.get(firstXQParents.get(0) + " " + secondXQParents.get(0)).add(formattedSubClause);

        return results;
    }

    @Override
    public LinkedList<String> visitIdEqualityCond(XQueryParser.IdEqualityCondContext ctx) {
        LinkedList<String> first = visit(ctx.xq(0));
        LinkedList<String> second = visit(ctx.xq(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitAndCond(XQueryParser.AndCondContext ctx) {
        //System.out.println("AND");
        //System.out.println(ctx.getText());
        LinkedList<String> first = visit(ctx.condition(0));
        LinkedList<String> second = visit(ctx.condition(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitOrCond(XQueryParser.OrCondContext ctx) {
        LinkedList<String> first = visit(ctx.condition(0));
        LinkedList<String> second = visit(ctx.condition(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitSomeCond(XQueryParser.SomeCondContext ctx) {
        return visit(ctx.someClause());
    }

    @Override
    public LinkedList<String> visitSomeClause(XQueryParser.SomeClauseContext ctx) {
        LinkedList<String> results = new LinkedList<String>();

        return results;
    }

    @Override
    public LinkedList<String> visitEmptyCond(XQueryParser.EmptyCondContext ctx) {
        return visit(ctx.xq());
    }

    @Override
    public LinkedList<String> visitParenCond(XQueryParser.ParenCondContext ctx) {
        return visit(ctx.condition());
    }


    /**************************************************
     * Filter Expressions
    **************************************************/
    @Override
    public LinkedList<String> visitRpPF(XQueryParser.RpPFContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public LinkedList<String> visitApPF(XQueryParser.ApPFContext ctx) {
        LinkedList<String> first = visit(ctx.filter(0));
        LinkedList<String> second = visit(ctx.filter(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitOrPF(XQueryParser.OrPFContext ctx) {
        LinkedList<String> first = visit(ctx.filter(0));
        LinkedList<String> second = visit(ctx.filter(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitNotPF(XQueryParser.NotPFContext ctx) {
        return visit(ctx.filter());
    }

    @Override
    public LinkedList<String> visitEqualsPF(XQueryParser.EqualsPFContext ctx) {
        LinkedList<String> first = visit(ctx.rp(0));
        LinkedList<String> second = visit(ctx.rp(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitSamePF(XQueryParser.SamePFContext ctx) {
        LinkedList<String> first = visit(ctx.rp(0));
        LinkedList<String> second = visit(ctx.rp(1));

        LinkedList<String> results = new LinkedList<String>();
        makeCopy(first, results);
        makeCopy(second, results);

        return results;
    }

    @Override
    public LinkedList<String> visitParenPF(XQueryParser.ParenPFContext ctx) {
        return visit(ctx.filter());
    }

    @Override
    public LinkedList<String> visitStringPF(XQueryParser.StringPFContext ctx) {
        return visit(ctx.rp());
    }



}
