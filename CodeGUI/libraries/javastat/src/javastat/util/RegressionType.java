package javastat.util;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

/**
 * The enumerated regression types.
 */

public enum RegressionType
{

        /**
         * Linear regression model.
         */

        LINEAR,

        /**
         * Cox's proportional hazards regression model.
         */

        COX,

        /**
         * Logistic regression model.
         */

        LOGISTIC,

        /**
         * Log-linear regression model.
         */

        LOG_LINEAR,

        /**
         * P-spline regression model.
         */

        P_SPLINE
}
