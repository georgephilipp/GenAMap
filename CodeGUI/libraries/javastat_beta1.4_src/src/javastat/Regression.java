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
 * <p>The class defines the required method in general regression analysis. </p>
 */

public abstract class Regression extends StatisticalInference
{

    /**
     * Default Regression constructor.
     */

    public Regression(){}

    /**
     * The estimated coefficients.
     * @param argument the arguments.
     * @param dataObject the input data.
     * @return the estimated values of the coefficients.
     */

    public abstract Object coefficients(Hashtable argument,
                                        Object... dataObject);

}

