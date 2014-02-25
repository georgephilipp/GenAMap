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
 * The enumerated arguments.
 */

public enum Argument
{

        /**
         * The null value in the null hypothesis.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample and twosamples, SignRankTest, and
         * StatisticalInferenceTemplate.
         */

        NULL_VALUE,

        /**
         * The specification of the alternative hypothesis.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample twosamples and nonparametric, and
         * the class StatisticalInferenceTemplate.
         */

        SIDE,

        /**
         * The level of significance.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample, twosamples, and nonparametric,
         * StatisticalInferenceTemplate, LinearRegression, CoxRegression, and
         * KaplanMeierEstimate.
         */

        ALPHA,

        /**
         * The critical value corresponding to the confidence interval.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * StatisticalInferenceTemplate, OneSampMeanTest, OneSampProp,
         * TwoSampMeansTest, and TwoSampProps.
         */

        CRITICAL_VALUE,

        /**
         * The cumulative distribution function.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * StatisticalInferenceTemplate, OneSampMeanTest, OneSampProp,
         * TwoSampMeansTest, and TwoSampProps.
         */

        CDF,

        /**
         * The type of the test.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * OneSampMeanTest, TwoSampMeansTest, and StatisticalTests.
         */

        TEST_TYPE,

        /**
         * The index indicating whether the intercept should be included in
         * linear regression analysis.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        HAS_INTERCEPT,

        /**
         * The type of the regression model.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * RegressionModels and WeightedSelectionCriterion.
         */

        REGRESSION_TYPE,

        /**
         * The number of classes used in a frequency distribution.
         * <br>
         * The constant can be used with the following class:
         * QuantitativeDataAnalysis.
         */

        NUMBER_OF_CLASS,

        /**
         * The criterion for including just enough components to explain some
         * amount (100%*level) of the variance.
         * <br>
         * The constant can be used with the following class: PCA.
         */

        LEVEL,

        /**
         * The covariannce matrix or correlation matrix used in principal
         * component analysis.
         * <br>
         * The constant can be used with the following class: PCA.
         */

        COVARIANCE_CHOICE,

        /**
         * The link function in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        LINK_FUNCTION,

        /**
         * The criterion used for regression model selection.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * PSplineRegression and WeightedSelectionCriterion.
         */

        SELECTION_CRITERION,

        /**
         * The value reflects the effect of the weight matrix on the weighted
         * prediction risk.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * SelectionCriterionTemplate, PSplineRegreesion, and
         * WeightedSelectionCriterion.
         */

        PSI_FUNCTION,

        /**
         * The smoothing parameter in smoothing spline.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * PSplineRegression and WeightedSelectionCriterion.
         */

        SMOOTHING_PARAMETER,

        /**
         * The number of intervals on the x-domain.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * BSplineBasis, PSplineRegression, and WeightedSelectionCriterion.
         */

        DIVISIONS,

        /**
         * The degree of the piecewise polynomial.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * BSplineBasis, PSplineRegression, and WeightedSelectionCriterion.
         */

        DEGREE,

        /**
         * The order of the penalty.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * BSplineBasis, PSplineRegression, and WeightedSelectionCriterion.
         */

        ORDER,

        /**
         * The probability distribution.
         * <br>
         * The constant can be used with the following class:
         * <br>
         * Probability.
         */

        DISTRIBUTION_TYPE,

        /**
         * The parameter(s) of the probability distribution.
         * <br>
         * The constant can be used with the following class:
         * <br>
         * Probability.
         */

        DISTRIBUTION_PARAMETER,

        /**
         * The index indicating whether the chi-square test is for multinomial
         * population.
         * <br>
         * The constant can be used with the following class: ChisqTest.
         */

        IS_MULTINOMIAL_POPULATION;


    /**
     * The descriptions of the arguments.
     * @return the description.
     */

