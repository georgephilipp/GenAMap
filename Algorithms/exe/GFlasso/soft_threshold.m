function B=soft_threshold(A, c)
    
    B=zeros(size(A));
    pos_idx=A(:)>c;
    neg_idx=A(:)<-c;
    
    B(pos_idx)=A(pos_idx)-c;
    B(neg_idx)=A(neg_idx)+c;

end