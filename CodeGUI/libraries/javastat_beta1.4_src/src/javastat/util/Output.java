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
 * The enumerated outputs.
 */

public enum Output
{

        /**
         * The data values in qualitative data analysis.
         * <br>
         * The constant can be used with the following class:
         * QualitativeDataAnalysis.
         */

        DATA_VALUES,

        /**
         * The number of counts associated with the data values.
         * <br>
         * The constant can be used with the following class:
         * QualitativeDataAnalysis.
         */

        FREQUENCY,

        /**
         * The table consisting of the number of counts and associated data
         * values.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * QualitativeDataAnalysis and QuantitativeDataAnalysis.
         */

        FREQUENCY_TABLE,

        /**
         * The five number summary, which are minimum, Q1, Q2, Q3, and maximum.
         * <br>
         * The constant can be used with the following class:
         * QuantitativeDataAnalysis.
         */

        FIVE_NUMBER_SUMMARY,

        /**
         * The confidence interval.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample and twosamples, ChisqTest,
         * OneWayANOVA, StatisticalInferenceTemplate, LinearRegression,
         * CoxRegression, and KaplanMeierEstimate.
         */

        CONFIDENCE_INTERVAL,

        /**
         * The test statistic.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages nonparametric, onesample, twosamples
         * and survival.inference, ChisqTest, OneWayANOVA,
         * StatisticalInferenceTemplate, LinearRegression, and CoxRegression.
         */

        TEST_STATISTIC,

        /**
         * The p-value.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages nonparametric, onesample, twosamples and
         * survival.inference, ChisqTest, OneWayANOVA,
         * StatisticalInferenceTemplate, LinearRegression, and CoxRegression.
         */

        PVALUE,

        /**
         * The selected constant such that the upper-tailed probability for the
         * null distribution of the test statistic is equal to alpha.
         * <br>
         * The constant can be used with the following class: RankSumTest.
         */

        WALPHA,

        /**
         * The selected constant such that the upper-tailed probability for
         * the null distribution of the test statistic is equal to alpha.
         * <br>
         * The constant can be used with the following class: SignRankTest.
         */

        TALPHA,

        /**
         * The point estimate of the parameter of interest.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample and twosamples.
         */

        POINT_ESTIMATE,

        /**
         * The standard error of the point estimate.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * all classes in packages onesample and twosamples.
         */

        POINT_ESTIMATE_SE,

        /**
         * The standard error of the sample proportion under the null
         * hypothesis.
         * <br>
         * The constant can be used with the following class: OneSampProp.
         */

        PROPORTION_SE_H0,

        /**
         * The sample proportion under the null hypothesis.
         * <br>
         * The constant can be used with the following class: TwoSampProps.
         */

        PROPORTION_H0,

        /**
         * The standard error of the sample proportion difference under the null
         * hypothesis.
         * <br>
         * The constant can be used with the following class: TwoSampProps.
         */

        PROPORTION_DIFFERENCE_SE_H0,

        /**
         * The degree of freedom.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * OneSampMeanTTest, MatchedSampMeansTTest, TwoSampMeansTTest,
         * ChisqTest, OneWayANOVA, and LinearRegression.
         */

        DEGREE_OF_FREEDOM,

        /**
         * The linear discriminants.
         * <br>
         * The constant can be used with the following class:
         * DiscriminantAnalysis.
         */

        LINEAR_DISCRIMINANTS,

        /**
         * The groups the new data belong to.
         * <br>
         * The constant can be used with the following class:
         * DiscriminantAnalysis.
         */

        PREDICTED_GROUP,

        /**
         * The principal components.
         * <br>
         * The constant can be used with the following class: PCA.
         */

        PRINCIPAL_COMPONENTS,

        /**
         * The variances of the principal components.
         * <br>
         * The constant can be used with the following class: PCA.
         */

        COMPONENT_VARIANCE,

        /**
         * The fitted values in regression analysis.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression and PSplineRegression.
         */

        FITTED_VALUES,

        /**
         * The residuals in regression analysis.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression and PSplineRegression.
         */

        RESIDUALS,

        /**
         * The hat matrix.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression and PSplineRegression.
         */

        HAT_MATRIX,

        /**
         * The regression sum of squares.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        SSR,

        /**
         * The residual (error) sum of squares.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        SSE,

        /**
         * The total sum of squares (corrected).
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression, SelectionCriterionTemplate, and
         * WeightedSelectionCriterion.
         */

        SST,

        /**
         * The mean residual sum of squares.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        MSE,

        /**
         * The p value for f test.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        F_PVALUE,

        /**
         * The R square.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        R_SQUARE,

        /**
         * The parameter estimates.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression, CoxRegression, and PSplineRegression.
         */

        COEFFICIENTS,

        /**
         * The variance-covariance matrix of the estimated coefficients.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression and CoxRegression.
         */

        COEFFICIENT_VARIANCE,

        /**
         * The F statistic for testing if the coefficients (excluding intercept)
         * are significant.
         * <br>
         * The constant can be used with the following class: LinearRegression.
         */

