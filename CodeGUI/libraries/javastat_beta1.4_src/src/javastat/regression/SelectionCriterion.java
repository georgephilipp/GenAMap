package javastat.regression;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @author Wen Hsiang Wei
 * @version 1.4
 */

/**
 * The enumerated selection criteria.
 */

public enum SelectionCriterion
{

        /**
         * Akaike's information criterion.
         */

        AIC,

        /**
         * Bayesian (Schwarz') information criterion.
         */

        BIC,

        /**
         * Mallows' Cp criterion.
         */

        CP,

        /**
         * Akaike's FPE criterion.
         */

        FPE,

        /**
         * Generalized cross-validation criterion.
         */

        GCV,

        /**
         * Shibata's nS criterion.
         */

        nS,

        /**
         * Rice's T criterion.
         */

        T,

        /**
         * Hocking's U criterion.
         */

        U
}
