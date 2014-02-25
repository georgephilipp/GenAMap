package javastat;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

import java.util.*;

/**
 *
 * <p>The class defines the required methods of general statistical inference.
 * </p>
 */

public abstract class StatisticalInference extends StatisticalAnalysis
{

    /**
     * Default StatisticalInference constructor.
     */

    public StatisticalInference() {}

    /**
     * The test statistic.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the value of the test statistic.
     */

    public abstract Object testStatistic(Hashtable argument,
                                         Object ...dataObject);

    /**
     * The p value.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the p value for the test.
     */

    public abstract Object pValue(Hashtable argument,
                                  Object ...dataObject);

}
