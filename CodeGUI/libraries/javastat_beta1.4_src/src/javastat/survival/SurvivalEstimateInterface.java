package javastat.survival;

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
 * This interface defines the required methods for estimating the survival
 * function.
 */

public interface SurvivalEstimateInterface
{

    /**
     * The estimate of the survival function.
     * @param argument the input arguments.
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the values of the estimates.
     */

    Object estimate(Hashtable argument,
                    Object... dataObject);

    /**
     * The confidence intervals for the survival function,
     * @param argument the arguments with the following choices,
     * <br> ALPHA: the level of significance;
     * <br> empty argument: default level of significance equal to 0.05.
     * <br><br>
     * @param dataObject the survival times of the patients and censor
     *                   indicators for the patients.
     * @return the confidence intervals for values of the survival function.
     */

    Object confidenceInterval(Hashtable argument,
                              Object... dataObject);

}
