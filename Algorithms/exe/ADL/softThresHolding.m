function [beta] = softThresHolding(beta_input, gamma)

if abs(beta_input) > gamma
    if beta_input > 0
        beta = beta_input - gamma;
    elseif beta_input < 0
        beta = beta_input + gamma;
    end
else
    beta = 0;
end


    


