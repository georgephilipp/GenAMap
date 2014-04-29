Adaptive Multi-Task Lasso: with Application to eQTL Detection
in Advances in Neural Information Processing Systems 23 (NIPS 2010) 
Authors: Seunghak Lee, Jun Zhu and Eric P. Xing 
Contact: Seunghak Lee (seunghak@cs.cmu.edu)

* this version uses SLEP package as a sparse multi-task lasso solver
http://www.public.asu.edu/~jye02/Software/SLEP/

1. Demo (run in matlab)
This simple demo is designed to show you how to run adaptive multi-task Lasso.

% Make test data
make_demo_test_data;

% Run adaptive multi-task Lasso in matlab:
run_aml;

% Check the results
% Left penal shows true beta and 
% right penal shows the beta prediction by adaptive multi-task Lasso
true_beta = load('true_beta.txt');
figure; hold on; 
subplot(1,2,1)
imagesc(true_beta);
subplot(1,2,2)
imagesc(outputBeta);





2. Input files
* For all input files delimiters are space.


2.1 features.txt (priors on predictors):

the number of rows: the number of predictors p
the number of cols: the number of features T

This file includes priors on predictors.
Each row corresponds to T features of a predictor.
For example, if features.txt contains

0.3 0.2
0.2 0.4

it means that the first predictor has two features 0.3 and 0.2, 
and the second predictor has two features 0.2 and 0.4.
Here for simple explanation we show two predictors but it will be much larger in practice.

* Note 
all predictors should have the same number of features.
Also, predictors which are more likely to be true should have small feature
values compared to the otherwise case. For example, if predictor_1 is more likely 
to be true than predictor_2, predictor_1 and predictor_2 may have 0.1 and 1, respectively.
In practice, if you have larger feature values for predictors which are
more likely to be true, you may flip this trend by 1/(feature values).

The features can be anything (e.g. confidence level of a predictor), and
they can have any range of values (e.g. one feature has a range from 0.001 to 0.1;
another feature may have a range from 100 to 10000).


2.2 dictionary.txt (predictors):

This file includes predictors which is N by p matrix.
N: the number of samples
p: the number of predictors


2.3 y.txt (responses):

This file includes responses which is N by K matrix.
N: the number of samples
K: the number of responses





3. Output file


3.1 outputBeta:
Results of regression coefficients which is p by K matrix


3.2 theta:
Results of scaling parameter theta which is p vector (see paper for details)


3.3 omega:
Results of feature weights which is T vector (see paper for details)


3.4 rho:
Results of scaling parameter rho which is p vector (see paper for details)


3.5 nu:
Results of feature weights which is T vector (see paper for details)








4. List of source files

4.1 run_aml.m
: script to run adaptive multi-task lasso

4.2 holdOutMethod.m
: hold out method for determining regularization parameters 

4.3 adaptive_multi_task_lasso.m
: main source code for adaptive multi-task lasso

4.4 learning_weights.m
: dynamically learn theta and rho (see our paper for details of these parameters)

4.5 learning_weights_grad.m
: compute gradient of regularization parameter theta

4.6 learning_weights_grad2.m
: compute gradient of rho

4.7 multi_task_group_lasso.m
: implementation of multi-task group lasso

4.8 multi_task_sgroup_lasso.m
: implementation of multi-task sparse group lasso
  This version is the same as 1.6 but has Lasso penalty as well as
  group penalty acorss multiple responses

4.9 vanilla_block_lasso.m
: implementation of vanilla block lasso
  This is the implementation of the following paper
  Blockwise Coordinate Descent Procedures for the Multi-task Lasso, with
  Applications to Neural Semantic Basis Discovery, ICML, 2009.
  Han Liu, Mark Palatucci, and Jian Zhang 
  
4.10 softThresHolding.m
: soft thresholding function

4.11 projection_routine.m
: Euclidean projection

4.12 standardize.m
: standardize a vector

4.13 make_demo_test_data.m
: make simulation data for demo 

4.14 README.txt
: documentation of adaptive multi-task Lasso implementation