        F_STATISTIC,

        /**
         * The values of the Kaplan Meier estimates.
         * <br>
         * The constant can be used with the following class:
         * KaplanMeierEstimate.
         */

        SURVIVAL_ESTIMATE,

        /**
         * The variances of the Kaplan-Meier estimates.
         * <br>
         * The constant can be used with the following class:
         * KaplanMeierEstimate.
         */

        SURVIVAL_ESTIMATE_VARIANCE,

        /**
         * The means of the responses in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        MEANS,

        /**
         * The linear predictors in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        LINEAR_PREDICTORS,

        /**
         * The variances of the responses in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        RESPONSE_VARIANCE,

        /**
         * The response residuals in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        RESPONSE_RESIDUALS,

        /**
         * The Pearson residuals in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        PEARSON_RESIDUALS,

        /**
         * The deviance residuals in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        DEVIANCE_RESIDUALS,

        /**
         * The deviance table in the generalized linear models.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * GLMTemplate, LogisticRegression, and LogLinearRegression.
         */

        DEVIANCE_TABLE,

        /**
         * The spline regression design matrix.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * BSplineBasis and PSplineRegression.
         */

        BASIS,

        /**
         * The difference matrix.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * BSplineBasis and PSplineRegression.
         */

        DIFFERENCE,

        /**
         * The penalty.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * SelectionCriterionTemplate and WeightedSelectionCriterion.
         */

        PENALTY,

        /**
         * The minimizer of the (weighted) selection criterion.
         * <br>
         * The constant can be used with the following class: PSplineRegression.
         */

        MINIMIZER,

        /**
         * The residual (error) sum of squares.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * LinearRegression, SelectionCriterionTemplate, and
         * WeightedSelectionCriterion.
         */

        RSS,

        /**
         * The (weighted) model selection criterion.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * SelectionCriterionTemplate and WeightedSelectionCriterion.
         */

        SELECTION_CRITERION,

        /**
         * The cumulative probability.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * PROBABILITY.
         */

        CUMULATIVE,

        /**
         * The probability density.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * PROBABILITY.
         */

        PROBABILITY,

        /**
         * The percentile.
         * <br>
         * The constant can be used with the following classes:
         * <br>
         * PROBABILITY.
         */

        INVERSE;

    /**
     * The descriptions of the outputs.
     * @return the description.
     */