    public String description()
    {
        switch(this)
        {
            case NULL_VALUE: return
                "Description: the null value;\n" +
                "Classes: all classes in packages onesample and twosamples, " +
                "SignRankTest, StatisticalInferenceTemplate";
            case SIDE: return
                "Description: the specification of the alternative hypothesis" +
                ";\n Classes: all classes in packages onesample twosamples " +
                "and nonparametric, StatisticalInferenceTemplate";
            case ALPHA: return
                "Description: the level of significance;\n" +
                "Classes: all classes in packages onesample, twosamples, and " +
                "nonparametric, StatisticalInferenceTemplate, " +
                "LinearRegression, CoxRegression, KaplanMeierEstimate";
            case CRITICAL_VALUE: return
                "Description: the critical value corresponding to the " +
                "confidence interval;\n" +
                "Classes: StatisticalInferenceTemplate, OneSampMeanTest, " +
                "OneSampProp, TwoSampMeansTest, TwoSampProps";
            case CDF: return
                "Description: the cumulative distribution function;\n" +
                "Classes: StatisticalInferenceTemplate, OneSampMeanTest, " +
                "OneSampProp, TwoSampMeansTest, TwoSampProps";
            case TEST_TYPE: return
                "Description: the type of the test;\n" +
                "Classes: OneSampMeanTest, TwoSampMeansTest, StatisticalTests";
            case HAS_INTERCEPT: return
                "Description: the index indicating whether the intercept " +
                "should be included in linear regression analysis;\n" +
                "Class: LinearRegression";
            case REGRESSION_TYPE: return
                "Description: the type of the regression model;\n" +
                "Classes: RegressionModels, WeightedSelectionCriterion";
            case NUMBER_OF_CLASS: return
                "Description: the number of classes used in a frequency " +
                "distribution;\n" +
                "Class: QuantitativeDataAnalysis";
            case LEVEL: return
                "Description: the criterion for including just enough " +
                "components to explain some amount (100%*level) of the " +
                "variance;\n" +
                "Class: PCA";
            case COVARIANCE_CHOICE: return
                "Description: the covariannce matrix or correlation matrix " +
                "used in principal component analysis;\n" +
                "Class: PCA";
            case LINK_FUNCTION: return
                "Description: the link function in the generalized linear " +
                "models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case SELECTION_CRITERION: return
                "Description: the criterion used for regression model " +
                "selection;\n" +
                "Classes: PSplineRegression, WeightedSelectionCriterion";
            case PSI_FUNCTION: return
                "Description: the value reflects the effect of the weight " +
                "matrix on the weighted prediction risk;\n" +
                "Classes: SelectionCriterionTemplate, PSplineRegreesion, " +
                "WeightedSelectionCriterion";
            case SMOOTHING_PARAMETER: return
                "Description: the smoothing parameter in smoothing spline;\n" +
                "Classes: PSplineRegression, WeightedSelectionCriterion";
            case DIVISIONS: return
                "Description: the number of intervals on the x-domain;\n" +
                "Classes: BSplineBasis, PSplineRegression, " +
                "WeightedSelectionCriterion";
            case DEGREE: return
                "Description: the degree of the piecewise polynomial;\n" +
                "Classes: BSplineBasis, PSplineRegression, " +
                "WeightedSelectionCriterion";
            case ORDER: return
                "Description: the order of the penalty;\n" +
                "Classes: BSplineBasis, PSplineRegression, " +
                "WeightedSelectionCriterion";
            case DISTRIBUTION_TYPE: return
                "Description: the probability distribution;\n" +
                "Class: PROBABILITY";
            case DISTRIBUTION_PARAMETER: return
                "Description: the parameter(s) of the probability" +
                " distribution;\n" +
                "Class: PROBABILITY";
            case IS_MULTINOMIAL_POPULATION: return
                "Description: The index indicating whether the "+
                "chi-square test is for multinomial population;\n" +
                "Class: ChisqTest";
            default: return "Unknown argument";
        }
    }

}
