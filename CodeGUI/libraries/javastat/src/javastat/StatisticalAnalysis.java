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
 * <p>The base class for statistical analysis. A number of classes for a variety
 * of statistical methods can inherit from this class.</p>
 */

public abstract class StatisticalAnalysis
{

    /**
     * Default StatisticalAnalysis constructor.
     */

    public StatisticalAnalysis() {}

    /**
     * The arguments in statistical analysis.
     */

    public Hashtable argument = new Hashtable();

    /**
     * The input data in statistical analysis.
     */

    public Object[] dataObject;

    /**
     * The output in statistical analysis.
     */

    public Hashtable output = new Hashtable();

    /**
     * Gets the arguments.
     * @return the arguments.
     */

    public Hashtable getArgument()
    {
        return this.argument;
    }

    /**
     * Obtains the output.
     * @return the output.
     */

    public Hashtable getOutput()
    {
        return this.output;
    }

}
