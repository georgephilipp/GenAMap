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
 * The enumerated test types.
 */

public enum TestType
{

        /**
         * Z test.
         */

        Z,

        /**
         * T test.
         */

        T,

        /**
         * Paired z test.
         */

        PAIRED_Z,

        /**
         * Paired t test.
         */

        PAIRED_T,

        /**
         * Wilcoxon sign rank test.
         */

        SIGNRANK,

        /**
         * Wilcoxon rank sum test.
         */

        RANKSUM,

        /**
         * Chi-square test on a two-dimensional contingency table.
         */

        CHISQUARE,

        /**
         * One-way ANOVA (F test).
         */

        ONE_WAY_ANOVA,

        /**
         * Log rank test.
         */

        LOGRANK,

        /**
         * Wilcoxon test in survival analysis.
         */

        WILCOXON
}
