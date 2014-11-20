package ezbake.data.common.classification;

import ezbake.base.thrift.DocumentClassification;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.accumulo.core.security.ColumnVisibility.Node;
import org.apache.accumulo.core.security.VisibilityParseException;
import org.apache.commons.lang.StringUtils;
import ezbake.classification.ClassificationConversionException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class VisibilityUtils {

    /**
     * Given a DocumentClassification object that has a CAPCO string representing the classification return a list of
     * string arrays which represent the permutations of the visibilities
     *
     * @param documentClassification
     * @return list of string arrays
     *
     * @throws ClassificationConversionException
     * @throws VisibilityParseException
     */
    public static List generateVisibilityList(DocumentClassification documentClassification)
            throws ClassificationConversionException, VisibilityParseException {

        return generateVisibilityList(documentClassification.getClassification(), true);
    }

    /**
     * Given a CAPCO string or an accumulo-style boolean expression string,
     * return a list of string arrays which represent the permutations of the visibilities
     *
     * Example: 'S//REL TO USA,GBR,AUS' becomes this boolean expression 'S&(USA|GBR|AUS)' which results in this List [
     * [S,USA],[S,GBR],[S,AUS] ]
     *
     * @param classification
     * @param isCAPCO true if the 'classificaton' string is a CAPCO, false if it is an accumulo-style boolean expression string.
     * @return
     * @throws ClassificationConversionException
     * @throws VisibilityParseException
     */
    public static List generateVisibilityList(String classification, boolean isCAPCO) throws ClassificationConversionException,
            VisibilityParseException {
        List classificationList = new ArrayList();

        if (!StringUtils.isEmpty(classification)) {
            String booleanExpression = classification;
            if (isCAPCO) {
                booleanExpression = ClassificationUtils.getAccumuloVisibilityStringFromCAPCO(classification);
            }
            final ColumnVisibility visibility = new ColumnVisibility(booleanExpression);

            evaluateAccumuloExpression(visibility.getExpression(), visibility.getParseTree(), classificationList);

            // Some CAPCO strings (e.g. U) will not generate a list at all
            // so we need to check such that they are all wrapped in sublists
            if (classificationList.size() == 1 && classificationList.get(0) instanceof String) {
                final ArrayList check = new ArrayList();
                check.add(classificationList);

                classificationList = check;
            }
        }

        return classificationList;
    }

    /**
     * Based on Accumulo's VisibilityEvaluator.java's "evaluate" method; From the Accumulo-style boolean expression
     * string, generates the security tagging field format for Mongo's $redact operator. The final returned list
     * should be inserted into another list to make the double array format.
     */
    private static void evaluateAccumuloExpression(final byte[] expression, final Node root, List list)
            throws VisibilityParseException {

        switch (root.getType()) {
            case TERM:
                final String term = ClassificationUtils.getAccumuloNodeTermString(expression, root);
                list.add(term);
                break;
            case AND:
                if (root.getChildren() == null || root.getChildren().size() < 2) {
                    throw new VisibilityParseException("AND has less than 2 children", expression,
                            root.getTermStart());
                }
                final List andList = new ArrayList();

                for (final Node child : root.getChildren()) {
                    evaluateAccumuloExpression(expression, child, andList);
                }

                // TODO: i shouldn't have to do this; should just be an add
                if (andList.get(0) instanceof List) {
                    list.addAll(andList);
                } else {
                    list.add(andList);
                }

                break;
            case OR:
                if (root.getChildren() == null || root.getChildren().size() < 2) {
                    throw new VisibilityParseException("OR has less than 2 children", expression, root.getTermStart());
                }
                final List saved = new ArrayList();
                saved.addAll(list);
                list.clear();

                for (final Node child : root.getChildren()) {
                    final List orList = new ArrayList();
                    orList.addAll(saved);

                    evaluateAccumuloExpression(expression, child, orList);

                    list.add(orList);
                }

                break;
            // $CASES-OMITTED$
            default:
                throw new VisibilityParseException("No such node type", expression, root.getTermStart());
        }
    }
}