    public String description()
    {
        switch(this)
        {
            case DATA_VALUES: return
                "Description: the data values;\nClass: QualitativeDataAnalysis";
            case FREQUENCY: return
                "Description: the number of counts associated with the data " +
                "values;\n" +
                "Class: QualitativeDataAnalysis";
            case FREQUENCY_TABLE: return
                "Description: the table consisting of the number of counts " +
                "and associated data values;\n" +
                "Classes: QualitativeDataAnalysis, QuantitativeDataAnalysis";
            case FIVE_NUMBER_SUMMARY: return
                "Description: the five number summary, which are minimum, " +
                "Q1, Q2, Q3, and maximum;\n" +
                "Class: QuantitativeDataAnalysis";
            case CONFIDENCE_INTERVAL: return
                "Description: the confidence interval;\n" +
                "Classes: all classes in packages onesample and twosamples, " +
                "ChisqTest, OneWayANOVA, StatisticalInferenceTemplate,\n" +
                " LinearRegression, CoxRegression, KaplanMeierEstimate";
            case TEST_STATISTIC: return
                "Description: the test statistic;\n" +
                "Classes: all classes in packages nonparametric, onesample, " +
                "twosamples and survival.inference, ChisqTest, OneWayANOVA, " +
                "StatisticalInferenceTemplate,\n" +
                " LinearRegression, CoxRegression";
            case PVALUE: return
                "Description: the p-value;\n" +
                "Classes: all classes in packages nonparametric, onesample, " +
                "twosamples and survival.inference, ChisqTest, OneWayANOVA, " +
                "StatisticalInferenceTemplate,\n" +
                " LinearRegression, CoxRegression";
            case WALPHA: return
                "Description: the selected constant such that the " +
                "upper-tailed probability for the null distribution of the " +
                "test statistic is equal to alpha;\n" +
                "Class: RankSumTest";
            case TALPHA: return
                "Description: the selected constant such that the " +
                "upper-tailed probability for the null distribution of " +
                "the test statistic is equal to alpha;\n" +
                "Class: SignRankTest";
            case POINT_ESTIMATE: return
                "Description: the point estimate of the parameter of " +
                "interest;\n" +
                "Classes: all classes in packages onesample and twosamples";
            case POINT_ESTIMATE_SE: return
                "Description: the standard error of the point estimate;\n" +
                "Classes: all classes in packages onesample and twosamples";
            case PROPORTION_SE_H0: return
                "Description: the standard error of the sample proportion " +
                "under the null hypothesis;\n" +
                "Class: OneSampProp";
            case PROPORTION_H0: return
                "Description: the sample proportion under the null " +
                "hypothesis;\n" +
                "Class: TwoSampProps";
            case PROPORTION_DIFFERENCE_SE_H0: return
                "Description: the standard error of the sample proportion " +
                "difference under the null hypothesis;\n" +
                "Class: TwoSampProps";
            case DEGREE_OF_FREEDOM: return
                "Description: the degree of freedom;\n" +
                "Classes: OneSampMeanTTest, MatchedSampMeansTTest, " +
                "TwoSampMeansTTest, ChisqTest, OneWayANOVA, LinearRegression";
            case LINEAR_DISCRIMINANTS: return
                "Description: the linear discriminants;\n" +
                "Class: DiscriminantAnalysis";
            case PREDICTED_GROUP: return
                "Description: the groups the new data belong to;\n" +
                "Class: DiscriminantAnalysis";
            case PRINCIPAL_COMPONENTS: return
                "Description: the principal components;\n" +
                "Class: PCA";
            case COMPONENT_VARIANCE: return
                "Description: the variances of the principal components;\n" +
                "Class: PCA";
            case FITTED_VALUES: return
                "Description: the fitted values in regression analysis;\n" +
                "Class: LinearRegression, PSplineRegression";
            case RESIDUALS: return
                "Description: the residuals in regression analysis;\n" +
                "Class: LinearRegression, PSplineRegression";
            case HAT_MATRIX: return
                "Description: the hat matrix;\n" +
                "Class: LinearRegression, PSplinseRegression";
            case SSR: return
                "Description: regression sum of squares;\n" +
                "Class: LinearRegression";
            case SSE: return
                "Description: residual (error) sum of squares;\n" +
                "Class: LinearRegression, SelectionCriterionTemplate, " +
                "WeightedSelectionCriterion";
            case SST: return
                "Description: total sum of squares (corrected);\n" +
                "Class: LinearRegression";
            case MSE: return
                "Description: mean residual sum of squares;\n" +
                "Class: LinearRegression";
            case F_PVALUE: return
                "Description: the p value for f test;\n" +
                "Class: LinearRegression";
            case R_SQUARE: return
                "Description: R square;\n" +
                "Class: LinearRegression";
            case COEFFICIENTS: return
                "Description: the parameter estimates;\n" +
                "Class: LinearRegression, CoxRegression, PSplineRegression";
            case COEFFICIENT_VARIANCE: return
                "Description: the variance-covariance matrix of the " +
                "estimated coefficients;\n" +
                "Class: LinearRegression, CoxRegression";
            case F_STATISTIC: return
                "Description: F statistic for testing if the coefficients " +
                "(excluding intercept) are significant;\n" +
                "Class: LinearRegression";
            case SURVIVAL_ESTIMATE: return
                "Description: the values of the Kaplan Meier estimates;\n" +
                "Class: KaplanMeierEstimate";
            case SURVIVAL_ESTIMATE_VARIANCE: return
                "Description: the variances of the Kaplan-Meier estimates;\n" +
                "Class: KaplanMeierEstimate";
            case MEANS: return
                "Description: the means of the responses in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case LINEAR_PREDICTORS: return
                "Description: the linear predictors in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case RESPONSE_VARIANCE: return
                "Description: the variances of the responses in the " +
                "generalized linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case RESPONSE_RESIDUALS: return
                "Description: the response residuals in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case PEARSON_RESIDUALS: return
                "Description: the Pearson residuals in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case DEVIANCE_RESIDUALS: return
                "Description: the deviance residuals in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case DEVIANCE_TABLE: return
                "Description: the deviance table in the generalized " +
                "linear models;\n" +
                "Classes: GLMTemplate, LogisticRegression, LogLinearRegression";
            case BASIS: return
                "Description: the spline regression design matrix;\n" +
                "Classes: BSplineBasis, PSplineRegression";
            case DIFFERENCE: return
                "Description: the difference matrix;\n" +
                "Classes: BSplineBasis, PSplineRegression";
            case PENALTY: return
                "Description: the penalty;\n" +
                "Classes: SelectionCriterionTemplate, " +
                "WeightedSelectionCriterion";
            case MINIMIZER: return
                "Description: the minimizer of the (weighted) selection " +
                "criterion;\n" +
                "Classes: PSplineRegression";
            case RSS: return
                "Description: the (weighted) residual sum of squares;\n" +
                "Classes: LinearRegression, SelectionCriterionTemplate, " +
                "WeightedSelectionCriterion";
            case SELECTION_CRITERION: return
                "Description: the (weighted) model selection criterion;\n" +
                "Classes: SelectionCriterionTemplate, " +
                "WeightedSelectionCriterion";
            case CUMULATIVE: return
                "Description: the cumulative probability;\n" +
                "Classes: Probability";
            case PROBABILITY: return
                "Description: the probability density;\n" +
                "Classes: Probability";
            case INVERSE: return
                "Description: the percentile;\n" +
                "Classes: Probability";
            default: return "Unknown output";
        }
    }

}

