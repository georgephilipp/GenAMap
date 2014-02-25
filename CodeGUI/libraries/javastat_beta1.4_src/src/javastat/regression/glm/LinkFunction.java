package javastat.regression.glm;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

/**
 * The enumerated link functions.
 */

public enum LinkFunction
{

        /**
         * Identity function.
         */

        IDENTITY,

        /**
         * Log function.
         */

        LOG,

        /**
         * Inverse function.
         */

        INVERSE,

        /**
         * Inverse square function.
         */

        INVERSE_SQUARE,

        /**
         * Square root function.
         */

        SQUARE_ROOT,

        /**
         * Logistic function.
         */

        LOGIT,

        /**
         * Probit function.
         */

        PROBIT,

        /**
         * Complementary log-log function.
         */

        COMPLEMENTARY_LOGLOG
}

